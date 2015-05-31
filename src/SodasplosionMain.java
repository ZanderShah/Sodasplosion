import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * Main program for Sodasplosion
 *
 * @author Alexander Shah
 * @version May 27, 2015
 */
public class SodasplosionMain extends JFrame
{
	SodasplosionGrid sodasplosionGrid;

	public SodasplosionMain()
	{
		// Set up the frame and the grid
		super("Sodasplosion");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("connectFourIcon.png"));

		// Set up for the maze area	
		sodasplosionGrid = new SodasplosionGrid();
		add(new SodasplosionGrid(), BorderLayout.CENTER);
	}

	// Sets up the main frame for the game
	public static void main(String[] args)
	{
		SodasplosionMain frame = new SodasplosionMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
