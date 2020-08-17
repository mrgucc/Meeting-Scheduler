package scheduler.widgets;

import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import scheduler.models.Controller;

public class NavigationButtons extends VBox {

    Button dashBoardButton = new Button("Dashboard View");
    Button ViewMapButton = new Button("Map View");
    Button logoutButton = new Button("Log out");
    Region spacer = new Region();

    public NavigationButtons() {
        
        spacer.setPrefHeight(500);

        dashBoardButton.setPrefSize(110,25);
        dashBoardButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("dashboard");
        });

        ViewMapButton.setPrefSize(110,25);
        ViewMapButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("map");
        });

        logoutButton.setPrefSize(60,25);
        logoutButton.setStyle("-fx-base: #EB8B78;");
        logoutButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().logout();
        });
        VBox main = new VBox(5);
        main.setAlignment(Pos.CENTER);
        main.getChildren().addAll(
                dashBoardButton,
                ViewMapButton,
                spacer,
                logoutButton
        );

        this.getChildren().addAll(
                main
        );

    }
}
