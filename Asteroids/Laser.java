/*
Laser.java
Ingrid Crant
A laser class for asteroids arcade game. Destroys all other objects in the game.
*/

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;

/**
 * Contains the data and instructions for movement
 * for the lasers shot in the game.
 * @author Casey Scarborough
 * @version 1.0.0
 * @see GameBoard
 * @see SpaceShip
 *
 */

public class Laser extends Rectangle {
	/**
	 *
	 */
	private static final long serialVersionUID = -1439482085434890112L;											// required a static final serialVersionIUD field of type long

	private Point center;																						// center of the laser
	public static final int WIDTH = 5;																			// width of laser
	public static final int GAMEWIDTH = AsteroidsPanel.WIDTH, GAMEHEIGHT = AsteroidsPanel.HEIGHT;				// width and height of AsteroidsPanel

	private Point2D.Double velocity = new Point2D.Double(16,16);												// stores laser's velocity
	
	public Laser(int centerX, int centerY, double movingAngle) {
		super(centerX, centerY, WIDTH, WIDTH);																	// constructs the rectangle

		center = new Point(centerX, centerY);																	// sets the center
		
		// sets up x and y velocity to move in the direction of the moving angle
		velocity.x *= Math.cos(Math.toRadians(movingAngle));
		velocity.y *= Math.sin(Math.toRadians(movingAngle));
	}
	
	// getter methods for velocity
	public double getXVelocity() { return this.velocity.x; }
	public double getYVelocity() { return this.velocity.y; }

	public void move() {																						// moves the laser
		// WRAPPING AROUND SCREEN
		if(center.x < 0)  {																						// if on or past the left of the screen and is moving left
			if(velocity.x < 0) {
				// move laser to the right of the screen
				int ogy = center.y;
				center.y = GAMEHEIGHT-center.y;
				center.x += GAMEWIDTH;
				this.translate(GAMEWIDTH,center.y-ogy);
			}
		}
		else if (center.x > GAMEWIDTH) {																		// if on or past the right of the screen and is moving right
			if(velocity.x > 0) {
				// move laser to the left of the screen
				int ogy = center.y;
				center.y = GAMEHEIGHT-center.y;
				center.x -= GAMEWIDTH;
				this.translate(-GAMEWIDTH,center.y-ogy);
			}
		}
		else if(center.y < 0) {																					// if on or past the top of the screen and is moving up
			if(velocity.y < 0) {
				// move laser to the bottom of the screen
				int ogX = center.x;
				center.x = GAMEWIDTH-center.x;
				center.y += GAMEHEIGHT;
				this.translate(center.x-ogX,GAMEHEIGHT);
			}
		} 
		else if(center.y > GAMEHEIGHT) {																		// if on or past the bottom of the screen and is moving down
			if(velocity.y > 0) {
				// move laser to the top of the screen
				int ogX = center.x;
				center.x = GAMEWIDTH-center.x;
				center.y -= GAMEHEIGHT;
				this.translate(center.x-ogX,-GAMEHEIGHT);
			}
		}
		// moves the x and y coordinate of the laser by the respective x and y velocity
        center.x += velocity.x;
        center.y += velocity.y;
		this.translate((int) getXVelocity(), (int) getYVelocity());
	}

    public void draw(Graphics2D graphicSettings) {																// draws the laser
		graphicSettings.translate(center.x, center.y);															// moves the laser to its center
		graphicSettings.draw(this);																				// draw the laser on the screen
    }
}