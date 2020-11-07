package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.NumberTextField;
import castle.comp3021.assignment.protocol.Configuration;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GamePane extends BasePane {
    @NotNull
    private final VBox container = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Button playButton = new BigButton("Play");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button useDefaultButton = new BigButton("Use Default");
    @NotNull
    private final Button isHumanPlayer1Button = new BigButton("");
    @NotNull
    private final Button isHumanPlayer2Button = new BigButton("");

    @NotNull
    private final NumberTextField sizeFiled = new NumberTextField("");


    private final BorderPane sizeBox = new BorderPane(null, null, sizeFiled, null, new Label("Size of Board:"));

    @NotNull
    private final NumberTextField numMovesProtectionField = new NumberTextField("");

    @NotNull
    private final BorderPane numMovesProtectionBox = new BorderPane(null, null, numMovesProtectionField, null, new Label("Protection Moves:"));


    private FXJesonMor fxJesonMor = null;

    public GamePane() {
        fillValues();
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        //TODO
        container.getChildren().addAll(title, sizeBox, numMovesProtectionBox, isHumanPlayer1Button,
                isHumanPlayer2Button, useDefaultButton, playButton, returnButton);
        this.setCenter(container);
    }

    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * Set callbacks to buttons
     * Hint:
     * -  When fill in board size and step of protections, numbers need to validate
     * -  useDefaultButton: use default value for {@link GamePane#sizeFiled}, {@link GamePane#numMovesProtectionField}, and two players
     *    as they are saved in {@link SettingPane}
     * -  The current configuration (including {@link GamePane#sizeFiled}, {@link GamePane#numMovesProtectionField} and two players role)
     *    should not affect the default settings.
     * -  After clicking "play" button, the handler is implemented in {@link GamePane#startGame(FXJesonMor)},
     *    which links to {@link GamePlayPane} using the current configuration.
     */
    @Override
    void setCallbacks() {
        //TODO
        isHumanPlayer1Button.setOnMouseClicked(e->{
            String currentText = isHumanPlayer1Button.getText();
            if (currentText.equals("Player 1: Computer")) {
                isHumanPlayer1Button.setText("Player 1: Human");
            } else {
                isHumanPlayer1Button.setText("Player 1: Computer");
            }
        });

        isHumanPlayer2Button.setOnMouseClicked(e->{
            String currentText = isHumanPlayer2Button.getText();
            if (currentText.equals("Player 2: Computer")) {
                isHumanPlayer2Button.setText("Player 2: Human");
            } else {
                isHumanPlayer2Button.setText("Player 2: Computer");
            }
        });

        useDefaultButton.setOnMouseClicked(e-> fillValues());

        playButton.setOnMouseClicked(e-> {
            int size = sizeFiled.getValue();
            int numMovesProtection = numMovesProtectionField.getValue();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            Optional<String> errorMsg = validate(size, numMovesProtection);
            if(errorMsg == null) {
                globalConfiguration.setSize(size);
                globalConfiguration.setNumMovesProtection(numMovesProtection);
                globalConfiguration.setFirstPlayerHuman(isHumanPlayer1Button.getText().equals("Player 1: Human"));
                globalConfiguration.setSecondPlayerHuman(isHumanPlayer2Button.getText().equals("Player 2: Human"));
                Configuration newConfiguration = new Configuration(globalConfiguration.getSize(), globalConfiguration.getPlayers(), globalConfiguration.getNumMovesProtection());
                fxJesonMor = new FXJesonMor(newConfiguration);
                startGame(fxJesonMor);
            }
            else {
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Validation Failed");
                errorAlert.setContentText(errorMsg.get());
                errorAlert.show();
            }
        });

        returnButton.setOnMouseClicked(e->SceneManager.getInstance().showPane(MainMenuPane.class));
    }

    /**
     * Handler when clicking "play" button, using the current configuration to pass a {@link FXJesonMor} instance
     * Hint:
     *      - You may need to initialize and set up {@link GamePlayPane} by passing {@link FXJesonMor}
     * @param fxJesonMor an instance of {@link FXJesonMor}
     */
    void startGame(@NotNull FXJesonMor fxJesonMor) {
        final var gameplayPane = SceneManager.getInstance().<GamePlayPane>getPane(GamePlayPane.class);
        gameplayPane.initializeGame(fxJesonMor);
        SceneManager.getInstance().showPane(GamePlayPane.class);
    }

    /**
     * Fill in the default values for all editable fields.
     */
    void fillValues(){
        // TODO
        isHumanPlayer1Button.setText(globalConfiguration.isFirstPlayerHuman() ? "Player 1: Human" : "Player 1: Computer");
        isHumanPlayer2Button.setText(globalConfiguration.isSecondPlayerHuman() ? "Player 2: Human" : "Player 2: Computer");

        sizeFiled.setText(String.valueOf(globalConfiguration.getSize()));
        numMovesProtectionField.setText(String.valueOf(globalConfiguration.getNumMovesProtection()));
    }

    /**
     * Validate the text fields
     * The useful msgs are predefined in {@link ViewConfig#MSG_BAD_SIZE_NUM}, etc.
     * @param size number in {@link GamePane#sizeFiled}
     * @param numProtection number in {@link GamePane#numMovesProtectionField}
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     *      * otherwise.
     */
    public static Optional<String> validate(int size, int numProtection) {
        //TODO
        if(size < 3)
            return Optional.of(ViewConfig.MSG_BAD_SIZE_NUM);
        else if(size % 2 != 1)
            return Optional.of(ViewConfig.MSG_ODD_SIZE_NUM);
        else if(size > 26)
            return Optional.of(ViewConfig.MSG_UPPERBOUND_SIZE_NUM);
        else if(numProtection < 0)
            return Optional.of(ViewConfig.MSG_NEG_PROT);
        else
            return null;
    }
}
