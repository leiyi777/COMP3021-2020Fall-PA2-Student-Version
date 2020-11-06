package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.GUIMain;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;

public class MainMenuPane extends BasePane {
    @NotNull
    private final VBox container = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Button playButton = new BigButton("Play Game");

    @NotNull
    private final Button settingsButton = new BigButton("Settings / About ");
    @NotNull
    private final Button validationButton = new BigButton("Validation");
    @NotNull
    private final Button quitButton = new BigButton("Quit");

    public MainMenuPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        // TODO
        container.getChildren().addAll(title, playButton, settingsButton, validationButton, quitButton);
        this.setCenter(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * add callbacks to buttons
     * Hint:
     *      - playButton -> {@link GamePane}
     *      - settingsButton -> {@link SettingPane}
     *      - validationButton -> {@link ValidationPane}
     *      - quitButton -> quit the game
     */
    @Override
    void setCallbacks() {
        //TODO
        playButton.setOnMouseClicked(e->{
            SceneManager.getInstance().showPane(GamePane.class);
            ((GamePane)SceneManager.getInstance().getPane(GamePane.class)).fillValues();
        });
        settingsButton.setOnMouseClicked(e->SceneManager.getInstance().showPane(SettingPane.class));
        validationButton.setOnMouseClicked(e->SceneManager.getInstance().showPane(ValidationPane.class));
        quitButton.setOnMouseClicked(e->{
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });
    }

}
