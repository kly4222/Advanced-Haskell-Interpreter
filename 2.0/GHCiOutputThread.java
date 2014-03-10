import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Thread that deals with printing the output of the ghci.
 *
 * @author    Kevin Ly
 * @version    2.0    (14/01/2014)
 */
public class GHCiOutputThread extends Thread
{
    private BufferedReader ghciOutput;
    private LogWriter lw;
    private ArrayList<String> output;
    private String currentLine;
    private boolean running;

    /**
     * Constructor for the GHCiOutputThread thread.
     * @param name The name given to this particular thread.
     * @param input The input stream used to read the output of the GHCi.
     * @param lw The LogWriter object.
     */
    public GHCiOutputThread(String name, InputStream input, LogWriter lw)
    {
        super(name);

        ghciOutput = new BufferedReader(new InputStreamReader(input));
        this.lw = lw;
        running = true;
        output = new ArrayList<String>();
        currentLine = "";
    }

    /**
     * Executes this thread.
     * GHCiOutputThread prints out the output of the GHCi to standard output.
     */
    public void run()
    {
        try {
            while (running) {
                do {
                    char c = (char) ghciOutput.read();

                    if (c == '\n') {
                        currentLine += c;
                        System.out.print(currentLine);

                        if (currentLine.equals("Leaving GHCi.\n")) {
                            running = false;
                        }
                        else if (currentLine.startsWith("Ok, modules loaded: ")) {
                            lw.createNewLogFile();
                        }
                        else {
                            output.add(currentLine);
                            currentLine = "";
                        }
                    }
                    else {
                        currentLine += c;
                    }
                }
                while (ghciOutput.ready());

                if (isPrompt(currentLine)) {
                    System.out.print(currentLine);
                    currentLine = "";

                    if (lw.isWritingToLog()) {
                        String line = output.get(0);

                        if (!line.equals('\n')) {
                            lw.writeOutput(line);
                        }
                    }
                    output.clear();
                }
            }
            ghciOutput.close();
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
}
