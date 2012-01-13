package nl.utwente.ce.imagexport.export.svg;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import nl.utwente.ce.imageexport.IImageFormatProvider;
import nl.utwente.ce.imagexport.export.svg.utils.GraphicsToGraphics2DAdaptor;

public class ExportSVG implements IImageFormatProvider
{

    @Override
    public void provideSettings(String formatID, Composite container)
    {
    }

    @Override
    public void exportImage(String formatID, String filename, GraphicalViewer graphicalViewer)
    {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // We need a converter from Draw2D.Graphics (GEF) to awt.Graphics2D (Batik)
        GraphicsToGraphics2DAdaptor graphicsAdaptor = new GraphicsToGraphics2DAdaptor(svgGenerator);

        LayerManager layerManager = (LayerManager) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
        IFigure rootFigure = layerManager.getLayer(LayerConstants.PRINTABLE_LAYERS);
        rootFigure.paint(graphicsAdaptor);
        
        try
        {
            svgGenerator.stream(filename);
        } catch (SVGGraphics2DIOException e)
        {
            // TODO Something went wrong, we probably want to alert the user...
        }
    }
}
