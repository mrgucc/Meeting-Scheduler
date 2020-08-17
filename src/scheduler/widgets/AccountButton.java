package scheduler.widgets;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import scheduler.models.Controller;
import scheduler.models.User;

public class AccountButton extends Button {

    public AccountButton(User user) {
        //Set up the account button and image next to it        
        double btnSize = 45.0;
        setShape(new Circle(btnSize));
        setPrefSize(btnSize, btnSize);
        Image image = new Image("/scheduler/resources/icon_account.png", 30, 30, true, true);
        ImageView icon = new ImageView(image);
        setGraphic(icon);
        setFocusTraversable(false);
        setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("account");
        });
    }
}
