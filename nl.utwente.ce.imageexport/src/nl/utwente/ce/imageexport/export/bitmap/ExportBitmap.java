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
package nl.utwente.ce.imageexport.export.bitmap;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import nl.utwente.ce.imageexport.IImageFormatProvider;
import nl.utwente.ce.imageexport.Utils;

public class ExportBitmap implements IImageFormatProvider
{
    @Override
    public void provideSettings(String formatID, Composite container, IPreferenceStore store)
    {
    }

    @Override
    public void storePreferences(IPreferenceStore store)
    {
    }

    @Override
    public void exportImage(String formatID, String filename, IFigure figure)
    {
        int format;
        if (formatID.equals("Bitmap.JPEG"))
        {
            format = SWT.IMAGE_JPEG;
        }
        else if (formatID.equals("Bitmap.PNG"))
        {
            format = SWT.IMAGE_PNG;
        }
        else

        {
            return; // Unknown format...?!
        }

        Rectangle minimumBounds = Utils.getMinimumBounds(figure);
        Image img;
        if (minimumBounds == null)
        {
            img = new Image(Display.getDefault(), 10, 10);
        }
        else
        {
            img = new Image(Display.getDefault(), minimumBounds.width, minimumBounds.height);
        }
        GC gc = new GC(img);

        SWTGraphics swtGraphics = new SWTGraphics(gc);
        if (minimumBounds != null)
        {
            // Reset origin to make it the top/left most part of the diagram
            swtGraphics.translate(minimumBounds.x * -1, minimumBounds.y * -1);
            Utils.paintDiagram(swtGraphics, figure);
        }

        ImageLoader imgLoader = new ImageLoader();
        imgLoader.data = new ImageData[] { img.getImageData() };
        imgLoader.save(filename, format);

        swtGraphics.dispose();
        gc.dispose();
        img.dispose();
    }
}
