package scheduler.scenes;


import com.sun.javafx.webkit.WebConsoleListener;
import java.util.StringJoiner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;


import scheduler.widgets.*;
//import com.google.maps.*;
import javafx.scene.web.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import jdk.nashorn.api.scripting.JSObject;
import scheduler.models.Controller;
import scheduler.models.User;


public class MapView extends BorderPane {

    Insets insets = new Insets(15);

    public MapView(User user) {
        super();

        VBox center = new VBox();
        final Button google = new Button("Map Metting Place");
        final Button mapCenter = new Button("Zoom to Center");
        final ComboBox comboBox = new ComboBox();
        String[] zipCodes1 = {"22153", "20147", "20120", "20878", "20011"};
        String[] zipCodes2 = {"22153", "20147", "20120"};
        String[] zipCodes3 = {"22611", "22724", "22554", "17257", "20011", "22203"};
        String[] zipCodes4 = {
            "16836", "17745", "18411", "10952", "10962", "07676", "16662",
            "22551", "20613", "21632", "08085", "18901", "17019", "17267"
        };
        comboBox.getItems().addAll(
                "Zip1",
                "Zip2",
                "Zip3",
                "Zip4"
                
        );
        
        Browser browser = new Browser();
        
        GridPane grid = new GridPane();
        grid.setHgap(9);
        grid.setVgap(1);
        
        grid.add(comboBox, 0, 0);
        grid.add(google, 1, 0);
        grid.add(mapCenter, 2 ,0);
        center.getChildren().add(grid);
        center.getChildren().add(browser);
        mapCenter.setOnAction((ActionEvent t) -> {
            browser.centerMap();
        });
        google.setOnAction((ActionEvent t) -> {
            String zipStr = "";
            switch (comboBox.getValue().toString()){
                case "Zip1":
                        zipStr = convertZip(zipCodes1);
                        break;
                case "Zip2":
                        zipStr = convertZip(zipCodes2);
                        break;
                case "Zip3":
                        zipStr = convertZip(zipCodes3);
                        break;
                case "Zip4":
                        zipStr = convertZip(zipCodes4);
                        break;
            }
            browser.executeGoogle(zipStr);
        });
        // Filler for use later

        // Adding general navigation
        
        VBox left = new VBox();
        left.getChildren().add(new NavigationButtons());

        DashboardView.setMargin(left, insets);
        DashboardView.setMargin(center, insets);

        this.setTop(new SceneHeader("Group Map", user));
        this.setCenter(center);
        this.setLeft(left);

    }
    public String convertZip(String[] zipCodes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < zipCodes.length; i ++){
            if (i > 9){
                return joiner.toString();
            }
            joiner.add(zipCodes[i]);
        }
        
        return joiner.toString();
    }

}
class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
    public void centerMap(){
        webEngine.executeScript("centerMap()"); 
    }
    
    public void executeGoogle(String zipStr){
        webEngine.executeScript("runGoogle('" + zipStr + "')");
        
        
    }
    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load(getClass().getResource("/scheduler/resources/map.html").toExternalForm());
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
            System.out.println(message + "[at " + lineNumber + "]");
        });
        
        //add the web view to the scene
        

        getChildren().add(browser);
        
        
        

    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 200;
    }

    @Override protected double computePrefHeight(double width) {
        return 1000;
    }
}
