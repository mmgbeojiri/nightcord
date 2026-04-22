import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetHandler implements HttpHandler {
    
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
        
        if ("GET".equals(exchange.getRequestMethod())) {
            
            
            String channel = "";
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received: " + body);
                if (body.isEmpty()) {
                    throw new Error("Empty body");
                }
                channel = body.split("\"Channel\":\"")[1].split("\"")[0];
            } catch (Error e) {
                channel = "Tweets"; // default, assuming were going to the endpoint in the browser.
                //send("Error: Missing Channel parameter", exchange);
            }
            System.out.println("Channel: " + channel);
            
            String response = "";
            
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:twitter.db")) {
                
                String query = "SELECT * FROM ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, channel);
                System.out.println("Executing at channel: " + channel);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Format result as JSON
                    response = "{\"Id\":\"" + rs.getString("Id") + 
                              "\",\"Name\":\"" + rs.getString("Name") + 
                              "\",\"Message\":\"" + rs.getString("Message") + 
                              "\",\"Timestamp\":\"" + rs.getString("Timestamp") + 
                              "\",\"Edited\":\"" + rs.getString("Edited") + "\"}";

                    System.out.println("Response: " + response);
                } else {
                    exchange.sendResponseHeaders(500, -1);
                    response = "{\"error\":\"Tweet not found\"}";
                }
                
                rs.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
                response = "{\"error\":\"Database error: " + e.getMessage() + "\"}";
            }
            
            send(response, exchange);
            
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
    
    public static void send(String response, HttpExchange exchange) throws IOException{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin","*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
    
    byte[] bytes = response == null ? new byte[0] : response.getBytes(StandardCharsets.UTF_8);
    System.out.println(bytes.length);
    try{
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
    
        os.write(bytes);
        os.close();
    }catch(IOException e){
      System.out.println("From Send: " + e.getMessage());
    }
  }
}
