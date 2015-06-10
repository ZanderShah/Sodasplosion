import java.awt.*;
import javax.swing.*;

/**
 * Main program for Sodasplosion
 *
 * @author Alexander Shah and Amy Zhang
 * @version May 27, 2015
 */
public class SodasplosionMain extends JFrame
{
	SodasplosionGrid sodasplosionGrid;

	/**
	 * Constructs a new SodasplosionMain frame
	 */
	public SodasplosionMain()
	{
		// Set up the frame and the grid
		super();
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("img/Sodasplosion.png"));
		sodasplosionGrid = new SodasplosionGrid();
		add(new SodasplosionGrid(), BorderLayout.CENTER);
	}

	/**
	 * Starts up the SodaSplosionMain frame
	 * 
	 * @param args An array of Strings (ignored)
	 */
	public static void main(String[] args)
	{
		SodasplosionMain frame = new SodasplosionMain();
	    frame.setUndecorated(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
