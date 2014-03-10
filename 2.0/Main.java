import java.io.IOException;

/**
 * A Haskell enhanced interpreter written in Java.
 *
 * @author    Kevin Ly
 * @version    2.0    (14/01/2014)
 */
public class Main
{
    /**
     * The main method.
     */
    static public void main(String[] args)
    {
        ProcessBuilder builder = new ProcessBuilder("ghci");
        builder.redirectErrorStream(true);
        LogWriter lw = new LogWriter();
        
        try {
            // Start threads to read from/write to ghci process.
            Process ghci = builder.start();
            GHCiOutputThread ghciOutput = new GHCiOutputThread("Output", ghci.getInputStream(), lw);
            GHCiInputThread ghciInput = new GHCiInputThread("Input", ghci.getOutputStream(), lw);
            
            // Execute ghci threads.
            ghciOutput.start();
            ghciInput.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
