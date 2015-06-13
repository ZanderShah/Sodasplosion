/**
 * Creates a player object
 *
 * @author Alexander Shah and Amy Zhang
 * @version Jun 1, 2015
 */
public class Player
{
	private final int LIVES = 1;
	private final int RANGE = 2;
	private final int TOTAL_CANS = 3;
	
	private int[] powerUps;
	private int currentCans, noOfWins;

	/**
	 * Constructs a player object, starting all power ups at 1
	 */
	public Player()
	{
		// 0 is left empty so that the power-ups do not interfere with the
		// empty tiles
		powerUps = new int[4];
		powerUps[LIVES] = 1;
		powerUps[RANGE] = 1;
		powerUps[TOTAL_CANS] = 1;
		
		currentCans = 1;
		noOfWins = 0;
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
			if ((type == RANGE || type == TOTAL_CANS) && powerUps[type] < 5)
			{
				powerUps[type]++;
				if (type == TOTAL_CANS)
				{
					currentCans++;
				}
			}
			else if (type == LIVES && powerUps[type] < 3)
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
		return powerUps[LIVES];
	}

	/**
	 * Takes away a life from the given player
	 */
	public void loseLife()
	{
		powerUps[LIVES]--;
	}

	/**
	 * Gets the given player's blast range
	 * 
	 * @return the given player's blast range
	 */
	public int getRange()
	{
		return powerUps[RANGE];
	}

	/**
	 * Get the given player's total number of cans
	 * 
	 * @return the given player's total number of cans
	 */
	public int getTotalCans()
	{
		return powerUps[TOTAL_CANS];
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

	/**
	 * Resets all the power levels for the given player 
	 * to prepare for a new round or game
	 */
	public void resetPower()
	{
		powerUps[LIVES] = 1;
		powerUps[RANGE] = 1;
		powerUps[TOTAL_CANS] = 1;
		currentCans = 1;
	}
	
	/**
	 * Resets the number of wins to prepare for a new game
	 */
	public void resetWins()
	{
		noOfWins = 0;
	}
}
