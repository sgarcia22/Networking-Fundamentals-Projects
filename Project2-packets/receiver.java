import java.io.*;
import java.net.*;
import java.util.*;

//One message at a time,
//Corrupted - send back content again - Deal with duplicates - just check id

public class receiver {

  //Keeps track of the total number of packets
  private static int totalPackets = 0;
  //Used to check if the sequence number is correct
  private static int sequenceNumber = 0;
  //Keep track of the words in the packet
  private static ArrayList<String> packets = new ArrayList<String> ();

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
      try {
        //Create a two-way communication with the network
        Socket socket = new Socket(serverName, port);
        //WWhat the receiver will send back to the network
        PrintWriter sendToServer = new PrintWriter(socket.getOutputStream(), true);

        //Packet from server
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Send the information to the receiver
        String input;
        while ((input = serverInput.readLine()) != null) {
          if (input.equals("-1"))
            break;

          String [] packetSections = input.split(" ");

          //Determine whether the packet is corrupt or not
          boolean notCorrupt = corruptOrNot(Integer.parseInt(packetSections[2]), packetSections[3]);
          boolean ackBool = (notCorrupt && (sequenceNumber == Integer.parseInt(packetSections[0])) ? true : false);
          String ackString = (ackBool ? "ACK0 " : "ACK1 ");

          System.out.println("Waiting " + sequenceNumber + ", " + packetSections[1] + ", " + input.trim() + ", " + ackString);

          //Check if the full message has been received
          String totalWord = "";
          if (packetSections[3].charAt(packetSections[3].length() - 1) == '.') {
            if (ackBool)
                packets.add (packetSections[3]);
            for (String e : packets)
              totalWord += e + " ";
            //Print out the whole word
            System.out.println(totalWord);
          }

          if (ackBool) {
            packets.add (packetSections[3]);
            sendToServer.println("ACK0");
          }
          else
            sendToServer.println("ACK1");
          sequenceNumber = (sequenceNumber == 0) ? 1 : 0;
        }
        serverInput.close();
        sendToServer.close();
        socket.close();
      }
      catch (Exception e) {
        System.out.println("Could not reach server");
        e.printStackTrace();
      }
  }

  //Determine whether the packet is corrupt or not
  private static boolean corruptOrNot (int checksum, String contents) {
    int realChecksum = generateChecksum(contents);
    return (checksum == realChecksum ? true : false);
  }

  //Generate the checksum for the file
  private static int generateChecksum(String contents) {
    int totalANSICount = 0;
    for (char c : contents.toCharArray()) {
      totalANSICount += (int) c;
    }
    return totalANSICount;
  }

}
