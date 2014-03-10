import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

/**
* Thread that deals with sending user commands to the GHCi.
*
* @author    Kevin Ly
* @version    1.0    (30/10/2013)
*/
public class GHCiInputThread extends Thread
{
    private BufferedWriter ghciInput;
    private boolean running;

    /**
     * Constructor for the GHCiInputThread thread.
     * @param output The output stream used to write to the GHCi.
     */
    public GHCiInputThread(OutputStream output)
    {
        ghciInput = new BufferedWriter(new OutputStreamWriter(output));
        running = true;
    }

    /**
     * Execute the GHCiInput thread.
     */
    public void run()
    {
        try {
            Scanner scanner = new Scanner(System.in);
            
            while (running) {
                // Read user input.
                String cmd = scanner.nextLine();
                
                // Check for ghci exit condition.
                if (cmd.equals(":q")) {
                    running = false;
                }
                // Send command to ghci process.
                ghciInput.write(cmd + '\n');
                ghciInput.flush();
            }
            // ghci has exited, so close all I/O streams.
            scanner.close();
            ghciInput.close();
        }
        catch (IOException e) {
        }
    }
}
