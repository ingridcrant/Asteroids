/*
Asteroids.java
Ingrid Crant
An Asteroids Arcade game make with java and JFrame
*/

import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.geom.*;
import java.io.File;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class Asteroids extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -3305616962231380099L;									// required a static final serialVersionIUD field of type long
	AsteroidsPanel game;
		
    public Asteroids() {
		super("Basic Graphics");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AsteroidsPanel game = new AsteroidsPanel();
		add(game);
		pack();
		setVisible(true);
		setResizable(false);
    }
	
    public static void main(String[] arguments) {
		Asteroids frame = new Asteroids();
    }
}

// INTERFACE
class AsteroidsPanel extends JPanel implements MouseListener, ActionListener, KeyListener{
	/**
	 *
	 */
	private static final long serialVersionUID = -8089749952073692288L;									// required a static final serialVersionIUD field of type long
	private static final Random rand = new Random();

	private static boolean isGameStarted; 																// keeps track of if user has started the game by moving

	private static int score;
	private static int level; 																			// current level (default is 1)

	private static SpaceShip spaceShip; 																// holds current SpaceShip
	private static Saucer saucer;
	private static ArrayList<Laser> lasers; 															// holds lasers
	private static ArrayList<Laser> tempLasers; 														// temporarily holds lasers to avoid deleting a laser from lasers while iterating through lasers
	private static ArrayList<Laser> saucerLasers; 														// holds saucer lasers
	private static ArrayList<Laser> saucerTempLasers; 													// temporarily holds saucer lasers to avoid deleting a laser from saucerLasers while iterating through saucerLasers
	private static ArrayList<Asteroid> Asteroids; 														// holds asteroids
	private static ArrayList<Asteroid> tempAsteroids; 													// temporarily holds asteroids to avoid deleting an asteroid from asteroids while iterating through asteroids

	private boolean spacePressed = false; 																// keeps track of an initial space press for firing the lasers
	private static int numStartingAsteroids; 															// number of initial asteroids on the screen (starts with 4)

	public static Font myFont = new Font("Courier New", 1, 30);											// sets default font

	public static String scoreUpdate; 																	// Guides the user and gives updates on their score

