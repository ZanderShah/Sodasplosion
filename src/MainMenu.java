import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class MainMenu extends JPanel{

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
	
	public MainMenu()
	{
		setPreferredSize(new Dimension(1024, 768)); // set size of this panel
		
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

		// Add mouse listeners to the drawing panel
		this.addMouseListener(new MouseHandler());
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
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		
		if (menu == MAIN_MENU)
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
			
			if (start == 1)
				g.drawRect(100, 100, 100, 100);
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
