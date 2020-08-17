package scheduler.widgets;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class QuarterHourTimeBlock extends Button {

    private final String IDLE_STYLE = "-fx-background-color: #EFF0F1; -fx-border-width: 1 0 0 1; -fx-border-color: #FFFFFF; fx-background-radius: 0;";
    private final String HOVER_STYLE = "-fx-background-color: #FFFFFF; -fx-border-width: 1 0 0 1;-fx-border-color: #FFFFFF; fx-background-radius: 0;";
    private final String SELECTED_STYLE = "-fx-background-color: #D192FF; -fx-border-width: 1 0 0 1; -fx-border-color: #FFFFFF; fx-background-radius: 0;";
    private final String DARK_IDLE_STYLE = "-fx-background-color: #e0e0e0; -fx-border-width: 1 0 0 1; -fx-border-color: #FFFFFF; fx-background-radius: 0;";

    private String idleStyle = IDLE_STYLE;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM HH:mm");

    public QuarterHourTimeBlock(LocalDateTime date, MeetingDetail meetingBuilder) {
        super();
        String formattedDateTime = date.format(formatter);
        String id = formattedDateTime;

        setPrefWidth(100);
        setMaxSize(Double.MAX_VALUE, 100);  //allow button to grow super wide               
        setMinHeight(10);

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getHour() < 6 || date.getHour() > 18) {
            idleStyle = DARK_IDLE_STYLE;
        }

        setStyle(idleStyle);

        setOnMouseEntered((MouseEvent e) -> {
            setStyle(HOVER_STYLE);
        });

        setOnMousePressed((MouseEvent e) -> {
            setStyle(SELECTED_STYLE);
        });

        setOnMouseExited((MouseEvent e) -> {
            setStyle(idleStyle);
        });

        setOnMouseReleased((MouseEvent e) -> {
            setStyle(idleStyle);
        });

        setOnAction((ActionEvent e) -> {
            meetingBuilder.createMeeting(date);
        });
    }
}
