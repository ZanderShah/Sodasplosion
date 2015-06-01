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
	private int currentCans = 1;

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
	 * @param player the given player
	 * @param type the given item type
	 */
	public void addPower(Player player, int type)
	{
		if (powerUps[type] < 5)
		{
			powerUps[type]++;
			if (type == 2)
				currentCans++;
		}
	}
	
	/**
	 * Returns the given player's sodasplosion range
	 * 
	 * @param player the given player
	 * @return the sodasplosion range of the given player
	 */
	public int checkRange(Player player)
	{
		return powerUps[2];
	}
	
	/**
	 * Checks if the player has enough current cans to place a can
	 */
	public boolean canPlaceCan()
	{
		if (currentCans > 0)
		{
			currentCans--;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a can to the player after the sodasplosion is done
	 */
	public void returnCan()
	{
		currentCans++;
	}
}
