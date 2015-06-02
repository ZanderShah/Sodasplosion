import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * The grid for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionGrid extends JPanel
{
	private final int IMAGE_WIDTH, IMAGE_HEIGHT;

	private Image gridImages[];
	private Image playerImages[];
	
	Player playerOne = new Player();
	int[] playerOneCanRow, playerOneCanCol;
	
	Player playerTwo = new Player();
	int[] playerTwoCanRow, playerTwoCanCol;
	
	private Image border;

	private int[][] grid;
	private int currentRowOne, currentColOne, currentRowTwo, currentColTwo;
	
	final int EMPTY, TIRE, MENTOS, CAN, BUILDING, CRATE, REDCAN, BLUECAN, EXPLOSION;

	Timer sodasplosion;

	/**
	 * Constructs a new grid
	 */
	public SodasplosionGrid()
	{
		// Loads up the player and breakable block images
		playerImages = new Image[8];
		playerImages[0] = new ImageIcon("RedTruck.png").getImage();
		playerImages[4] = new ImageIcon("BlueTruck.png").getImage();
		playerOneCanRow = new int[5];
		playerOneCanCol = new int[5];
		playerTwoCanRow = new int[5];
		playerTwoCanCol = new int[5];

		gridImages = new Image[9];

		// Leaves gridImages[0] blank so that the default image for each tile is
		// nothing
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

			if (player == playerOne)
			{
				grid[canRow][canCol] = REDCAN;
				
				playerOneCanRow[currentCanPos] = canRow;
				playerOneCanCol[currentCanPos] = canCol;
			}
			else
			{
				grid[canRow][canCol] = BLUECAN;
				
				playerTwoCanRow[currentCanPos] = canRow;
				playerTwoCanCol[currentCanPos] = canCol;
			}	
			
			System.out.println(playerOneCanRow[currentCanPos]);
			System.out.println(playerOneCanCol[currentCanPos]);
			System.out.println(playerTwoCanRow[currentCanPos]);
			System.out.println(playerTwoCanCol[currentCanPos]);
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
		g.drawImage(playerImages[0],
				currentColOne * IMAGE_WIDTH + 160,
				currentRowOne * IMAGE_HEIGHT + 32, this);

		g.drawImage(playerImages[4],
				currentColTwo * IMAGE_WIDTH + 160,
				currentRowTwo * IMAGE_HEIGHT + 32, this);
	}

	/**
	 * An inner class to deal with the timer events
	 */
	private class TimerEventHandler implements ActionListener
	{
		/**
		 * Acts whenever the timer fires
		 * 
		 * @param event the Timer event
		 */
		public void actionPerformed(ActionEvent event)
		{
			// Repaint the screen
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
			}
			else if (event.getKeyCode() == KeyEvent.VK_D
					&& currentColOne < grid[0].length - 1
					&& grid[currentRowOne][currentColOne + 1] != BUILDING
					&& grid[currentRowOne][currentColOne + 1] != CRATE
					&& grid[currentRowOne][currentColOne + 1] != REDCAN
					&& grid[currentRowOne][currentColOne + 1] != BLUECAN)
			{
				currentColOne++;
			}
			else if (event.getKeyCode() == KeyEvent.VK_W && currentRowOne > 0
					&& grid[currentRowOne - 1][currentColOne] != BUILDING
					&& grid[currentRowOne - 1][currentColOne] != CRATE
					&& grid[currentRowOne - 1][currentColOne] != REDCAN
					&& grid[currentRowOne - 1][currentColOne] != BLUECAN)
			{
				currentRowOne--;
			}
			else if (event.getKeyCode() == KeyEvent.VK_S
					&& currentRowOne < grid.length - 1
					&& grid[currentRowOne + 1][currentColOne] != BUILDING
					&& grid[currentRowOne + 1][currentColOne] != CRATE
					&& grid[currentRowOne + 1][currentColOne] != REDCAN
					&& grid[currentRowOne + 1][currentColOne] != BLUECAN)
			{
				currentRowOne++;
			}
			// Places a can
			else if (event.getKeyCode() == KeyEvent.VK_Q
					&& currentRowOne != currentRowTwo
					&& currentColOne != currentColTwo)
			{
				placeCan(playerOne, currentRowOne, currentColOne);
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
			}
			else if (event.getKeyCode() == KeyEvent.VK_RIGHT
					&& currentColTwo < grid[0].length - 1
					&& grid[currentRowTwo][currentColTwo + 1] != BUILDING
					&& grid[currentRowTwo][currentColTwo + 1] != CRATE
					&& grid[currentRowTwo][currentColTwo + 1] != REDCAN
					&& grid[currentRowTwo][currentColTwo + 1] != BLUECAN)
			{
				currentColTwo++;
			}
			else if (event.getKeyCode() == KeyEvent.VK_UP && currentRowTwo > 0
					&& grid[currentRowTwo - 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo - 1][currentColTwo] != CRATE
					&& grid[currentRowTwo - 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo - 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo--;
			}
			else if (event.getKeyCode() == KeyEvent.VK_DOWN
					&& currentRowTwo < grid.length - 1
					&& grid[currentRowTwo + 1][currentColTwo] != BUILDING
					&& grid[currentRowTwo + 1][currentColTwo] != CRATE
					&& grid[currentRowTwo + 1][currentColTwo] != REDCAN
					&& grid[currentRowTwo + 1][currentColTwo] != BLUECAN)
			{
				currentRowTwo++;
			}
			// Places a can
			else if (event.getKeyCode() == KeyEvent.VK_SLASH
					&& currentRowOne != currentRowTwo
					&& currentColOne != currentColTwo)
			{
				placeCan(playerTwo, currentRowTwo, currentColTwo);
			}

			// Repaint the screen after the change
			repaint();
		}
	}
}
