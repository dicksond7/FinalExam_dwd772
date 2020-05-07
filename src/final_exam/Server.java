package final_exam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


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
    private static ArrayList<AuctionItem> itemList;
    private static HashMap<String, AuctionItem> auctionItems;



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
        int port = 4242;
        //continues to run even with RE (easier debugging and client connection)
        while(true) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ClientHandler implements Runnable {
        private ObjectInputStream reader;
        private  ClientObserver writer; //Extends ObjectOutputStream, implements Observer
        Socket clientSocket;

        public ClientHandler(Socket clientSocket, ClientObserver writer) throws IOException{
            this.writer = writer;
            this.clientSocket = clientSocket;
            clients.add(clientSocket);
            this.reader = new ObjectInputStream(clientSocket.getInputStream());

            try {
                //first item sent by client will be username and each subsequent item will be in form of AuctionItem
                for(int i = 0; i < itemList.size(); i++){
                    writer.update(server, itemList.get(i));
                }
                String username = (String) reader.readObject();
                usernames.add(username);
                System.out.println(username);

            }catch(ClassNotFoundException e){
                System.out.println("Invalid Username");
            }

        }

        public void run() {

            AuctionItem itemUpdate;
            try{
                while(true) {
                    synchronized (auctionItems) {
                        itemUpdate = (AuctionItem) reader.readObject();
                        auctionItems.replace(itemUpdate.getName(), itemUpdate);
                        updateItem(auctionItems.get(itemUpdate.getName()));
                        System.out.println("Auction item received: " + itemUpdate.getName());
                        setChanged();
                        writer.update(server, itemUpdate);
                    }
                }
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();

            }


        }
    } // end of class ClientHandler






    private static void fillAuction() throws FileNotFoundException {
        auctionItems = new HashMap<String, AuctionItem>();
        itemList = new ArrayList<AuctionItem>();

        Scanner newScanner = new Scanner(new File("StartingItems.txt"));
        int pagePosition = 1; //used for making sure pagepostion is unique on each Auctionitem
        while(newScanner.hasNextLine()){
            String name = newScanner.nextLine();
            String cost = newScanner.nextLine();
            int costInt = Integer.parseInt(cost);
            int buyNowPrice = Integer.parseInt(newScanner.nextLine());
            AuctionItem newItem = new AuctionItem(name, costInt, pagePosition,buyNowPrice);
            auctionItems.put(name, newItem);
            itemList.add(newItem);
            pagePosition++;
        }
    }

    private static void updateItem(AuctionItem item){
        if(item.getBid() >= item.getBuyNowPrice()){
            item.sold();
        }
        item.setPrice(item.getBid());
    }


    private static void updateAuctionItems(ClientObserver writer){

    }
}
