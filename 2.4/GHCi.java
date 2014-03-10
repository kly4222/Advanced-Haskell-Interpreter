
/**
 * A GHCi instance is used to represent the state of the external GHCi process
 * at any given time.
 * 
 * @author Kevin Ly
 * @version 2.4 (12/02/2014)
 */
public class GHCi
{
    private GHCiState currentState;
    
    /**
     * Constructor for the GHCi class.
     */
    public GHCi()
    {
        currentState = GHCiState.PRELUDE;
    }
    
    /**
     * Sets the new state of the GHCi when inputting.
     * @param newState    The new GHCi state.
     */
    public void setState(GHCiState newState)
    {
        currentState = newState;
    }
    
    /**
     * Returns the current state of the GHCi.
     * @return    The current state of the GHCi.
     */
    public GHCiState getState()
    {
        return currentState;
    }
}
