import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
* Thread that deals with printing the output of the ghci.
*
* @author Kevin Ly
* @version 2.2 (10/02/2014)
*/
public class GHCiOutputThread extends Thread
{
    private final String EXITING = "Leaving GHCi.\n";
    private final String NO_MODULES_LOADED = "Ok, modules loaded: none.\n";
    private final String LOAD_SUCCESSFUL = "Ok, modules loaded: ";
    private final String LOAD_UNSUCCESSFUL = "Failed, modules loaded: ";

    private BufferedReader ghciOutput;
    private String currentLine;
    private TestWriter tw;
    private boolean printing;

    /**
     * Constructor for the GHCiOutputThread thread.
     * @param name The name given to this particular thread.
     * @param in The input stream used to read the output of the GHCi.
     * @param tw The test writer object.
     */
    public GHCiOutputThread(String name, InputStreamReader in, TestWriter tw)
    {
        super(name);
        ghciOutput = new BufferedReader(in);
        currentLine = "";
        this.tw = tw;
        printing = false;
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
                        switch (tw.getState()) {
                        case QUITTING:
                            if (currentLine.equals(EXITING)) {
                                System.out.print(currentLine);
                                break loop;
                            }
                        case LOADING:
                            if (currentLine.equals(NO_MODULES_LOADED)) {
                                tw.setState(GHCiState.PRELUDE);
                            }
                            else if (currentLine.startsWith(LOAD_SUCCESSFUL)) {
                                tw.setState(GHCiState.MODULES_LOADED);
                            }
                            else if (currentLine.startsWith(LOAD_UNSUCCESSFUL)) {
                                tw.setState(GHCiState.NO_MODULES_LOADED);
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
                
                tw.setPrinting(false);
                
                if (isPrompt(currentLine)) {
                    if (tw.isRecording()) {
                        while (tw.isBlocking()) {
                            ;
                        }
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
