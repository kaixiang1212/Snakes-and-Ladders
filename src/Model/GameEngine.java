package Model;

import Controller.MusicController;
import Model.Board.BoardType;
import Controller.AnimationController;
import Controller.Networking.Server;
import javafx.scene.image.ImageView;

import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.PauseTransition;

public class GameEngine {

	private static final int pickedUpItemExpiry = -1000;	// Special expiry counter number for picked up items to remove on pickup
    private static final int poisonChance = 90;				// Chance of being poisoned by a snake

    private static ArrayList<Player> players;
    private static Player currentPlayer;
    private static int currentPlayerNum;
    private static Board gameboard;
    private static boolean finished;
    private static StringBuilder console;
    private static Server server;
    private static boolean reverse;
    private static boolean dynamicSnakes = true;

    /**
     * Default constructor generates a 10x10 board with some snakes and ladders
     */
    public GameEngine(){
        players = new ArrayList<>();
        currentPlayer = null;
        currentPlayerNum = 0;
        gameboard = new Board(10, 10, BoardType.DEFAULT);
        finished = false;
        console = new StringBuilder();
        console.setLength(0);
        MusicController.initGame();
        reverse = false;
    }

    public GameEngine(BoardType type){
    	players = new ArrayList<>();
        currentPlayer = null;
        currentPlayerNum = 0;
        gameboard = new Board(10, 10, type);
        finished = false;
        console = new StringBuilder();
        console.setLength(0);
        MusicController.initGame();
        reverse = false;
    }

    /**
     * This constructor is used to pass in a pre-made gameboard
     * @param board: pre-made gameboard
     */
    public GameEngine(Board board){
        if(players == null)
        	players = new ArrayList<>();
        gameboard = board;
        finished = false;
        console = new StringBuilder();
        console.setLength(0);
        MusicController.initGame();
        reverse = false;
    }

    public static void setPlayers(ArrayList<Player> players){
    	GameEngine.players = players;
        currentPlayerNum = 0;
        currentPlayer = players.get(currentPlayerNum);
    }

    /**
     * Add a player into the game
     * @param player: A player object to be added into the game
     */
    public static void addPlayer(Player player){
        if (currentPlayer == null) {
            currentPlayerNum = 0;
            currentPlayer = player;
        }
        if(players == null)
        	players = new ArrayList<Player>();
        players.add(player);
    }



    /**
     * Get the number of players in the game
     * @return number of players
     */
    public static int getPlayerNum(){
        return players.size();
    }

    /**
     * Update player's position to pos
     * @param player: Player object to update their position
     * @param pos: player's new position
     * @return updated player position
     */
	public static int updatePosition(Player player, int pos) {
		pos = Math.min(pos, gameboard.getMaxPos());
		pos = Math.max(pos, gameboard.getMinPos());
		int x,y;
		x = gameboard.getCoords(pos).getKey();
		y = gameboard.getCoords(pos).getValue();
		player.setX(x);
		player.setY(y);
		MusicController.playMove();
		Item item = gameboard.isItem(x, y);
		if(item != null) {
			player.pickupItem(item);
			item.setExpiry(pickedUpItemExpiry);
			System.out.println("[!] " + item.getName() + " item picked up by " + player.getPlayerName() + ".\n");
		}
		return pos;
	}

	/**
     * Get current player
     * @return current player
     */
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Get index of current player
     * @return current player index
     */
	public static int getCurrentPlayerNum() {
		return currentPlayerNum;
	}

	public static ArrayList<Player> getPlayers(){
		return players;
	}

	public static Board getBoard() {
		return gameboard;
	}

	/**
     * Set the next player in turn as current player, looping over the list of all players
     */
    public static Player setNextPlayer(){
    	if(!isReverse()) {
    		currentPlayerNum = (currentPlayerNum + 1) % getPlayerNum();
    	} else {
    		currentPlayerNum = (currentPlayerNum + getPlayerNum()-1) % getPlayerNum();
    	}

        currentPlayer = players.get(currentPlayerNum);
        if(currentPlayer.isSkipped()) {
        	currentPlayer.setSkipped(false);
        	setNextPlayer();
        }
        return currentPlayer;
    }

    public static Player getNextPlayer(){
    	int nextPlayerNum;
    	if(!isReverse()) {
    		nextPlayerNum = (currentPlayerNum + 1) % getPlayerNum();
    	} else {
    		nextPlayerNum = (currentPlayerNum + getPlayerNum()-1) % getPlayerNum();
    	}
        Player nextPlayer = players.get(nextPlayerNum);
        return nextPlayer;
    }

    public static ArrayList<Player> getPlayerSequence(){
    	ArrayList<Player> sequence = new ArrayList<>();
    	int i = 1;
    	while (sequence.size() != 4){
			if (!isReverse()) {
				Player player = players.get((currentPlayerNum + i) % getPlayerNum());
				if (player.isSkipped()){
					i++;
					continue;
				} // else if (player.isExtraRoll()) sequence.add(player);
				sequence.add(player);
			} else {
				Player player = players.get((currentPlayerNum + getPlayerNum()-i) % getPlayerNum());
				if (player.isSkipped()){
					i++;
					continue;
				}// else if (player.isExtraRoll()) sequence.add(player);
				sequence.add(player);
			}
			i++;
		}
		return sequence;
	}

