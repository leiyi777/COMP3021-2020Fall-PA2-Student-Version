package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.DurationTimer;
import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.ResourceLoader;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.GameplayInfoPane;
import castle.comp3021.assignment.gui.views.SideMenuVBox;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.player.RandomPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.gui.controllers.Renderer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.TimerTask;

/**
 * This class implements the main playing function of Jeson Mor
 * The necessary components have been already defined (e.g., topBar, title, buttons).
 * Basic functions:
 *      - Start game and play, update scores
 *      - Restart the game
 *      - Return to main menu
 *      - Elapsed Timer (ticking from 00:00 -> 00:01 -> 00:02 -> ...)
 *          - The format is defined in {@link GameplayInfoPane#formatTime(int)}
 * Requirement:
 *      - The game should be initialized by configuration passed from {@link GamePane}, instead of the default configuration
 *      - The information of the game (including scores, current player name, ect.) is implemented in {@link GameplayInfoPane}
 *      - The center canvas (defined as gamePlayCanvas) should be disabled when current player is computer
 * Bonus:
 *      - A countdown timer (if this is implemented, then elapsed timer can be either kept or removed)
 *      - The format of countdown timer is defined in {@link GameplayInfoPane#countdownFormat(int)}
 *      - If one player runs out of time of each round {@link DurationTimer#getDefaultEachRound()}, then the player loses the game.
 * Hint:
 *      - You may find it useful to synchronize javafx UI-thread using {@link javafx.application.Platform#runLater}
 */ 

public class GamePlayPane extends BasePane {
    @NotNull
    private final HBox topBar = new HBox(20);
    @NotNull
    private final SideMenuVBox leftContainer = new SideMenuVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Text parameterText = new Text();
    @NotNull
    private final BigButton returnButton = new BigButton("Return");
    @NotNull
    private final BigButton startButton = new BigButton("Start");
    @NotNull
    private final BigButton restartButton = new BigButton("Restart");
    @NotNull
    private final BigVBox centerContainer = new BigVBox();
    @NotNull
    private final Label historyLabel = new Label("History");

    @NotNull
    private final Text historyFiled = new Text();
    @NotNull
    private final ScrollPane scrollPane = new ScrollPane();

    /**
     * time passed in seconds
     * Hint:
     *      - Bind it to time passed in {@link GameplayInfoPane}
     */
    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();

    @NotNull
    private final Canvas gamePlayCanvas = new Canvas();

    private GameplayInfoPane infoPane = null;

    /**
     * You can add more necessary variable here.
     * Hint:
     *      - the passed in {@link FXJesonMor}
     *      - other global variable you want to note down.
     */
    // TODO
    FXJesonMor fxJesonMor;
    int remainingTime;
    int numMoves;
    Player lastPlayer;
    Player winner;
    Player currentPlayer;
    Piece lastPiece;
    Move lastMove;
    Thread currentThread;
    boolean isStopped;
    boolean scoreNeedUpdate;

    public GamePlayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * Components are added, adjust it by your own choice
     */
    @Override
    void connectComponents() {
        //TODO
        topBar.getChildren().add(title);
        topBar.setAlignment(Pos.CENTER);
        leftContainer.getChildren().addAll(parameterText, historyLabel,
                scrollPane, startButton, restartButton, returnButton);
        centerContainer.getChildren().addAll(gamePlayCanvas);
        this.setTop(topBar);
        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
    }

