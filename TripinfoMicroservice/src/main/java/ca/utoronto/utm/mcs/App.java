package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.InetSocketAddress;


public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException, ClassNotFoundException {

      // This code must be used when you connect this service to MongoDB
      // Failure to use environment variables will result in a 0 for A2 correctness
      // and any remark requests will be denied.

      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/trip", new TripInfo());
      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
