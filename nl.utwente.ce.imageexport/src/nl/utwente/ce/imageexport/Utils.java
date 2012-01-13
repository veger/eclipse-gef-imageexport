package nl.utwente.ce.imageexport;

import java.io.File;
import java.io.IOException;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

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

    /**
     * Recursively (partly at least) finds the minimum bounds of the given figure by looking at the bounds of the sub
     * figures
     */
    public static Rectangle getMinimumBounds(IFigure figure)
    {
        Rectangle minimumBounds = null;
        for (Object layer : figure.getChildren())
        {
            Rectangle bounds;
            if (layer instanceof FreeformLayer || layer instanceof ConnectionLayer)
            {
                bounds = getMinimumBounds((IFigure) layer);
            }
            else
            {
                bounds = ((IFigure) layer).getBounds();
            }
            if (minimumBounds == null)
            {
                minimumBounds = bounds;
            }
            else
            {
                minimumBounds.union(bounds);
            }
        }
        return minimumBounds;
    }
}
