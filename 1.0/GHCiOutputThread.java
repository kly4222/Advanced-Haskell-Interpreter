import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Thread that deals with printing the output of the ghci.
 *
 * @author    Kevin Ly
 * @version    1.0    (30/10/2013)
 */
public class GHCiOutputThread extends Thread
{
    private BufferedReader ghciOutput;
    private boolean running;

    /**
     * Constructor for the GHCiOutputThread thread.
     * @param input The input stream used to read the output of the GHCi.
     */
    public GHCiOutputThread(InputStream input)
    {
        ghciOutput = new BufferedReader(new InputStreamReader(input));
        running = true;
    }

    /**
     * Executes this thread.
     * GHCiOutputThread prints out the output of the GHCi to standard output.
     */
    public void run()
    {
        while (running) {
            try {
                String s = "";
                
                // Read from the output of the ghci.
                while (ghciOutput.ready()) {
                    char i = (char) ghciOutput.read();
                    s += i;
                }
                System.out.print(s);
                
                // Check ghci exit condition.
                if (s.endsWith("Leaving GHCi.\n")) {
                    running = false;
                    ghciOutput.close();
                }
            }
            catch (IOException e) {
            }
        }
    }
}
