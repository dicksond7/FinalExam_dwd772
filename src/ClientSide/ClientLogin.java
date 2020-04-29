package ClientSide;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Hashtable;

public class ClientLogin{

    private boolean hasLoggedOn;

    ClientLogin(){
        this.hasLoggedOn = false;
    }

    public void displayLogin() {
        Stage primaryStage = new Stage();

        primaryStage.setTitle("EBuy Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

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
                String user = userTextField.getText();

                hasLoggedOn = true;
            }



        });

    }

    public boolean hasLoggedOn(){
        return hasLoggedOn;
    }


}
