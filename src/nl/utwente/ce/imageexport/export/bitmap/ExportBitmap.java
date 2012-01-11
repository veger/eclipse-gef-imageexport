package nl.utwente.ce.imageexport.export.bitmap;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import nl.utwente.ce.imageexport.IImageFormatProvider;

public class ExportBitmap implements IImageFormatProvider
{
    @Override
    public void provideSettings(String formatID, Composite container)
    {
    }

    @Override
    public void exportImage(String formatID, String filename, GraphicalViewer graphicalViewer)
    {
        int format;
        if (formatID.equals("Bitmap.JPEG"))
        {
            format = SWT.IMAGE_JPEG;
        }
        else
        {
            return; // Unknown format...?!
        }

        LayerManager layerManager = (LayerManager) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
        IFigure rootFigure = layerManager.getLayer(LayerConstants.PRINTABLE_LAYERS);

        Rectangle minimumBounds = getBounds(rootFigure);

        Image img = new Image(Display.getDefault(), minimumBounds.width, minimumBounds.height);
        GC gc = new GC(img);

        SWTGraphics swtGraphics = new SWTGraphics(gc);
        swtGraphics.translate(minimumBounds.x * -1, minimumBounds.y * -1);
        rootFigure.paint(swtGraphics);

        ImageLoader imgLoader = new ImageLoader();
        imgLoader.data = new ImageData[] { img.getImageData() };
        imgLoader.save(filename, format);

        swtGraphics.dispose();
        gc.dispose();
        img.dispose();
    }

    /** Recursively (partly at least) finds the minimum bounds of the given figure */
    private Rectangle getBounds(IFigure figure)
    {
        Rectangle minimumBounds = null;
        for (Object layer : figure.getChildren())
        {
            Rectangle bounds;
            if (layer instanceof FreeformLayer || layer instanceof ConnectionLayer)
            {
                bounds = getBounds((IFigure) layer);
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
