package castle.comp3021.assignment.gui.views;

import castle.comp3021.assignment.gui.DurationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.StyleConverter;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Displays info about the current level being played by the user.
 */
public class GameplayInfoPane extends BigVBox {
    private final Label score1Label = new Label();
    private final Label score2Label = new Label();
    private final Label curPlayerLabel = new Label();
    private final Label timerLabel = new Label();

    /**
     * Construct function, you need to bind given properties to the corresponding components
     * @param score1Property score of players1
     * @param score2Property score of player2
     * @param curPlayer current player name
     * @param ticksElapsed time passed in seconds
     */
    public GameplayInfoPane(IntegerProperty score1Property, IntegerProperty score2Property, StringProperty curPlayer,
                            IntegerProperty ticksElapsed) {
        //TODO
        bindTo(score1Property, score2Property, curPlayer, ticksElapsed);
        getChildren().addAll(score1Label, score2Label, timerLabel, curPlayerLabel);
    }

    /**
     * @param s Seconds duration
     * @return A string that formats the duration stopwatch style
     */
    public static String formatTime(int s) {
        final var d = Duration.of(s, SECONDS);

        int seconds = d.toSecondsPart();
        int minutes = d.toMinutesPart();

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * @param s Seconds duration
     * @return A string that formats the duration stopwatch style
     */
    public static String countdownFormat(int s) {
        final var d = Duration.of(DurationTimer.getDefaultEachRound() - s, SECONDS);

        int seconds = d.toSecondsPart();
        int minutes = d.toMinutesPart();

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Binds all properties to their respective UI elements.
     * Hint:
     *     - You may find it useful to synchronize javafx UI-thread using {@link javafx.application.Platform#runLater}
     *
     * @param score1Property Score of Player 1
     * @param score2Property Score of Player 2
     * @param ticksElapsed Timer Property, count down
     * @param curPlayer current player name
     */
    private void bindTo(IntegerProperty score1Property, IntegerProperty score2Property, StringProperty curPlayer,
                        IntegerProperty ticksElapsed) {
        // TODO
        score1Label.textProperty().bindBidirectional(score1Property, new NumberStringConverter("Score of player 1: "));
        score2Label.textProperty().bindBidirectional(score2Property, new NumberStringConverter("Score of player 2: "));
        timerLabel.textProperty().bindBidirectional(ticksElapsed, new NumberStringConverter("Time: "));
        curPlayerLabel.textProperty().bind(Bindings.concat("Current player: ").concat(curPlayer));
    }
}
