package nl.utwente.ce.imageexport;

import java.io.File;
import java.io.IOException;

/** Utility class for ImageExport and its plugins */
public abstract class Utils
{
    private final static String HOMEDIR_START = "~" + File.separatorChar;

    /** @return the given filename as a sanitized and absolute path */
    public static String sanitizePath(File f)
    {
        String filename = f.getPath();
        if (filename.startsWith(HOMEDIR_START))
        {
            // Expand with absolute path to home directory
            f = new File(System.getProperty("user.home") + File.separatorChar
                    + filename.substring(HOMEDIR_START.length()));
        }
        try
        {
            filename = f.getCanonicalPath();
        } catch (IOException e)
        {
            filename = f.getAbsolutePath();
        }
        return filename;
    }
}
