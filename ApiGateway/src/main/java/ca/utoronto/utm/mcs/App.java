package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {

      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/", new Gateway());
      ExecutorService executor = Executors.newCachedThreadPool();
      server.setExecutor(executor);
      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
