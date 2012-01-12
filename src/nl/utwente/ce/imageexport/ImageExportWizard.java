package nl.utwente.ce.imageexport;

import java.io.File;
import java.io.IOException;

import nl.utwente.ce.imageexport.page.ExportImagePage;

import org.eclipse.gef.GraphicalViewer;
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
        // Get filename and expand to canonical (or absolute) path
        String filename = mainPage.getFilename();
        File f = new File(filename);
        try
        {
            filename = f.getCanonicalPath();
        } catch (IOException e)
        {
            filename = f.getAbsolutePath();
        }
        ImageFormatProvider imageProvider = mainPage.getImageProvider();
        imageProvider.getProvider().exportImage(imageProvider.getID(), filename, graphicalViewer);

        return true;
    }
}
