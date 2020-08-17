package scheduler.widgets;

import java.time.LocalDateTime;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import scheduler.models.Meeting;
import scheduler.utilities.Constants;

public class MeetingBlock extends Button {

    public MeetingBlock(Meeting meeting, MeetingDetail pane) {
        super(meeting.getButtonDisplay());
        setStyle("-fx-base: " + Constants.MAIN_THEME_COLOR);
        setAlignment(Pos.TOP_LEFT);

        this.setOnAction(e -> {
            pane.displayMeeting(meeting);
        });
    }

}
