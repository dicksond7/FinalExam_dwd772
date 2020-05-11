package ClientSideCode_NotRunnable;
/*
 * Author: Vallath Nandakumar and EE 422C instructors
 * Date: April 20, 2020
 * This starter code is from the MultiThreadChat example from the lecture, and is on Canvas.
 * It doesn't compile.
 */

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import final_exam.AuctionItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class AuctionClient extends Application {
    // I/O streams
    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    Socket server;
    private boolean hasLoggedOn = false;
    private ArrayList<AuctionItem> auctionItems;
    private ArrayList<Button> buttons;
    private HashMap <Button, AuctionItem> itemButtons;
    private String username;
    private ArrayList<String> History;
    private ArrayList<Text> priceLabels;
    private int itemsSold;
    ArrayList<Integer> soldItems;





    @Override
    public void start(Stage primaryStage) {
        itemsSold = 0;
        priceLabels = new ArrayList<Text>();
        History = new ArrayList<String>();
        itemButtons = new HashMap<Button, AuctionItem>();
        initializeButtons(5);
        GridPane mainPane = new GridPane();





        //display login which will update gridpane if user logs on
        displayLogin(primaryStage, mainPane);
        try {
            // Create a socket to connect to the server
            server = new Socket("192.168.5.171", 4242);

            // Create an output stream to send data to the server
            toServer = new ObjectOutputStream(server.getOutputStream());

            // Create an input stream to receive data from the server
            fromServer = new ObjectInputStream(server.getInputStream());

//            String userName = "Davo";
//            toServer.writeObject(userName);
//            toServer.flush();


            Thread readServer = new Thread(new inputHandler(toServer, fromServer, mainPane));
            readServer.start();
            //System.out.println("Connection Started");


        }
        catch (IOException ex) {
            //ex.printStackTrace();
            System.out.println("Could not connect");

        }





        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 700, 650);
        primaryStage.setTitle("Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage

        //updateGridPane(mainPane);
        mainPane.setGridLinesVisible(false);
        //newButton(mainPane); //extra button used for debugging gui updating

        //primaryStage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }


    //test if bid is valid
    private boolean isBidValid(double bid, AuctionItem item){
        if(bid > item.getPrice()) {
            //System.out.println("Valid Bid");
            return true;
        }else{
            return false;
        }
    }


    //use buttons as way to organize auction items
    private void initializeButtons(int itemsPerPage){
        buttons = new ArrayList<Button>();
        for(int i = 1; i <= itemsPerPage; i++) {
            Button newButton = new Button("Place Bid");
            Text priceLabel = new Text();
            priceLabels.add(priceLabel);
            buttons.add(newButton);
            itemButtons.put(newButton, null);



        }
    }

    //used to generate gridpane nodes such as buttons, texts, textboxes
    private void updateGridPane(GridPane mainPane){
        mainPane.getChildren().clear();
        //System.out.println(itemButtons.size());
        int numItemsPerPage = 5;
        mainPane.setPadding(new Insets(10, 5, 10, 30));
        //mainPane.setGridLinesVisible(true);
        mainPane.getColumnConstraints().add(new ColumnConstraints(300));
        mainPane.getRowConstraints().add(new RowConstraints(30));
        Label welcomeLabel = new Label("Welcome to EBuy");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 30));
        GridPane.setValignment(welcomeLabel, VPos.TOP);
        Label welcomeUser = new Label("Hello, \n" + username);
        Button logout = new Button("Logout");
        GridPane.setHalignment(welcomeUser, HPos.CENTER);
        GridPane.setValignment(welcomeUser, VPos.TOP);
        GridPane.setHalignment(logout, HPos.LEFT);
        welcomeUser.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        mainPane.add(welcomeLabel, 0, 0, 2, 2);
        mainPane.add(welcomeUser, 1, 0, 1, 2);
        mainPane.add(logout, 1, 0);
        logout.setFocusTraversable(false);
        logout.setOnAction(event -> {

            Platform.exit();
            System.exit(0);

        });
        for(int i = 0; i < 2*numItemsPerPage; i++){
            mainPane.getRowConstraints().add(new RowConstraints(500/(2*numItemsPerPage)));
        }
        mainPane.setHgap(10);
        mainPane.setVgap(5);
        mainPane.getColumnConstraints().add(new ColumnConstraints(500));
        Button history = new Button("History");
        Text historyError = new Text("");

        VBox historyBox = new VBox();
        historyBox.getChildren().addAll(history, historyError);
        GridPane.setValignment(historyBox, VPos.TOP);
        mainPane.add(historyBox , 1, 1);
        ArrayList<Text> errorMessages = new ArrayList<Text>();

        for(int i = 0; i < itemButtons.size(); i++){
            AuctionItem correspondingItem = itemButtons.get(buttons.get(i));
            Label itemName = new Label(correspondingItem.getName());
            itemName.setFont(Font.font("Arial", FontWeight.LIGHT, 21));
            VBox itemWithErrorLabel = new VBox();
            VBox.setMargin(itemName, new Insets(5, 10, 20, 0));
            Text errorMessage = new Text("");
            itemWithErrorLabel.getChildren().addAll(itemName , errorMessage);
            errorMessages.add(errorMessage);
            mainPane.add(itemWithErrorLabel, 0, 2 * correspondingItem.getPagePosition(), 1, 2);
        }
        int positionCounter = 1;
        for(int i = 0; i < itemButtons.size(); i++){
            Button placeBid = buttons.get(i);
            AuctionItem correspondingItem = itemButtons.get(buttons.get(i));
            if(correspondingItem != null) {
                if(false){
//                    Label itemName = new Label(correspondingItem.getName());
//                    itemName.setFont(Font.font("Arial", FontWeight.LIGHT, 21));
//                    mainPane.add(itemName, 0, 2 * correspondingItem.getPagePosition(), 1, 2);
//                    Label sold = new Label("SOLD");
//                    GridPane.setHalignment(sold, HPos.CENTER);
//                    sold.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//                    mainPane.add(sold, 1, 2 * correspondingItem.getPagePosition(), 1, 2);
                    Label sold = new Label("Sold to " + correspondingItem.getBuyer() + " for $" + correspondingItem.getPrice());
                    GridPane.setHalignment(sold, HPos.LEFT);
                    sold.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                    mainPane.add(sold, 1, 2 * correspondingItem.getPagePosition(), 1, 2);
                }else {
                    //initialize texts
                    Text errorMessage = errorMessages.get(i);
                    Text priceLabel = priceLabels.get(correspondingItem.getPagePosition() - 1);
                    priceLabel.setText("Current Bid: $" + correspondingItem.getPrice());
                    //initialize item name on left column
                    //initialize item price
                    Text buyNowLabel = new Text("Buy Now: $" + correspondingItem.getBuyNowPrice());

                    VBox prices = new VBox();
                    prices.getChildren().addAll(priceLabel, buyNowLabel);
                    FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

                    double prefWidth = Math.max(fontLoader.computeStringWidth(priceLabel.getText(), buyNowLabel.getFont()), fontLoader.computeStringWidth(priceLabel.getText(), buyNowLabel.getFont()));
                    //System.out.println(prefWidth);
                    prices.setPrefWidth(prefWidth);
                    prices.setSpacing(30);
                    Button buyNow = new Button("Buy Now");
                    HBox topHalf = new HBox();
                    topHalf.getChildren().addAll(prices, buyNow);

                    topHalf.setSpacing(220 - prefWidth);
                    GridPane.setHalignment(topHalf,HPos.CENTER);
                    mainPane.add(topHalf, 1, 2 * correspondingItem.getPagePosition());


                    mainPane.add(priceLabel, 1, 2 * correspondingItem.getPagePosition());
                    // hbox containing bid button and bid field
                    Label bitLabel = new Label("Enter Bid:");
                    TextField bidAmount = new TextField();
                    HBox setBidding = new HBox();
                    setBidding.getChildren().addAll(bitLabel, bidAmount, placeBid);
                    setBidding.setSpacing(10);
                    mainPane.add(setBidding, 1, 2 * correspondingItem.getPagePosition() + 1);


                    positionCounter++;

                    //normal bid button initialization
                    buttons.get(i).setOnAction(event -> {
                        try {
                            AuctionItem item = itemButtons.get(buttons.get(correspondingItem.getPagePosition() - 1));
                            double bid = Double.parseDouble(bidAmount.getText());


                            if(isBidValid(bid, item)){
                                errorMessage.setText("");
                                item.setBid(bid);
                                //send to server
                                toServer.reset();
                                toServer.writeObject(item);
                                toServer.flush();
                            }else{
                                errorMessage.setText("");
                                errorMessage.setFill(Color.FIREBRICK);
                                errorMessage.setText("*Invalid Bid: Bid lower than current price");
                            }

                        } catch (IOException | NumberFormatException ex) {
                            errorMessage.setText("");
                            errorMessage.setFill(Color.FIREBRICK);
                            errorMessage.setText("**Invalid Bid: Invalid Number");

                        }


                    });

                    //buy now button initialization
                    buyNow.setOnAction(e -> {

                        try {
                            double buyNowPrice = correspondingItem.getBuyNowPrice();
                            correspondingItem.setBid(buyNowPrice);
                            //System.out.println(correspondingItem.getBid());
                            toServer.reset();
                            toServer.writeObject(correspondingItem);
                            toServer.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    });

                }
                history.setOnAction(e ->{
                    if(History.size() > 0){
//                        for(int j = 0; j < History.size(); j++){
//                            System.out.println(History.get(j));
//                        }
                        getHistory();
                    }else{
                        historyError.setFill(Color.FIREBRICK);
                        historyError.setText("No bids have occurred");
                    }

                });
            }
        }

    }

    //inner class used for handling object stream from server
    class inputHandler implements Runnable {
        ObjectOutputStream toServer;
        ObjectInputStream fromServer;
        GridPane mainPane;



        inputHandler(ObjectOutputStream toServer, ObjectInputStream fromServer, GridPane mainPane){
            this.toServer = toServer;
            this.fromServer = fromServer;
            this.mainPane = mainPane;
            soldItems = new ArrayList<Integer>();
        }
        public ArrayList<Integer> getSoldItems(){
            return soldItems;
        }

        @Override
        public void run() {

            AuctionItem input;
            int buttonPosition;
            try{
                //used to populate Auction
                for(int i = 0; i < buttons.size(); i++){
                    input = (AuctionItem) fromServer.readObject();
                    buttonPosition = input.getPagePosition() - 1;
                    itemButtons.put(buttons.get(buttonPosition), input);
                }

                while(true) {
                    //page position - 1 is equal to corresponding button
                    input = (AuctionItem) fromServer.readObject();
                    buttonPosition = input.getPagePosition() - 1;
                    itemButtons.put(buttons.get(buttonPosition), input);
                    History.add(itemButtons.get(buttons.get(buttonPosition)).getRecentHistory());
                    if(itemButtons.get(buttons.get(buttonPosition)).isSold()) {
                        soldItems.add(buttonPosition + 1);
                        AuctionItem itemSold = itemButtons.get(buttons.get(buttonPosition));
                        //System.out.println("Item bought by " + itemButtons.get(buttons.get(buttonPosition)).getBuyer());
                        Platform.runLater( () -> itemSold(mainPane, itemSold, soldItems));

                    }else{
                        AuctionItem itemSold = itemButtons.get(buttons.get(buttonPosition));
                        priceLabels.get(buttonPosition).setText("Current Bid: $" + itemButtons.get(buttons.get(buttonPosition)).getPrice());
                        //History.add(itemSold.getName() + " bid on by " + itemSold.getBuyer() + " for $" + itemSold.getPrice());
                    }

                }
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

        }


    }

    //Login page code
    public void displayLogin(Stage auctionStage, GridPane mainPane) {
        Stage primaryStage = new Stage();

        primaryStage.setTitle("EBuy Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(false);

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        Text scenetitle = new Text("Welcome to EBuy");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button btn = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Text onAction = new Text();
        grid.add(onAction, 1, 6);
        primaryStage.show();









        btn.setOnAction(e ->{

            if(userTextField.getText().isEmpty()){
                onAction.setFill(Color.FIREBRICK);
                onAction.setText("No username entered");

            }else{
                try {
                    String user = userTextField.getText();
                    toServer.writeObject(user);
                    toServer.flush();
                    System.out.println("Logging on...");
                    username = user;
                    updateGridPane(mainPane);

                    primaryStage.hide();
                    for(int i = 0; i < 5; i++){
                        if(itemButtons.get(buttons.get(i)).isSold()){
                            itemSold(mainPane, itemButtons.get(buttons.get(i)), soldItems);
                        }

                    }
                    auctionStage.show();

                }catch(IOException ex){

                }
            }



        });

    }

    //used for setting scene of history and generating data
    private void getHistory(){
        Stage primaryStage = new Stage();


        primaryStage.setTitle("Bidding History");
        ScrollPane grid = new ScrollPane();

//        grid.setAlignment(Pos.TOP_LEFT);
//        grid.setHgap(4);
//        grid.setVgap(4);
        grid.setPadding(new Insets(5, 25, 25, 25));
//        grid.setGridLinesVisible(true);
//        grid.getColumnConstraints().add(new ColumnConstraints(150));
        VBox history = new VBox();
        history.setSpacing(5);

        Text historyTitle = new Text("Bidding History");
        historyTitle.setFont(new Font("Arial", 20));

        history.getChildren().add(historyTitle);
        Scene scene = new Scene(grid, 300, 400);


        for(int i = 1; i <= History.size(); i++){
            Text newHistory = new Text(i + ": " + History.get(i - 1));
            newHistory.setFont(new Font("alegreya" , 12));
            //grid.getRowConstraints().add(new RowConstraints(20));
            //grid.add(newHistory, 0, i);
            history.getChildren().add(newHistory);
        }
        grid.setContent(history);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    //handles item being sold
    private void itemSold(GridPane mainPane, AuctionItem itemSold, ArrayList<Integer> soldItems){
        ObservableList<Node> paneNodes = mainPane.getChildren();
        //System.out.println(mainPane.getChildren().size());
        int indexOfNodes = calculateIndex(itemSold.getPagePosition(), paneNodes.size(), soldItems);
        paneNodes.remove(indexOfNodes, indexOfNodes + 3);
        Label sold = new Label("Sold to " + itemSold.getBuyer() + " for $" + itemSold.getPrice());
        GridPane.setHalignment(sold, HPos.LEFT);
        sold.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        mainPane.add(sold, 1, 2 * itemSold.getPagePosition(), 1, 2);
        //System.out.println(mainPane.getChildren().size());
        //History.add(itemSold.getName() + " bought by " + itemSold.getBuyer() + " for $" + itemSold.getPrice());
        itemsSold++;
    }
    //calculates index to delete nodes from gridpane
    private int calculateIndex(int pagePosition, int numChildren, ArrayList<Integer> soldItems){
        int itemsSoldAbove = 0;
        for(int i = 0; i < soldItems.size(); i++){
            if(soldItems.get(i) > pagePosition){
                itemsSoldAbove++;
            }
        }

        return 3*pagePosition - 3*(itemsSold - itemsSoldAbove) + 6;
    }



    //used for debugging button actions as well as some javafx functions
    private void newButton(GridPane pane){
        Stage testStage = new Stage();
        Pane newPane = new Pane();
        Scene scene = new Scene(newPane, 100, 100);
        testStage.setTitle("Test Button"); // Set the stage title
        testStage.setScene(scene); // Place the scene in the stage
        Button updateItem = new Button("Update Basketball");
        newPane.getChildren().add(updateItem);
        testStage.setScene(scene);
        testStage.show();
        Button history = new Button("History");
        Text historyError = new Text("");

        VBox historyBox = new VBox();
        historyBox.getChildren().addAll(history, historyError);
        GridPane.setValignment(historyBox, VPos.TOP);



        updateItem.setOnAction(event -> {
            //itemButtons.get(buttons.get(0)).sold("Test Buyer");
            System.out.println(pane.getChildren().size());

            if(pane.getChildren().size() % 2 == 0) {
                pane.add(historyBox, 1, 1);
            }else{
                //itemSold(pane);
            }

            System.out.println("Button Works");
        });
    }


}