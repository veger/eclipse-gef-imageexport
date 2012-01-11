package nl.utwente.ce.imageexport.page;

import java.util.ArrayList;
import java.util.List;

import nl.utwente.ce.imageexport.Activator;
import nl.utwente.ce.imageexport.ImageFormatProvider;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportImagePage extends WizardPage implements ModifyListener {
	static private final String EXPORTIMAGEPAGEID = "export-image-page";

	private Combo formatField;

	private Text fileName;

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

			formatField = new Combo(composite, SWT.LEFT);
			List<String> availableFormats = new ArrayList<String>();
			for (ImageFormatProvider provider : Activator.getImageProviders()) {
				availableFormats.add(provider.getName());
			}
			formatField.setItems(availableFormats
					.toArray(new String[availableFormats.size()]));
			formatField.addModifyListener(this);
		}

		// Filename
		{
			Label label = new Label(composite, SWT.LEFT);
			label.setText("File name:");

			Composite panel = new Composite(composite, SWT.NONE);
			{
				GridData gridData = new GridData(GridData.FILL, GridData.END,
						true, false);
				panel.setLayoutData(gridData);
				panel.setLayout(new GridLayout(2, false));
			}

			fileName = new Text(panel, SWT.LEFT);
			{
				GridData gridData = new GridData(GridData.FILL, GridData.END,
						true, false);
				fileName.setLayoutData(gridData);
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

		if (Activator.getImageProviders().size() > 0) {
			formatField.select(0);
		}
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (e.getSource().equals(formatField)) {
			fileName.setText("Image." + findImageProvider(formatField.getText()).getDefaultExtension());
		}
	}

	/** @return the image provider with the given name */
	public static ImageFormatProvider findImageProvider(String name) {
		for (ImageFormatProvider provider : Activator.getImageProviders()) {
			if(provider.getName().equals(name)) {
				return provider;
			}
		}
		return null;
	}
}
