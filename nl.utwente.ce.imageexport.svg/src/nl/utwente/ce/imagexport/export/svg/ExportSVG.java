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
package nl.utwente.ce.imagexport.export.svg;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import nl.utwente.ce.imageexport.IImageFormatProvider;
import nl.utwente.ce.imageexport.Utils;
import nl.utwente.ce.imagexport.export.svg.utils.GraphicsToGraphics2DAdaptor;
import nl.utwente.ce.imagexport.export.svg.utils.NoClippingGraphics;

public class ExportSVG implements IImageFormatProvider
{
    private Button disableClippingButton;

    @Override
    public void provideSettings(String formatID, Composite container, IPreferenceStore store)
    {
        container.setLayout(new GridLayout());

        disableClippingButton = new Button(container, SWT.CHECK);
        disableClippingButton.setText("Disable clipping regions");
        disableClippingButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        disableClippingButton.setSelection(store.getBoolean(PreferenceConstants.SVG_DISABLE_CLIPPING));
        disableClippingButton
                .setToolTipText("Disabling clipping reduces the complexity and size of the exported SVG image.\n\n"
                        + "Use at own risk! Most (properly) designed figures do not require clipping as they do not wrongly paint outside their 'boundaries'.");

    }

    @Override
    public void storePreferences(IPreferenceStore store)
    {
        store.setValue(PreferenceConstants.SVG_DISABLE_CLIPPING, disableClippingButton.getSelection());
    }

    @Override
    public void exportImage(String formatID, String filename, IFigure figure) throws SVGGraphics2DIOException
    {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // We need a converter from Draw2D.Graphics (GEF) to awt.Graphics2D (Batik)
        Graphics graphics;
        if (disableClippingButton.getSelection())
        {
            // Use a no clipping adapter
            graphics = new NoClippingGraphics(svgGenerator);
        }
        else
        {
            // Use regular adapter
            graphics = new GraphicsToGraphics2DAdaptor(svgGenerator);
        }

        Rectangle minimumBounds = Utils.getMinimumBounds(figure);
        if (minimumBounds != null)
        {
            // Reset origin to make it the top/left most part of the diagram
            graphics.translate(minimumBounds.x * -1, minimumBounds.y * -1);
            Utils.paintDiagram(graphics, figure);
        }

        svgGenerator.stream(filename);
    }
}
