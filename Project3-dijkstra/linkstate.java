import java.io.*;
import java.net.*;
import java.util.*;

public class linkstate {
  public static void main(String[] args) {

    if (args.length != 1) {
        System.err.println("Input Arguments wrong, write the name of the text file.");
        System.exit(1);
      }

    File file = new File (args[0]);
    BufferedReader fileReader = null;
    ArrayList<Character> contents = new ArrayList<Character> ();

    int sizeOfArr = 0;

    //Parse the file and add the contents into an ArrayList
    try {
      fileReader = new BufferedReader (new FileReader (file));
      String currentLine = "";
      while (!(currentLine = fileReader.readLine()).equals("EOF.")) {
        String trimmedLine = currentLine.replaceAll("\\s+","");
        for (Character e : trimmedLine.toCharArray()) {
          if (e == '.') {
            sizeOfArr++;
            break;
          }
          contents.add (e);
        }
      }
    } catch (IOException e) {
      System.out.println("Reading File Issue");
    }

    //2d array to store the nodes
    int [][] arr = new int[sizeOfArr][sizeOfArr];

    int contentsIndex = 0;
    for (int i = 0; i < sizeOfArr; ++i) {
      for (int j = 0; j < sizeOfArr; ++j) {
        if (contents.get(contentsIndex) == ',')
          contentsIndex++;
        if (contents.get(contentsIndex) == 'N') {
          arr[i][j] = Integer.MAX_VALUE;
          contentsIndex++;
        }
        else
          arr[i][j] = Character.getNumericValue(contents.get(contentsIndex++));
      }
    }
    //Find the shortest algorithm
    dijkstra (arr, sizeOfArr);
  }

  //Find the shortest distance between vertices
  public static int minDistance (int [] shortestDistance, Boolean [] vertexIncluded) {
    //Have a "minimum" value
    int min = Integer.MAX_VALUE;
    //Have a "minimum" index
    int minIndex = -1;
    //Find the shortest distance between vertices
    for (int i = 0; i < shortestDistance.length; ++i) {
      //System.out.print(shortestDistance[i] + "\t");
      if (vertexIncluded[i] == false && min > shortestDistance[i]) {
        min = shortestDistance[i];
        //System.out.print(min + "\t");
        minIndex = i;
      }
    }
    //System.out.println("\n");
    return minIndex;
  }

  //Perform dijkstra's algoritm
  public static void dijkstra(int arr [][], int sizeOfArr) {
    //Print out first part
    int length = 70;
    for (int i = 1; i <= sizeOfArr; ++i) {
      //System.out.printf("D(%d),p(%d)\t", i, i);
      length += 15;
    }
    for (int i = 0; i <= length; ++i) {
        System.out.print("-");
    }
    System.out.print("\n");
    System.out.print("Step \t N' \t\t\t\t\t\t");
    for (int i = 2; i <= sizeOfArr; ++i) {
        System.out.printf("D(%d),p(%d)\t", i, i);
    }
    System.out.print("\n");
    for (int i = 0; i <= length; ++i) {
        System.out.print("-");
    }
    System.out.print("\n");
    String nPrime = "";

    //Keep track of the shortest distance to each of the vertices
    int [] shortestDistance = new int [sizeOfArr];
    //Keep track of which vertices have been done
    Boolean [] vertexIncluded = new Boolean [sizeOfArr];
    //Keep track of the previous node
    int [] previousNode = new int [sizeOfArr];
    //Initialize the array
    for (int i = 0; i < sizeOfArr; ++i) {
      shortestDistance[i] = Integer.MAX_VALUE;
      vertexIncluded[i] = false;
      previousNode[i] = 1;
    }
    shortestDistance[0] = 0;

    //Perform the algorithm
    for (int i = 0; i < sizeOfArr; ++i) {
      int vertexChosen = minDistance (shortestDistance, vertexIncluded);
      vertexIncluded[vertexChosen] = true;
      nPrime += Integer.toString(vertexChosen + 1);
      if (sizeOfArr >= 22) {
        if (i > 6 && i < 12)
          System.out.print(i + "\t" + nPrime + "\t\t\t\t\t");
        else if (i >= 12 && i < 16)
           System.out.print(i + "\t" + nPrime + "\t\t\t\t");
        else if (i >= 16 && i < 20)
           System.out.print(i + "\t" + nPrime + "\t\t\t");
       else if (i >= 20 && i < 24)
          System.out.print(i + "\t" + nPrime + "\t\t");
        else if (i >= 24)
           System.out.print(i + "\t" + nPrime + "\t");
        else
          System.out.print(i + "\t" + nPrime + "\t\t\t\t\t\t");
      }
      else {
        if (i >= 6 && i < 10)
          System.out.print(i + "\t" + nPrime + "\t\t\t\t\t");
        else if (i >= 10 && i < 15)
          System.out.print(i + "\t" + nPrime + "\t\t\t\t");
        else if (i >= 15)
          System.out.print(i + "\t" + nPrime + "\t\t\t");
        else
          System.out.print(i + "\t" + nPrime + "\t\t\t\t\t\t");
      }
      //Update the adjacent/neighbor vertices values
      for (int j = 0; j < sizeOfArr; ++j) {
        if ((arr[vertexChosen][j] != 0) && (!vertexIncluded[j]) && (arr[vertexChosen][j] != Integer.MAX_VALUE))
         {
            int prev = shortestDistance[j];
            shortestDistance[j] = Math.min(shortestDistance[vertexChosen] + arr[vertexChosen][j], shortestDistance[j]);
            if (prev != shortestDistance[j])
              previousNode[j] = vertexChosen + 1;
        }
         //Print out the values
         if (j != 0) {
           if (vertexIncluded[j] == true)
            System.out.print("\t\t");
           else {
             if (shortestDistance[j] == Integer.MAX_VALUE)
               System.out.print("∞" + "\t\t");
             else
               System.out.print(shortestDistance[j] + ", " + previousNode[j] + "\t\t");
           }
         }
      }
      System.out.println();
      for (int k = 0; k <= length; ++k) {
          System.out.print("-");
      }
      System.out.print("\n");
    }
  }
}
