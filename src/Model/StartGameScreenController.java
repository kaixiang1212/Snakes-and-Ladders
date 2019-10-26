package Sneks_and_Ladders;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;

// StartScreen Controller communicates between View and Model.

public class StartGameScreenController {
	
	@FXML
	private Button StartGameButton;
	
	@FXML
	private Button ExitGameButton;
	

	
	//private GameScreen game;
	private Stage s;
	
	public StartGameScreenController(Stage s) {
		this.s = s;
	}
	
	@FXML
	public void handleStartGameButton (ActionEvent event) {
		// We always start the game with level 0.
		//Game game = new Game(0, null);
		//LevelIntroScreen introScreen = new LevelIntroScreen(s, game);
		//introScreen.start();
		GameScreen gameScreen = new GameScreen(s);
		try {
			gameScreen.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@FXML
	// Exits the Game Application
	public void handleExitGameButton (ActionEvent event) {
		Platform.exit();
	}

}
