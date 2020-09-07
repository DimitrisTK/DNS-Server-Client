/**
 * @author DimitrisKourias cs141092
 */

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class DNSserver {
    
    public static void main(String args[]) throws Exception {
        HashMap<String, String> hostsHashmap = new HashMap();
        
        //set default server port or get from argument
        int portNumber = 8888;
        if (args.length == 0) {
            System.out.println("Usage: java DNSServer <port number>");
            System.out.println("The default port is 8888\n");
        } else {
            portNumber = Integer.parseInt(args[0]);
        }
        System.out.println("The UDP Server  is running....");
        DatagramSocket serverSocket = new DatagramSocket(portNumber);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        

        
        
        //Check if hosts.txt file exists if not create it with default entry localhost
        File hostsfile = new File("hosts.txt");
        if ((hostsfile.exists()) == false){
            try(PrintWriter writer = new PrintWriter("hosts.txt", "UTF-8");              )
            {
                writer.println("localhost 127.0.0.1");
                writer.close();
            } catch (IOException e) {
                System.err.format("Cannot write on hosts.txt.");
                e.printStackTrace();
            }
            
        }
		
        
        
        //Get entries from hosts.txt to hashtable
        BufferedReader filereader = null;
        try {
            filereader = new BufferedReader(new FileReader("hosts.txt"));
            String line;
            
            //Read file line by line
            while ((line = filereader.readLine()) != null) {
                //Split the domain and the IP from each line and put it in array
                String [] hostsarray = line.split(" ");
                hostsHashmap.put(hostsarray[0],hostsarray[1]);
            }
        } catch (IOException e) { 
            System.err.format("Cannot read hosts.txt.");
            e.printStackTrace();
        } finally {
            //Close file
            filereader.close();
        }
        
        
        
        
        //Start the server
        while (true) {
            //receive the domain from the clients
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String domain = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            System.out.println("A client requested IP address for " + domain);
            
            //Find the ip from hashtable
            String ip = hostsHashmap.get(domain);
            
            if (ip != null) {
                //If there is an ip for the domain: 
                String ServerResponse = ip;
                sendData = ServerResponse.getBytes();
            } else {
                
                //If there isn't an IP:
                try{
                    //Find domain's ip
                    InetAddress lookup = InetAddress.getByName(domain);  
                    String ServerResponse = lookup.getHostAddress();
                    sendData = ServerResponse.getBytes();
                   
                    //Append new entry to hosts file
                    try(PrintWriter writer = new PrintWriter(new FileOutputStream(new File("hosts.txt"), true));             )
                    {
                        writer.println(lookup.getHostName() + " " + lookup.getHostAddress());
                        writer.close();
                    } catch (IOException e) {
                        System.err.format("Cannot write on hosts.txt.");
                        e.printStackTrace();
                    }
                }catch(Exception e){
                    //if cannot found ip, return -1
                    String strServerResponse = "-1";
                    sendData = strServerResponse.getBytes();
                    System.out.println(e);
                }
            }
            
            //Serve the client the IP or -1
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
        
       
    }
}
