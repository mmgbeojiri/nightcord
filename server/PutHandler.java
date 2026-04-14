import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
//import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.net.httpserver.Headers;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URI;
import java.sql.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public  class PutHandler implements HttpHandler {
        private void addCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("PUT".equals(exchange.getRequestMethod())) {
               
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);

                String name = body.split("\"Name\":\"")[1].split("\"")[0];
                String message = body.split("\"Message\":\"")[1].split("\"")[0];
                String id = body.split("\"Id\":\"")[1].split("\"")[0];
                // note how we arent taking the timestamp.
                
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {

                    String insert = "UPDATE Tweets SET Name = ?, Message = ?, Edited = ? WHERE Id = ?";
                    PreparedStatement ps = connection.prepareStatement(insert);
                    ps.setString(1, name);
                    ps.setString(2, message);
                    ps.setString(3, "1"); // Set Edited to 1 (true)
                    ps.setString(4, id);

                    ps.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Send response
                String response = "Data Received";
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
        }
    }