import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

/**
 * A Haskell enhanced interpreter written in Java.
 *
 * @author Kevin Ly
 * @version 2.7 (18/2/2014)
 */
public class Main
{
    /**
     * The main method.
     * @param args    The arguments given to the program by the user.
     */
    static public void main(String[] args)
    {
        ProcessBuilder builder = new ProcessBuilder("ghci");
        builder.redirectErrorStream(true);
        
        try {
            // Start threads to read from/write to ghci process.
            Process ghci = builder.start();
            AdvancedHaskellInterpreter ahi = new AdvancedHaskellInterpreter();
            GHCiOutputThread ghciOutput = new GHCiOutputThread("Output", new InputStreamReader(ghci.getInputStream()), ahi);
            GHCiInputThread ghciInput = new GHCiInputThread("Input", new OutputStreamWriter(ghci.getOutputStream()), ahi);
            
            // Execute ghci threads.
            ghciOutput.start();
            ghciInput.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
