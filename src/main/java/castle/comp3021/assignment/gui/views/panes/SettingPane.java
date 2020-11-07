package castle.comp3021.assignment.gui.views.panes;
import castle.comp3021.assignment.gui.DurationTimer;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.AudioManager;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.NumberTextField;
import castle.comp3021.assignment.gui.views.SideMenuVBox;
import castle.comp3021.assignment.protocol.Configuration;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.jetbrains.annotations.NotNull;


import java.util.Optional;

public class SettingPane extends BasePane {
    @NotNull
    private final Label title = new Label("Jeson Mor <Game Setting>");
    @NotNull
    private final Button saveButton = new BigButton("Save");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button isHumanPlayer1Button = new BigButton("Player 1: ");
    @NotNull
    private final Button isHumanPlayer2Button = new BigButton("Player 2: ");
    @NotNull
    private final Button toggleSoundButton = new BigButton("Sound FX: Enabled");

    @NotNull
    private final VBox leftContainer = new SideMenuVBox();

    @NotNull
    private final NumberTextField sizeFiled = new NumberTextField(String.valueOf(globalConfiguration.getSize()));

    @NotNull
    private final BorderPane sizeBox = new BorderPane(null, null, sizeFiled, null, new Label("Board size"));

    @NotNull
    private final NumberTextField durationField = new NumberTextField(String.valueOf(DurationTimer.getDefaultEachRound()));
    @NotNull
    private final BorderPane durationBox = new BorderPane(null, null, durationField, null,
            new Label("Max Duration (s)"));

    @NotNull
    private final NumberTextField numMovesProtectionField =
            new NumberTextField(String.valueOf(globalConfiguration.getNumMovesProtection()));
    @NotNull
    private final BorderPane numMovesProtectionBox = new BorderPane(null, null,
            numMovesProtectionField, null, new Label("Steps of protection"));

    @NotNull
    private final VBox centerContainer = new BigVBox();
    @NotNull
    private final TextArea infoText = new TextArea(ViewConfig.getAboutText());


    public SettingPane() {
        fillValues();
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * Add components to corresponding containers
     */
    @Override
    void connectComponents() {
        //TODO
        leftContainer.getChildren().addAll(title, sizeBox, numMovesProtectionBox, durationBox,
                isHumanPlayer1Button, isHumanPlayer2Button, toggleSoundButton, saveButton, returnButton);
        centerContainer.getChildren().add(infoText);
        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
    }

    @Override
    void styleComponents() {
        infoText.getStyleClass().add("text-area");
        infoText.setEditable(false);
        infoText.setWrapText(true);
        infoText.setPrefHeight(ViewConfig.HEIGHT);
    }

    /**
     * Add handlers to buttons, textFields.
     * Hint:
     *  - Text of {@link SettingPane#isHumanPlayer1Button}, {@link SettingPane#isHumanPlayer2Button},
     *            {@link SettingPane#toggleSoundButton} should be changed accordingly
     *  - You may use:
     *      - {@link Configuration#isFirstPlayerHuman()},
     *      - {@link Configuration#isSecondPlayerHuman()},
     *      - {@link Configuration#setFirstPlayerHuman(boolean)}
     *      - {@link Configuration#isSecondPlayerHuman()},
     *      - {@link AudioManager#setEnabled(boolean)},
     *      - {@link AudioManager#isEnabled()},
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

        toggleSoundButton.setOnMouseClicked(e->{
            String currentText = toggleSoundButton.getText();
            if (currentText.equals("Sound FX: Enabled")) {
                toggleSoundButton.setText("Sound FX: Disabled");
            } else {
                toggleSoundButton.setText("Sound FX: Enabled");
            }
        });

        saveButton.setOnMouseClicked(e->{
            int size = sizeFiled.getValue();
            int numMovesProtection = numMovesProtectionField.getValue();
            int duration = durationField.getValue();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            Optional<String> errorMsg = validate(size, numMovesProtection, duration);
            if(errorMsg == null)
                returnToMainMenu(true);
            else {
                errorAlert.setTitle("Error!");
                errorAlert.setHeaderText("Validation Failed");
                errorAlert.setContentText(errorMsg.get());
                errorAlert.show();
            }
        });

        returnButton.setOnMouseClicked(e-> SceneManager.getInstance().showPane(MainMenuPane.class));
    }

    /**
     * Fill in the default values for all editable fields.
     */
    private void fillValues() {
        // TODO
        String defaultPlayer1 = globalConfiguration.isFirstPlayerHuman() ? "Player 1: Human" : "Player 1: Computer";
        String defaultPlayer2 = globalConfiguration.isSecondPlayerHuman() ? "Player 2: Human" : "Player 2: Computer";
        String defaultSound = AudioManager.getInstance().isEnabled() ? "Sound FX: Enabled" : "Sound FX: Disabled";

        isHumanPlayer1Button.setText(defaultPlayer1);
        isHumanPlayer2Button.setText(defaultPlayer2);
        toggleSoundButton.setText(defaultSound);

        sizeFiled.setText(String.valueOf(globalConfiguration.getSize()));
        numMovesProtectionField.setText(String.valueOf(globalConfiguration.getNumMovesProtection()));
    }

    /**
     * Switches back to the {@link MainMenuPane}.
     *
     * @param writeBack Whether to save the values present in the text fields to their respective classes.
     */
    private void returnToMainMenu(final boolean writeBack) {
        //TODO
        if(writeBack){
            String settingPlayer1 = isHumanPlayer1Button.getText();
            boolean isPlayer1Human = settingPlayer1.equals("Player 1: Human");
            globalConfiguration.setFirstPlayerHuman(isPlayer1Human);

            String settingPlayer2 = isHumanPlayer2Button.getText();
            boolean isPlayer2Human = settingPlayer2.equals("Player 2: Human");
            globalConfiguration.setSecondPlayerHuman(isPlayer2Human);

            String settingAudio = toggleSoundButton.getText();
            boolean audioEnabled = settingAudio.equals("Sound FX: Enabled");
            AudioManager.getInstance().setEnabled(audioEnabled);

            globalConfiguration.setSize(sizeFiled.getValue());
            globalConfiguration.setNumMovesProtection(numMovesProtectionField.getValue());
            DurationTimer.setDefaultEachRound(durationField.getValue());

            SceneManager.getInstance().showPane(MainMenuPane.class);
        }
    }

    /**
     * Validate the text fields
     * The useful msgs are predefined in {@link ViewConfig#MSG_BAD_SIZE_NUM}, etc.
     * @param size number in {@link SettingPane#sizeFiled}
     * @param numProtection number in {@link SettingPane#numMovesProtectionField}
     * @param duration number in {@link SettingPane#durationField}
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     *      * otherwise.
     */
    public static Optional<String> validate(int size, int numProtection, int duration) {
        //TODO
        if(size < 3)
            return Optional.of(ViewConfig.MSG_BAD_SIZE_NUM);
        else if(size % 2 != 1)
            return Optional.of(ViewConfig.MSG_ODD_SIZE_NUM);
        else if(size > 26)
            return Optional.of(ViewConfig.MSG_UPPERBOUND_SIZE_NUM);
        else if(numProtection < 0)
            return Optional.of(ViewConfig.MSG_NEG_PROT);
        else if(duration < 0)
            return Optional.of(ViewConfig.MSG_NEG_DURATION);
        else
            return null;
    }
}
