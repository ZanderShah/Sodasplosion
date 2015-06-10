/**
 * Creates a player object
 *
 * @author Alexander Shah and Amy Zhang
 * @version Jun 1, 2015
 */
public class Player
{
	private int[] powerUps;
	private int currentCans, noOfWins;

	/**
	 * Constructs a player object, starting all power ups at 1
	 */
	public Player()
	{
		// 0 is left empty so that the power-ups do not interfere with the
		// empty tiles
		// 1 is number of lives
		// 2 is strength
		// 3 is total cans
		powerUps = new int[4];
		powerUps[1] = 1;
		powerUps[2] = 1;
		powerUps[3] = 1;
		currentCans = 1;
		noOfWins = 1;
	}

	/**
	 * Adds one power level to the player's given skill as long as it isn't
	 * already at max power
	 * 
	 * @param type the given item type
	 */
	public void addPower(int type)
	{
		if (powerUps[type] < 5)
		{
			if ((type == 2 || type == 3) && powerUps[type] < 5)
			{
				powerUps[type]++;
				if (type == 3)
				{
					currentCans++;
				}
			}
			else if (type == 1 && powerUps[type] < 3)
			{
				powerUps[type]++;	
			}	
		}
	}

	/**
	 * Get the given player's current number of lives
	 * 
	 * @return the given player's current number of lives
	 */
	public int getNoOfLives()
	{
		return powerUps[1];
	}

	/**
	 * Takes away a life from the given player
	 */
	public void loseLife()
	{
		powerUps[1]--;
	}
	
	/**
	 * Gets the given player's blast range
	 * 
	 * @return the given player's blast range
	 */
	public int getRange()
	{
		return powerUps[2];
	}

	/**
	 * Get the given player's total number of cans
	 * 
	 * @return the given player's total number of cans
	 */
	public int getTotalCans()
	{
		return powerUps[3];
	}

	/**
	 * Get the given player's current cans
	 * 
	 * @return the given player's current cans
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
	
	/**
	 * Adds one to the given player's number of wins
	 */
	public void winRound()
	{
		noOfWins++;
	}
	
	/**
	 * Gets the given player's number of wins
	 * 
	 * @return the given player's number of wins
	 */
	public int getNoOfWins()
	{
		return noOfWins;
	}
}
