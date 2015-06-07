import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A help-screen demo that also shows how to draw images and move a single
 * player using the keyboard. NOTE: you must have the "Images" folder (and all
 * its images) in your project folder
 * @author: ICS3U
 * @version: 2009 ICS3U Updated December 2014
 */

public class Menu extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainMenu menu;

	/**
	 * Constructs a HelpDemo frame and sets up the game to start.
	 */
	public Menu()
	{

		// Sets up the game frame
		super("Menu");
		setLocation(100, 50);
		setResizable(false);

		// Image for the HelpDemo icon
		setIconImage(Toolkit.getDefaultToolkit().getImage("Can.png"));

		// create and add the drawing panel.
		menu = new MainMenu();
		add(menu, BorderLayout.CENTER);
	}

	// The main is the starting point of the program and constructs the game.
	public static void main(String[] args)
	{
		Menu frame = new Menu ();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	} // Main method
} // class
