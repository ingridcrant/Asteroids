/*
Spaceship.java
Ingrid Crant
A spaceship class for asteroids arcade game. Moves and fires lasers based on user input.
*/

import java.awt.*;
import java.awt.geom.Point2D;

public class SpaceShip extends Polygon {
	/**
	 *
	 */
	private static final long serialVersionUID = -1755420491084514663L;										// required a static final serialVersionIUD field of type long

	private int UP, DOWN, LEFT, RIGHT;																		// stores the key values of the up, down, left, and right keys
	private boolean hasMoved = false;																		// keeps track of if the spaceship moved or not
	private static final int GAMEHEIGHT = AsteroidsPanel.WIDTH, GAMEWIDTH = AsteroidsPanel.HEIGHT;			// stores the width and height of AsteroidsPanel
	
    private static int[] polyXArray = {-13,14,-13,-5,-13};													// stores the x-points of the spaceship polygon
	private static int[] polyYArray = {-15,0,15,0,-15};														// stores the y-points of the spaceship polygon
	private static final int rotateDegrees = 10;																// angle of rotation
	private int speed = 4;																					// speed of the spaceship
    private static final int WIDTH = 27, HEIGHT = 30;														// width and height of the spaceship
    private int orientationAngle = 270;																					// angle of orientation
	private Point2D.Double pull = new Point2D.Double(0,0), velocity = new Point2D.Double(0, 0);				// stores x and y velocity and x and y pull (pull gives the "floating is space" effect)
    private Point center = new Point(GAMEHEIGHT/2, GAMEWIDTH/2);												// center of spaceship

	private Rectangle hitBox;																				// rectangle object surrounding the spaceship. used to detect collisions.
	private int lives = 3; 																					// start with 3 lives
	
	public SpaceShip(int []keys) {
		super(polyXArray, polyYArray, 5);																	// constructs the polygon
		hitBox = new Rectangle(center.x-(WIDTH/2), center.y-(HEIGHT/2), WIDTH, HEIGHT);						// constructs hitBox

		// initializes keys needed to control the spaceship
		UP = keys[0];
		DOWN = keys[1];
		LEFT = keys[2];
		RIGHT = keys[3];
	}
	public void increaseVelocity() { 																		// used to increase the x and y velocity for a new level
		velocity.x += 5;
		velocity.y += 5;
	}

	// GETTERS
	public int getRotationAngle() { return orientationAngle; }
    public double getShipNoseX() {
		return center.x + Math.cos(orientationAngle) * 14;
	}
	public double getShipNoseY() {
		return center.y + Math.sin(orientationAngle) * 14;
	}
    public Rectangle getRect() {
		return hitBox;
	}
	public int getLives() {
		return lives;
	}

	public void regenerateLives() {																			// regenerates lives when for a new level
		lives = 3;
	}
	public void loseLife() {																				// called when the user loses a life
		lives--;																							// subtract one from lives
		orientationAngle = 270;																				// reset orientation angle to default
		center = new Point(GAMEHEIGHT/2, GAMEWIDTH/2);														// reset spaceship postition to center of screen

		// stop moving
		velocity = new Point2D.Double(0, 0);
		pull = new Point2D.Double(0, 0);
		hasMoved = false;
	}

	public boolean hasMoved() {
		return hasMoved;
	}
	public void move(boolean []keys) {																		// moves the spaceship
		// WRAPPING AROUND SCREEN
		if(center.x <= 0)  {																					// if on or past the left of the screen and is moving left
			if(velocity.x < 0) {
				// move spaceship and hitBox to the right of the screen
				int ogy = center.y;
				center.y = AsteroidsPanel.HEIGHT-center.y;
				center.x += AsteroidsPanel.WIDTH;
				hitBox.translate(AsteroidsPanel.WIDTH,center.y-ogy);
			}
		}
		else if (center.x >= AsteroidsPanel.WIDTH) {															// if on or past the right of the screen and is moving right
			if(velocity.x > 0) {
				// move spaceship and hitBox to the left of the screen
				int ogy = center.y;
				center.y = AsteroidsPanel.HEIGHT-center.y;
				center.x -= AsteroidsPanel.WIDTH;
				hitBox.translate(-AsteroidsPanel.WIDTH,center.y-ogy);
			}
		}
		else if(center.y <= 0) {																				// if on or past the top of the screen and is moving up
			if(velocity.y < 0) {
				// move spaceship and hitBox to the bottom of the screen
				int ogX = center.x;
				center.x = AsteroidsPanel.WIDTH-center.x;
				center.y += AsteroidsPanel.HEIGHT;
				hitBox.translate(center.x-ogX,AsteroidsPanel.HEIGHT);
			}
		} 
		else if(center.y >= AsteroidsPanel.HEIGHT) {															// if on or past the bottom of the screen and is moving down
			if(velocity.y > 0) {
				// move spaceship and hitBox to the top of the screen
				int ogX = center.x;
				center.x = AsteroidsPanel.WIDTH-center.x;
				center.y -= AsteroidsPanel.HEIGHT;
				hitBox.translate(center.x-ogX,-AsteroidsPanel.HEIGHT);
			}
		}

		if(keys[UP]) {																							// the spaceship moves and accelerates
			// move in the direction of orientationAngle
			velocity.y = speed * Math.sin(Math.toRadians(orientationAngle));
        	velocity.x = speed * Math.cos(Math.toRadians(orientationAngle));

			hasMoved = true;

			if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.thrust);						// plays the thrust sound effect

			// moves the x and y coordinate of spaceship by the respective x and y velocity
			center.x += velocity.x;
            center.y += velocity.y;

			// accelerates by the respective x and y velocity
            pull.x += velocity.x;
            pull.y += velocity.y;
        }
        if (keys[LEFT]) {																						// rotates the spaceship to the left
            rotate(-rotateDegrees);
        }
        if (keys[RIGHT]) {																						// rotates the spaceship to the right
            rotate(rotateDegrees);
        }
        if(keys[DOWN]) {																						// spaceship stops moving
            pull.x = 0;
            pull.y = 0;
        }
        drift();
		hitBox.setLocation(center.x, center.y);																	// moves hitBox to the current spaceship position
	}

	public void rotate(int deg) {																				// rotates orientationAngle by "deg" degrees
		// adds "deg" onto orientationAngle
		// also keeps orientationAngle between 0 to 360
        orientationAngle = (deg + orientationAngle) % 360;
        orientationAngle = (orientationAngle < 0) ? 360 - -orientationAngle : orientationAngle;
    }

    public void drift() {
		// accelerates the asteroid
        center.x += pull.x;
        center.y += pull.y;
    }

	public void draw(Graphics2D graphicSettings) {
		graphicSettings.translate(2*center.x, 2*center.y);														// translates the shape so that it appears in the correct position on the screen
		graphicSettings.rotate(Math.toRadians(orientationAngle));												// Rotate the spaceship
		graphicSettings.draw(this);																				// draws the spaceship
    }
}
