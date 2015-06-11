import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.net.*;
import java.util.LinkedList;
import java.applet.*;

/**
 * Handles the grid and menu for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionGrid extends JPanel
{
	// Program constants

	// Directions
	private final int[] DROW = { 0, 0, 1, -1 };
	private final int[] DCOL = { 1, -1, 0, 0 };

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
	private final int ROUND = 0;
	private final int MAIN_MENU = 1;
	private final int START_MENU = 2;
	private final int STORY = 3;
	private final int INSTRUCTIONS_1 = 4;
	private final int INSTRUCTIONS_2 = 5;

	// Player
	private final int PLAYER_ONE = 1;
	private final int PLAYER_TWO = -1;

	// Program variables

	private boolean gameOver;
	private boolean roundOver;

	// Grid
	private int[][] grid;
	private Image border;
	private Image gridImages[];

	// In-game interface
	private Image sidebar;
	private Image roundWin;
	private int roundWinner;
	private Image gameWin;

	// Player
	private Player playerOne = new Player();
	private Player playerTwo = new Player();
	private int currentRowOne, currentColOne, currentRowTwo, currentColTwo;
	private Image playerImages[];
	private int playerOneImg = 0;
	private int playerTwoImg = 4;

	//AI
	private int [][] aiTraversalGrid;
	private static final int MOVE = 0;
	private static final int PLACE_BOMB = 1;
	private LinkedList<Integer> aiMoveQueue;
	
	// Timers
	private Timer explosions;

	// Game option
	private Font standardFont = new Font("Apple LiGothic", Font.BOLD, 32);
	private Font largeFont = new Font("Apple LiGothic", Font.BOLD, 48);
	private int menu = MAIN_MENU;
	private int noOfPlayers = 2;
	private int totalWins = 1;
	private int mapType = 1;

	// Rectangles for in-game side bar
	private Rectangle IN_GAME_BACK = new Rectangle(15, 631, 100, 70);
	private Rectangle EXIT_BUTTON = new Rectangle(15, 711, 100, 45);

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
	private Rectangle[] TOTAL_WINS = new Rectangle[10];

	// Images for the menu screen
	private Image mainMenu, startMenu, instructions1, instructions2, story;

	// Sound
	private AudioClip intro, storyline, howToPlay, boom, collision;

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

		gridImages[1] = new ImageIcon("img/Tire.png").getImage();
		gridImages[2] = new ImageIcon("img/Mentos.png").getImage();
		gridImages[3] = new ImageIcon("img/Can.png").getImage();
		gridImages[4] = new ImageIcon("img/Building.png").getImage();
		gridImages[5] = new ImageIcon("img/Crate.png").getImage();
		gridImages[6] = new ImageIcon("img/RedCan.png").getImage();
		gridImages[7] = new ImageIcon("img/BlueCan.png").getImage();
		gridImages[8] = new ImageIcon("img/Sodasplosion.png").getImage();
		border = new ImageIcon("img/border.png").getImage();
		sidebar = new ImageIcon("img/Sidebar.png").getImage();
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
		roundWin = new ImageIcon("img/RoundWin.png").getImage();
		gameWin = new ImageIcon("img/GameWin.png").getImage();

		intro = Applet.newAudioClip(getCompleteURL("sound/intro.wav"));
		storyline = Applet.newAudioClip(getCompleteURL("sound/story.wav"));
		howToPlay = Applet.newAudioClip(getCompleteURL("sound/howToPlay.wav"));
		boom = Applet.newAudioClip(getCompleteURL("sound/boom.wav"));
		collision = Applet.newAudioClip(getCompleteURL("sound/collision.wav"));

		// Sets up the icons for the number of rounds
		for (int round = 1; round <= 9; round++)
		{
			TOTAL_WINS[round] = new Rectangle(82 + 100 * (round - 1),
					408, 60, 60);
		}

		// Sets up the timer for the explosion
		explosions = new Timer(100, null);
		explosions.start();

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
	public void resetGame(int roundOrGame)
	{
		playerOne.resetPower();
		playerTwo.resetPower();

		roundOver = false;
		gameOver = false;

		if (roundOrGame == GAME)
		{
			playerOne.resetWins();
			playerTwo.resetWins();
		}

		// Declares number of rows and number of columns and sets up an array to
		// keep track of the grid
		int noOfRows = 11;
		int noOfColumns = 13;
		grid = new int[noOfRows][noOfColumns];
		aiTraversalGrid = new int[noOfRows][noOfColumns];

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
			for (int type = 1; type <= 3; type++)
			{
				for (int power = 1; power <= 5; power++)
				{
					playerOne.addPower(type);
					playerTwo.addPower(type);
				}
			}
		}
	}

	/**
	 * Checks to see if the given player can place a can and if so, starts the
	 * explosion timer
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
		if (player.getCurrentCans() > 0 && grid[row][col] != BLUECAN
				&& grid[row][col] != REDCAN)
		{
			player.placeCan();

			if (player == playerOne)
			{
				grid[row][col] = REDCAN;
			}
			else
			{
				grid[row][col] = BLUECAN;
			}

			explosions.addActionListener(new Sodasplosion(player, player
					.getRange(),
					row, col));
		}
	}

	/**
	 * An inner class that deals with the timer events
	 * 
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 8, 2015
	 */
	private class Sodasplosion implements ActionListener
	{
		Player player;
		int canRow, canCol, range, counter;

		/**
		 * Constructor for the TimerEventHandler
		 * 
		 * @param player the given player
		 * @param canPos which can is being placed (e.g 1st can)
		 */
		public Sodasplosion(Player player, int range, int canRow, int canCol)
		{
			this.player = player;
			this.canRow = canRow;
			this.canCol = canCol;
			this.range = range;
			counter = 0;
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

			if (counter == 20)
			{
				if (grid[canRow][canCol] == REDCAN
						|| grid[canRow][canCol] == BLUECAN)
				{
					grid[canRow][canCol] = EXPLOSION;
					checkCollision(canRow, canCol, player);
				}
			}
			else if (counter == 23)
			{
				clearExplosions(canRow, canCol, range);
				player.returnCan();
				explosions.removeActionListener(this);
			}

			repaint();
		}
	}

	/**
	 * Checks if a given explosion hits anything
	 * 
	 * @param canRow the given row of the can
	 * @param canCol the given column of the can
	 * @param player the player that placed the can
	 */
	public void checkCollision(int canRow, int canCol, Player player)
	{
		boom.play();

		int range = player.getRange();

		// Collision code all directions using an outer for loop
		boolean alreadyHitSomething = false;
		
		for (int direction = 0; direction < DROW.length; direction++)
		{
			alreadyHitSomething = false;
			
			for (int dPos = 1; dPos <= range && canRow + dPos*DROW[direction] >= 0 && canRow + dPos*DROW[direction] < grid.length &&
					canCol + dPos*DCOL[direction] >= 0 && canCol + dPos*DCOL[direction] < grid[0].length
					&& grid[canRow + dPos*DROW[direction]][canCol + dPos*DCOL[direction]] != BUILDING
					&& !alreadyHitSomething; dPos++)
			{
				int checkRow = canRow + dPos*DROW[direction];
				int checkCol = canCol + dPos*DCOL[direction];
				
				if (grid[checkRow][checkCol] == CRATE)
				{
					int item = (int) (Math.random() * 10);

					if (item <= 3)
					{
						grid[checkRow][checkCol] = item;
					}
					else
					{
						grid[checkRow][checkCol] = EXPLOSION;
					}

					alreadyHitSomething = true;
				}
				else if (grid[checkRow][checkCol] == REDCAN
						|| grid[checkRow][checkCol] == BLUECAN)
				{
					grid[checkRow][checkCol] = EXPLOSION;
					checkCollision(checkRow, checkCol, player);
				}
				else
				{
					grid[checkRow][checkCol] = EXPLOSION;
				}

				if (currentRowOne == checkRow && currentColOne == checkCol)
				{
					playerOne.loseLife();
					collision.play();
					if (playerOne.getNoOfLives() < 1)
					{
						playerTwo.winRound();
						roundOver = true;
						roundWinner = PLAYER_TWO;
						currentRowOne = -1;
						currentColOne = -1;
						currentRowTwo = -1;
						currentColTwo = -1;
					}
				}
				if (currentRowTwo == checkRow && currentColTwo == checkCol)
				{
					playerTwo.loseLife();
					collision.play();
					if (playerTwo.getNoOfLives() < 1)
					{
						playerOne.winRound();
						roundOver = true;
						roundWinner = PLAYER_ONE;
						currentRowOne = -1;
						currentColOne = -1;
						currentRowTwo = -1;
						currentColTwo = -1;
					}
				}
			}
		}
	}

	/**
	 * Clears all explosions on the board
	 * 
	 * @param canRow the given row of the can
	 * @param canCol the given column of the can
	 * @param range the range that the explosion could reach
	 */
	public void clearExplosions(int canRow, int canCol, int range)
	{
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[0].length; col++)
			{
				if (grid[row][col] == EXPLOSION)
				{
					grid[row][col] = EMPTY;
				}
			}
		}
	}

	/**
	 * Gets the URL needed for newAudioClip
	 * 
	 * @param fileName the given file name
	 * @return the URL needed for newAudioClip
	 */
	public URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
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

				// Go to main menu if back to main menu button is pressed at a
				// menu
				if (BACK_MENU_BUTTON.contains(pressed) && menu != GAME)
				{
					menu = MAIN_MENU;
				}

				// Respond if the back to menu button or exit button in game
				// have been pressed
				if (menu == GAME)
				{
					if (roundOver && !gameOver)
					{
						resetGame(ROUND);
					}
					if (IN_GAME_BACK.contains(pressed))
					{
						menu = MAIN_MENU;
					}
					else if (EXIT_BUTTON.contains(pressed))
					{
						System.exit(0);
					}
				}

				// When on the start menu, set game variables based buttons
				// clicked on mouse
				else if (menu == START_MENU)
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
						for (int noOfWins = 1; noOfWins <= 9; noOfWins++)
						{
							if (TOTAL_WINS[noOfWins].contains(pressed))
							{
								totalWins = noOfWins;
							}
						}
					}

					// If a player has selected all the settings,
					// starts a new game and loads up the grid (sets size of
					// grid array)
					if (PLAY_BUTTON.contains(pressed))
					{
						menu = GAME;
						resetGame(GAME);
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
					intro.play();
					menu = START_MENU;
				}
				else if (STORY_BUTTON.contains(pressed))
				{
					storyline.play();
					menu = STORY;
				}
				else if (INSTRUCTIONS_BUTTON.contains(pressed))
				{
					howToPlay.play();
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
			if (menu == GAME && !roundOver)
			{
				// Change the currentRow and currentColumn of the player
				// based on the key pressed

				// If the player is trying to move, makes sure that the player
				// cannot move past the border or go through an unbreakable
				// block

				// Calls placeCan method whenever the user attempts to place a
				// can

				// If the player steps on a power-up, that skill type is
				// increased
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
				else if (event.getKeyCode() == KeyEvent.VK_W
						&& currentRowOne > 0
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
				else if (event.getKeyCode() == KeyEvent.VK_Q
						|| event.getKeyCode() == KeyEvent.VK_G)
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
				else if (event.getKeyCode() == KeyEvent.VK_UP
						&& currentRowTwo > 0
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
				else if (event.getKeyCode() == KeyEvent.VK_SLASH
						|| event.getKeyCode() == KeyEvent.VK_NUMPAD0)
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
	}

	/**
	 * Repaint the drawing panel
	 * 
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(standardFont);
		g.setColor(Color.WHITE);

		// Redraws the grid with current images and draws the players on top of
		// the grid if the game is selected
		// Draws a menu screen if one of the menus are selected
		if (menu == GAME)
		{
			g.drawImage(border, 128, 0, this);
			g.drawImage(sidebar, 0, 0, this);

			g.drawString("" + playerOne.getNoOfLives(), 70, 254);
			g.drawString("" + playerOne.getTotalCans(), 70, 291);
			g.drawString("" + playerOne.getRange(), 70, 330);
			g.drawString("" + playerOne.getNoOfWins(), 70, 370);

			g.drawString("" + playerTwo.getNoOfLives(), 70, 490);
			g.drawString("" + playerTwo.getTotalCans(), 70, 528);
			g.drawString("" + playerTwo.getRange(), 70, 565);
			g.drawString("" + playerTwo.getNoOfWins(), 70, 605);

			if (!roundOver)
			{
				for (int row = 0; row < grid.length; row++)
				{
					for (int column = 0; column < grid[0].length; column++)
					{
						g.drawImage(gridImages[grid[row][column]], column
								* IMAGE_WIDTH + 160, row * IMAGE_HEIGHT + 32,
								this);
					}
				}

				g.drawImage(playerImages[playerOneImg], currentColOne
						* IMAGE_WIDTH
						+ 160, currentRowOne * IMAGE_HEIGHT + 32, this);

				g.drawImage(playerImages[playerTwoImg], currentColTwo
						* IMAGE_WIDTH
						+ 160, currentRowTwo * IMAGE_HEIGHT + 32, this);
			}
			else
			{
				g.setFont(largeFont);

				if (playerOne.getNoOfWins() == totalWins
						|| playerTwo.getNoOfWins() == totalWins)
				{
					gameOver = true;

					g.drawImage(gameWin, 326, 204, this);

					if (playerOne.getNoOfWins() == totalWins)
					{
						g.drawImage(playerImages[1], 535, 330, this);
						g.drawString("Player One Wins the Game!", 290, 175);
					}
					else
					{
						g.drawImage(playerImages[5], 535, 330, this);
						g.drawString("Player Two Wins the Game!", 290, 175);
					}
				}
				else
				{
					g.drawImage(roundWin, 326, 204, this);

					if (roundWinner == PLAYER_ONE)
					{
						g.drawImage(playerImages[1], 542, 335, this);
						g.drawString("Player One Wins Round!", 325, 175);
					}
					else
					{
						g.drawImage(playerImages[5], 542, 335, this);
						g.drawString("Player Two Wins Round!", 325, 175);
					}

					g.drawString("Click anywhere on the", 300, 600);
					g.drawString("screen to continue ...", 300, 650);
				}
			}
		}
		else if (menu == MAIN_MENU)
		{
			g.drawImage(mainMenu, 0, 0, this);
		}
		else if (menu == START_MENU)
		{
			g.drawImage(startMenu, 0, 0, this);

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

			if (totalWins > 0)
			{
				g.drawRect(100 * (totalWins - 1) + 77, 403, 70, 70);
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

	private void updateAItraversalGrid(){
		for (int row = 0; row < grid.length; row ++){
			for ( int col = 0; col < grid[row].length; col ++){
				aiTraversalGrid[row][col] = grid[row][col];
				if (aiTraversalGrid[row][col] == REDCAN){
					markDangerInAIGrid(row,col, playerOne.getRange());
				} else if (aiTraversalGrid[row][col] == BLUECAN){
					markDangerInAIGrid(row,col, playerTwo.getRange());
				}
			}
		}
	}
	
	private void markDangerInAIGrid (int row, int col, int range){
		aiTraversalGrid[row][col] = EXPLOSION;
		for (int direction = 0; direction < 4; direction ++){
			boolean wallHit = false;
			for (int dPos = 1; dPos < range && !wallHit; dPos ++){
				int checkRow = row + dPos*DROW[direction];
				int checkCol = col + dPos*DCOL[direction];
				if (isInBounds(checkRow,checkCol) && grid[checkRow][checkCol] != BUILDING && grid[checkRow][checkCol] != CRATE){
					grid[checkRow][checkCol] = EXPLOSION;
				} else {
					wallHit = true;
				}
			}
		}
	}
	
	boolean isInBounds (int row, int col){
		return row >= 0 && row < grid.length && col >= 0 && col < grid[row].length;
	}
	
	private void generateAiMove (){
		if (aiTraversalGrid[currentRowTwo][currentColTwo] == EXPLOSION){
			//GTfO
		}
		//Check if in danger zone
		//get out of danger zone
		
		//check for enemy player
		//go near
		//drop bomb
		
		//check for available power ups
		//get it
		
		//Check for closest available wall
		//Move to wall
		//drop bomb
		
	}
	
}
