import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.event.*;

/**
 * The grid for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionGrid1 extends JPanel
{
	// Set up variables
	private final int IMAGE_WIDTH, IMAGE_HEIGHT;

	private Image gridImages[];
	private Image playerImages[];

	Player playerOne = new Player();
	Player playerTwo = new Player();

	private Image border;

	int playerOnePos = 0;
	int playerTwoPos = 4;

	private int[][] grid;
	private int currentRowOne, currentColOne, currentRowTwo, currentColTwo;

	final int EMPTY, TIRE, MENTOS, CAN, BUILDING, CRATE, REDCAN, BLUECAN,
			EXPLOSION;

	Timer[] explosion;

	private int[] canRows, canCols;
	
	private final Image mainMenu, startMenu, instructions1, instructions2, story;

	private Rectangle START_BUTTON, STORY_BUTTON, BACK_MENU_BUTTON,
			INSTRUCTIONS_BUTTON, PAGE_BUTTON, ONE_PLAYER,
			TWO_PLAYERS, CLASSIC, SHOWDOWN, PLAY_BUTTON;
	
	private final int GAME = -1;
	private final int MAIN_MENU = 1;
	private final int START_MENU = 2;
	private final int STORY = 3;
	private final int INSTRUCTIONS_1 = 4;
	private final int INSTRUCTIONS_2 = 5;
	private int start = 0;
	Font font = new Font("Serif", Font.BOLD, 12);
	
	private Rectangle NO_OF_ROUNDS[] = new Rectangle [10];
	private int menu = MAIN_MENU;
	private int noOfPlayers = 0;
	private int noOfRounds = 0;
	private int mapType = 0;

	/**
	 * Constructs a new grid
	 */
	public SodasplosionGrid1()
	{
		explosion = new Timer[10];
		canRows = new int[10];
		canCols = new int[10];

		// Loads up the player and breakable block images
		playerImages = new Image[8];
		for (int imageNo = 0; imageNo < 4; imageNo++)
		{
			playerImages[imageNo] = new ImageIcon("RedTruck" + imageNo + ".png")
					.getImage();
		}
		for (int imageNo = 4; imageNo < 8; imageNo++)
		{
			playerImages[imageNo] = new ImageIcon("BlueTruck" + imageNo
					+ ".png").getImage();
		}

		// Sets up the timers for each explosion
		for (int timer = 0; timer < explosion.length / 2; timer++)
		{
			explosion[timer] = new Timer(1000, new TimerEventHandler(playerOne,
					timer));
		}
		for (int timer = explosion.length / 2; timer < explosion.length; timer++)
		{
			explosion[timer] = new Timer(1000, new TimerEventHandler(playerTwo,
					timer));
		}

		// Loads up the grid images
		// Leaves gridImages[0] blank so that the default image
		// for each tile is nothing
		gridImages = new Image[9];
		EMPTY = 0;
		gridImages[1] = new ImageIcon("Tire.png").getImage();
		TIRE = 1;
		gridImages[2] = new ImageIcon("Mentos.png").getImage();
		MENTOS = 2;
		gridImages[3] = new ImageIcon("Can.png").getImage();
		CAN = 3;
		gridImages[4] = new ImageIcon("Building.png").getImage();
		BUILDING = 4;
		gridImages[5] = new ImageIcon("Crate.png").getImage();
		CRATE = 5;
		gridImages[6] = new ImageIcon("RedCan.png").getImage();
		REDCAN = 6;
		gridImages[7] = new ImageIcon("BlueCan.png").getImage();
		BLUECAN = 7;
		gridImages[8] = new ImageIcon("Explosion.png").getImage();
		EXPLOSION = 8;
		border = new ImageIcon("Border.png").getImage();

		// Starts a new game and loads up the grid (sets size of grid array)
		newGame();

		// Set the image height and width based on the path image size
		// Also sizes this panel based on the image and grid size
		IMAGE_WIDTH = 64;
		IMAGE_HEIGHT = 64;
		Dimension size = new Dimension(1024, 768);
		this.setPreferredSize(size);
		
		//Get all the menu images
		mainMenu = new ImageIcon("MainMenu.png").getImage();
		startMenu = new ImageIcon("StartMenu.png").getImage();
		instructions1 = new ImageIcon("Instructions1.png").getImage();
		instructions2 = new ImageIcon("Instructions2.png").getImage();
		story = new ImageIcon("Story.png").getImage();
		
		//Initialize the rectangles
		START_BUTTON = new Rectangle (170, 453, 150, 40);
		STORY_BUTTON = new Rectangle (366, 453, 150, 40);
		INSTRUCTIONS_BUTTON = new Rectangle (568, 453, 285, 40);
		BACK_MENU_BUTTON = new Rectangle (760, 723, 250, 35);
		PAGE_BUTTON = new Rectangle (640, 723, 100, 35);
		ONE_PLAYER = new Rectangle (45, 78, 150, 150);
		TWO_PLAYERS = new Rectangle (220, 78, 150, 150);
		CLASSIC = new Rectangle (560, 73, 150, 150);
		SHOWDOWN = new Rectangle (787, 73, 150, 150);
		PLAY_BUTTON = new Rectangle (265, 518, 530, 150);
		for (int round = 1; round <= 9; round++)	
			NO_OF_ROUNDS[round] = new Rectangle (82 + 100*(round-1), 408, 60, 60);

		// Add mouse listeners to the panel
		this.addMouseListener(new MouseHandler());

		// Sets up for keyboard input (arrow keys) on this panel
		this.setFocusable(true);
		this.addKeyListener(new KeyHandler());
		this.requestFocusInWindow();
	}

	/**
	 * Resets the grid to prepare for a new game
	 */
	public void newGame()
	{
		// Initial position of the player
		currentRowOne = 0;
		currentColOne = 0;
		currentRowTwo = 10;
		currentColTwo = 12;

		// Declares number of rows and number of columns
		int noOfRows = 11;
		int noOfColumns = 13;

		// Set up the array
		grid = new int[noOfRows][noOfColumns];

		// Adds the buildings to the grid
		for (int row = 1; row < grid.length; row++)
		{
			for (int column = 1; column < grid[0].length; column++)
			{
				if (row % 2 != 0 && column % 2 != 0)
				{
					grid[row][column] = BUILDING;
				}
			}
		}

		// Adds the crates to the grid
		for (int row = 0; row < grid.length; row++)
		{
			for (int column = 0; column < grid[0].length; column++)
			{
				if (grid[row][column] != BUILDING
						&& (row > 1 || column > 1)
						&& (row < 9 || column < 11)
						&& Math.random() * 10 <= 7.5)
				{
					grid[row][column] = CRATE;
				}
			}
		}
	}

	public void placeCan(Player player, int canRow, int canCol)
	{
		if (player.getCurrentCans() > 0)
		{
			player.placeCan();

			int currentCanPos = player.getCurrentCans();

			// Sets the current can's row and column and starts the explosion
			// timer
			if (player == playerOne)
			{
				grid[canRow][canCol] = REDCAN;
				canRows[currentCanPos] = canRow;
				canCols[currentCanPos] = canCol;
				explosion[currentCanPos].start();
			}
			else
			{
				grid[canRow][canCol] = BLUECAN;
				canRows[currentCanPos + 5] = canRow;
				canCols[currentCanPos + 5] = canCol;
				explosion[currentCanPos + 5].start();
			}
		}
	}

	/**
	 * An inner class to deal with the timer events
	 */
	private class TimerEventHandler implements ActionListener
	{
		int whichExplosion;
		Player player;

		/**
		 * Constructor for the TimerEventHandler
		 * 
		 * @param player the given player
		 * @param whichExplosion the given explosion
		 */
		public TimerEventHandler(Player player, int whichExplosion)
		{
			this.player = player;
			this.whichExplosion = whichExplosion;
		}

		/**
		 * Acts upon a fired timer Explodes the can and checks for collisions
		 */
		public void actionPerformed(ActionEvent event)
		{
			int range = player.getRange();
			int currentCanRow = canRows[whichExplosion];
			int currentCanCol = canCols[whichExplosion];

			grid[currentCanRow][currentCanCol] = EMPTY;
			
			boolean alreadyHitSomething = false;
			
			// Collision code for the upwards direction
			for (int upPos = 1; upPos <= range
					&& currentCanRow - upPos >= 0
					&& grid[currentCanRow - upPos][currentCanCol] != BUILDING
					&& !alreadyHitSomething; upPos++)
			{
				if (grid[currentCanRow - upPos][currentCanCol] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow - upPos][currentCanCol] = item;
					}
					else
					{
						grid[currentCanRow - upPos][currentCanCol] = EMPTY;
					}
					
					alreadyHitSomething = true;
				}
			}
			
			alreadyHitSomething = false; 
			
			// Collision code for the downwards direction
			for (int downPos = 1; downPos <= range
					&& currentCanRow + downPos < grid.length
					&& grid[currentCanRow + downPos][currentCanCol] != BUILDING
					&& !alreadyHitSomething; downPos++)
			{
				if (grid[currentCanRow + downPos][currentCanCol] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow + downPos][currentCanCol] = item;
					}
					else
					{
						grid[currentCanRow + downPos][currentCanCol] = EMPTY;
					}

					alreadyHitSomething = true;
				}
			}
			
			alreadyHitSomething = false; 
			
			// Collision code for the left direction
			for (int leftPos = 1; leftPos <= range
					&& currentCanCol - leftPos >= 0
					&& grid[currentCanRow][currentCanCol - leftPos] != BUILDING
					&& !alreadyHitSomething; leftPos++)
			{
				if (grid[currentCanRow][currentCanCol - leftPos] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow][currentCanCol - leftPos] = item;
					}
					else
					{
						grid[currentCanRow][currentCanCol - leftPos] = EMPTY;
					}

					alreadyHitSomething = true;
				}
			}

			alreadyHitSomething = false; 
			
			// Collision code for the right direction
			for (int rightPos = 1; rightPos <= range
					&& currentCanCol + rightPos < grid[0].length
					&& grid[currentCanRow][currentCanCol + rightPos] != BUILDING
					&& !alreadyHitSomething; rightPos++)
			{
				if (grid[currentCanRow][currentCanCol + rightPos] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow][currentCanCol + rightPos] = item;
					}
					else
					{
						grid[currentCanRow][currentCanCol + rightPos] = EMPTY;
					}

					alreadyHitSomething = true;
				}
			}

			// Resets the timer and returns the can to the given player
			player.returnCan();
			explosion[whichExplosion].stop();
			repaint();
		}
	}

	/**
	 * Repaint the drawing panel
	 * 
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
g.setColor(Color.WHITE);
		
		if (menu == GAME)
		{
			g.drawImage(border, 128, 0, this);

			// Redraw the grid with current images
			for (int row = 0; row < grid.length; row++)
			{
				for (int column = 0; column < grid[0].length; column++)
				{
					g.drawImage(gridImages[grid[row][column]], column * IMAGE_WIDTH
							+ 160,
							row * IMAGE_HEIGHT + 32, this);
				}
			}
			
			// Draw the moving player on top of the grid
			g.drawImage(playerImages[playerOnePos],
					currentColOne * IMAGE_WIDTH + 160,
					currentRowOne * IMAGE_HEIGHT + 32, this);
			
			g.drawImage(playerImages[playerTwoPos],
					currentColTwo * IMAGE_WIDTH + 160,
					currentRowTwo * IMAGE_HEIGHT + 32, this);
		}
		else if(menu == MAIN_MENU)
		{
			g.drawImage(mainMenu, 0, 0, this);
		}
		else if (menu == START_MENU)
		{
			g.drawImage(startMenu, 0, 0, this);
			if (!canPlay())
			{
				g.setFont(font);
				g.drawString("PLEASE SELECT GAME SETTINGS", 430, 515);
			}
			if (noOfPlayers == 1)
				g.drawRect(40, 73, 160, 160);
			else if (noOfPlayers == 2)
				g.drawRect(215, 73, 160, 160);
			
			if (mapType == 1)
				g.drawRect(555, 68, 160, 160);
			else if (mapType == 2)
				g.drawRect(782, 68, 160, 160);
			
			if (noOfRounds > 0)
				g.drawRect(100*(noOfRounds-1) + 77, 403, 70, 70);
		}
		else if (menu == STORY)
		{
			g.drawImage(story, 0, 0, this);
		}
		else if (menu == INSTRUCTIONS_1)
		{
			g.drawImage(instructions1, 0, 0, this);
		}
		else if (menu == INSTRUCTIONS_2)
		{
			g.drawImage(instructions2, 0, 0, this);
		}
		
	}
	
	/**
	 * Checks if all settings (map type, number of rounds, and number of players) has been selected
	 * @return whether or not the game can start depending on if the settings have been set
	 */
	private boolean canPlay()
	{
		if (noOfPlayers > 0 && noOfRounds > 0 && mapType > 0)
			return true;
		else 
			return false;
	}
	
	/**
	 * Handles mouse clicks
	 * @author Amy Zhang
	 */
	private class MouseHandler extends MouseAdapter {	

		/**
		 * Responds to a mousePressed event
		 * @param event Information about the the mouse when its button was
		 *            pressed.
		 */
		public void mousePressed(MouseEvent event)
		{
			Point pressed = event.getPoint();
			
			//Check menu the screen is currently on
			if (menu != MAIN_MENU)
			{
				//Go to main menu if back to main menu button is pressed
				if (BACK_MENU_BUTTON.contains(pressed))
					menu = MAIN_MENU;
				
				//When on the start menu, set game variables based buttons clicked on mouse
				if (menu == START_MENU)
				{
					//Respond if any of the buttons on the start screen were pressed
					//(Number of players, map type, and number of rounds)
					if (ONE_PLAYER.contains(pressed))
					{
						noOfPlayers = 1;
					}
					else if (TWO_PLAYERS.contains(pressed))
					{
						noOfPlayers = 2;
					}
					else if (CLASSIC.contains(pressed))
					{
						mapType = 1;
					}
					else if (SHOWDOWN.contains(pressed))
					{
						mapType = 2;
					}
					else 
					{
						for (int round = 1; round <= 9; round++)
							if (NO_OF_ROUNDS[round].contains(pressed))
								noOfRounds = round;
					}
					
					//If a player has selected all the settings, start game
					if (canPlay() && PLAY_BUTTON.contains(pressed))
					{
						start = 1;
						menu = GAME;
					}
				}
				//Allow user to change pages between the instruction menus
				else if (PAGE_BUTTON.contains(pressed))
				{
					if (menu == INSTRUCTIONS_1)
						menu = INSTRUCTIONS_2;
					else if (menu == INSTRUCTIONS_2)
						menu = INSTRUCTIONS_1;
				}
			}
			//When on the main menu, go to appropriate screen when button is clicked
			else
			{
				if (START_BUTTON.contains(pressed))
					menu = START_MENU;
				else if (STORY_BUTTON.contains(pressed))
					menu = STORY;
				else if (INSTRUCTIONS_BUTTON.contains(pressed))
					menu = INSTRUCTIONS_1;
				//setCursor(Cursor.getDefaultCursor()); 
			}
			repaint();
		}
	}
	
	// Inner class to handle key events
	private class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent event)
		{
			// Change the currentRow and currentColumn of the first player
			// based on the key pressed
			// If the player is trying to move, make sure player cannot move
			// past border or go through an unbreakable block
			if (event.getKeyCode() == KeyEvent.VK_A && currentColOne > 0
					&& grid[currentRowOne][currentColOne - 1] != BUILDING
					&& grid[currentRowOne][currentColOne - 1] != CRATE
					&& grid[currentRowOne][currentColOne - 1] != REDCAN
					&& grid[currentRowOne][currentColOne - 1] != BLUECAN)
			{
				currentColOne--;
				playerOnePos = 0;
			}
			else if (event.getKeyCode() == KeyEvent.VK_D
					&& currentColOne < grid[0].length - 1
					&& grid[currentRowOne][currentColOne + 1] != BUILDING
					&& grid[currentRowOne][currentColOne + 1] != CRATE
					&& grid[currentRowOne][currentColOne + 1] != REDCAN
					&& grid[currentRowOne][currentColOne + 1] != BLUECAN)
			{
				currentColOne++;
				playerOnePos = 2;
			}
			else if (event.getKeyCode() == KeyEvent.VK_W && currentRowOne > 0
					&& grid[currentRowOne - 1][currentColOne] != BUILDING
					&& grid[currentRowOne - 1][currentColOne] != CRATE
					&& grid[currentRowOne - 1][currentColOne] != REDCAN
					&& grid[currentRowOne - 1][currentColOne] != BLUECAN)
			{
				currentRowOne--;
				playerOnePos = 1;
			}
			else if (event.getKeyCode() == KeyEvent.VK_S
					&& currentRowOne < grid.length - 1
					&& grid[currentRowOne + 1][currentColOne] != BUILDING
					&& grid[currentRowOne + 1][currentColOne] != CRATE
					&& grid[currentRowOne + 1][currentColOne] != REDCAN
					&& grid[currentRowOne + 1][currentColOne] != BLUECAN)
			{
				currentRowOne++;
				playerOnePos = 3;
			}
			// Places a can
			else if (event.getKeyCode() == KeyEvent.VK_Q)
			{
				placeCan(playerOne, currentRowOne, currentColOne);
			}

			// Checks to see if the player is standing on a power-up
			if (grid[currentRowOne][currentColOne] == TIRE ||
					grid[currentRowOne][currentColOne] == MENTOS ||
					grid[currentRowOne][currentColOne] == CAN)
			{
				playerOne.addPower(grid[currentRowOne][currentColOne]);
				grid[currentRowOne][currentColOne] = EMPTY;
			}

			// Change the currentRow and currentColumn of the second player
			// based on the key pressed
			// If the player is trying to move, make sure player cannot move
			// past border or go through an unbreakable block
			if (event.getKeyCode() == KeyEvent.VK_LEFT && currentColTwo > 0
					&& grid[currentRowTwo][currentColTwo - 1] != BUILDING
					&& grid[currentRowTwo][currentColTwo - 1] != CRATE
					&& grid[currentRowTwo][currentColTwo - 1] != REDCAN
					&& grid[currentRowTwo][currentColTwo - 1] != BLUECAN)
			{
				currentColTwo--;
				playerTwoPos = 4;
			}
			else if (event.getKeyCode() == KeyEvent.VK_RIGHT
					&& currentColTwo < grid[0].length - 1
					&& grid[currentRowTwo][currentColTwo + 1] != BUILDING
					&& grid[currentRowTwo][currentColTwo + 1] != CRATE
					&& grid[currentRowTwo][currentColTwo + 1] != REDCAN
					&& grid[currentRowTwo][currentColTwo + 1] != BLUECAN)
			{
				currentColTwo++;
				playerTwoPos = 6;
			}
			else if (event.getKeyCode() == KeyEvent.VK_UP && currentRowTwo > 0
					&& grid[currentRowTwo - 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo - 1][currentColTwo] != CRATE
					&& grid[currentRowTwo - 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo - 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo--;
				playerTwoPos = 5;
			}
			else if (event.getKeyCode() == KeyEvent.VK_DOWN
					&& currentRowTwo < grid.length - 1
					&& grid[currentRowTwo + 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo + 1][currentColTwo] != CRATE
					&& grid[currentRowTwo + 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo + 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo++;
				playerTwoPos = 7;
			}
			// Places a can
			else if (event.getKeyCode() == KeyEvent.VK_SLASH)
			{
				placeCan(playerTwo, currentRowTwo, currentColTwo);
			}

			// Checks to see if the player is standing on a power-up
			if (grid[currentRowTwo][currentColTwo] == TIRE ||
					grid[currentRowTwo][currentColTwo] == MENTOS ||
					grid[currentRowTwo][currentColTwo] == CAN)
			{
				playerTwo.addPower(grid[currentRowTwo][currentColTwo]);
				grid[currentRowTwo][currentColTwo] = EMPTY;
			}

			// Repaint the screen after the change
			repaint();
		}
	}
}
