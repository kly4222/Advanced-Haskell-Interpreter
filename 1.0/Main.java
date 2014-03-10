import java.io.IOException;

/**
 * A Haskell enhanced interpreter written in Java.
 *
 * @author    Kevin Ly
 * @version    1.0    (30/10/2013)
 */
public class Main
{
    /**
     * The main method.
     * @param args The arguments given to the program by the user.
     */
    static public void main(String[] args)
    {
        ProcessBuilder builder = new ProcessBuilder("ghci");
        builder.redirectErrorStream(true);

        try {
            // Start threads to read from/write to ghci process.
            Process ghci = builder.start();
            GHCiOutputThread ghciOutput = new GHCiOutputThread(ghci.getInputStream());
            GHCiInputThread ghciInput = new GHCiInputThread(ghci.getOutputStream());
            
            ghciOutput.start();
            ghciInput.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