	private static Timer myTimer;
	private static boolean[] allKeys;																	// keeps track of all keys and if they are pressed or not
	private static boolean isSoundOn = true; 															// keeps track of if the sound is on (can be changed on intro screen)
	private static int[] usefulKeys = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE};				// loads in an array of the necessary keys for the game
	
	public static final int WIDTH=1000, HEIGHT=750; 													// width and height of game window
	public static final int SCREENWIDTH = 2*WIDTH, SCREENHEIGHT = 2*HEIGHT; 							// width and height needed to fill the background black
	private static String screen = "intro"; 															// keeps track of the current screen of the game

	private static final Image introPic = new ImageIcon("images/AsteroidsMenu.png").getImage(); 												// intro screen
	private static final Image gameOverPic = new ImageIcon("images/GameOver.png").getImage(); 													// game over screen
	private static final Image level2Pic = new ImageIcon("images/level2.png").getImage(); 														// begin level 2 screen
	
	public static final Point MINASTEROIDPOSITION = new Point(100,100), MAXASTEROIDPOSITION = new Point(WIDTH-100,HEIGHT-100); 					// range of possible asteroid positions

	public AsteroidsPanel(){
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
		addKeyListener(this);

		score = 0;
		level = 1;
		initialize();

		// loads in and sets a custom font, if not, nothing is changed
		InputStream is = AsteroidsPanel.class.getResourceAsStream("SpaceObsessed.ttf");
		try {
			myFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch(Exception e) {}

		generateAsteroids("B", true, numStartingAsteroids);

		myTimer = new Timer(100, this);
		setFocusable(true);
		requestFocus();
		myTimer.start();
 	}
	private static void newLevel() {																	// method that reinitializes components to start another level
		screen = "game";																				// we are back to the game

		initialize();
		numStartingAsteroids += level-1;																// increase the number of asteroids by 1 for each level to make the level harder

		spaceShip.increaseVelocity();																	// increase SpaceShip's speed to make the level harder
        spaceShip = new SpaceShip(usefulKeys);															// construct new spaceship
		spaceShip.regenerateLives();																	// back to 3 lives

		Asteroid.increaseSpeed();																		// increase the asteroids' speed to make the level harder
		
		generateAsteroids("B", true, numStartingAsteroids);
	}
	private static void restartGame() {																	// resets all settings to initial settings
		screen = "game";
		score = 0;
		level = 1;

		initialize();
		generateAsteroids("B", true, numStartingAsteroids);
	}
	private static void initialize() {
		isGameStarted = false;
		saucer = null;
		scoreUpdate = "Move to start!";
		numStartingAsteroids = 4;

		allKeys = new boolean[KeyEvent.KEY_LAST+1]; 														// resets all key presses (set to false by default)
		lasers = new ArrayList<Laser>();
		tempLasers = new ArrayList<Laser>();
		saucerLasers = new ArrayList<Laser>();
		saucerTempLasers = new ArrayList<Laser>();
		Asteroids = new ArrayList<Asteroid>();
		tempAsteroids = new ArrayList<Asteroid>();

        spaceShip = new SpaceShip(usefulKeys);
	}
	private static void generateAsteroids(String size, boolean isInitialAsteroid, int numStartingAsteroids) {
		boolean intersects = false;																		// keeps track of if current asteroid intersects anything
		int successfulAsteroidCount = 0; 																// keeps track of amount of successful asteroids generated

		while(successfulAsteroidCount < numStartingAsteroids) {											// while we have not generated enough successful asteroids
			intersects = false; 																		// we have not generated anything yet, so intersects is false

			Asteroid newAsteroid = new Asteroid(size);													// generates a new asteroid (every initial asteroid is big)

			if(newAsteroid.getRect().intersects(spaceShip.getRect())) intersects = true;				// if new asteroid intersects the spaceship
			for(Asteroid asteroid: tempAsteroids) { 													// iterates through already existing asteroids
				if(asteroid.getRect().intersects(newAsteroid.getRect())) intersects = true;				// if new asteroid intersects an asteroid
			}
			if(!intersects) {																			// if there were no intersections, new asteroid is valid
				successfulAsteroidCount++;
				
				// add our asteroid to our asteroid arraylists
				// add it to both if it's an initial asteroid, only add it to tempAsteroids if it's not to avoid modifying the asteroids arraylist while iterating through it
				if(isInitialAsteroid) Asteroids.add(newAsteroid);
				tempAsteroids.add(newAsteroid);
			}
		}
	}
	public static void changeScore(int change) {
		score += change;
	}
	public static int getScore() {
		return score;
	}
	public static ArrayList<Laser> getLasers() {
		return lasers;
	}
	public static void breakAsteroid(Asteroid asteroid, Laser laser, boolean isSaucerLaser) {									// method that hamdles breaking asteroids into smaller pieces

		// remove the colliding laser and asteroid from the temporary arraylists to avoid ConcurrentException Error by modifying the arraylists directly
		if(isSaucerLaser) removeSaucerLaser(laser);														// if the laser is from a saucer, remove it from the temporary saucer laser arraylist
		else removeLaser(laser);																		// if the laser is from the user, remove it from the temporary laser arraylist
		
		tempAsteroids.remove(asteroid);																	// remove the asteroid from the temporary asteroid arraylist

		if(asteroid.getSize() == "B" || asteroid.getSize() == "M") {									// if the size if the asteriod is big or medium, it can still be broken down into smaller pieces
			String asteroidSize;																		// stores the size of the asteroids broken off

			if(asteroid.getSize() == "B") {
				asteroidSize = "M";																		// if asteroid is big, it breaks apart into medium asteroids
			} else {
				asteroidSize = "S";																		// if asteroid is medium, it breaks apart into small asteroids
			}
			generateAsteroids(asteroidSize, false, 2);													// generates the broken off asteroids (there are two broken off asteroids per one asteroid)
		}
	}
	public static boolean isSoundOn() {
		return isSoundOn;
	}
	public static int getTotalNumAsteroids() {
		return Asteroids.size();
	}
	public void updateAsteroids() {																		// updates the asteroids arraylist after it's done iterating
		// clears the asteroid arraylist and copies every element from tempAsteroids onto it
		Asteroids.clear();
		for(Asteroid asteroid: tempAsteroids) {
			Asteroids.add(asteroid);
		}
	}
	public void updateLasers() {																			// updates the lasers arraylist after it's done iterating
		// clears the lasers arraylist and copies every element from tempLasers onto it
		lasers.clear();
		for(Laser laser: tempLasers) {
			lasers.add(laser);
		}
	}
	public void updateSaucerLasers() {																		// updates the saucerLasers arraylist after it's done iterating
		// clears the saucerLasers arraylist and copies every element from saucerTempLasers onto it
		saucerLasers.clear();
		for(Laser laser: saucerTempLasers) {
			saucerLasers.add(laser);
		}
	}
	public static void removeSaucer() {																		// resets saucer to null
		saucer = null;
	}
	public static void addSaucerLaser(Laser laser) {														// adds saucer laser to both temp and real saucerLaser arraylists
		saucerTempLasers.add(laser);
		saucerLasers.add(laser);
	}
	public static void removeLaser(Laser laser) {															// removes user's laser from temp laser arraylist
		tempLasers.remove(laser);
	}
	public static void removeSaucerLaser(Laser laser) {														// removes saucer's laser from temp laser arraylist
		saucerTempLasers.remove(laser);
	}
	public static BufferedImage loadBuffImg(String n) { 													// used to load BufferedImages
        try {
            return ImageIO.read(new File("Images/" + n));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
	public static void saucerLaserCollision(Laser laser) {																		// checks and implements collisions with a suacer laser
		if(laser.intersects(spaceShip.getRect())) {																				// if saucer laser collides with spaceship
			spaceShip.loseLife();																								// spaceship loses a life

			if(isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);											// if sound setting is on, play the small explosion sound
			scoreUpdate = "HIT! You lose 1 life.";																				// tell the user they were hit and lost a life
		}
		for(Asteroid asteroid: Asteroids) {
			if(laser.intersects(asteroid.getRect())) {
				AsteroidsPanel.breakAsteroid(asteroid, laser, true);															// removes the laser and breaks our asteroid into two smaller asteroids
				if(asteroid.getSize() == Asteroid.BIG) {
					if(isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.largeExplosion);									// if sound setting is on, play a large explostion
				}
				else if(asteroid.getSize() == Asteroid.MEDIUM) {
					if(isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.mediumExplosion);									// if sound setting is on, play a medium explostion
				}
				else if(asteroid.getSize() == Asteroid.SMALL) {
					if(isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.smallExplosion);									// if sound setting is on, play a small explostion
				}
			}
		}
	}

 	// Main Game Loop
	@Override
	public void actionPerformed(ActionEvent e){
		if(isGameStarted) {
			move();																						// if the game started (the user has pressed a key)
		}
		repaint();
	}
	
    public void move(){											
		if (AsteroidsPanel.getTotalNumAsteroids() == 0) {												// if all the asteroids are destroyed
			level++;																					// advance a level
			if(level == 2) {
				screen = "level 2";
				isGameStarted = false;
			} else {																					// further levels are not yet developped, so it takes you to game over
				screen = "game over";
				isGameStarted = false;																	// game is stopped
			}
		}
		if (spaceShip.getLives() == 0) {
			screen = "game over";
			isGameStarted = false;																		// game is stopped
		}

		// if game is still going
		for (Asteroid asteroid : Asteroids) {															// iterate through the asteroids
			asteroid.move(spaceShip, lasers, Asteroids, saucer);										// move the asteroid, and detect collisions
		}

		updateAsteroids();																				// update asteroids and lasers after all the collisions
		updateLasers();

		spaceShip.move(allKeys);																		// move the spaceship
		
		if(rand.nextDouble() < 0.01 && saucer == null) {												// there is a 1% chance of generating a new saucer if one is not generated yet
			saucer = new Saucer();
		}
		if(saucer != null) {
			if(rand.nextDouble() < 0.03) saucer.startMovingAtAngle();									// there is an 3% chance that the saucer starts moving at an angle if the sacuer is generated
			saucer.move();																				// move the saucer if it is generated
		}

		for (Laser laser : lasers) {																	// iterate through the lasers and move each of them
			laser.move();
		}

		for (Laser laser : saucerLasers) {																// iterate through the saucerlasers, move it, and check for collisions with it
			laser.move();
			saucerLaserCollision(laser);
		}

		updateAsteroids();																				// update asteroids and saucerLasers after saucer laser collisions
		updateSaucerLasers();
    }
    
	@Override
    public void paint(Graphics g) {
		if(screen == "intro") {
			g.drawImage(introPic, -2, 0, null);																// draw the intro background (drawn at (-2,0) to fill the screen completely)

			// draws a green rectangle outline around the sound setting currently selected
			g.setColor(Color.GREEN);
			if(isSoundOn) {
				g.drawRect(115, 380, 386-115, 436-380);														// draws sound on rectangle outline
			}
			else {
				g.drawRect(590, 375, 910-596, 436-380);														// draws sound off rectangle outline
			}
		}
		if(screen == "level 2") {
			g.drawImage(level2Pic, -2, 0, null);															// draws level 2 background (drawn at (-2,0) to fill the screen completely)
		}
		if(screen == "game") {
			Graphics2D graphicSettings = (Graphics2D) g;
			AffineTransform identity = new AffineTransform();												// need AffineTransform to rotate the spaceShip

			// Fill the background width black the height and width of the game board
			graphicSettings.setTransform(identity);
			graphicSettings.setColor(Color.BLACK);
			graphicSettings.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);
			
			// Set the rendering rules
			graphicSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphicSettings.setPaint(Color.WHITE);

			// draw the score, the score update, and the number of lives to the screen
			graphicSettings.setFont(myFont.deriveFont(Font.BOLD, 30f));
			graphicSettings.drawString("Score: "+score, 50, 50);
			graphicSettings.drawString(scoreUpdate, 550, 50);
			graphicSettings.drawString("Lives: "+spaceShip.getLives(), 1800, 50);
			
			// Cycle through all asteroids in asteroids ArrayList
			for(Asteroid asteroid : Asteroids) {
				// reset AffineTransform and graphics settings
				identity = new AffineTransform();
				graphicSettings.setTransform(identity);

				asteroid.draw(graphicSettings); 														// draw the asteroid on the screen
			}

			// reset AffineTransform and graphics settings
			identity = new AffineTransform();
			graphicSettings.setTransform(identity);

			spaceShip.draw(graphicSettings);															// draw the spaceShip on the screen
			
			// reset AffineTransform and graphics settings
			identity = new AffineTransform();
			graphicSettings.setTransform(identity);

			if(saucer != null) saucer.draw(graphicSettings);

			// iterate through lasers
			// user's lasers are green
			graphicSettings.setPaint(Color.GREEN);
			for (Laser laser : lasers) {
				// reset AffineTransform and graphics settings
				identity = new AffineTransform();
				graphicSettings.setTransform(identity);

				laser.draw(graphicSettings);															// draw the laser on the screen
			}

			// iterate through saucer lasers
			// saucer lasers are red
			graphicSettings.setPaint(Color.RED);
			for (Laser laser : saucerLasers) {
				// reset AffineTransform and graphics settings
				identity = new AffineTransform();
				graphicSettings.setTransform(identity);

				laser.draw(graphicSettings);															// draw the laser on the screen
			}
		}
		if (screen == "game over") {
			g.drawImage(gameOverPic, -1, 0, null);															// draws game over background (drawn at (-1,0) to fill the screen completely)

			// draws the score to the screen
			g.setColor(Color.WHITE);
			g.setFont(myFont.deriveFont(Font.BOLD, 60f));
			g.drawString("Score: "+score, 350, 480);
		}
	}

	@Override
	public void	mousePressed(MouseEvent e){
		if(screen != "game") {																				// gets mouse location (needed to press areas on the intro and level 2 screen)
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
			int mouseX = mouse.x-offset.x;
			int mouseY = mouse.y-offset.y;

			if(screen == "intro") {
				Point soundOnTopLeftCorner = new Point(115, 380), soundOnBottomRightCorner = new Point(382, 429);
				Point soundOffTopLeftCorner = new Point(596, 380), soundOffBottomRightCorner = new Point(894, 429);
				Point startGameTopLeftCorner = new Point(286, 506), startGameBottomRightCorner = new Point(751, 568);
	
				if(soundOnTopLeftCorner.x < mouseX && mouseX < soundOnBottomRightCorner.x && soundOnTopLeftCorner.y < mouseY && mouseY < soundOnBottomRightCorner.y) {					// if the user clicks the sound on area
					isSoundOn = true;
				}
				else if(soundOffTopLeftCorner.x < mouseX && mouseX < soundOffBottomRightCorner.x && soundOffTopLeftCorner.y < mouseY && mouseY < soundOffBottomRightCorner.y) {			// if the user clicks the sound off area
					isSoundOn = false;
				}
				else if(startGameTopLeftCorner.x < mouseX && mouseX < startGameBottomRightCorner.x && startGameTopLeftCorner.y < mouseY && mouseY < startGameBottomRightCorner.y) {		// if the user clicks the start game area
					screen = "game";
				}
			}
			if(screen == "game over") {
				Point restartTopLeftCorner = new Point(335, 595), restartBottomRightCorner = new Point(673, 654);
				if(restartTopLeftCorner.x < mouseX && mouseX < restartBottomRightCorner.x && restartTopLeftCorner.y < mouseY && mouseY < restartBottomRightCorner.y) {					// if the user clicks the sound on area
					restartGame();
				}
			}
			if(screen == "level 2") {
				Point startLevelTopLeftCorner = new Point(298, 401), startLevelBottomRightCorner = new Point(708, 458);

				if(startLevelTopLeftCorner.x < mouseX && mouseX < startLevelBottomRightCorner.x && startLevelTopLeftCorner.y < mouseY && mouseY < startLevelBottomRightCorner.y) {		// if the user clicks the start level area
					if(isSoundOn) SoundPlayer.playSoundEffect(SoundPlayer.beat2);
					newLevel();
					isGameStarted = true;
				}
			}
		}
	}

	public void	mouseClicked(MouseEvent e){}
	public void	mouseEntered(MouseEvent e){}
	public void	mouseExited(MouseEvent e){}
	public void	mouseReleased(MouseEvent e){}
	
	public void	keyPressed(KeyEvent e){
		if(screen == "game") {	
			isGameStarted = true;																											// starts the game if a key is pressed
			if(spaceShip.hasMoved() && scoreUpdate == "Move to start!") {																	// if the screen is on the game and the player has moved the spaceShip
				scoreUpdate = "";																											// remove the move prompt to the user																										// start the game
			}
			allKeys[e.getKeyCode()] = true;

			if(!spacePressed && e.getKeyCode() == KeyEvent.VK_SPACE && lasers.size() < 4) {													// if spacepressed is false, the space bar is currently pressed, and there are fewer than 4 lasers on the screen
				if(isSoundOn()) SoundPlayer.playSoundEffect(SoundPlayer.fire);																// play the fire sound effect if the sound setting is on
				
				Laser templaser = new Laser((int) spaceShip.getShipNoseX(), (int) spaceShip.getShipNoseY(), spaceShip.getRotationAngle());				// generate new laser
				
				// add the laser to the laser arraylists
				lasers.add(templaser);
				tempLasers.add(templaser);

				spacePressed = true;																										// spacepressed is true (user is prevented from firing multiple lasers by holding down the space bar)
			}
		}
	}
	public void	keyReleased(KeyEvent e){
		allKeys[e.getKeyCode()] = false;
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {																							// if the user released the space key, reset spacepressed to false
			spacePressed = false;
		}
	}
		
	public void	keyTyped(KeyEvent e){}
}