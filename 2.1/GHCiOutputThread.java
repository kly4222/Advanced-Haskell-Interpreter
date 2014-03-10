import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
* Thread that deals with printing the output of the ghci.
*
* @author    Kevin Ly
* @version    2.1    (05/02/2014)
*/
public class GHCiOutputThread extends Thread
{
    private final String EXITING = "Leaving GHCi.";
    private final String LOADED = "Ok, modules loaded: ";
    
    private BufferedReader ghciOutput;
    private String currentLine;
    private ArrayList<String> output;
    private TestWriter tw;
    private boolean running;

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
        output = new ArrayList<String>();
        currentLine = "";
        running = true;
        this.tw = tw;
    }

    /**
     * Executes this thread.
     * GHCiOutputThread prints out the output of the GHCi to standard output.
     */
    public void run()
    {
        try {
            while (running) {
                readOutput();
            }
        }
        catch (IOException e) {
        }
    }

    /**
     * Checks if a string is the prompt indicator.
     * @param s The string to check.
     * @return True if s is a prompt, else false.
     */
    public boolean isPrompt(String s)
    {
        return s.endsWith("> ") || s.endsWith("| ");
    }
    
    /**
     * Print the output of the ghci.
     * @throws IOException If an error occurs with I/O.
     */
    public void readOutput() throws IOException
    {
        // Prints the output of the ghci line-by-line.
        do {
            // Read character and concatenate with currentLine variable.
            char c = (char) ghciOutput.read();

            // Check for any character except the new line character.
            if (c != '\n') {
                currentLine += c;
                
                // Check for ghci prompt.
                if (isPrompt(currentLine)) {
                    System.out.print(currentLine);
                    currentLine = "";
                    output.clear();
                }
            }
            // Character is a new line.
            else {
                // Check for ghci exit condition.
                if (currentLine.equals(EXITING)) {
                    running = false;
                }
                
                // Line is just a normal ouput.
                System.out.println(currentLine);
                output.add(currentLine);
                currentLine = "";
            }
        }
        while (ghciOutput.ready());
    }
}
