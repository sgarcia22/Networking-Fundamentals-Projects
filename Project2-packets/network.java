import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;

//Emulator of a network between a sender and a receiver with a network as an intermediary
public class network {

  public static int threadsRunning = 0;
  public static int threadId = 0;
  public static ArrayList<Client> clients = new ArrayList<Client>();

  public static void main (String [] args) {
    if (args.length != 1) {
      System.err.println("Input Arguments wrong, write the port number.");
      System.exit(1);
    }

    //Get the command line Arguments
    int port = Integer.parseInt(args[0]);

    //Accept requests from the clients
    ServerSocket networkSocket = null;
    Socket network;
    try {
     networkSocket = new ServerSocket(port);
     while (true) {
       if (threadsRunning < 2) {
         network = networkSocket.accept();
         if (network != null) {
           //Create a new client
           Client c = new Client (network);
           clients.add(c);
           c.start();
           threadsRunning++;
         }
       }
       if (threadsRunning == 2) {
         break;
       }
     }
    } catch (IOException e) {
      System.out.println("Network cannot accept requests from clients: " + e);
    }
  }

  static class Client extends Thread {

    public Socket currentSocket;
    public Client otherThread;
    public PrintWriter outputStream;
    public BufferedReader inputStream;
    public int threadIden;

    Client (Socket s) {
      super("Client");
      currentSocket = s;
      threadIden = threadId++;
    }

    @Override
    public void run () {

      try {
        //The response the network is sending to the receiver
        outputStream = new PrintWriter(currentSocket.getOutputStream(), true);
        //What the Server is receiving from the receiver
        inputStream = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));

        System.out.println("Connecting to: " + currentSocket.getRemoteSocketAddress().toString());

      } catch (IOException e) {
        System.out.println("Network IOException: " + e);
      }

      //Wait until there are two threads running
      while (threadsRunning != 2) {
        try {
          while(!inputStream.ready()) {}
        } catch (IOException e) {
          System.err.println("Cannot determine if buferred reader is ready to read; attempting to read anyways");
        }
      }

      String input;
      //Get the other thread
      otherThread = clients.get((threadIden == 0 ? 1 : 0));

      try {

        while ((input = inputStream.readLine()) != null) {
          //The end was reached
          if (input.equals("-1")) {
            otherThread.sendMessage("-1");
            outputStream.close();
            inputStream.close();
            System.exit(0);
          }
          //Determine the action the network will take on the packet
          else {
            String action = randomPacket ();
            String [] packetSections = input.split(" ");
            //Change the packet accordingly
            if (action.equals("PASS") || packetSections.length == 1) {
                if (input.contains("ACK")) {
                  System.out.println("Received: " + input + ", " + (input.equals("ACK0") ? "PASS" : "RESEND"));
                  otherThread.sendMessage(input);
                }
                else {
                  System.out.println("Received: Packet" + packetSections[0] + ", " + packetSections[1] + ", PASS");
                  otherThread.sendMessage(input);
                }
            }
            else if (action.equals("CORRUPT") && threadIden == 1) {
              int newChecksum = Integer.parseInt(packetSections[2]) + 1;
              String check = Integer.toString(newChecksum);
              packetSections[2] = check;
              System.out.println("Received: Packet" + packetSections[0] + ", " + packetSections[1] + ", CORRUPT");
              String send = "";
              for (String e : packetSections)
                send += e + " ";
              input = send;
              if (!input.contains("ACK"))
                otherThread.sendMessage(input);
            }
            else {
              System.out.println("Received: Packet" + packetSections[0] + ", " + packetSections[1] + ", DROP");
              sendMessage("ACK2");
            }
          }
        }
      }
      catch (IOException e) {
        System.out.println("Network IOException: " + e);
      }
    }

    public void sendMessage(String message) {
        outputStream.println(message);
    }

    //Randomly choose whether to PASS, DROP, or CORRUPT the packet
    public String randomPacket () {
      Random r = new Random ();
      //Returns double value between 0.0 and 1.0
      double randValue = r.nextDouble ();
      if (randValue < 0.5)
        return "PASS";
      else if (randValue >= 0.5 && randValue <= 0.75)
        return "CORRUPT";
      else
        return "DROP";
    }
  }
}
