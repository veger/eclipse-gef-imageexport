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
package nl.utwente.ce.imagexport.export.svg.utils;

import java.awt.Graphics2D;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Path;

/**
 * {@link GraphicsToGraphics2DAdaptor} implementation that disables clipping. This simplifies the exported (SVG) image
 * and reduces the file size.
 */
public class NoClippingGraphics extends GraphicsToGraphics2DAdaptor
{
    public NoClippingGraphics(Graphics2D graphics)
    {
        super(graphics);
    }

    @Override
    public void setClip(Rectangle rect)
    {
        // Ignore, do not set clipping area
    }

    @Override
    public void setClip(Path path)
    {
        // Ignore, do not set clipping area
    }

    @Override
    public void clipPath(Path path)
    {
        // Ignore, do not set clipping area
    }

    @Override
    public void clipRect(Rectangle rect)
    {
        // Ignore, do not set clipping area
    }
}