    public static ArrayList<Player> getNextNearestPlayers() {
    	ArrayList<Player> targetPlayers = new ArrayList<Player>();
    	int dist = gameboard.getMaxPos() - gameboard.getMinPos();
    	for(Player player : players) {
    		if(player == getCurrentPlayer())
    			continue;
    		int pos1 = gameboard.getPosition(getCurrentPlayer().getX(), getCurrentPlayer().getY());
    		int pos2 = gameboard.getPosition(player.getX(), player.getY());
    		if((pos2-pos1 >= 0) && (pos2-pos1 < dist)) {
    			targetPlayers.clear();
    			dist = pos2-pos1;
    			targetPlayers.add(player);
    		} else if ((pos2-pos1 >= 0) && (pos2-pos1 == dist)) {
    			targetPlayers.add(player);
    		}
    	}
    	return targetPlayers;
    }

    public static Player getLeadingPlayer() {
    	Player targetPlayer = null;
    	int dist = 0;
    	for(Player player : players) {
    		int pos1 = gameboard.getPosition(getCurrentPlayer().getX(), getCurrentPlayer().getY());
    		int pos2 = gameboard.getPosition(player.getX(), player.getY());
    		if((pos2-pos1 >= 0) && (pos2-pos1 >= dist)) {
    			dist = pos2-pos1;
    			targetPlayer = player;
    		}
    	}
    	return targetPlayer;
    }

    public static void swapPlayers(Player player1, Player player2) {
    	int x1, y1;
    	x1 = player1.getX();
    	y1 = player1.getY();
    	player1.setX(player2.getX());
    	player1.setY(player2.getY());
    	player2.setX(x1);
    	player2.setY(y1);
    }

	/**
	 * Checks whether the game is finished
	 * @return true if finished, false otherwise
	 */
	public static boolean isFinished() {
		updateState();
		//if (finished)
		return finished;
	}

	/**
	 * Updates the state of the game. This does:
	 * - Checks if player lands on snake, and changes their position
	 * - Checks if player lands on ladder, and changes their position
	 * - Checks if player lands on end position, and updates finished flag
	 */
	public static void updateState() {
		for(Player currPlayer : players) {
			int currX, currY;
			currX = currPlayer.getX();
			currY = currPlayer.getY();
			int currPos = gameboard.getPosition(currX, currY);
			if(currPos == -1)
				break;
			if(currPos == gameboard.getMaxPos()) {
				finished = true;
				return;
			} else if ((gameboard.isSnake(currX, currY) != null) && !currPlayer.isSnakeImmunity()) {
				int newX, newY;
				Snake currSnake = gameboard.isSnake(currX, currY);
				newX = gameboard.isSnake(currX, currY).getTail().getKey();
				newY = gameboard.isSnake(currX, currY).getTail().getValue();
				int newPos = updatePosition(currPlayer, gameboard.getPosition(newX, newY));
				currPlayer.getStats().incrementSnakes();
				MusicController.playSnake();

					
				
				// Get snake ImageViews and wriggle snake
				if (currSnake.isPoisonous() == false) {
					ImageView snakeGif = AnimationController.getGifView(currSnake.getId());
					ImageView snakeImg = AnimationController.getImgView(currSnake.getId());
					
					
					if (Math.random() < ((float) poisonChance/100f) && dynamicSnakes) {
						if (currSnake.getId().equals("Snake6")) {
							// Disable periodic movement temporarily
							AnimationController.pauseperiodicMovement();
						}
						currSnake.setPoisonous();
						ImageView psnakeImg = AnimationController.getpoisonousImgView(currSnake.getId());
						AnimationController.wriggleSnake(snakeGif, snakeImg);
						// Stop wriggling snake after 2 secs
						PauseTransition pause = new PauseTransition(Duration.seconds(1));
						pause.setOnFinished(event ->
							AnimationController.stopwriggleSnake(snakeGif, psnakeImg)
						);
						pause.play();
						
						if (currSnake.getId().equals("Snake6")) {
							AnimationController.startpoisonMovement();
						}
					} else {
						AnimationController.wriggleSnake(snakeGif, snakeImg);
						// Stop wriggling snake after 2 secs
						PauseTransition pause = new PauseTransition(Duration.seconds(1));
						pause.setOnFinished(event ->
							AnimationController.stopwriggleSnake(snakeGif, snakeImg)
						);
						pause.play();
					}
				} else {
					currPlayer.setPoison(3);
					ImageView psnakeGif = AnimationController.getpoisonousGifView(currSnake.getId());
					ImageView psnakeImg = AnimationController.getpoisonousImgView(currSnake.getId());
					System.out.println(psnakeGif);
					AnimationController.wriggleSnake(psnakeGif, psnakeImg);
					// Stop wriggling snake after 2 secs
					PauseTransition pause = new PauseTransition(Duration.seconds(1));
					pause.setOnFinished(event ->
						AnimationController.stopwriggleSnake(psnakeGif, psnakeImg)
					);
					pause.play();
				}
				updateState();
			} else if (gameboard.isLadder(currX, currY) != null) {
				int newX, newY;
				Ladder currLadder = gameboard.isLadder(currX, currY);
				newX = gameboard.isLadder(currX, currY).getTop().getKey();
				newY = gameboard.isLadder(currX, currY).getTop().getValue();
				int newPos = updatePosition(currPlayer, gameboard.getPosition(newX, newY));
				currPlayer.getStats().incrementLadders();
				MusicController.playLadder();

				// Get ladder ImageViews and shake Ladder
				ImageView ladderGif = AnimationController.getGifView(currLadder.getId());
				ImageView ladderImg = AnimationController.getImgView(currLadder.getId());
				// Shake the ladder
				AnimationController.shakeLadder(ladderGif, ladderImg);
				// Stop laddershake after 1 second
				PauseTransition pause = new PauseTransition(Duration.seconds(1));
				pause.setOnFinished(event ->
					AnimationController.stopShakeLadder(ladderGif, ladderImg)
				);
				pause.play();


				console.append(currPlayer.getPlayerName())
						.append(" climbs a ladder moves up from ")
						.append(currPos).append(" to ")
						.append(newPos).append("\n");
				
				//updateState();
			}
		}
		finished = false;
		
		// Start periodic movement temporarily
		//AnimationController.startperiodicMovement();
	}

