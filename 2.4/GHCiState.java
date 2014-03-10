/**
 * Enumerated class representing the different states of the GHCi.
 * 
 * @author Kevin Ly
 * @version 2.4 (12/2/2014)
 */
public enum GHCiState {
    // In state
    PRELUDE, 
    LOADING, 
    MODULES_LOADED,
    QUITTING
}