    /**
     * style of title and scrollPane have been set up, no need to add more
     */
    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(ViewConfig.WIDTH / 4.0, ViewConfig.HEIGHT / 3.0 );
        scrollPane.setContent(historyFiled);
    }

    /**
     * The listeners are added here.
     */
    @Override
    void setCallbacks() {
        //TODO
        startButton.setOnMouseClicked(e->{
            startButton.setDisable(true);
            restartButton.setDisable(false);
            startGame();
        });

        restartButton.setOnMouseClicked(e->{
            onRestartButtonClick();
            startButton.setDisable(false);
            restartButton.setDisable(true);
        });

        returnButton.setOnMouseClicked(e->doQuitToMenuAction());
    }

    /**
     * Set up necessary initialization.
     * Hint:
     *      - Set buttons enable/disable
     *          - Start button: enable
     *          - restart button: disable
     *      - This function can be invoked before {@link GamePlayPane#startGame()} for setting up
     *
     * @param fxJesonMor pass in an instance of {@link FXJesonMor}
     */
    void initializeGame(@NotNull FXJesonMor fxJesonMor) {
        //TODO
        this.fxJesonMor = fxJesonMor;
        remainingTime = DurationTimer.getDefaultEachRound();
        isStopped = true;
        ticksElapsed.setValue(remainingTime);

        if(infoPane != null)
            centerContainer.getChildren().remove(infoPane);
        infoPane = new GameplayInfoPane(fxJesonMor.getPlayer1Score(), fxJesonMor.getPlayer2Score(), fxJesonMor.getCurPlayerName(), ticksElapsed);
        centerContainer.getChildren().add(infoPane);

        gamePlayCanvas.setWidth((ViewConfig.PIECE_SIZE * fxJesonMor.getConfiguration().getSize()));
        gamePlayCanvas.setHeight(ViewConfig.PIECE_SIZE * fxJesonMor.getConfiguration().getSize());
        endGame();
        enableCanvas();
    }

    /**
     * enable canvas clickable
     */
    private void enableCanvas(){
        gamePlayCanvas.setDisable(false);
    }

    /**
     * disable canvas clickable
     */
    private void disableCanvas(){
        gamePlayCanvas.setDisable(true);
    }

    /**
     * After click "start" button, everything will start from here
     * No explicit skeleton is given here.
     * Hint:
     *      - Give a carefully thought to how to activate next round of play
     *      - When a new {@link Move} is acquired, it needs to be check whether this move is valid.
     *          - If is valid, make the move, render the {@link GamePlayPane#gamePlayCanvas}
     *          - If is invalid, abort the move
     *          - Update score, add the move to {@link GamePlayPane#historyFiled}, also record the move
     *          - Move forward to next player
     *      - The player can be either computer or human, when the computer is playing, disable {@link GamePlayPane#gamePlayCanvas}
     *      - You can add a button to enable next move once current move finishes.
     *          - or you can add handler when mouse is released
     *          - or you can take advantage of timer to automatically change player. (Bonus)
     */
    public void startGame() {
        //TODO
        Configuration configuration = fxJesonMor.getConfiguration();
        numMoves = 0;
        lastPlayer = null;
        winner = null;
        currentPlayer = fxJesonMor.getCurrentPlayer();
        lastPiece = null;
        lastMove = null;
        isStopped = false;

        scoreNeedUpdate = false;

        this.fxJesonMor.addOnTickHandler(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ticksElapsed.setValue(remainingTime);
                    remainingTime--;
                    if(remainingTime < 0) {
                        isStopped = true;
                        endGame();
                        createWinPopup("");
                    }
                });
            }
        });

        currentThread = new Thread(new Runnable() {
            public void run() {
                while (!isStopped) {
                    var player = configuration.getPlayers()[numMoves % configuration.getPlayers().length];
                    // let player make next move
                    currentPlayer = player;
                    var availableMoves = fxJesonMor.getAvailableMoves(player);

                    // there shouldn't be no available moves, if no available moves, the player with lower score wins
                    if (availableMoves.length <= 0) {
                        if (configuration.getPlayers()[0].getScore() < configuration.getPlayers()[1].getScore())
                            winner = configuration.getPlayers()[0];
                        else if (configuration.getPlayers()[0].getScore() > configuration.getPlayers()[1].getScore())
                            winner = configuration.getPlayers()[1];
                        else
                            winner = player;
                    } else {
                        fxJesonMor.startCountdown();

                        var move = player.nextMove(fxJesonMor, availableMoves);
                        var movedPiece = fxJesonMor.getPiece(move.getSource());
                        try {
                            if (!(currentPlayer instanceof ConsolePlayer))
                                Thread.sleep(2500);
                            else
                                Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    isStopped = true;
                                    endGame();
                                }
                            });
                        }

                        // check if there is a winner and if there is, return the winner.
                        // if there is no winner yet, continue the loop with label "round"

                                if(!isStopped) {
                                    // make move
                                    fxJesonMor.stopCountdown();

                                    lastPlayer = player;
                                    lastPiece = movedPiece;
                                    lastMove = move;

                                    fxJesonMor.movePiece(lastMove);
                                    numMoves++;
//                                    fxJesonMor.updateScore(lastPlayer, lastPiece, lastMove);
                                    scoreNeedUpdate = true;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Platform.runLater(()->{
//                    while(!isStopped)
                                                if(scoreNeedUpdate && lastMove != null && lastPiece != null && lastPlayer != null) {
                                                    scoreNeedUpdate = false;
                                                    fxJesonMor.updateScore(lastPlayer, lastPiece, lastMove);
                                                    System.out.println("HAHAHAA");
                                                }
                                            });
                                        }
                                    }).start();
                                    remainingTime = DurationTimer.getDefaultEachRound();
                                    fxJesonMor.renderBoard(gamePlayCanvas);
                                    updateHistoryField(move);
                                    checkWinner();
                                }

                    }
                    if (winner != null) {
                        isStopped = true;
                        String winnerName = winner.getName();
                        winner = null;
                        endGame();
                        createWinPopup(winnerName);
                    }
                }
            }
        });
        currentThread.start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Platform.runLater(()->{
////                    while(!isStopped)
//                        if(scoreNeedUpdate && lastMove != null && lastPiece != null && lastPlayer != null) {
//                            scoreNeedUpdate = false;
//                            fxJesonMor.updateScore(lastPlayer, lastPiece, lastMove);
//                            System.out.println("HAHAHAA");
//                        }
//                });
//            }
//        }).start();
    }

    /**
     * Restart the game
     * Hint: end the current game and start a new game
     */
    private void onRestartButtonClick(){
        //TODO
        isStopped = true;

        endGame();
    }

    /**
     * Add mouse pressed handler here.
     * Play click.mp3
     * draw a rectangle at clicked board tile to show which tile is selected
     * Hint:
     *      - Highlight the selected board cell using {@link Renderer#drawRectangle(GraphicsContext, double, double)}
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     * @param event mouse click
     */
    private void onCanvasPressed(MouseEvent event){
        // TODO
    }

    /**
     * When mouse dragging, draw a path
     * Hint:
     *      - When mouse dragging, you can use {@link Renderer#drawOval(GraphicsContext, double, double)} to show the path
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     * @param event mouse position
     */
    private void onCanvasDragged(MouseEvent event){
        //TODO
    }

    /**
     * Mouse release handler
     * Hint:
     *      - When mouse released, a {@link Move} is completed, you can either validate and make the move here, or somewhere else.
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     *      - If the piece has been successfully moved, play place.mp3 here (or somewhere else)
     * @param event mouse release
     */
    private void onCanvasReleased(MouseEvent event){
        // TODO
    }

    /**
     * Creates a popup which tells the winner
     */
    private void createWinPopup(String winnerName){
        //TODO
        Alert winAlert = new Alert(Alert.AlertType.CONFIRMATION);

        ButtonType startNewGame = new ButtonType("Start New game");
        ButtonType exportMoveRecords = new ButtonType("Export Move Records");
        ButtonType returnToMainMenu = new ButtonType("Return to Main Menu");

        if(winnerName != "") {
            winAlert.setTitle("Congratulations!");
            winAlert.setContentText(winnerName + " wins!");
        }
        else{
            winAlert.setTitle("Sorry! Time's out!");
            winAlert.setContentText(currentPlayer.getName() + " Lose!");
        }

        winAlert.getDialogPane().getButtonTypes().removeAll();
        winAlert.getButtonTypes().setAll(startNewGame, exportMoveRecords, returnToMainMenu);
        winAlert.setOnCloseRequest(e->{});

        winAlert.showAndWait();

        ButtonType result = winAlert.getResult();

        if (result.equals(startNewGame))
            onRestartButtonClick();
        else if(result.equals(exportMoveRecords)){
            //
        }
        else
            doQuitToMenuAction();

    }


    /**
     * check winner, if winner comes out, then play the win.mp3 and popup window.
     * The window has three options:
     *      - Start New Game: the same function as clicking "restart" button
     *      - Export Move Records: Using {@link castle.comp3021.assignment.protocol.io.Serializer} to write game's configuration to file
     *      - Return to Main menu, using {@link GamePlayPane#doQuitToMenuAction()}
     */
    private void checkWinner(){
        //TODO
        winner = null;

        // no winner within numMovesProtection moves
        if (numMoves <= fxJesonMor.getConfiguration().getNumMovesProtection()) {
            return;
        }

        // first way to win: a piece leaves the central square, the piece should not be an Archer
        if ((lastPiece instanceof Knight) && lastMove.getSource().equals(fxJesonMor.getConfiguration().getCentralPlace())
                && !lastMove.getDestination().equals(fxJesonMor.getConfiguration().getCentralPlace())) {
            winner = lastPlayer;
        } else {
            // second way to win: one player captures all the pieces of other players
            Player remainingPlayer = null;
            for (int i = 0; i < fxJesonMor.getConfiguration().getSize(); i++) {
                for (int j = 0; j < fxJesonMor.getConfiguration().getSize(); j++) {
                    var piece = fxJesonMor.getPiece(i, j);
                    if (piece == null) {
                        continue;
                    }
                    if (remainingPlayer == null) {
                        remainingPlayer = piece.getPlayer();
                    } else if (remainingPlayer != piece.getPlayer()) {
                        // there are still two players having pieces on board
                        return;
                    }
                }
            }
            // if the previous for loop terminates, then there must be 1 player on board (it cannot be null).
            // then winner appears
            winner = remainingPlayer;
        }
    }

    /**
     * Popup a window showing invalid move information
     * @param errorMsg error string stating why this move is invalid
     */
    private void showInvalidMoveMsg(String errorMsg){
        //TODO
        Alert invalidMoveAlert = new Alert(Alert.AlertType.ERROR);
        invalidMoveAlert.setTitle("Invalid Move");
        invalidMoveAlert.setHeaderText("Your movement is invalid due to following reason(s):");
        invalidMoveAlert.setContentText(errorMsg);
        invalidMoveAlert.show();
    }

    /**
     * Before actually quit to main menu, popup a alert window to double check
     * Hint:
     *      - title: Confirm
     *      - HeaderText: Return to menu?
     *      - ContentText: Game progress will be lost.
     *      - Buttons: CANCEL and OK
     *  If click OK, then refer to {@link GamePlayPane#doQuitToMenu()}
     *  If click Cancel, than do nothing.
     */
    private void doQuitToMenuAction() {
        // TODO

        Alert returnAlert = new Alert(Alert.AlertType.CONFIRMATION);
        returnAlert.setTitle("Confirm");
        returnAlert.setHeaderText("Return to menu?");
        returnAlert.setContentText("Game progress will be lost");
        returnAlert.showAndWait();
        if(returnAlert.getResult().equals(returnAlert.getButtonTypes().get(0))) {
            isStopped = true;
            doQuitToMenu();
        }
    }

    /**
     * Update the move to the historyFiled
     * @param move the last move that has been made
     */
    private void updateHistoryField(Move move){
        //TODO
        String prevText = historyFiled.getText();
        String newText = prevText +
                "[" + move.getSource().x() + ", " + move.getSource().y() + "]" +
                " -> " +
                "[" + move.getDestination().x() + ", " + move.getDestination().y() + "]" + "\n";
        historyFiled.setText(newText);
    }

    /**
     * Go back to main menu
     * Hint: before quit, you need to end the game
     */
    private void doQuitToMenu() {
        // TODO
        endGame();
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    /**
     * Converting a vertical or horizontal coordinate x to the coordinate in board
     * Hint:
     *      The pixel size of every piece is defined in {@link ViewConfig#PIECE_SIZE}
     * @param x coordinate of mouse click
     * @return the coordinate on board
     */
    private int toBoardCoordinate(double x){
        // TODO
        return 0;
    }

    /**
     * Handler of ending a game
     * Hint:
     *      - Clear the board, history text field
     *      - Reset buttons
     *      - Reset timer
     *
     */
    private void endGame() {
        //TODO
        fxJesonMor.stopCountdown();
        numMoves = 0;
        lastPlayer = null;
        winner = null;
        currentPlayer = null;
        lastPiece = null;
        lastMove = null;
        isStopped = true;

        disableCanvas();
        Configuration newConfiguration = new Configuration(globalConfiguration.getSize(), globalConfiguration.getPlayers(), globalConfiguration.getNumMovesProtection());
        newConfiguration.setAllInitialPieces();
        fxJesonMor = new FXJesonMor(newConfiguration);

        gamePlayCanvas.getGraphicsContext2D().clearRect(0, 0, gamePlayCanvas.getWidth(), gamePlayCanvas.getHeight());

        Configuration currentConfiguration = fxJesonMor.getConfiguration();
        currentConfiguration.getPlayers()[0].setScore(0);
        currentConfiguration.getPlayers()[1].setScore(0);

        remainingTime = DurationTimer.getDefaultEachRound();
        Platform.runLater(()->{
            ticksElapsed.setValue(remainingTime);
        });

        parameterText.setText("Parameters:" + "\n" + "\n" +
                "Size of board: " + currentConfiguration.getSize() + "\n"+
                "Num of protection moves: " + currentConfiguration.getNumMovesProtection() + "\n" +
                "Player " + currentConfiguration.getPlayers()[0].getName() + (currentConfiguration.isFirstPlayerHuman() ? "(human)" : "(computer)") + "\n" +
                "Player " + currentConfiguration.getPlayers()[1].getName() + (currentConfiguration.isSecondPlayerHuman() ? "(human)" : "(computer)") + "\n"
        );
        historyFiled.setText("");

        startButton.setDisable(false);
        restartButton.setDisable(true);

        fxJesonMor.updateScore(currentConfiguration.getPlayers()[0], null, new Move(0, 0, 0, 0));
        fxJesonMor.updateScore(currentConfiguration.getPlayers()[1], null, new Move(0, 0, 0, 0));

        fxJesonMor.renderBoard(gamePlayCanvas);
    }
}
