import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Handles the grid and menu for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionGrid extends JPanel
{
	// Program constants
	
	// Tile size
	private final int IMAGE_WIDTH = 64;
	private final int IMAGE_HEIGHT = 64;
	
	// Grid
	private final int EMPTY = 0;
	private final int TIRE = 1;
	private final int MENTOS = 2;
	private final int CAN = 3;
	private final int BUILDING = 4;
	private final int CRATE = 5;
	private final int REDCAN = 6;
	private final int BLUECAN = 7;
	private final int EXPLOSION = 8;
	
	// Menu 
	private final int GAME = -1;
	private final int MAIN_MENU = 1;
	private final int START_MENU = 2;
	private final int STORY = 3;
	private final int INSTRUCTIONS_1 = 4;
	private final int INSTRUCTIONS_2 = 5;

	// Program variables
	
	// Grid
	private int[][] grid;
	private Image border;
	private Image gridImages[];
	
	// Player 
	private Player playerOne = new Player();
	private Player playerTwo = new Player();
	private int currentRowOne, currentColOne, currentRowTwo, currentColTwo;
	private int[] canRows, canCols;
	private Image playerImages[];
	private int playerOneImg = 0;
	private int playerTwoImg = 4;
	private Timer[] explosion;

	// Game options
	private int start = 0;
	private Font font = new Font("Serif", Font.BOLD, 12);
	private int menu = MAIN_MENU;// GAME
	private int noOfPlayers = 0;
	private int noOfRounds = 0;
	private int mapType = 1;

	// Rectangles for the menu screens
	private Rectangle START_BUTTON = new Rectangle(170, 453, 150, 40);
	private Rectangle STORY_BUTTON = new Rectangle(366, 453, 150, 40);
	private Rectangle INSTRUCTIONS_BUTTON = new Rectangle(568, 453, 285, 40);
	private Rectangle BACK_MENU_BUTTON = new Rectangle(760, 723, 250, 35);
	private Rectangle PAGE_BUTTON = new Rectangle(640, 723, 100, 35);
	private Rectangle ONE_PLAYER = new Rectangle(45, 78, 150, 150);
	private Rectangle TWO_PLAYERS = new Rectangle(220, 78, 150, 150);
	private Rectangle CLASSIC = new Rectangle(560, 73, 150, 150);
	private Rectangle SHOWDOWN = new Rectangle(787, 73, 150, 150);
	private Rectangle PLAY_BUTTON = new Rectangle(265, 518, 530, 150);
	private Rectangle[] NO_OF_ROUNDS = new Rectangle[10];
	
	// Images for the menu screen
	private Image mainMenu, startMenu, instructions1, instructions2, story;

	/**
	 * Constructs a new grid
	 */
	public SodasplosionGrid()
	{
		// Loads up all the images
		// Leaves gridImages[0] blank so that the default image
		// for each tile is empty
		gridImages = new Image[9];
		playerImages = new Image[8];
		canRows = new int[10];
		canCols = new int[10];

		gridImages[1] = new ImageIcon("img/Tire.png").getImage();
		gridImages[2] = new ImageIcon("img/Mentos.png").getImage();
		gridImages[3] = new ImageIcon("img/Can.png").getImage();
		gridImages[4] = new ImageIcon("img/Building.png").getImage();
		gridImages[5] = new ImageIcon("img/Crate.png").getImage();
		gridImages[6] = new ImageIcon("img/RedCan.png").getImage();
		gridImages[7] = new ImageIcon("img/BlueCan.png").getImage();
		gridImages[8] = new ImageIcon("img/Sodasplosion.png").getImage();
		border = new ImageIcon("img/border.png").getImage();
		for (int imageNo = 0; imageNo < 4; imageNo++)
		{
			playerImages[imageNo] = new ImageIcon("img/RedTruck" + imageNo
					+ ".png").getImage();
		}
		for (int imageNo = 4; imageNo < 8; imageNo++)
		{
			playerImages[imageNo] = new ImageIcon("img/BlueTruck" + imageNo
					+ ".png").getImage();
		}
		mainMenu = new ImageIcon("img/MainMenu.png").getImage();
		startMenu = new ImageIcon("img/StartMenu.png").getImage();
		instructions1 = new ImageIcon("img/Instructions1.png").getImage();
		instructions2 = new ImageIcon("img/Instructions2.png").getImage();
		story = new ImageIcon("img/Story.png").getImage();

		// Sets up the icons for the number of rounds
		for (int round = 1; round <= 9; round++)
		{
			NO_OF_ROUNDS[round] = new Rectangle(82 + 100 * (round - 1),
					408, 60, 60);
		}

		// Sets up the timers for each explosion
		explosion = new Timer[10];
		for (int timer = 0; timer < explosion.length / 2; timer++)
		{
			explosion[timer] = new Timer(500, new TimerEventHandler(playerOne,
					timer));
		}
		for (int timer = explosion.length / 2; timer < explosion.length; timer++)
		{
			explosion[timer] = new Timer(500, new TimerEventHandler(playerTwo,
					timer));
		}

		// Set the image height and width based on the path image size
		// Also sizes this panel based on the image and grid size
		Dimension size = new Dimension(1024, 768);
		this.setPreferredSize(size);

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
		// Declares number of rows and number of columns and sets up an array to
		// keep track of the grid
		int noOfRows = 11;
		int noOfColumns = 13;
		grid = new int[noOfRows][noOfColumns];

		// Sets the initial positions for player one and player two
		currentRowOne = 0;
		currentColOne = 0;
		currentRowTwo = 10;
		currentColTwo = 12;

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

		// Adds crates to the grid if the classic game mode is set
		// Gives both players full power if the showdown game mode is set
		if (mapType == 1)
		{
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
		else
		{
			for (int power = 1; power < 5; power++)
			{
				playerOne.addPower(1);
				playerOne.addPower(2);
				playerOne.addPower(3);
				playerTwo.addPower(1);
				playerTwo.addPower(2);
				playerTwo.addPower(3);
			}
		}
	}

	/**
	 * Checks to see if the given player can place a can and if so, starts the
	 * sodasplosion timer
	 * 
	 * @param player the given player
	 * @param row the row of the given player
	 * @param col the column of the given player
	 */
	public void placeCan(Player player, int row, int col)
	{
		// If the player has at least one can, places the can on the ground,
		// checks to see which can is being placed (e.g. 1st can, 2nd can, etc),
		// sets the current can's row and column, and starts the explosion
		// timer
		if (player.getCurrentCans() > 0)
		{
			player.placeCan();
			int canPos = player.getCurrentCans();

			if (player == playerOne)
			{
				grid[row][col] = REDCAN;
				canRows[canPos] = row;
				canCols[canPos] = col;
				explosion[canPos].start();
			}
			else
			{
				grid[row][col] = BLUECAN;
				canRows[canPos + 5] = row;
				canCols[canPos + 5] = col;
				explosion[canPos + 5].start();
			}
		}
	}

	/**
	 * An inner class that deals with the timer events
	 * 
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 8, 2015
	 */
	private class TimerEventHandler implements ActionListener
	{
		Player player;
		int canPos, counter, range, currentCanRow, currentCanCol;

		/**
		 * Constructor for the TimerEventHandler
		 * 
		 * @param player the given player
		 * @param canPos which can is being placed (e.g 1st can)
		 */
		public TimerEventHandler(Player player, int canPos)
		{
			this.player = player;
			this.canPos = canPos;
			// Sets all the initial values to 0 so that there
			// is no error message
			counter = 0;
			range = 0;
			currentCanRow = 0;
			currentCanCol = 0;
		}

		/**
		 * Acts upon a fired timer Creates a sodasplosion and checks for
		 * collisions Uses multiple timer fires to animate the explosion
		 * 
		 * @param event the Timer event
		 */
		public void actionPerformed(ActionEvent event)
		{
			counter++;

			if (counter == 1)
			{
				range = player.getRange();
				currentCanRow = canRows[canPos];
				currentCanCol = canCols[canPos];
			}
			else if (counter == 5)
			{
				grid[currentCanRow][currentCanCol] = EXPLOSION;

				// Collision code for the upwards direction
				boolean alreadyHitSomething = false;
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
							grid[currentCanRow - upPos][currentCanCol] = EXPLOSION;
						}

						alreadyHitSomething = true;
					}
					else
					{
						grid[currentCanRow - upPos][currentCanCol] = EXPLOSION;
					}
				}

				// Collision code for the downwards direction
				alreadyHitSomething = false;
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
							grid[currentCanRow + downPos][currentCanCol] = EXPLOSION;
						}

						alreadyHitSomething = true;
					}
					else
					{
						grid[currentCanRow + downPos][currentCanCol] = EXPLOSION;
					}
				}

				// Collision code for the left direction
				alreadyHitSomething = false;
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
							grid[currentCanRow][currentCanCol - leftPos] = EXPLOSION;
						}

						alreadyHitSomething = true;
					}
					else
					{
						grid[currentCanRow][currentCanCol - leftPos] = EXPLOSION;
					}
				}

				// Collision code for the right direction
				alreadyHitSomething = false;
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
							grid[currentCanRow][currentCanCol + rightPos] = EXPLOSION;
						}

						alreadyHitSomething = true;
					}
					else
					{
						grid[currentCanRow][currentCanCol + rightPos] = EXPLOSION;
					}
				}
			}
			// Clears all explosion images
			else if (counter == 6)
			{
				grid[currentCanRow][currentCanCol] = EMPTY;

				for (int upPos = 1; currentCanRow - upPos >= 0
						&& grid[currentCanRow - upPos][currentCanCol] == EXPLOSION; upPos++)
				{
					grid[currentCanRow - upPos][currentCanCol] = EMPTY;
				}

				for (int downPos = 1; currentCanRow + downPos < grid.length
						&& grid[currentCanRow + downPos][currentCanCol] == EXPLOSION; downPos++)
				{
					grid[currentCanRow + downPos][currentCanCol] = EMPTY;
				}

				for (int leftPos = 1; currentCanCol - leftPos >= 0
						&& grid[currentCanRow][currentCanCol - leftPos] == EXPLOSION; leftPos++)
				{
					grid[currentCanRow][currentCanCol - leftPos] = EMPTY;
				}

				for (int rightPos = 1; currentCanCol + rightPos < grid[0].length
						&& grid[currentCanRow][currentCanCol + rightPos] == EXPLOSION; rightPos++)
				{
					grid[currentCanRow][currentCanCol + rightPos] = EMPTY;
				}

				// Resets the timer, resets the counter, and returns
				// the can to the given player
				player.returnCan();
				explosion[canPos].stop();
				counter = 0;
			}
			repaint();
		}
	}

	/**
	 * Checks if all settings has been selected
	 * 
	 * @return whether or not the game can start (if all the settings have been
	 *         set)
	 */
	private boolean canPlay()
	{
		return (noOfPlayers > 0 && noOfRounds > 0 && mapType > 0);
	}

	/**
	 * Inner class to deal with mouse presses
	 *
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 7, 2015
	 */
	private class MouseHandler extends MouseAdapter
	{

		/**
		 * Responds to a mousePressed event
		 * 
		 * @param event the mousePressed event
		 */
		public void mousePressed(MouseEvent event)
		{
			Point pressed = event.getPoint();

			// Check menu the screen is currently on
			if (menu != MAIN_MENU)
			{
				// Go to main menu if back to main menu button is pressed
				if (BACK_MENU_BUTTON.contains(pressed))
				{
					menu = MAIN_MENU;
				}

				// When on the start menu, set game variables based buttons
				// clicked on mouse
				if (menu == START_MENU)
				{
					// Respond if any of the buttons on the start screen were
					// pressed
					// (Number of players, map type, and number of rounds)
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
						{
							if (NO_OF_ROUNDS[round].contains(pressed))
							{
								noOfRounds = round;
							}
						}
					}

					// If a player has selected all the settings,
					// starts a new game and loads up the grid (sets size of grid array)
					if (canPlay() && PLAY_BUTTON.contains(pressed))
					{
						start = 1;
						menu = GAME;
						newGame();
					}
				}
				// Allow user to change pages between the instruction menus
				else if (PAGE_BUTTON.contains(pressed))
				{
					if (menu == INSTRUCTIONS_1)
					{
						menu = INSTRUCTIONS_2;
					}
					else if (menu == INSTRUCTIONS_2)
					{
						menu = INSTRUCTIONS_1;
					}
				}
			}
			// When on the main menu, go to appropriate screen when button is
			// clicked
			else
			{
				if (START_BUTTON.contains(pressed))
				{
					menu = START_MENU;
				}
				else if (STORY_BUTTON.contains(pressed))
				{
					menu = STORY;
				}
				else if (INSTRUCTIONS_BUTTON.contains(pressed))
				{
					menu = INSTRUCTIONS_1;
				}
			}
			repaint();
		}
	}

	/**
	 * Inner class to deal with key presses
	 *
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 7, 2015
	 */
	private class KeyHandler extends KeyAdapter
	{
		/**
		 * Acts on a keyPressed event
		 * 
		 * @param event the given Key event
		 */
		public void keyPressed(KeyEvent event)
		{
			// Change the currentRow and currentColumn of the player
			// based on the key pressed

			// If the player is trying to move, makes sure that the player
			// cannot move past the border or go through an unbreakable block

			// Calls placeCan method whenever the user attempts to place a can

			// If the player steps on a power-up, that skill type is increased
			// by one level

			// Player One
			if (event.getKeyCode() == KeyEvent.VK_A && currentColOne > 0
					&& grid[currentRowOne][currentColOne - 1] != BUILDING
					&& grid[currentRowOne][currentColOne - 1] != CRATE
					&& grid[currentRowOne][currentColOne - 1] != REDCAN
					&& grid[currentRowOne][currentColOne - 1] != BLUECAN)
			{
				currentColOne--;
				playerOneImg = 0;
			}
			else if (event.getKeyCode() == KeyEvent.VK_D
					&& currentColOne < grid[0].length - 1
					&& grid[currentRowOne][currentColOne + 1] != BUILDING
					&& grid[currentRowOne][currentColOne + 1] != CRATE
					&& grid[currentRowOne][currentColOne + 1] != REDCAN
					&& grid[currentRowOne][currentColOne + 1] != BLUECAN)
			{
				currentColOne++;
				playerOneImg = 2;
			}
			else if (event.getKeyCode() == KeyEvent.VK_W && currentRowOne > 0
					&& grid[currentRowOne - 1][currentColOne] != BUILDING
					&& grid[currentRowOne - 1][currentColOne] != CRATE
					&& grid[currentRowOne - 1][currentColOne] != REDCAN
					&& grid[currentRowOne - 1][currentColOne] != BLUECAN)
			{
				currentRowOne--;
				playerOneImg = 1;
			}
			else if (event.getKeyCode() == KeyEvent.VK_S
					&& currentRowOne < grid.length - 1
					&& grid[currentRowOne + 1][currentColOne] != BUILDING
					&& grid[currentRowOne + 1][currentColOne] != CRATE
					&& grid[currentRowOne + 1][currentColOne] != REDCAN
					&& grid[currentRowOne + 1][currentColOne] != BLUECAN)
			{
				currentRowOne++;
				playerOneImg = 3;
			}
			else if (event.getKeyCode() == KeyEvent.VK_Q)
			{
				placeCan(playerOne, currentRowOne, currentColOne);
			}

			if (grid[currentRowOne][currentColOne] == TIRE ||
					grid[currentRowOne][currentColOne] == MENTOS ||
					grid[currentRowOne][currentColOne] == CAN)
			{
				playerOne.addPower(grid[currentRowOne][currentColOne]);
				grid[currentRowOne][currentColOne] = EMPTY;
			}

			// Player two
			if (event.getKeyCode() == KeyEvent.VK_LEFT && currentColTwo > 0
					&& grid[currentRowTwo][currentColTwo - 1] != BUILDING
					&& grid[currentRowTwo][currentColTwo - 1] != CRATE
					&& grid[currentRowTwo][currentColTwo - 1] != REDCAN
					&& grid[currentRowTwo][currentColTwo - 1] != BLUECAN)
			{
				currentColTwo--;
				playerTwoImg = 4;
			}
			else if (event.getKeyCode() == KeyEvent.VK_RIGHT
					&& currentColTwo < grid[0].length - 1
					&& grid[currentRowTwo][currentColTwo + 1] != BUILDING
					&& grid[currentRowTwo][currentColTwo + 1] != CRATE
					&& grid[currentRowTwo][currentColTwo + 1] != REDCAN
					&& grid[currentRowTwo][currentColTwo + 1] != BLUECAN)
			{
				currentColTwo++;
				playerTwoImg = 6;
			}
			else if (event.getKeyCode() == KeyEvent.VK_UP && currentRowTwo > 0
					&& grid[currentRowTwo - 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo - 1][currentColTwo] != CRATE
					&& grid[currentRowTwo - 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo - 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo--;
				playerTwoImg = 5;
			}
			else if (event.getKeyCode() == KeyEvent.VK_DOWN
					&& currentRowTwo < grid.length - 1
					&& grid[currentRowTwo + 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo + 1][currentColTwo] != CRATE
					&& grid[currentRowTwo + 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo + 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo++;
				playerTwoImg = 7;
			}
			else if (event.getKeyCode() == KeyEvent.VK_SLASH)
			{
				placeCan(playerTwo, currentRowTwo, currentColTwo);
			}

			if (grid[currentRowTwo][currentColTwo] == TIRE ||
					grid[currentRowTwo][currentColTwo] == MENTOS ||
					grid[currentRowTwo][currentColTwo] == CAN)
			{
				playerTwo.addPower(grid[currentRowTwo][currentColTwo]);
				grid[currentRowTwo][currentColTwo] = EMPTY;
			}
			// Repaints the screen after the changes
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
		g.drawImage(border, 128, 0, this);

		// Redraws the grid with current images and draws the players on top of
		// the grid if the game is selected
		// Draws a menu screen if one of the menus are selected
		if (menu == GAME)
		{
			g.drawImage(border, 128, 0, this);

			for (int row = 0; row < grid.length; row++)
			{
				for (int column = 0; column < grid[0].length; column++)
				{
					g.drawImage(gridImages[grid[row][column]], column
							* IMAGE_WIDTH + 160, row * IMAGE_HEIGHT + 32, this);
				}
			}

			g.drawImage(playerImages[playerOneImg], currentColOne * IMAGE_WIDTH
					+ 160, currentRowOne * IMAGE_HEIGHT + 32, this);

			g.drawImage(playerImages[playerTwoImg], currentColTwo * IMAGE_WIDTH
					+ 160, currentRowTwo * IMAGE_HEIGHT + 32, this);
		}
		else if (menu == MAIN_MENU)
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

			// Draws a white rectangle around the setting that has been selected
			if (noOfPlayers == 1)
			{
				g.drawRect(40, 73, 160, 160);
			}
			else if (noOfPlayers == 2)
			{
				g.drawRect(215, 73, 160, 160);
			}

			if (mapType == 1)
			{
				g.drawRect(555, 68, 160, 160);
			}
			else if (mapType == 2)
			{
				g.drawRect(782, 68, 160, 160);
			}

			if (noOfRounds > 0)
			{
				g.drawRect(100 * (noOfRounds - 1) + 77, 403, 70, 70);
			}
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
}
