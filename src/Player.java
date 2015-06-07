import java.awt.Color;

/**
 * Creates a player object
 *
 * @author Alexander Shah
 * @version Jun 1, 2015
 */
public class Player
{
	private int[] powerUps;
	private int currentCans;
	

	/**
	 * Constructs a player object, starting all power ups at 1
	 */
	public Player()
	{
		// 1 is speed
		// 2 is strength
		// 3 is total cans
		powerUps = new int[4];
		powerUps[1] = 1;
		powerUps[2] = 1;
		powerUps[3] = 1;
		this.currentCans = 1;
	}

	/**
	 * Adds one power level to the player's given skill as long as it isn't
	 * capped
	 * 
	 * @param type the given item type
	 */
	public void addPower(int type)
	{
		if (powerUps[type] < 5)
		{
			powerUps[type]++;
			if (type == 3)
				currentCans++;
		}
	}
	
	/**
	 * Returns the given player's speed
	 */
	public int getSpeed()
	{
		return powerUps[1];
	}
	
	/**
	 * Returns the given player's sodasplosion range
	 */
	public int getRange()
	{
		return powerUps[2];
	}
	
	/**
	 * Returns the given player's total number of cans
	 */
	public int getTotalCans()
	{
		return powerUps[3];
	}
	
	/**
	 * Returns the given player's current cans
	 */
	public int getCurrentCans()
	{
		return currentCans;
	}
	
	/**
	 * Removes a can from the given player
	 */
	public void placeCan()
	{
		currentCans--;
	}

	/**
	 * Returns a can to the given player
	 */
	public void returnCan()
	{
		currentCans++;
	}
}
