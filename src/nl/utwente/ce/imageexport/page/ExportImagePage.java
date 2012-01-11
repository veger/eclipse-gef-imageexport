package nl.utwente.ce.imageexport.page;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportImagePage extends WizardPage {
	static private final String EXPORTIMAGEPAGEID = "export-image-page";

	public ExportImagePage() {
		this(EXPORTIMAGEPAGEID);
	}

	protected ExportImagePage(String name) {
		super(name);
		setTitle("Export image");
		setDescription("Fill in the settings for the exported image");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.verticalSpacing = 12;
			composite.setLayout(layout);

			GridData data = new GridData();
			data.verticalAlignment = GridData.FILL;
			data.grabExcessVerticalSpace = true;
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			composite.setLayoutData(data);
		}
		// Add Components
		// Image Format
		{
			Label label = new Label(composite, SWT.LEFT);
			label.setText("Image format:");

			Combo combo = new Combo(composite, SWT.LEFT);
			String availableFormats[] = { "PNG", "JPG" };
			combo.setItems(availableFormats);
			combo.select(0);
		}

		// Filename
		{
			Label label = new Label(composite, SWT.LEFT);
			label.setText("File name:");

			Composite panel = new Composite(composite, SWT.NONE);
			{
				GridData gridData = new GridData(GridData.FILL, GridData.END, true,
						false);
				panel.setLayoutData(gridData);
				panel.setLayout(new GridLayout(2, false));
			}

			Text filename = new Text(panel, SWT.LEFT);
			{
				GridData gridData = new GridData(GridData.FILL, GridData.END, true,
						false);
				filename.setLayoutData(gridData);
			}
			Button browseButton = new Button(panel, SWT.LEFT);
			browseButton.setText("Browse...");
		}

		// Settings
		Group settings = new Group(composite, SWT.LEFT);
		settings.setText("Settings");
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		gridData.horizontalSpan = 2;
		settings.setLayoutData(gridData);

		setControl(composite);
	}
}
