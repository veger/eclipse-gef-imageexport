package nl.utwente.ce.imageexport;

import nl.utwente.ce.imageexport.page.ExportImagePage;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ImageExportWizard extends Wizard implements IExportWizard {

	private ExportImagePage mainPage;
	
	public ImageExportWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		setWindowTitle("Export image");
	}

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new ExportImagePage();
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		return false;
	}
}
