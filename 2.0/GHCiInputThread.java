import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Thread that deals with printing the output of the ghci.
 *
 * @author    Kevin Ly
 * @version    2.0    (14/01/2014)
 */
public class GHCiInputThread extends Thread
{
    private BufferedWriter ghciInput;
    private LogWriter lw;
    private boolean running;
    
    /**
     * Constructor for the GHCiInputThread thread. 
     * @param name The name given to this thread.
     * @param output The output stream used to write to the GHCi.
     * @param lw The LogWriter class that will record tests.
     */
    public GHCiInputThread(String name, OutputStream output, LogWriter lw)
    {
        super(name);
        
        ghciInput = new BufferedWriter(new OutputStreamWriter(output));
        this.lw = lw;
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
                
                if (cmd.startsWith(":q")) { // Check for ghci exit condition.
                    running = false;
                }
                else if (cmd.startsWith(":l")) { // Check for ghci load condition.
                    lw.tempSetLogFilePath(cmd);
                }
                else if (lw.isWritingToLog()) {
                    lw.writeInput(cmd);
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
