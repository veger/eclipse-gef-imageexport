package nl.utwente.ce.imageexport.export.bitmap;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
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
    public void provideSettings(String formatID, Composite container)
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

        Image img = new Image(Display.getDefault(), minimumBounds.width, minimumBounds.height);
        GC gc = new GC(img);

        SWTGraphics swtGraphics = new SWTGraphics(gc);
        // Reset origin to make it the top/left most part of the diagram
        swtGraphics.translate(minimumBounds.x * -1, minimumBounds.y * -1);
        Utils.paintDiagram(swtGraphics, figure);

        ImageLoader imgLoader = new ImageLoader();
        imgLoader.data = new ImageData[] { img.getImageData() };
        imgLoader.save(filename, format);

        swtGraphics.dispose();
        gc.dispose();
        img.dispose();
    }
}
