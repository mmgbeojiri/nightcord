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
            if ("GET".equals(exchange.getRequestMethod())) {
                new RouteHandler(discord, "SELECT * FROM ChannelNames");
            }
            if ("POST".equals(exchange.getRequestMethod())) {
               
            
                // Read input stream
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);
                // {"Name":"Michael","Message":"First POST request on the server.","Timestamp":"3/27/2026, 2:48:46 PM"} 
                // we need thisinto "('Michael','First POST request on the server.','3/27/2026, 2:48:46 PM')"
                String name = body.split("\"Name\":\"")[1].split("\"")[0];
                String message = body.split("\"Message\":\"")[1].split("\"")[0];
                String timestamp = body.split("\"Timestamp\":\"")[1].split("\"")[0];
                // we need to make a future one to accept id

                //This is SQL injection code at risk, and will only be left in as an example of what not to do.
                /*String semiSQLString = String.format("('%s','%s','%s')", name, message, timestamp);
                System.out.println(semiSQLString);
                String fullSQLString = "INSERT INTO Tweets VALUES " + semiSQLString;

                discord.runSQL(fullSQLString);
                System.out.println(fullSQLString);
                System.out.println("successfully inserted into it.");*/
                
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {
                    // Connection is established and available for use here
                    
                    // You can now create your PreparedStatement
                String insert = "INSERT INTO Tweets (Name, Message, Timestamp) VALUES (?, ?, ?);";
                PreparedStatement ps = connection.prepareStatement(insert);
                ps.setString(1, name);
                ps.setString(2, message);
                ps.setString(3, timestamp);

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
                os.close();}
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
                }
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