import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.util.Map;

//For compiling on the shell on repl: Same on mac

/*
javac -cp sqlite-jdbc-3.23.1.jar: Main.java
java -cp sqlite-jdbc-3.23.1.jar: Main
*/
//Use for windows
//javac -cp sqlite-jdbc-3.23.1.jar; Main.java
public class Main {

    public static void main(String[] args) throws IOException {
    (new Main()).init();
  }

  void print(Object o){ System.out.println(o);}
  void printt(Object o){ System.out.print(o);}

  void init() throws IOException {
   
      // create a port - our Gateway
      int port = 8500;

      Database discord = new Database("jdbc:sqlite:twitter.db");
      String content = discord.runSQL("SELECT * FROM Tweets", "json");
      
      //create the HTTPserver object
      HttpServer server = HttpServer.create(new InetSocketAddress(port),0);

      server.createContext("/", new RouteHandler(discord, "SELECT * FROM Tweets"));
      server.createContext("/post", new PostHandler()); // new file i made to create post requests
      server.createContext("/put", new PutHandler()); // new file i made to create put requests
      server.createContext("/delete", new DeleteHandler()); // new file i made to create delete requests

      //String html = Input.readFile("./index.html");


      //server.createContext("/myPage", new RouteHandler(html)); // the html will no longer be server sided.  to revert this change, make the entire html file into one, combining js and css and put it into this server folder.

      //Start the server      
      server.start();

      System.out.println("Server is listening on port "+port);
      

      

      
    }    
}


