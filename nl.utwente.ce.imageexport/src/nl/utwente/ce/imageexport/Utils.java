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

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IClippingStrategy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
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
                minimumBounds = new Rectangle(bounds);
            }
            else
            {
                minimumBounds.union(bounds);
            }
        }
        if (minimumBounds != null)
        {
            // Add a padding of 2 pixels and return
            minimumBounds.expand(2, 2);
        }

        return minimumBounds;
    }

    /** Paints the figure onto the given graphics */
    public static void paintDiagram(Graphics g, IFigure figure)
    {
        // Store state, so modified state of Graphics (while painting children) can be easily restored
        g.pushState();
        try
        {
            IClippingStrategy clippingStrategy = figure.getClippingStrategy();

            // Iterate over the children to check whether a child is a(nother) layer or an actual figure
            // Not painting the layers themselves is likely to get rid of borders and graphics settings that are not
            // supported (like Graphics#setTextAntiAliassing())
            for (Object childObject : figure.getChildren())
            {
                if (childObject instanceof Layer)
                {
                    // Found another layer, process it to search for actual figures
                    paintDiagram(g, (IFigure) childObject);
                }
                else
                {
                    // Found something to draw
                    // Use same/similar method as being using in Figure#paintChildren() in order to get clipping right
                    IFigure child = (IFigure) childObject;
                    if (child.isVisible())
                    {
                        // determine clipping areas for child
                        Rectangle[] clipping = null;
                        if (clippingStrategy != null)
                        {
                            clipping = clippingStrategy.getClip(child);
                        }
                        else
                        {
                            // default clipping behaviour is to clip at bounds
                            clipping = new Rectangle[] { child.getBounds() };
                        }
                        // child may now paint inside the clipping areas
                        for (int j = 0; j < clipping.length; j++)
                        {
                            if (clipping[j].intersects(g.getClip(Rectangle.SINGLETON)))
                            {
                                g.clipRect(clipping[j]);
                                child.paint(g);
                                g.restoreState();
                            }
                        }
                    }
                }
            }
        } finally
        {
            // Always pop the state again to prevent problems
            g.popState();
        }
    }
}
