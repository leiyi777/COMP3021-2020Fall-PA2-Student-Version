package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.AudioManager;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class ValidationPane extends BasePane{
    @NotNull
    private final VBox leftContainer = new BigVBox();
    @NotNull
    private final BigVBox centerContainer = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Label explanation = new Label("Upload and validation the game history.");
    @NotNull
    private final Button loadButton = new BigButton("Load file");
    @NotNull
    private final Button validationButton = new BigButton("Validate");
    @NotNull
    private final Button replayButton = new BigButton("Replay");
    @NotNull
    private final Button returnButton = new BigButton("Return");

    private Canvas gamePlayCanvas = new Canvas();

    /**
     * store the loaded information
     */
    private Configuration loadedConfiguration;
    private Integer[] storedScores;
    private FXJesonMor loadedGame;
    private Place loadedcentralPlace;
    private ArrayList<MoveRecord> loadedMoveRecords = new ArrayList<>();

    private BooleanProperty isValid = new SimpleBooleanProperty(false);

    String loadError;

    public ValidationPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        // TODO
        leftContainer.getChildren().addAll(title, explanation, loadButton, validationButton, replayButton, returnButton);
        centerContainer.getChildren().add(gamePlayCanvas);
        validationButton.setDisable(true);
        replayButton.setDisable(true);
        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
    }

    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * Add callbacks to each buttons.
     * Initially, replay button is disabled, gamePlayCanvas is empty
     * When validation passed, replay button is enabled.
     */
    @Override
    void setCallbacks() {
        //TODO
        loadButton.setOnMouseClicked(e->{
            if(loadFromFile())
                validationButton.setDisable(false);
        });
        validationButton.setOnMouseClicked(e->onClickValidationButton());
        replayButton.setOnMouseClicked(e->onClickReplayButton());
        returnButton.setOnMouseClicked(e-> returnToMainMenu());
    }

    /**
     * load From File and deserializer the game by two steps:
     *      - {@link ValidationPane#getTargetLoadFile}
     *      - {@link Deserializer}
     * Hint:
     *      - Get file from {@link ValidationPane#getTargetLoadFile}
     *      - Instantiate an instance of {@link Deserializer} using the file's path
     *      - Using {@link Deserializer#parseGame()}
     *      - Initialize {@link ValidationPane#loadedConfiguration}, {@link ValidationPane#loadedcentralPlace},
     *                   {@link ValidationPane#loadedGame}, {@link ValidationPane#loadedMoveRecords}
     *                   {@link ValidationPane#storedScores}
     * @return whether the file and information have been loaded successfully.
     */
    private boolean loadFromFile() {
        //TODO
        File loaded;
        Deserializer deserializer;
        loadError = null;
        try {
            loaded = getTargetLoadFile();
            deserializer = new Deserializer(loaded.toPath());
            deserializer.parseGame();
            loadedConfiguration = deserializer.getLoadedConfiguration();
            loadedConfiguration.setAllInitialPieces();
            loadedcentralPlace = deserializer.getLoadedConfiguration().getCentralPlace();
            loadedGame = new FXJesonMor(loadedConfiguration);
            loadedMoveRecords = deserializer.getMoveRecords();
            storedScores = deserializer.getStoredScores();
        } catch (Error e) {
            loadError = e.getMessage();
//            return false;
        } catch (Exception e) {
            loadError = e.getMessage();
//            return false;
        }

        return true;
    }

    /**
     * When click validation button, validate the loaded game configuration and move history
     * Hint:
     *      - if nothing loaded, call {@link ValidationPane#showErrorMsg}
     *      - if loaded, check loaded content by calling {@link ValidationPane#validateHistory}
     *      - When the loaded file has passed validation, the "replay" button is enabled.
     */
    private void onClickValidationButton(){
        //TODO
        if(loadError != null)
            showErrorConfiguration("Loading error: " + loadError);
        else if(loadedConfiguration == null || loadedMoveRecords == null ||
                loadedGame == null || loadedcentralPlace == null || storedScores == null)
            showErrorMsg();
        else {
            boolean valid = false;
            try {
                valid = validateHistory();
            } catch (Exception e){
                showErrorConfiguration(e.getMessage());
            }
            if (valid) {
                Alert confirm = new Alert(Alert.AlertType.INFORMATION);
                confirm.setTitle("Confirm");
                confirm.setHeaderText("Pass validation!");
                confirm.show();

                validationButton.setDisable(true);
                replayButton.setDisable(false);
            }
        }
    }

    /**
     * Display the history of recorded move.
     * Hint:
     *      - You can add a "next" button to render each move, or
     *      - Or you can refer to {@link Task} for implementation.
     */
    private void onClickReplayButton(){
        //TODO
        Player player1 = new ConsolePlayer(loadedConfiguration.getPlayers()[0].getName());
        Player player2 = new ConsolePlayer(loadedConfiguration.getPlayers()[1].getName());
        Player[] players = {player1, player2};
        loadedConfiguration = new Configuration(loadedConfiguration.getSize(), players, loadedConfiguration.getNumMovesProtection());
        loadedConfiguration.setAllInitialPieces();
        loadedGame = new FXJesonMor(loadedConfiguration);

        gamePlayCanvas.setWidth(loadedConfiguration.getSize() * ViewConfig.PIECE_SIZE);
        gamePlayCanvas.setHeight(loadedConfiguration.getSize() * ViewConfig.PIECE_SIZE);
//        loadedGame.refreshOutput();
        new Thread(()->{
            loadedGame.renderBoard(gamePlayCanvas);
            for(int i = 0; i < loadedMoveRecords.size(); i++){
                Player currentPlayer = loadedMoveRecords.get(i).getPlayer();
                Move currentMove = loadedMoveRecords.get(i).getMove();
                Piece currentPiece = loadedGame.getPiece(currentMove.getSource());

                loadedGame.movePiece(currentMove);
                loadedGame.updateScore(currentPlayer, currentPiece, currentMove);
                AudioManager.getInstance().playSound(AudioManager.SoundRes.PLACE);
                loadedGame.renderBoard(gamePlayCanvas);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                }
        }).start();
    }

    /**
     * Validate the {@link ValidationPane#loadedConfiguration}, {@link ValidationPane#loadedcentralPlace},
     *              {@link ValidationPane#loadedGame}, {@link ValidationPane#loadedMoveRecords}
     *              {@link ValidationPane#storedScores}
     * Hint:
     *      - validate configuration of game
     *      - whether each move is valid
     *      - whether scores are correct
     */
    private boolean validateHistory(){
        //TODO
        boolean configurationValid = true;
        for(int i = 0; i < loadedMoveRecords.size(); i++){
            Player currentPlayer = loadedMoveRecords.get(i).getPlayer();
            Move currentMove = loadedMoveRecords.get(i).getMove();
            Piece currentPiece = loadedGame.getPiece(currentMove.getSource());
            if(currentPiece.getPlayer().getName().equals(currentPlayer.getName())) {
                boolean moveValid = false;
                Move[] availableMoves = loadedGame.getAvailableMoves(currentPlayer);
                for(int j = 0; j < availableMoves.length; j++) {
                    if (availableMoves[j].equals(currentMove)) {
                        moveValid = true;
                        break;
                    }
                }
                if (moveValid){
                    loadedGame.movePiece(currentMove);
                    loadedGame.updateScore(currentPlayer, currentPiece, currentMove);
                } else{
                    configurationValid = false;
                    break;
                }
            } else{
                configurationValid = false;
                break;
            }
        }
        isValid.setValue(true);
        return configurationValid;
    }

    /**
     * Popup window show error message
     * Hint:
     *      - title: Invalid configuration or game process!
     *      - HeaderText: Due to following reason(s):
     *      - ContentText: errorMsg
     * @param errorMsg error message
     */
    private void showErrorConfiguration(String errorMsg){
        // TODO
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Invalid configuration or game process!");
        error.setHeaderText("Due to following reason(s):");
        error.setContentText(errorMsg);

        error.show();
    }

    /**
     * Pop up window to warn no record has been uploaded.
     * Hint:
     *      - title: Error!
     *      - ContentText: You haven't loaded a record, Please load first.
     */
    private void showErrorMsg(){
        //TODO
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error!");
        error.setHeaderText("You haven't loaded a record, Please load first.");

        error.showAndWait();
    }

    /**
     * Pop up window to show pass the validation
     * Hint:
     *     - title: Confirm
     *     - HeaderText: Pass validation!
     */
    private void passValidationWindow(){
        //TODO
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");
        confirm.setHeaderText("Pass validation!");

        confirm.showAndWait();
    }

    /**
     * Return to Main menu
     * Hint:
     *  - Before return, clear the rendered canvas, and clear stored information
     */
    private void returnToMainMenu(){
        // TODO
        loadedConfiguration = null;
        storedScores = null;
        loadedGame = null;
        loadedcentralPlace = null;
        for(int i = 0; i < loadedMoveRecords.size(); i++)
            loadedMoveRecords.remove(i);

        isValid = new SimpleBooleanProperty(false);

        gamePlayCanvas.getGraphicsContext2D().clearRect(0, 0, gamePlayCanvas.getWidth(), gamePlayCanvas.getHeight());
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }


    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        //TODO
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));

        return file;
    }

}
