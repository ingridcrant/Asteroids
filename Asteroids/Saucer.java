/*
Saucer.java
Ingrid Crant
A saucer class for asteroids arcade game. Saucer moves and shoots randomly. Can only be destroyed by user.
*/

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Saucer {
    private Rectangle hitBox;                                                                   // rectangle object surrounding the saucer. used to detect collisions.
    private Point position;                                                                     // stores saucer's position

    private static final int speed = 6;                                                         // stores saucer's speed
    private int initialAngle;                                                                   // stores the initial angle of the saucer
    private int angleChange;                                                                    // stores the angle change of the saucer
    private int currentAngle;                                                                   // stores the current angle of the saucer

    private static final int MAXNUMFRAMESATANGLE = 200, MINNUMFRAMESATANGLE = 120;                      // stores the min and max amount of frames that a saucer can stay at an angle
    private static final int GAMEWIDTH = AsteroidsPanel.WIDTH, GAMEHEIGHT = AsteroidsPanel.HEIGHT;      // stores the frame width and height
    private boolean atAngle = false;                                                            // keeps track of if the saucer is at an angle
    private int numFramesAtAngle = 0;                                                           // caps the number of frames the saucer can stay at an angle
    private int currentNumFrames = 0;                                                           // keeps track of the number of frames it is at if the saucer is at an angle
    private Point velocity = new Point();                                                       // keeps track of the saucer's x and y velocity

    private static final Image largeSaucer = new ImageIcon("images/largeSaucer.png").getImage();                        // saucer image
    private static final int WIDTH = largeSaucer.getWidth(null), HEIGHT = largeSaucer.getHeight(null);                  // saucer width and height
    private static final Random rand = new Random();

    public Saucer() {
        int[] xCoordChoices = new int[] {-WIDTH, AsteroidsPanel.WIDTH};

        // the saucer can either start at the left or right of the screen
        position = new Point(xCoordChoices[rand.nextInt(2)], rand.nextInt(AsteroidsPanel.HEIGHT-HEIGHT));       

        if(position.x == WIDTH) initialAngle = 0;                                               // if it starts on the left, it heads right
        else initialAngle = 180;                                                                // if it starts on the right, it heads left

        int[] negativeOrPositive = new int[] {-1,1};
        angleChange = (rand.nextInt(45)+1)*negativeOrPositive[rand.nextInt(2)];                 // angle change is an angle between -45 and 45 not including 0

        hitBox = new Rectangle(position.x, position.y, WIDTH, HEIGHT);                          // hitBox is initialized to saucer position, width, and height
    }

    public void move() {
        if(atAngle) {
            // only moves the saucer at an angle numFramesAtAngle times
            currentNumFrames++;
            if(currentNumFrames == numFramesAtAngle) {
                // reset moving at an angle settings
                currentNumFrames = 0;
                numFramesAtAngle = 0;
                currentAngle = initialAngle;
                atAngle = false;
            }
        }

        // WRAPPING AROUND SCREEN
		if(position.x+WIDTH < 0)  {																						// if on or past the left of the screen and is moving left
			if(velocity.x < 0) {
				// move saucer to the right of the screen
				position.x = GAMEWIDTH;
			}
		}
		else if (position.x > GAMEWIDTH) {																		        // if on or past the right of the screen and is moving right
			if(velocity.x > 0) {
				// move saucer to the left of the screen
				position.x = -WIDTH;
			}
		}
		else if(position.y+HEIGHT < 0) {																				// if on or past the top of the screen and is moving up
			if(velocity.y < 0) {
				// move saucer to the bottom of the screen
				position.y = GAMEHEIGHT;
			}
		} 
		else if(position.y > GAMEHEIGHT) {																		        // if on or past the bottom of the screen and is moving down
			if(velocity.y > 0) {
				// move saucer to the top of the screen
				position.y = -HEIGHT;
			}
		}
        
        velocity.x = (int) (speed * Math.cos(Math.toRadians(currentAngle)));
        velocity.y = (int) (speed * Math.sin(Math.toRadians(currentAngle)));

        // update positions
        position.x += velocity.x;
        position.y += velocity.y;
        hitBox.setLocation(position.x, position.y);

        if(rand.nextDouble() < 0.01) fire();                                                                             // 1% chance of firing a laser
    }

    public void startMovingAtAngle() {
        atAngle = true;
        numFramesAtAngle = rand.nextInt(MAXNUMFRAMESATANGLE - MINNUMFRAMESATANGLE) + MINNUMFRAMESATANGLE;                // pick a cap numFramesAtAngle between the min and max num frames
        currentAngle = (initialAngle + angleChange + 360) % 360;                                                         // keeps angle between 0 and 360
    }

    public void fire() {
        if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.fire);                                    // plays a firing sound
        AsteroidsPanel.addSaucerLaser(new Laser(position.x+(WIDTH/2), position.y+(HEIGHT/2), rand.nextInt(360)));        // adds a laser to saucer lasers
    }

    public boolean atAngle() {
        return atAngle;
    }

    public Rectangle getRect() {
        return hitBox;
    }

    public void draw(Graphics2D graphicSettings) {
        graphicSettings.translate(position.x, position.y);
        graphicSettings.drawImage(largeSaucer, position.x, position.y, null);
    }
}
