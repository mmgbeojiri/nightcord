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

public  class DeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
               
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);


                // note how we arent taking the timestamp.
                String id = body.split("\"Id\":\"")[1].split("\"")[0];
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {

                    String insert = "DELETE FROM Tweets WHERE Id = ?";
                    // Search up what is a "transaction" and how i can use it to not delete my entire database.
                    // update: im not doing this because im gay and lazy and i trust myself in writing a single statement
                    PreparedStatement ps = connection.prepareStatement(insert);
                    ps.setString(1, id);

                    ps.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Send response
                String response = "Data Received";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
        }
    }