package nl.utwente.ce.imageexport;

import nl.utwente.ce.imageexport.page.ExportImagePage;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ImageExportWizard extends Wizard implements IExportWizard
{
    private ExportImagePage mainPage;
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
        mainPage = new ExportImagePage();
        addPage(mainPage);
    }

    @Override
    public boolean performFinish()
    {
        GraphicalEditor editor = (GraphicalEditor) workbench.getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        GraphicalViewer graphicalViewer = (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
        String filename = mainPage.getFilename();
        ImageFormatProvider imageProvider = mainPage.getImageProvider();
        imageProvider.getProvider().exportImage(imageProvider.getID(), filename, graphicalViewer);

        return true;
    }
}
