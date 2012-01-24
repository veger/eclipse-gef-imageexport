/*
 * This file is part of ImageExport.
 *
 * ImageExport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImageExport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ImageExport.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.utwente.ce.imageexport;

import java.io.File;
import java.io.IOException;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
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
            if (layer instanceof FreeformLayer)
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
        // Add a padding of 2 pixels and return
        return minimumBounds.expand(2, 2);
    }

    /** Paints the figure onto the given graphics */
    public static void paintDiagram(Graphics g, IFigure figure)
    {
        // We want to ignore the first FreeformLayer (or we lose also all figure, as it draws the 'page boundaries'
        // which is obviously not wanted in the exported images.
        for (Object child : figure.getChildren())
        {
            // ConnectionLayer inherits from FreeformLayer, so rather checking for FreeformLayer we check whether child
            // is not a ConnectionLayer!
            if (child instanceof FreeformLayer && !(child instanceof ConnectionLayer))
            {
                paintDiagram(g, (IFigure) child);
            }
            else
            {
                ((IFigure) child).paint(g);
            }
        }
    }
}
