package Model;

import javafx.util.Pair;

public class Snake extends Entity {
	
	// Enum to represent the snake type for differentiating ImageViews
	public enum SnakeType {
		  BLUESNAKE,
		  SNAKE,
		  PINKSNAKE,
	}
	
	private int x2, y2;
	protected SnakeType snaketype;
	private String id;
	private boolean isPoisonous;
	
	
	/**
	 * Create a snake positioned at (x,y) that leads to position (x2,y2)
	 * @param x x-pos of snake head
	 * @param y y-pos of snake head
	 * @param x2 x-pos of snake tail
	 * @param y2 y-pos of snake tail
	 */
	public Snake(int x, int y, int x2, int y2, String id) {
        super(x, y, Type.SNAKE);
        this.x2 = x2;
        this.y2 = y2;
        this.id = id;
        this.isPoisonous = false;
    }
	
	

	public boolean isPoisonous() {
		return isPoisonous;
	}



	public void setPoisonous() {
		this.isPoisonous = true;
	}

	public void offPoisonous() {
		this.isPoisonous = false;
	}


	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public Pair<Integer, Integer> getTail() {
		return new Pair<>(x2,y2);
	}

	public void setTail(int x2, int y2) {
		this.x2 = x2;
		this.y2 = y2;
	}
	
	
	
}