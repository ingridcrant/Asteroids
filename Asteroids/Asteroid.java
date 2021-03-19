/*
Asteroid.java
Ingrid Crant
An asteroid class for asteroids arcade game. Moves around the screen, and can be destroyed/broken apart by lasers.
*/

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Asteroid extends Polygon {
	/**
	 *
	 */
	private static final long serialVersionUID = 2789197000084170003L;																			// required a static final serialVersionIUD field of type long

	private Point velocity = new Point();
	private static int speed = 8;

	public static final int MAXBIGASTEROIDRADIUS = 100, MINBIGASTEROIDRADIUS = 85; 																// range of possible big asteroid radius'
	public static final int MAXMEDIUMASTEROIDRADIUS = 80, MINMEDIUMASTEROIDRADIUS = 65; 														// range of possible medium asteroid radius'
	public static final int MAXSMALLASTEROIDRADIUS = 35, MINSMALLASTEROIDRADIUS = 20;															// range of possible small asteroid radius'
	public static final Point MINASTEROIDPOSITION = new Point(100,100), MAXASTEROIDPOSITION = new Point(AsteroidsPanel.WIDTH-100, AsteroidsPanel.HEIGHT-100); 					// range of possible asteroid positions

	public static final int maxNumSides = 15, minNumSides = 5;																					// asteroid can have anywhere between 5 and 15 sides
	
	private Rectangle hitBox;																													// rectangle object surrounding the asteroid. used to detect collisions.
	
	private Point center;																														// stores the center of the asteroid
	private String size;																														// stores the size of the asteroid
    public static final String SMALL = "S", MEDIUM = "M", BIG = "B";

	public Asteroid(String size) {
		super(); // Call the constructor of Polygon

		Random rand = new Random();

		center = new Point(rand.nextInt(MAXASTEROIDPOSITION.x-MINASTEROIDPOSITION.x)+MINASTEROIDPOSITION.x,										// generates a random x-coordinate within the possible range
							rand.nextInt(MAXASTEROIDPOSITION.y-MINASTEROIDPOSITION.y)+MINASTEROIDPOSITION.y);									// generates a random Y-coordinate within the possible range
		
		// calculates the minimum and maximum radius of the asteroid based on its siize
		int minRadius, maxRadius;
		if(size == "B") {
			minRadius = MINBIGASTEROIDRADIUS; 
			maxRadius = MAXBIGASTEROIDRADIUS;
		} else if(size == "M") {
			minRadius = MINMEDIUMASTEROIDRADIUS; 
			maxRadius = MAXMEDIUMASTEROIDRADIUS;
		} else {
			minRadius = MINSMALLASTEROIDRADIUS; 
			maxRadius = MAXSMALLASTEROIDRADIUS;
		}

		int numSides = rand.nextInt(maxNumSides-minNumSides)+minNumSides;																		// generates a random number of sides within the possible range of the number of sides																					// generates a random number of sides from 5 to 15

		double angleStep = Math.PI * 2 / numSides;																								// the central angle of the asteroid (a full rotation of a circle divided by the number of sides)
		int biggestRadius = 0;																													// keeps track of the largest radius generated

		for(int i = 0; i < numSides; ++i) {
			double targetAngle = angleStep * i; 																								// the angle if all parts are equally spaced
			double angle = targetAngle + (rand.nextDouble() - 0.5) * angleStep * 0.25; 															// generate a random angle by multiplying a random factor to the angle which is +/- 25% of the angle step
			double radius = minRadius + rand.nextDouble() * (maxRadius - minRadius); 															// generate random radius between minRadius and maxRadius
			if(radius > biggestRadius) biggestRadius = (int) radius;																			// if generated radius is larger than our biggest radius, replace the value of biggest radius with the generated radius

			// calculate x and y positions and add the point to our asteroid polygon
			addPoint((int) (Math.cos(angle) * radius),(int) (Math.sin(angle) * radius));
		}

		this.translate(center.x, center.y);																										// move the asteroid to its center
		hitBox = new Rectangle(center.x-(biggestRadius/2), center.y-(biggestRadius/2), biggestRadius, biggestRadius);							// a square surrounding the asteroid and at the same position. used to detect collisions with other objects.
		
		this.size = size;
		
		// Set the x and y direction speeds to a random int between (-1)*speed and (-3/4)*speed, and the range between (3/4)*speed and speed
		boolean validDirection = false;
		while(!validDirection) {
			int randomX = (int) (Math.random() * speed);
			int randomY = (int) (Math.random() * speed);
			if((randomX < (speed/4) || randomX > (3*speed/4)) && (randomY < (speed/4) || randomY > (3*speed/4))) {
				validDirection = true;
				this.velocity.x = randomX - (speed/2);
				this.velocity.y = randomY - (speed/2);
			}
		}
	}
	public String getSize() {
		return size;
	}
	public static void increaseSpeed() {
		speed += 8;
	}
	public Rectangle getRect() {
		return hitBox;
	}

	public void move(SpaceShip spaceShip, ArrayList<Laser> lasers, ArrayList<Asteroid> Asteroids, Saucer saucer) {												// move to asteroids and check for collisions
		// check for collisions between asteroids and make them bouce off each other
		for(Asteroid asteroid: Asteroids) {																												// iterate through asteroids
			if(asteroid != this && asteroid.getRect().intersects(getRect())) {																	// if the hitBox rectangles collide with each other, that means they collide
				// switch the first asteroid's x velocity with the second asteroid's x velocity
				// equivalently, switch the first asteroid's y velocity with the second asteroid's y velocity
				int tempx = asteroid.getXVelocity();
				int tempy = asteroid.getYVelocity();
				asteroid.setXVelocity(this.getXVelocity());
				asteroid.setYVelocity(this.getYVelocity());
				this.setXVelocity(tempx);
				this.setYVelocity(tempy);
			}
		}
		
		// WRAPPING AROUND SCREEN
		if(center.x < 0)  {																														// if on or past the left of the screen and is moving left
			if(velocity.x < 0) {
				// move asteroid to the right of the screen
				int ogy = center.y;
				center.y = AsteroidsPanel.HEIGHT-center.y;
				center.x += AsteroidsPanel.WIDTH;
				this.translate(AsteroidsPanel.WIDTH,center.y-ogy);
				hitBox.translate(AsteroidsPanel.WIDTH,center.y-ogy);
			}
		}
		else if (center.x > AsteroidsPanel.WIDTH) {																								// if on or past the right of the screen and is moving right
			if(velocity.x > 0) {
				// move laser to the left of the screen
				int ogy = center.y;
				center.y = AsteroidsPanel.HEIGHT-center.y;
				center.x -= AsteroidsPanel.WIDTH;
				this.translate(-AsteroidsPanel.WIDTH,center.y-ogy);
				hitBox.translate(-AsteroidsPanel.WIDTH,center.y-ogy);
			}
		}
		else if(center.y < 0) {																													// if on or past the top of the screen and is moving up
			if(velocity.y < 0) {
				// move laser to the bottom of the screen
				int ogX = center.x;
				center.x = AsteroidsPanel.WIDTH-center.x;
				center.y += AsteroidsPanel.HEIGHT;
				this.translate(center.x-ogX,AsteroidsPanel.HEIGHT);
				hitBox.translate(center.x-ogX,AsteroidsPanel.HEIGHT);
			}
		} 
		else if(center.y > AsteroidsPanel.HEIGHT) {																								// if on or past the bottom of the screen and is moving down
			if(velocity.y > 0) {
				// move laser to the top of the screen
				int ogX = center.x;
				center.x = AsteroidsPanel.WIDTH-center.x;
				center.y -= AsteroidsPanel.HEIGHT;
				this.translate(center.x-ogX,-AsteroidsPanel.HEIGHT);
				hitBox.translate(center.x-ogX,-AsteroidsPanel.HEIGHT);
			}
		}

		// move the x and y coordinate of the asteroid's center, the asteroid, and the hitBox by the respective x and y velocity
		center.x += velocity.x;
		center.y += velocity.y;
		this.translate(velocity.x,  velocity.y);
		hitBox.translate(velocity.x,  velocity.y);

		if(spaceShip.hasMoved()) {																												// if the user has moved the spaceship (avoids losing a life if the user has not moved the spaceship)
			if(getRect().intersects(spaceShip.getRect())) {																						// if the asteroid's boundary intersects the ship's boundary
				spaceShip.loseLife();

				if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);											// if sound setting is on, play the small explosion sound
				AsteroidsPanel.scoreUpdate = "HIT! You lose 1 life.";																			// tells the user that they were hit and they lost a life
			}
			if(saucer != null) {
				if(saucer.getRect().intersects(spaceShip.getRect())) {																				// if the saucer intersects the ship
					spaceShip.loseLife();
					AsteroidsPanel.removeSaucer();
	
					if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);											// if sound setting is on, play the small explosion sound
					AsteroidsPanel.scoreUpdate = "HIT! You lose 1 life.";																			// tells the user that they were hit and they lost a life
				}
			}
		}
		
		// check for collision between the lasers and our asteroid
		for(Laser laser : lasers) {
			if(getRect().intersects(laser)) {																									// if our hitBox intersects with a laser, we have a collision between the two
				AsteroidsPanel.breakAsteroid(this, laser, false);																				// removes the laser and breaks our asteroid into two smaller asteroids
				if(getSize() == BIG) {
					if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.largeExplosion);										// if sound setting is on, play a large explostion
					AsteroidsPanel.changeScore(20);																								// breaking a big asteroid is worth 20 points
					AsteroidsPanel.scoreUpdate = "You destroyed a big asteroid! You gain 20 points.";											// tell the user they broke a big asteroid and their score increase
				}
				else if(getSize() == MEDIUM) {
					if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.mediumExplosion);									// if sound setting is on, play a medium explostion
					AsteroidsPanel.changeScore(50);																								// breaking a medium asteroid is worth 50 points
					AsteroidsPanel.scoreUpdate = "You destroyed a medium asteroid! You gain 50 points.";										// tell the user they broke a medium asteroid and their score increase
				}
				else if(getSize() == SMALL) {
					if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);										// if sound setting is on, play a small explostion
					AsteroidsPanel.changeScore(100);																							// breaking a small asteroid is worth 50 points
					AsteroidsPanel.scoreUpdate = "You destroyed a small asteroid! You gain 100 points.";										// tell the user they broke a small asteroid and their score increase
				}
			}
			if(saucer != null) {
				if(laser.intersects(saucer.getRect())) {																			// if the saucer is hit by a laser
					AsteroidsPanel.removeLaser(laser);
					AsteroidsPanel.removeSaucer();
					
					if(AsteroidsPanel.isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);										// if sound setting is on, play the small explosion sound
					AsteroidsPanel.changeScore(200);																							// hitting a saucer is worth 200 points
					AsteroidsPanel.scoreUpdate = "You destroyed a saucer! You gain 200 points.";												// tells the user that they were hit and they lost a life
				}
			}
		}
	}
	public int getXVelocity() {
		return velocity.x;
	}
	public int getYVelocity() {
		return velocity.y;
	}
	public void setXVelocity(int xVel) {
		velocity.x = xVel;
	}
	public void setYVelocity(int yVel) {
		velocity.y = yVel;
	}
    public void draw(Graphics2D graphicSettings) {
		graphicSettings.translate(center.x, center.y);																							// move our asteroid to its center on the screen
		graphicSettings.draw(this);																												// draw the asteroid
    }
}