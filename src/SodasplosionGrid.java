import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
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

	// Boolean flags
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
	private int pOneRow, pOneCol, pTwoRow, pTwoCol;
	private Image playerImages[];
	private int playerOneImg = 0;
	private int playerTwoImg = 4;

	// Explosions
	private Timer explosions;
	private long sodasplosionId;
	private long[][] explosionIds;

	// Game option
	private Font standardFont = new Font("Apple LiGothic", Font.BOLD, 32);
	private Font largeFont = new Font("Apple LiGothic", Font.BOLD, 48);
	private int menu = MAIN_MENU;
	private int noOfPlayers = 2;
	private int totalWins = 1;
	private int mapType = 1;

	// In-game side-bar
	private Rectangle IN_GAME_BACK = new Rectangle(15, 632, 100, 30);
	private Rectangle IN_GAME_INSTRUCTIONS = new Rectangle(15, 678, 100, 30);
	private Rectangle IN_GAME_EXIT = new Rectangle(15, 724, 100, 30);
	private int inGameHelp = 0;
	private Image inGameInstructions1, inGameInstructions2;

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
	private Rectangle MENU_EXIT = new Rectangle(800, 698, 140, 40);
	private Rectangle[] TOTAL_WINS = new Rectangle[10];

	// Images for the menu screen
	private Image mainMenu, startMenu, instructions1, instructions2, story;

	// AI
	private int[][] aiGrid;
	private final int POWERUP = 9;
	private Timer aiTimer;

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
		inGameInstructions1 = new ImageIcon("img/InstructionsIG1.png")
				.getImage();
		inGameInstructions2 = new ImageIcon("img/InstructionsIG2.png")
				.getImage();

		// Sets up the icons for the number of rounds
		for (int round = 1; round <= 9; round++)
		{
			TOTAL_WINS[round] = new Rectangle(82 + 100 * (round - 1),
					408, 60, 60);
		}

		// Sets up the timer for the explosion
		explosions = new Timer(100, null);
		explosions.start();

		aiTimer = new Timer(500, new aiTimer());

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
	 * Resets the grid to prepare for a new game.
	 * 
	 * @param roundOrGame whether a new game or a new round is being started
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

		if (noOfPlayers == 1)
		{
			aiTimer.start();
			aiGrid = new int[11][13];
		}

		// Sets up an array to keep track of the grid and explosions
		grid = new int[11][13];
		explosionIds = new long[11][13];

		sodasplosionId = 1;

		// Sets the initial positions for player one and player two
		pOneRow = 0;
		pOneCol = 0;
		pTwoRow = 10;
		pTwoCol = 12;

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
		// Gives both players full power if the show-down game mode is set
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
		// and adds the can to the explosion timer
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

			explosions.addActionListener(new Sodasplosion(player, row, col));
		}
	}

	
	
	
	
	
	/**
	 * Gives the id for the next explosion group
	 * 
	 * @return the id for the next explosion group
	 */
	public long getNextSodasplosionId()
	{
		sodasplosionId++;
		return sodasplosionId - 1;
	}

	/**
	 * An inner class to deal with the AI movement rate
	 *
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 13, 2015
	 */
	private class aiTimer implements ActionListener
	{
		/**
		 * Constructor for the aiTimer
		 */
		public aiTimer()
		{
		}

		/**
		 * Acts upon a fired timer. Generates a new move for the AI
		 * 
		 * @param event the Timer event
		 */
		public void actionPerformed(ActionEvent event)
		{
			generateAIMove();
		}
	}

	/**
	 * An inner class that deals with the sodasplosions
	 * 
	 * @author Alexander Shah and Amy Zhang
	 * @version Jun 8, 2015
	 */
	private class Sodasplosion implements ActionListener
	{
		Player player;
		int canRow, canCol, counter;
		long id;

		/**
		 * Constructor for Sodasplosion
		 * 
		 * @param player the given player
		 * @param canRow the row that the can is going to be placed on
		 * @param canCol the column that the can is going to be placed on
		 */
		public Sodasplosion(Player player, int canRow, int canCol)
		{
			this.player = player;
			this.canRow = canRow;
			this.canCol = canCol;
			this.id = getNextSodasplosionId();
			counter = 0;
		}

		/**
		 * Acts upon a fired timer. Creates a sodasplosion and checks for
		 * collisions. Uses multiple timer fires to animate the explosion
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
					checkCollision(canRow, canCol, player, id);
				}
			}
			else if (counter == 23)
			{
				clearExplosions(id);
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
	 * @param id the id for the given explosion
	 */
	public void checkCollision(int canRow, int canCol, Player player, long id)
	{
		int range = player.getRange();

		boolean alreadyHitSomething;
		
		// Collision code all directions using an outer loop
		// Checks to see if the explosion hit a crate, another explosion,
		// or a player, and acts accordingly
		for (int direction = 0; direction < DROW.length; direction++)
		{
			alreadyHitSomething = false;

			for (int dPos = 0; dPos <= range
					&& isInBounds(canRow + dPos * DROW[direction], canCol
							+ dPos * DCOL[direction])
					&& grid[canRow + dPos * DROW[direction]][canCol + dPos
							* DCOL[direction]] != BUILDING
					&& !alreadyHitSomething; dPos++)
			{
				int checkRow = canRow + dPos * DROW[direction];
				int checkCol = canCol + dPos * DCOL[direction];

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
						explosionIds[checkRow][checkCol] = id;
					}

					alreadyHitSomething = true;
				}
				else if (grid[checkRow][checkCol] == REDCAN
						|| grid[checkRow][checkCol] == BLUECAN)
				{
					grid[checkRow][checkCol] = EXPLOSION;
					explosionIds[checkRow][checkCol] = id;
					checkCollision(checkRow, checkCol, player, id);
				}
				else
				{
					grid[checkRow][checkCol] = EXPLOSION;
					explosionIds[checkRow][checkCol] = id;
				}

				if (pOneRow == checkRow && pOneCol == checkCol)
				{
					playerOne.loseLife();
					if (playerOne.getNoOfLives() < 1)
					{
						playerTwo.winRound();
						roundOver = true;
						roundWinner = PLAYER_TWO;
						pOneRow = -1;
						pOneCol = -1;
						pTwoRow = -1;
						pTwoCol = -1;
					}
				}
				if (pTwoRow == checkRow && pTwoCol == checkCol)
				{
					playerTwo.loseLife();
					if (playerTwo.getNoOfLives() < 1)
					{
						playerOne.winRound();
						roundOver = true;
						roundWinner = PLAYER_ONE;
						pOneRow = -1;
						pOneCol = -1;
						pTwoRow = -1;
						pTwoCol = -1;
					}
				}
			}
		}
	}

	/**
	 * Clears the explosion off the board with a given id
	 * 
	 * @param id the id of the explosion that just went off
	 */
	private void clearExplosions(long id)
	{
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[0].length; col++)
			{
				if (explosionIds[row][col] == id)
				{
					grid[row][col] = EMPTY;
					explosionIds[row][col] = EMPTY;
				}
			}
		}
	}

	/**
	 * Checks whether or not a check will go out of bounds
	 * 
	 * @param row the given row
	 * @param col the given column
	 * @return whether or not a check will go out of bounds
	 */
	boolean isInBounds(int row, int col)
	{
		return (row >= 0 && row < grid.length && col >= 0
		&& col < grid[row].length);
	}

	/**
	 * Generates the best possible move for the AI
	 */
	private void generateAIMove()
	{
		updateAIGrid();

		if (isInBounds(pTwoRow, pTwoCol))
		{
			// Checks to see if the AI is in any immediate danger.
			// Moves the AI away if it is in any danger and places
			// a can if it is not
			boolean inDanger = (aiGrid[pTwoRow][pTwoCol] == EXPLOSION);

			if (inDanger)
			{
				findSafeZone(pTwoRow, pTwoCol);
			}
			else
			{
				placeCan(playerTwo, pTwoRow, pTwoCol);
			}
		}
	}

	/**
	 * Fills the AI grid with current information
	 */
	private void updateAIGrid()
	{
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				aiGrid[row][col] = grid[row][col];
			}
		}

		// Marks the power-ups and explosions on the grid
		// Marks the dangerous spots if an explosion has been placed
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				if (aiGrid[row][col] == TIRE ||
						aiGrid[row][col] == MENTOS ||
						aiGrid[row][col] == CAN)
				{
					aiGrid[row][col] = POWERUP;
				}
				else if (aiGrid[row][col] == REDCAN)
				{
					markDanger(row, col, playerOne.getRange());
				}
				else if (aiGrid[row][col] == BLUECAN)
				{
					markDanger(row, col, playerTwo.getRange());
				}
			}
		}

		// If standing on a bomb, convert it to an explosion, so still in
		// danger, but not unable to move
		if (aiGrid[pTwoRow][pTwoCol] == BLUECAN)
		{
			aiGrid[pTwoRow][pTwoCol] = EXPLOSION;
		}
	}

	/**
	 * Marks dangers in all directions using an outer loop Checks to see what
	 * tiles the explosion will hit and marks them as explosions prematurely
	 * 
	 * @param row the given row to start checking at
	 * @param col the given column to start checking at
	 * @param range the range of the given explosion
	 */
	private void markDanger(int row, int col, int range)
	{
		for (int direction = 0; direction < DROW.length; direction++)
		{
			boolean wallHit = false;

			for (int dPos = 0; dPos <= range && !wallHit; dPos++)
			{
				int checkRow = row + dPos * DROW[direction];
				int checkCol = col + dPos * DCOL[direction];

				if (isInBounds(checkRow, checkCol)
						&& grid[checkRow][checkCol] != BUILDING
						&& grid[checkRow][checkCol] != CRATE)
				{
					aiGrid[checkRow][checkCol] = EXPLOSION;
				}
				else
				{
					wallHit = true;
				}
			}
		}
	}

	/**
	 * Finds the closest safe zone relative to the player
	 * 
	 * @param row the row that the player is checking from
	 * @param col the column that the player is checking from
	 */
	private void findSafeZone(int row, int col)
	{
		// Starts the distance at a high number so that the first safe zone
		// found
		// will be the new closest
		int closestRow = 100;
		int closestCol = 100;

		// Approximates distance using "Manhattan Distance"
		// and keeps track of the row and column of the closest
		// safe zone
		for (int checkRow = 0; checkRow < aiGrid.length; checkRow++)
		{
			for (int checkCol = 0; checkCol < aiGrid[0].length; checkCol++)
			{
				if (aiGrid[checkRow][checkCol] == EMPTY
						|| aiGrid[checkRow][checkCol] == POWERUP)
				{
					if ((Math.abs(row - checkRow) + Math.abs(col - checkCol))
					< (Math.abs(row - closestRow) + Math.abs(col - closestCol)))
					{
						closestRow = checkRow;
						closestCol = checkCol;
					}
				}
			}
		}

		// Incomplete, need to add a path-finding method instead of warping
		pTwoRow = closestRow;
		pTwoCol = closestCol;
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

				// Returns to the menu, shows the instructions, or exits
				// the game if a button has been pressed
				if (menu == GAME)
				{
					if (roundOver && !gameOver)
					{
						resetGame(ROUND);
					}

					if (inGameHelp == 0)
					{
						if (IN_GAME_BACK.contains(pressed))
						{
							menu = MAIN_MENU;
						}
						else if (IN_GAME_INSTRUCTIONS.contains(pressed))
						{
							inGameHelp = 1;
						}
						else if (IN_GAME_EXIT.contains(pressed))
						{
							System.exit(0);
						}
					}
					else
					{
						if (BACK_MENU_BUTTON.contains(pressed))
						{
							inGameHelp = 0;
						}
						else if (PAGE_BUTTON.contains(pressed))
						{
							if (inGameHelp == 1)
							{
								inGameHelp = 2;
							}
							else
							{
								inGameHelp = 1;
								;
							}
						}
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
				else if (MENU_EXIT.contains(pressed))
				{
					System.exit(0);
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
				if (event.getKeyCode() == KeyEvent.VK_A && pOneCol > 0
						&& grid[pOneRow][pOneCol - 1] != BUILDING
						&& grid[pOneRow][pOneCol - 1] != CRATE
						&& grid[pOneRow][pOneCol - 1] != REDCAN
						&& grid[pOneRow][pOneCol - 1] != BLUECAN)
				{
					pOneCol--;
					playerOneImg = 0;
				}
				else if (event.getKeyCode() == KeyEvent.VK_D
						&& pOneCol < grid[0].length - 1
						&& grid[pOneRow][pOneCol + 1] != BUILDING
						&& grid[pOneRow][pOneCol + 1] != CRATE
						&& grid[pOneRow][pOneCol + 1] != REDCAN
						&& grid[pOneRow][pOneCol + 1] != BLUECAN)
				{
					pOneCol++;
					playerOneImg = 2;
				}
				else if (event.getKeyCode() == KeyEvent.VK_W
						&& pOneRow > 0
						&& grid[pOneRow - 1][pOneCol] != BUILDING
						&& grid[pOneRow - 1][pOneCol] != CRATE
						&& grid[pOneRow - 1][pOneCol] != REDCAN
						&& grid[pOneRow - 1][pOneCol] != BLUECAN)
				{
					pOneRow--;
					playerOneImg = 1;
				}
				else if (event.getKeyCode() == KeyEvent.VK_S
						&& pOneRow < grid.length - 1
						&& grid[pOneRow + 1][pOneCol] != BUILDING
						&& grid[pOneRow + 1][pOneCol] != CRATE
						&& grid[pOneRow + 1][pOneCol] != REDCAN
						&& grid[pOneRow + 1][pOneCol] != BLUECAN)
				{
					pOneRow++;
					playerOneImg = 3;
				}
				else if (event.getKeyCode() == KeyEvent.VK_Q
						|| event.getKeyCode() == KeyEvent.VK_G)
				{
					placeCan(playerOne, pOneRow, pOneCol);
				}

				if (grid[pOneRow][pOneCol] == TIRE ||
						grid[pOneRow][pOneCol] == MENTOS ||
						grid[pOneRow][pOneCol] == CAN)
				{
					playerOne.addPower(grid[pOneRow][pOneCol]);
					grid[pOneRow][pOneCol] = EMPTY;
				}

				// Player two
				if (noOfPlayers == 2)
				{
					if (event.getKeyCode() == KeyEvent.VK_LEFT
							&& pTwoCol > 0
							&& grid[pTwoRow][pTwoCol - 1] != BUILDING
							&& grid[pTwoRow][pTwoCol - 1] != CRATE
							&& grid[pTwoRow][pTwoCol - 1] != REDCAN
							&& grid[pTwoRow][pTwoCol - 1] != BLUECAN)
					{
						pTwoCol--;
						playerTwoImg = 4;
					}
					else if (event.getKeyCode() == KeyEvent.VK_RIGHT
							&& pTwoCol < grid[0].length - 1
							&& grid[pTwoRow][pTwoCol + 1] != BUILDING
							&& grid[pTwoRow][pTwoCol + 1] != CRATE
							&& grid[pTwoRow][pTwoCol + 1] != REDCAN
							&& grid[pTwoRow][pTwoCol + 1] != BLUECAN)
					{
						pTwoCol++;
						playerTwoImg = 6;
					}
					else if (event.getKeyCode() == KeyEvent.VK_UP
							&& pTwoRow > 0
							&& grid[pTwoRow - 1][pTwoCol] != BUILDING
							&& grid[pTwoRow - 1][pTwoCol] != CRATE
							&& grid[pTwoRow - 1][pTwoCol] != REDCAN
							&& grid[pTwoRow - 1][pTwoCol] != BLUECAN)
					{
						pTwoRow--;
						playerTwoImg = 5;
					}
					else if (event.getKeyCode() == KeyEvent.VK_DOWN
							&& pTwoRow < grid.length - 1
							&& grid[pTwoRow + 1][pTwoCol] != BUILDING
							&& grid[pTwoRow + 1][pTwoCol] != CRATE
							&& grid[pTwoRow + 1][pTwoCol] != REDCAN
							&& grid[pTwoRow + 1][pTwoCol] != BLUECAN)
					{
						pTwoRow++;
						playerTwoImg = 7;
					}
					else if (event.getKeyCode() == KeyEvent.VK_SLASH
							|| event.getKeyCode() == KeyEvent.VK_NUMPAD0)
					{
						placeCan(playerTwo, pTwoRow, pTwoCol);
					}
				}

				if (grid[pTwoRow][pTwoCol] == TIRE ||
						grid[pTwoRow][pTwoCol] == MENTOS ||
						grid[pTwoRow][pTwoCol] == CAN)
				{
					playerTwo.addPower(grid[pTwoRow][pTwoCol]);
					grid[pTwoRow][pTwoCol] = EMPTY;
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
		// Draws the victory images if a game or round is over
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

				g.drawImage(playerImages[playerOneImg], pOneCol
						* IMAGE_WIDTH
						+ 160, pOneRow * IMAGE_HEIGHT + 32, this);

				g.drawImage(playerImages[playerTwoImg], pTwoCol
						* IMAGE_WIDTH
						+ 160, pTwoRow * IMAGE_HEIGHT + 32, this);
			}
			else
			{
				aiTimer.stop();

				g.setFont(largeFont);

				if (playerOne.getNoOfWins() == totalWins
						|| playerTwo.getNoOfWins() == totalWins)
				{
					gameOver = true;

					g.drawImage(gameWin, 326, 204, this);

					if (playerOne.getNoOfWins() == totalWins)
					{
						g.drawImage(playerImages[1], 534, 330, this);
						g.drawString("Player One Wins the Game!", 277, 175);
					}
					else
					{
						g.drawImage(playerImages[5], 534, 330, this);
						g.drawString("Player Two Wins the Game!", 277, 175);
					}
				}
				else
				{
					g.drawImage(roundWin, 326, 204, this);

					if (roundWinner == PLAYER_ONE)
					{
						g.drawImage(playerImages[1], 539, 338, this);
						g.drawString("Player One Wins Round!", 312, 175);
					}
					else
					{
						g.drawImage(playerImages[5], 539, 338, this);
						g.drawString("Player Two Wins Round!", 312, 175);
					}
					g.setFont(standardFont);

					g.drawString("Click anywhere to continue...", 362, 600);
				}
			}

			if (inGameHelp == 1)
			{
				g.drawImage(inGameInstructions1, 0, 0, this);
			}
			else if (inGameHelp == 2)
			{
				g.drawImage(inGameInstructions2, 0, 0, this);
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
}
