import java.io.*;
import java.net.*;

public class server {
  public static void main (String [] args) {
    if (args.length != 1) {
      System.err.println("Input Arguments wrong, write the port number");
      System.exit(1);
    }
    //Get the command line Arguments
    int port = Integer.parseInt(args[0]);

    //Open up connection to the client
    connect(port);
  }

  public static void connect (int port) {
    try {
      //Accept requests from the client
      ServerSocket server = new ServerSocket(port);
      //Accept incoming requests
      Socket client = server.accept();
      //The response the Server is sending to the Client
      PrintWriter output = new PrintWriter(client.getOutputStream(), true);
      output.println("Hello!");
      //What the Server is receiving from the Client
      BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

      //Send the information back to the client
      String input;
      while ((input = inputStream.readLine()) != null) {
        int answer = determineInput(input);
        output.println(Integer.toString((answer == -6 ? -5 : answer)));
        //Terminate the server as well
        if (answer == -6) {
          try {
            server.close();
          }
          catch (IOException ex) {
            ex.printStackTrace(System.err);
          }
          break;
        }
      }
    }
    catch (Exception ex) {
      System.out.println("Server Exception: " + ex);
    }
  }

  public static int determineInput(String input) {
    //Split input string
    String [] tokens = input.split(" ");
    try {
      if (tokens[0].equals("terminate"))
        return -6;
      if (tokens[0].equals("bye"))
        return -5;
      else if (!tokens[0].equals("add") && !tokens[0].equals("subtract") && !tokens[0].equals("multiply"))
        return -1;
      //Return error code -3 if incorrect commands
      else if (tokens.length < 3)
        return -2;
      else if (tokens.length > 5)
        return -3;
      else {
        //Getting the integers from the tokens
        int [] numbers = new int [tokens.length - 1];
        for (int i = 0; i < numbers.length; ++i) {
          numbers [i] = Integer.parseInt(tokens[i + 1]);
        }
        return calculation (tokens[0], numbers);
      }
    } catch (NumberFormatException ex) {
      return -4;
    }
  }

  //Perform the calculation the client requests
  public static int calculation (String op, int [] numbers) {
    int count = numbers[0];
    for (int i = 1; i < numbers.length; ++i) {
      if (op.equals("add")) {
        count += numbers[i];
      }
      else if (op.equals("subtract")) {
        count -= numbers[i];
      }
      else if (op.equals("multiply")) {
        count *= numbers[i];
      }
    }
    return count;
  }
}
