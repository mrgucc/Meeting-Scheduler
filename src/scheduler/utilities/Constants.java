package scheduler.utilities;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/* Stores the application constants for theming 
 * and keeping styles consistent
 */
public class Constants {

    public static final String MAIN_THEME_COLOR = "#D192FF";
    public static final int CREDENTIAL_FIELD_WIDTH = 200;
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 500;
    public static final Font SCENE_TITLE_FONT = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25);
    public static final Font SUB_TITLE_FONT = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15);
    public static final Color TEXT_ERROR_COLOR = Color.RED;
    public static final String PANEL_STYLE = "-fx-padding: 10; -fx-border-width: 2; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: grey;";

}
