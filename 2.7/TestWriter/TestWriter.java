package TestWriter;

/**
 * @author Kevin Ly
 * @version 1.0 (07/03/2014)
 */
public abstract class TestWriter
{
    private String filePath;
    
    public TestWriter(String filePath)
    {
        setFilePath(filePath);
    }
    
    /**
     * Set the new file path that this TestWriter istnace writes to.
     * @param filePath    The file path the TestWrtier will write to.
     */
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    /**
     * Get the file path to the log file.
     * @return    The file path this TestWriter instance writes to.
     */
    public String getFilePath()
    {
        return filePath;
    }
    
    /**
     * Creates a new file which the TestWriter instance will write to.
     */
    abstract protected void createNewFile();
}