	public static String getConsole(){
		return console.toString();
	}

	public static void clearConsole(){
		console.setLength(0);
	}

	public static String printBoard() {
		StringBuilder sb = new StringBuilder();
		int width = gameboard.getWidth();
		int height = gameboard.getHeight();
		for(int y = height-1; y >= 0; y--) {
			for(int x = 0; x < width; x++) {
				boolean empty = true;
				sb.append("[");
				if(gameboard.isSnake(x, y) != null) {
					sb.append("x");
					empty = false;
				} else if(gameboard.isLadder(x, y) != null) {
					sb.append("H");
					empty = false;
				}
				for(Player currPlayer : players) {
					if(currPlayer.getX() == x && currPlayer.getY() == y) {
						sb.append(currPlayer.getPlayerToken());
						empty = false;
					}
				}
				if(empty) {
					sb.append(" ");
				}
				sb.append("]");
			}
			sb.append("\n");
		}
		for(Player player : players) {
			sb.append(player.getPlayerToken());
			sb.append(" = ");
			sb.append(player.getPlayerName());
			sb.append("\n");
		}
		sb.append("x = snake\n");
		sb.append("H = ladder\n");
		return sb.toString();
	}

	public static int getCurrentPlayerToken(){
		return getCurrentPlayer().getPlayerToken();
	}

	/**
	 * Spawns a random item in the board. Item spawns at position < top player position && position > last player position && position != any player or existing item position
	 * @return spawned item object
	 */
	public static Item spawnRandomItem() {
		// Set item position to be < top player's position
		int maxPlayerPos = gameboard.getMinPos();
		int minPlayerPos = gameboard.getMaxPos();
		for(Player player : players) {
			maxPlayerPos = Math.max(maxPlayerPos, gameboard.getPosition(player.getX(), player.getY()));
			minPlayerPos = Math.min(minPlayerPos, gameboard.getPosition(player.getX(), player.getY()));
		}
		int itemPos = (int) (minPlayerPos + (Math.random() * (maxPlayerPos - minPlayerPos)));
		int itemX = gameboard.getCoords(itemPos).getKey();
		int itemY = gameboard.getCoords(itemPos).getValue();

		// Check that position != any player position
		for(Player player : players) {
			int playerPos = gameboard.getPosition(player.getX(), player.getY());
			if(playerPos == itemPos)
				return null;
		}

		// Check that position != any existing item position
		if(gameboard.isItem(itemX, itemY) != null) {
			return null;
		}

		// Obtain a random item from the pool of available items
		ArrayList<Item> pool = gameboard.getItemPool();
		int size = pool.size();
		int index = (int) (Math.random()*size);
		Item itemTemplate = pool.get(index);


		Item item = new Item(itemX, itemY, itemTemplate.getItemType(), itemTemplate.getName(), itemTemplate.getFrequency(), itemTemplate.getExpiry());
		gameboard.addEntity(item);
		return item;
	}

	public static int getPickedUpItemExpiry() {
		return pickedUpItemExpiry;
	}

	public static void setServer(Server Server) { server = Server; }

	public static Server getServer(){ return server; }

    public static void killServer() throws IOException { server.kill(); }

	public static boolean isReverse() {
		return reverse;
	}

	public static void setReverse(boolean reverse) {
		GameEngine.reverse = reverse;
	}

	public static void decrementExpiry() {
		for(Item item : gameboard.getSpawnedItems()) {
			item.decrementExpiry();
		}
	}

	public static void setDynamicSnakes(boolean dynamicSnakes) {
		GameEngine.dynamicSnakes = dynamicSnakes;
	}

	public static boolean isDynamicSnakes() {
		return dynamicSnakes;
	}
}
