import java.io.*;
import java.net.*;

public class client {
  public static void main (String [] args) {
    //Check length of argument inputs
    if (args.length != 2) {
      System.err.println("Input Arguments wrong, write the server name and the port number");
      System.exit(1);
    }
    //Get command line arguments
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);

    connect(serverName, port);
  }

  public static void connect(String serverName, int port) {
    boolean exit = false;
    try {
      //Create a two-way communication with the server
      Socket socket = new Socket(serverName, port);
      //What the Client is sending to the Server
      PrintWriter sendToServer = new PrintWriter(socket.getOutputStream(), true);
      //What the server sends back
      BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      System.out.println(serverInput.readLine());
      //Client Input
      BufferedReader userInput = new BufferedReader(new InputStreamReader (System.in));

      String inputToServer;
      while ((inputToServer = userInput.readLine()) != null) {

        sendToServer.println(inputToServer);

        int response = Integer.parseInt(serverInput.readLine());

        System.out.print("receive: ");

        switch (response) {

          case -1:
            System.out.println("incorrect operation command.");
            break;

          case -2:
            System.out.println("number of inputs is less than two.");
            break;

          case -3:
            System.out.println("number of inputs is more than four.");
            break;

          case -4:
            System.out.println("one or more of the input contain(s) non-number(s)");
            break;

          case -5:
            try {
              userInput.close();
              serverInput.close();
              sendToServer.close();
              System.out.println("exit");
              exit = true;
            }
            catch (IOException ex) {
              ex.printStackTrace(System.err);
            }
            break;

          default:
            System.out.println(response);
            break;
        }
      }
    }
    catch (Exception ex) {
      if (exit == false) System.out.println("Could not reach server");
    }
  }

}
