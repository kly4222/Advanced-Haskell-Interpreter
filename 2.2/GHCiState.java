/**
 * Enumerated class representing the different states of the GHCi.
 * 
 * @author Kevin Ly
 * @version 2.2 (10/02/2014)
 */
public enum GHCiState {
    PRELUDE,
    LOADING,
    MODULES_LOADED,
    NO_MODULES_LOADED,
    OUTPUTTING,
    QUITTING
}
