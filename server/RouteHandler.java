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
/*
  EP - Feb 7, 2023, 
  Overloading Routehandler to reflect the 
  overloading of the runSQL function in Database class
  that accepts a return format.  
*/
public class RouteHandler implements HttpHandler {
    private String response;
    private String contentType;
    private Database db;
    private String sql;

    public RouteHandler(String response){
        this.response = response;
        this.contentType = "text";
    }
    public RouteHandler(Database db, String sql){
        this.db = db;
        this.sql = sql;
        this.contentType = "db";
    }
    public void handle(HttpExchange exchange) {
      if(this.contentType.equals("db") ){
          this.response = this.db.runSQL(this.sql,"json");
          System.out.println("Message to be sent: " + this.response);
      }
      try{
        send(this.response,exchange);
      }catch(IOException e){
        System.out.println("From Handle: " + e.getMessage());
      }
      
    }
	public static Map<String, Object> parseParameters(String method, HttpExchange exchange) throws UnsupportedEncodingException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String query = null;
		if(method.equals("post")){
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            query = br.readLine();
		}else if(method.equals("get")){
			URI requestedUri = exchange.getRequestURI();
            query = requestedUri.getRawQuery();
		}

		if (query != null) {
			String pairs[] = query.split("&");		
			for (String pair : pairs) {
				String param[] = pair.split("=");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
		return parameters;
	}
	public static void send(String response, HttpExchange exchange) throws IOException{
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
		exchange.getResponseHeaders().set("Content-Type", "text/html");
    System.out.println(response.length());
    System.out.println(response.getBytes());
    System.out.println(response.getBytes().length);
    try{
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
    
        os.write(response.getBytes());
        os.close();
    }catch(IOException e){
      System.out.println("From Send: " + e.getMessage());
    }
        
	}


}