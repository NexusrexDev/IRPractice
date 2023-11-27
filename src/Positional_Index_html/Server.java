package Positional_Index_html;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Server {

    public static String folderPath = "/files";
    public static HashMap<String, PositionalIndex> positionalIndex;
    public static HashMap<Integer, String> fileList;
    public static Preprocessor preProc;
    public static QueryHandler handler;
    public static void main(String[] args) throws IOException {
        preProc = new Preprocessor(folderPath);
        fileList = preProc.getFilenameList();
        positionalIndex = preProc.getPositionalIndex();

        HttpServer server = HttpServer.create(new InetSocketAddress(6969), 0);
        server.createContext("/api/search", new ApiSearchHandler());
        server.createContext("/", new FileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 6969");
    }

    static class ApiSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String contentParam = query.substring(query.indexOf("content=") + "content=".length());
            System.out.println("Content of the text box: " + contentParam);
            handler = new QueryHandler(positionalIndex, fileList.size());
            ArrayList<Integer> result = handler.makeQuery(contentParam.toLowerCase(Locale.ROOT));
            String response = printResults(result);
            sendResponse(exchange, response, "text/html");
        }
    }
    public static String printResults(ArrayList<Integer> resultList) {
        String response = "Content: ";
        if (resultList.size() != 0) {
            for (int docID : resultList) {
                response += "<a href='" + folderPath+"/" +fileList.get(docID) + "' target='_blank'>" + fileList.get(docID) + "</a><br>";
            }
        } else {
            System.out.println("Empty query");
        }
        return response;
    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if(path.equals("/")) {
                path = "/html/index.html";
            }
            if (path.endsWith(".html") || path.endsWith(".js") || path.endsWith(".txt")) {
                serveFile(exchange, path);
            } else {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
            }
        }

        private void serveFile(HttpExchange exchange, String path) throws IOException {
            String filePath = "." + path;
            File file = new File(filePath);

            if (file.exists()) {
                sendResponseHeaders(exchange, 200, file.length(), getContentType(filePath));
                copyFileContent(file, exchange.getResponseBody());
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
            exchange.close();
        }
        private String getContentType(String filePath) {
            if (filePath.endsWith(".html")) {
                return "text/html";
            } else if (filePath.endsWith(".js")) {
                return "application/javascript";
            }
            else if (filePath.endsWith(".txt")) {
                return "text/plain";
            }
            return "application/octet-stream";
        }

        private void copyFileContent(File file, OutputStream outputStream) throws IOException {
            try (InputStream is = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static void sendResponseHeaders(HttpExchange exchange, int statusCode, long contentLength, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, contentLength);
    }
}

