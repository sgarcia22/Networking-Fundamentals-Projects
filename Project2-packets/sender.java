import java.io.*;
import java.net.*;
import java.util.*;

public class sender {

  private static int packetNumber = 0;
  private static int sequenceNumber = 0;
  private static boolean sendNextPacket = false;

  public static void main (String [] args) {
    if (args.length != 3) {
      System.err.println("Input Arguments wrong, write the server name, port number, and text file name.");
      System.exit(1);
    }
    //Get the command line Arguments
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);
    String messageFileName = args[2];

    //Parse the file with the individual words
    ArrayList<String> parsedFile = parse (messageFileName);

    ArrayList<String> packets = generatePackets(parsedFile);

    connect(serverName, port, packets);
  }

  private static ArrayList<String> parse (String fileName) {
    File file = new File (fileName);
    BufferedReader fileReader = null;
    ArrayList<String> contents = new ArrayList<String> ();
    //Read the contents of the file
    try {
      fileReader = new BufferedReader (new FileReader (file));
      String currLine = "";
      while ((currLine = fileReader.readLine()) != null) {
          contents.add (currLine);
      }
      //Close the reader if not closed
      if (fileReader != null)
        fileReader.close();
    }
    catch (FileNotFoundException e) {
      System.out.println("Could not find server");
    }
    catch (IOException e) {
      System.out.println("IOException");
    }

    ArrayList<String> singleWords = new ArrayList<String> ();

    String [] tokens;

    //Parse each string
    for (String e : contents) {
      tokens = e.split(" ");
      //Get each word in the string
      for (String t : tokens)
        singleWords.add (t);
      //See if the last word ends in a period, means the end of the packet
      String lastWord = singleWords.get(singleWords.size() - 1);
      if (lastWord.charAt(lastWord.length() - 1) == '.')
        break;
    }
    return singleWords;
  }

  //Generate the checksum for the file
  private static int generateChecksum(String contents) {
    int totalANSICount = 0;
    for (char c : contents.toCharArray()) {
      totalANSICount += (int) c;
    }
    return totalANSICount;
  }

  //Generate the packets
  private static ArrayList<String> generatePackets (ArrayList<String> parsedFile) {
    ArrayList<String> packets = new ArrayList<String> ();
    String currPacket = "";
    for (String e : parsedFile) {
      //Add the sequence number
      currPacket += Integer.toString(sequenceNumber) + " ";
      sequenceNumber = (sequenceNumber == 0 ? 1 : 0);
      //Add the packetID
      currPacket += Integer.toString(packetNumber++) + " ";
      //Add the checksum for the word
      currPacket += Integer.toString(generateChecksum((e))) + " ";
      //Add the string
      currPacket += e;
      //Add the packet information to the Arraylist
      packets.add(currPacket);
      currPacket = "";
    }
    return packets;
  }

  //Connect to the network and send the packetss
  private static void connect (String serverName, int port, ArrayList<String> packets) {
    try {
      //Create a two-way communication with the network
      Socket senderSocket = new Socket(serverName, port);
      //What the sender is sending to the network
      PrintWriter sendToNetwork = new PrintWriter(senderSocket.getOutputStream(), true);
      //What the network sends back
      BufferedReader networkResponse = new BufferedReader(new InputStreamReader(senderSocket.getInputStream()));

      String response = null;

      //Send the packets over through the socket one by one
      for (int i = 0; i < packets.size(); ++i) {
        sendToNetwork.println (packets.get(i));
        while ((response = networkResponse.readLine()) != null) {
          //Corrupt
          if (response.equals("ACK1")) {
            System.out.println("Waiting ACK1, " + packetNumber + ", ACK1, resend Packet" + ((i == 0 || i % 2 == 0) ? "0" : "1"));
            sendToNetwork.println (packets.get(i));
          }
          //Dropped
          else if (response.equals("ACK2")) {
            System.out.println("Waiting ACK0, " + packetNumber + ", DROP, resend Packet" + ((i == 0 || i % 2 == 0) ? "0" : "1"));
            sendToNetwork.println (packets.get(i));
          }
          //Passed
          else if (response.equals("ACK0")) {
            System.out.println("Waiting ACK0, " + packetNumber + ", ACK0, no more packets to send");
            break;
          }
          response = null;
        }
      }
      sendToNetwork.println("-1");
      sendToNetwork.close();
      networkResponse.close();
      senderSocket.close();
    }
    catch (Exception ex) {
      System.out.println("Server Exception: " + ex);
    }
  }
}
