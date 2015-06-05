import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;

/**
 * The grid for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionGrid extends JPanel
{
	//Set up variables
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

	/**
	 * Constructs a new grid
	 */
	public SodasplosionGrid()
	{
		explosion = new Timer[10];
		canRows = new int [10];
		canCols = new int [10];

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
			
			// Sets up the timers for each explosion
			for (int timer = 0; timer < explosion.length / 2; timer++)
			{
				explosion[timer] = new Timer(3000, new TimerEventHandler(playerOne, timer));
			}
			for (int timer = explosion.length / 2; timer < explosion.length; timer++)
			{
				explosion[timer] = new Timer(3000, new TimerEventHandler(playerTwo, timer));
			}
			
			int currentCanPos = player.getCurrentCans();
			
			// Sets the current can's row and column and starts the explosion timer
			if (player == playerOne)
			{
				grid[canRow][canCol] = REDCAN;
				canRows[currentCanPos] = canRow;
				canCols[currentCanPos] = canCol;
				explosion[currentCanPos].start();
				System.out.println("Player 1 Cans: " + currentCanPos);
			}
			else
			{
				grid[canRow][canCol] = BLUECAN;
				canRows[currentCanPos + 5] = canRow;
				canCols[currentCanPos + 5] = canCol;
				explosion[currentCanPos + 5].start();
				System.out.println("Player 2 Cans: " + currentCanPos);
			}
			
		}
	}

	/**
	 * An inner class to deal with the timer events
	 */
	private class TimerEventHandler implements ActionListener
	{
		int currentCanRow, currentCanCol, range, whichExplosion;
		Player player;

		/**
		 * Constructor for the TimerEventHandler
		 *  
		 * @param player the given player
		 * @param whichExplosion the given explosion
		 */
		public TimerEventHandler(Player player,int whichExplosion)
		{
			this.player = player;
			this.whichExplosion = whichExplosion;
		}

		/**
		 * Acts upon a fired timer
		 * Explodes the can and checks for collisions
		 */
		public void actionPerformed(ActionEvent event)
		{	
			range = player.getRange();
			
			currentCanRow = canRows[whichExplosion];
			currentCanCol = canCols[whichExplosion];


			grid[currentCanRow][currentCanCol] = EXPLOSION;
			
			// Collision code for the upwards direction
			for (int upPos = 1; upPos <= range && currentCanRow - upPos >= 0
					&& grid[currentCanRow - upPos][currentCanCol] != BUILDING; upPos++)
			{
				if (grid[currentCanRow - upPos][currentCanCol] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow - upPos][currentCanCol] = item;
					}
				}
				grid[currentCanRow - upPos][currentCanCol] = EXPLOSION;
			}
			
			// Collision code for the downwards direction
			for (int downPos = 1; downPos <= range
					&& currentCanRow + downPos < grid.length
					&& grid[currentCanRow + downPos][currentCanCol] != BUILDING; downPos++)
			{
				if (grid[currentCanRow + downPos][currentCanCol] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow + downPos][currentCanCol] = item;
					}
				}	
				grid[currentCanRow + downPos][currentCanCol] = EXPLOSION;
			}
			
			// Collision code for the left direction
			for (int leftPos = 1; leftPos <= range && currentCanCol - leftPos >= 0
					&& grid[currentCanRow][currentCanCol - leftPos] != BUILDING; leftPos++)
			{
				if (grid[currentCanRow][currentCanCol - leftPos] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow][currentCanCol - leftPos] = item;
					}
				}
				grid[currentCanRow][currentCanCol - leftPos] = EXPLOSION;
			}
			
			// Collision code for the right direction
			for (int rightPos = 1; rightPos <= range
					&& currentCanCol + rightPos < grid[0].length
					&& grid[currentCanRow][currentCanCol + rightPos] != BUILDING; rightPos++)
			{
				if (grid[currentCanRow][currentCanCol + rightPos] == CRATE)
				{
					int item = (int) (Math.random() * 10);
					if (item <= 3)
					{
						grid[currentCanRow][currentCanCol + rightPos] = item;
					}
				}	
				grid[currentCanRow][currentCanCol + rightPos] = EXPLOSION;
			}

			// Resets the timer and returns the can to the given player
			repaint();
			explosion[whichExplosion].stop();
			player.returnCan();
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
