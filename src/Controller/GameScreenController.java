package Controller;

import Model.GameEngine;
import Model.Player;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class GameScreenController {

    @FXML
    public Button button;
    @FXML
    private ImageView diceImage;
    @FXML
    private Text text;
    @FXML
    private TextArea asciiMap;
    @FXML
    private Text message;

    private Stage stage;
    private GameEngine players;
    private final Image[] diceFace;

    public GameScreenController(Stage stage, GameEngine players){
        this.players = players;
        this.stage = stage;
        this.diceFace = new Image[6];
        diceFace[0] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice1.png")));
        diceFace[1] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice2.png")));
        diceFace[2] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice3.png")));
        diceFace[3] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice4.png")));
        diceFace[4] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice5.png")));
        diceFace[5] = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/dice6.png")));
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void rollButtonClicked(){
        text.setText("");
        animation.start();
        button.setText("Stop");
        button.setOnAction(event -> stopButtonClicked());
        diceImage.setOnMouseClicked(mouseEvent -> stopButtonClicked());
    }

    @FXML
    private void stopButtonClicked(){
        animation.stop();
        button.setDisable(true);
        diceImage.setDisable(true);

        Player currentPlayer = players.getCurrentPlayer();
        int diceResult = players.rollDice();
        text.setText(currentPlayer.getPlayerName() + " rolled " + diceResult);
        draw(diceResult);

        StringBuilder sb = new StringBuilder();

        if (players.isFinished()) {
            sb.append(currentPlayer.getPlayerName()).append(" has won the game! Congratulations!\n");
            return;
        }
        if (diceResult == 6){
            sb.append(currentPlayer.getPlayerName()).append(" roll again\n");
        } else {
            currentPlayer = players.nextPlayer();
        }
        sb.append(currentPlayer.getPlayerName()).append("'s turn:\n");

        button.setDisable(false);
        diceImage.setDisable(false);
        button.setText("Start Rolling");
        diceImage.setOnMouseClicked(mouseEvent -> rollButtonClicked());
        button.setOnAction(event -> rollButtonClicked());
        System.out.println(players.getConsole() + sb.toString() + players.printBoard());
        asciiMap.setText(players.getConsole() + players.printBoard());
        asciiMap.setFont(Font.font("Consolas", 18));
        message.setText(sb.toString());
        players.clearConsole();
    }

    private void draw(int dieFace){
        Image image = this.diceFace[dieFace-1];
        diceImage.setImage(image);
    }

    private final int maxFrame = 1000;
    private int frame = 0;
    /**
     * Randomise Dice face for infinite frames
     */
    private AnimationTimer animation = new AnimationTimer() {
        public void handle( long time ) {
            int diceFrame = (int)(Math.random()*6) + 1;
            draw(diceFrame);
            frame++;
            if (frame == maxFrame){
                stopButtonClicked();
                frame = 0;
            }
        }
    };

}
