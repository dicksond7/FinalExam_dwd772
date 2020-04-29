package ServerSide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import ClientSide.ClientObserver;


/*
 * Author: Vallath Nandakumar and the EE 422C instructors.
 * Data: April 20, 2020
 * This starter code assumes that you are using an Observer Design Pattern and the appropriate Java library
 * classes.  Also using Message objects instead of Strings for socket communication.
 * See the starter code for the Chat Program on Canvas.  
 * This code does not compile.
 */
public class Server extends Observable {

    static Server server;
    private static ArrayList<Socket> clients  = new ArrayList<Socket>();
    private static HashSet<String> usernames = new HashSet<String>();
    private static ArrayList<AuctionItem> auctionItems;


    public static void main (String [] args) {

        try{
            fillAuction();
            System.out.println(auctionItems.size());
        }catch(FileNotFoundException e){
            System.out.println("Text File not Found");
        }
        server = new Server();
        //server.populateItems();
        server.SetupNetworking();
    }

    private void SetupNetworking() {
        int port = 8000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(clientSocket, writer));
                t.start();
                addObserver(writer);
                System.out.println("Got a connection");
            }
        } catch (IOException e) {}
    }

    class ClientHandler implements Runnable {
        private ObjectInputStream reader;
        private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;

        public ClientHandler(Socket clientSocket, ClientObserver writer) throws IOException{
            this.clientSocket = clientSocket;
            clients.add(clientSocket);
            this.reader = new ObjectInputStream(clientSocket.getInputStream());

            
        }

        public void run() {
            AuctionItem itemUpdate;
            try{
                itemUpdate = (AuctionItem)reader.readObject();
                setChanged();
            }catch(IOException | ClassNotFoundException e){

            }


        }
    } // end of class ClientHandler






    private static void fillAuction() throws FileNotFoundException {
        auctionItems = new ArrayList<AuctionItem>();

        Scanner newScanner = new Scanner(new File("StartingItems.txt"));

        while(newScanner.hasNextLine()){
            String name = newScanner.nextLine();
            String cost = newScanner.nextLine();
            int costInt = Integer.parseInt(cost);
            auctionItems.add(new AuctionItem(name, costInt));
        }
    }
}
