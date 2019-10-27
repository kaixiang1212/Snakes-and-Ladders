package Controller;

import Model.*;
import View.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class BoardController {
	
	@FXML
	private GridPane squares;
	
	private List<Pair<Entity,ImageView>> initialEntities;
	//private Player player;
	GameEngine engine;
	private Stage stage;
	private GameScreen gamescreen;
	    

	public BoardController(GameEngine engine, List<Pair<Entity,ImageView>> initialEntities, Stage s, GameScreen game) {
		// The board contains the current game details at this point. Game is up to date.
		//this.player = gameboard.getPlayer();
		this.engine = engine;
		this.initialEntities = new ArrayList<>(initialEntities);
		this.stage = s;
		this.gamescreen = game;
	}

	@FXML
	public void initialize() {
		
        int lastrand = 0, rand = 0;
        for (int x = 0; x < engine.getBoard().getWidth(); x++) {
            for (int y = 0; y < engine.getBoard().getHeight(); y++) {
            	while (rand == lastrand)
            		rand = (int) (Math.random()*6);
            	Image boardFloor = new Image(String.valueOf(getClass().getClassLoader().getResource("asset/gametile" + rand + ".png")));
                ImageView floorView = new ImageView(boardFloor);
                floorView.setFitHeight(gamescreen.getHeight()/(float)engine.getBoard().getHeight());
                floorView.setFitWidth(gamescreen.getHeight()/(float)engine.getBoard().getWidth());
                squares.add(floorView, x, y);
                Text tilenum = new Text(Integer.toString(engine.getBoard().getPosition(x, engine.getBoard().getHeight()-y-1)));
                tilenum.setFont(Font.font("Harlow Solid Italic", 60));
                tilenum.setFill(Color.WHITE);
                tilenum.setStroke(Color.BLACK);
                tilenum.setStrokeWidth(3);
                squares.add(tilenum, x, y);
                GridPane.setHalignment(tilenum, HPos.CENTER);
                //GridPane.setValignment(tilenum, VPos.TOP);
                lastrand = rand;
            }
        }
        
        // Order entities by their order in the enum Type (in Model.Entity)
        initialEntities.sort(new Comparator<Pair<Entity, ImageView>>() { 
            @Override public int compare(Pair<Entity, ImageView> p1, Pair<Entity, ImageView> p2) 
            { 
                return p2.getKey().getEntityType().ordinal() - p1.getKey().getEntityType().ordinal();
            } 
        });
        
        for (Pair<Entity,ImageView> entityPair : initialEntities) {
        	Entity entity = entityPair.getKey();
        	ImageView entityImage = entityPair.getValue();
            squares.getChildren().add(entityImage);
            GridPane.setHalignment(entityImage, HPos.CENTER);
            if(entity instanceof Snake || entity instanceof Ladder) {
            	addSegments(entity);
            }
        }

    }
	
	public void addSegments(Entity entity) {
		int x,y,x_end,y_end, y_init, x_init;
		String name;
		if(entity instanceof Snake) {
			entity = (Snake) entity;
			x = entity.getX();
			y = entity.getY();
			x_end = ((Snake) entity).getTail().getKey();
			y_end = ((Snake) entity).getTail().getValue();
			name = "pipe";
			y_init = y;
			y--;
		} else if(entity instanceof Ladder) {
			entity = (Ladder) entity;
			x_end = entity.getX();
			y_end = entity.getY();
			x = ((Ladder) entity).getTop().getKey();
			y = ((Ladder) entity).getTop().getValue();
			name = "vine";
			y_init = y;
			
		} else {
			return;
		}
		x_init = x;
		ImageView image = null;
		
		while(x != x_end || y != y_end) {
			if(x == x_init) {
				if(x > x_end) {
					image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_c_lefttop.png"))));
				} else if (x < x_end) {
					image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_c_righttop.png"))));
				} else {
					image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_v.png"))));
				}
			} else {
				if(x > x_end) {
					image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_l.png"))));	
				} else if (x < x_end) {
					image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_r.png"))));
				} else {
					if((y == y_init && entity instanceof Ladder) || (y == y_init-1 && entity instanceof Snake)) {
						if(x < x_init) {
							image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_c_rightbottom.png"))));
						} else {
							image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_c_leftbottom.png"))));
						}
					} else {
						image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_v.png"))));
					}
				}
			}
			image.setFitHeight(gamescreen.getHeight()/(float)engine.getBoard().getHeight()*1.0f);
			image.setPreserveRatio(true);
			squares.add(image, x, engine.getBoard().getHeight()-1-y);
			if(x > x_end) {
				x--;
			} else if (x < x_end) {
				x++;
			} else {
				y--;
			}
			
		}
		if(entity instanceof Snake) {
			image = new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource("asset/"+name+"_end.png"))));
			image.setFitHeight(gamescreen.getHeight()/(float)engine.getBoard().getHeight()*1.0f);
			image.setPreserveRatio(true);
			squares.add(image, x, engine.getBoard().getHeight()-1-y);
		}
		
		
	}
	
	public void addLadder(Ladder ladder) {
		
	}
	
	@FXML
    public void handleKeyPress(KeyEvent event) {
        
         switch (event.getCode()) {

        case UP:
            engine.rollDice();
            engine.nextPlayer();
            break;
        case DOWN:
            break;
        case LEFT:
            break;
        case RIGHT:
            break;
        default:
            break;
        }
                 
	}
}