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
import java.sql.Connection; // possibly redundant but you know me ill import the entire library of alexandria if given the choice
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public  class ChannelHandler implements HttpHandler {
        private void addCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            Database discord = new Database("jdbc:sqlite:twitter.db");
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
               
            
                // Read input stream
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);

                String name = body.split("\"Name\":\"")[1].split("\"")[0];

                
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {
                    // Connection is established and available for use here
                    
                    // You can now create your PreparedStatement
                String insert = "INSERT INTO ChannelNames (Name) VALUES (?);"; 
                String create = "CREATE TABLE ? (Name TEXT,Message TEXT,Timestamp TEXT,Id INTEGER NOT NULL UNIQUE,Edited INTEGER DEFAULT 0,PRIMARY KEY('Id' AUTOINCREMENT));";
                PreparedStatement ps = connection.prepareStatement(insert + create);
                ps.setString(1, name);
                ps.setString(2, name);

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
                os.close(); return;
                }
            if ("PUT".equals(exchange.getRequestMethod())) {
            
               
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);

                String name = body.split("\"Name\":\"")[1].split("\"")[0];
                String id = body.split("\"Id\":\"")[1].split("\"")[0];

                // note how we arent taking the timestamp.
                
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {

                    String insert = "UPDATE ChannelNames SET Name = ? WHERE Id = ?";
                    PreparedStatement ps = connection.prepareStatement(insert);
                    ps.setString(1, name);
                    ps.setString(2, id);


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
                return;}
            if ("DELETE".equals(exchange.getRequestMethod())) {
               
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);


                // note how we arent taking the timestamp.
                String name = body.split("\"Name\":\"")[1].split("\"")[0];
                String id = body.split("\"Id\":\"")[1].split("\"")[0];
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {
                    String delete = "DELETE FROM ChannelNames WHERE Id = ?;";
                    String drop = "Drop TABLE IF EXISTS ?"; // what if theres multiple tables with the same name?

                    PreparedStatement ps = connection.prepareStatement(delete + drop);
                    ps.setString(1, name);
                    ps.setString(2, id);

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
                return;
                } 
                
            exchange.sendResponseHeaders(405, -1);
                

        
        }
        
    }