import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
* Thread that deals with printing the output of the ghci.
*
* @author Kevin Ly
* @version 2.4 (12/02/2014)
*/
public class GHCiOutputThread extends Thread
{
    private final String EXITING = "Leaving GHCi.\n";
    private final String NO_MODULES_LOADED = "Ok, modules loaded: none.\n";
    private final String LOAD_SUCCESSFUL = "Ok, modules loaded: ";
    private final String LOAD_UNSUCCESSFUL = "Failed, modules loaded: ";

    private BufferedReader ghciOutput;
    private String currentLine;
    private AdvancedHaskellInterpreter ahi;

    /**
     * Constructor for the GHCiOutputThread thread.
     * @param name    The name given to this particular thread.
     * @param in    The input stream used to read the output of the GHCi.
     * @param tw    The test writer object.
     */
    public GHCiOutputThread(String name, InputStreamReader in, AdvancedHaskellInterpreter ahi)
    {
        super(name);
        ghciOutput = new BufferedReader(in);
        currentLine = "";
        this.ahi = ahi;
    }

    /**
     * Executes this thread.
     * GHCiOutputThread prints out the output of the GHCi to standard output.
     */
    public void run()
    {
        try {
            loop: while (true) {
                // Prints the output of the GHCi line-by-line.
                do {
                    // Read character and concatenate with currentLine variable.
                    char c = (char) ghciOutput.read();
                    currentLine += c;
                    
                    // Check if a complete line has been outputted by the GHCi.
                    if (c == '\n') {
                        // Check the contents of the current line.
                        switch (ahi.getGHCiState()) {
                        case QUITTING:
                            if (currentLine.equals(EXITING)) {
                                System.out.print(EXITING);
                                break loop;
                            }
                        case LOADING:
                            if (currentLine.equals(NO_MODULES_LOADED) || currentLine.startsWith(LOAD_UNSUCCESSFUL)) {
                                ahi.setGHCiState(GHCiState.PRELUDE);
                                ahi.reset();
                            }
                            else if (currentLine.startsWith(LOAD_SUCCESSFUL)) {
                                ahi.setGHCiState(GHCiState.MODULES_LOADED);
                            }
                            break;
                        default:
                            break;
                        }
                        System.out.print(currentLine);
                        currentLine = "";
                    }
                }
                while (ghciOutput.ready());
                
                ahi.setPrinting(false);
                
                if (ahi.isRecording()) {
                    if (isPrompt(currentLine)) {
                        
                        currentLine = "";    // GHCi prompt will be printed when Haskell's appendFile is called.
                    }
                }
                if (isPrompt(currentLine)) {
                    if (!currentLine.equals(ahi.getPrompt())) {
                        ahi.setPrompt(currentLine);
                    }
                    System.out.print(currentLine);
                    currentLine = "";
                }
                
            }
            ghciOutput.close();
        }
        catch (IOException e) {
        }
    }

    /**
     * Checks if a string is the prompt indicator.
     * @param s    The string to check.
     * @return    True if s is a prompt, else false.
     */
    public boolean isPrompt(String s)
    {
        return s.endsWith("> ") || s.endsWith("| ");
    }
}
