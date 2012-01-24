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
package nl.utwente.ce.imageexport.core;

import java.io.File;

import nl.utwente.ce.imageexport.Utils;
import nl.utwente.ce.imageexport.page.ExportImagePage;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ImageExportWizard extends Wizard implements IExportWizard
{
    private static ExportImagePage mainPage;
    private IWorkbench workbench;

    public ImageExportWizard()
    {
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection currentSelection)
    {
        this.workbench = workbench;
        setWindowTitle("Export image");
    }

    @Override
    public void addPages()
    {
        super.addPages();
        if (mainPage == null)
        {
            // Keep between multiple exports (ie to keep the settings)
            mainPage = new ExportImagePage();
        }
        addPage(mainPage);
    }

    @Override
    public boolean performFinish()
    {
        IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        GraphicalViewer graphicalViewer = (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
        if (graphicalViewer == null)
        {
            // Could not find a suitable (GEF based) viewer...
            return false;
        }
        LayerManager layerManager = (LayerManager) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
        IFigure rootFigure = layerManager.getLayer(LayerConstants.PRINTABLE_LAYERS);

        String filename = Utils.sanitizePath(new File(mainPage.getFilename()));
        ImageFormatProvider imageProvider = mainPage.getImageProvider();

        imageProvider.getProvider().exportImage(imageProvider.getID(), filename, rootFigure);

        return true;
    }
}
