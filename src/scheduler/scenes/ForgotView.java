package scheduler.scenes;

import scheduler.utilities.Constants;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import scheduler.models.Controller;

public class ForgotView extends VBox {

    Button goLoginButton = new Button("Return to Login Screen");
    
    public ForgotView() {
        super(5);

        //Back to login button
        goLoginButton.setPrefWidth(200);
        goLoginButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("login");
        });

        //Set the VBox
        this.setFillWidth(false);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(
                new Text("Please contact Super Scheduler support to change your password.\n"),
                goLoginButton
        );
    }

}
