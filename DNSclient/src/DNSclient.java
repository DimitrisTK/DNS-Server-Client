/**
 * @author DimitrisKourias cs141092
 */
import java.io.*;
import java.net.*;

public class DNSclient {
   public static void main(String args[]) throws Exception
   {
       //Set default hostname and port
        String hostName = "127.0.0.1"; 
        int portNumber = 8888; 
        //Replace the default host-port with given arguments
        if (args.length == 2) { 
           hostName = args[0]; 
           portNumber = Integer.parseInt(args[1]); 
        } 
        else
        {
            System.out.println("Usage: java DNSClient <host name> <port number>"); 
            System.out.println("The default values for host and port are 127.0.0.1 and 8888\n");
        }       
      
      //Read the host's name 
      System.out.println("Enter a domain name to get host's IP");        
      BufferedReader keybInput = new BufferedReader(new InputStreamReader(System.in));
      
      //send the domain name to server
      DatagramSocket clientSocket = new DatagramSocket();
      clientSocket.setSoTimeout(1000);
      InetAddress IPAddress = InetAddress.getByName(hostName);
      
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      
      String sentence = keybInput.readLine();
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

      //receive DNSserver responce
      try {
        clientSocket.receive(receivePacket);
        String serverResponse = new String(receivePacket.getData());
        
        if (serverResponse.equals("-1")) {
            System.out.println("NOT FOUND");
        } else {
            System.out.println("The server response: " + serverResponse);
        }
      } 
      catch (SocketTimeoutException e) {
           System.out.println("Timeout reached!!! " + e);
         }
        clientSocket.close();
    }
}