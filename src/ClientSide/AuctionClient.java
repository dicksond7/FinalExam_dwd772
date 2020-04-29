package ClientSide;
/*
 * Author: Vallath Nandakumar and EE 422C instructors
 * Date: April 20, 2020
 * This starter code is from the MultiThreadChat example from the lecture, and is on Canvas. 
 * It doesn't compile.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class AuctionClient extends Application {
	// I/O streams 
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	private boolean hasLoggedOn = false;
	private ArrayList<AuctionItem> auctionItems;


	@Override
	public void start(Stage primaryStage) {

		try {
			// Create a socket to connect to the server
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new ObjectInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException ex) {

		}




			GridPane mainPane = new GridPane();

			// Create a scene and place it in the stage
			Scene scene = new Scene(mainPane, 450, 580);
			primaryStage.setTitle("Client"); // Set the stage title
			primaryStage.setScene(scene); // Place the scene in the stage
			mainPane.setPadding(new Insets(10, 5, 10, 15));
			mainPane.setGridLinesVisible(true);
			primaryStage.show(); // Display the stage

//			Button placeBid = new Button("Place Bid");
//			Label bitLabel = new Label("Enter Bid:");
//			TextField bidAmount = new TextField();
//			HBox setBidding = new HBox();
//			setBidding.getChildren().addAll(bitLabel, bidAmount, placeBid);
//			setBidding.setSpacing(10);

		int numItemsPerPage = 5;
		mainPane.getColumnConstraints().add(new ColumnConstraints(100));
		mainPane.getRowConstraints().add(new RowConstraints(30));
		Label welcomeLabel = new Label("Welcome to EBuy");
		welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 21));
		mainPane.add(welcomeLabel, 0, 0, 2, 1);
		for(int i = 0; i < numItemsPerPage; i++){
			mainPane.getRowConstraints().add(new RowConstraints(420/(numItemsPerPage)));
		}
		mainPane.setHgap(10);
		mainPane.setVgap(15);
		mainPane.getColumnConstraints().add(new ColumnConstraints(300));
		for(int i = 1; i < numItemsPerPage + 1; i++){
			Button placeBid = new Button("Place Bid");
			Label bitLabel = new Label("Enter Bid:");
			TextField bidAmount = new TextField();
			HBox setBidding = new HBox();
			setBidding.getChildren().addAll(bitLabel, bidAmount, placeBid);
			setBidding.setSpacing(10);
			mainPane.add(setBidding, 1, i);
		}



			/*
			placeBid.setOnAction(e -> {

			});  // etc.
			*/


	}
	
	public static void main(String[] args) {
		launch(args);
	}


}
