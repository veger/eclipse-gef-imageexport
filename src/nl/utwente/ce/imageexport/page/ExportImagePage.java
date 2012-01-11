package nl.utwente.ce.imageexport.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.utwente.ce.imageexport.Activator;
import nl.utwente.ce.imageexport.ImageFormatProvider;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportImagePage extends WizardPage implements SelectionListener
{
    static private final String EXPORTIMAGEPAGEID = "export-image-page";

    private Combo formatField;

    private Text fileNameField;

    private Group settingsGroup;

    private Map<ImageFormatProvider, Composite> settingComposites;

    public ExportImagePage()
    {
        this(EXPORTIMAGEPAGEID);
    }

    protected ExportImagePage(String name)
    {
        super(name);
        setTitle("Export image");
        setDescription("Fill in the settings for the exported image");
    }

    @Override
    public void createControl(Composite parent)
    {
        settingComposites = new HashMap<ImageFormatProvider, Composite>();

        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            layout.verticalSpacing = 12;
            composite.setLayout(layout);

            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }
        // Add Components
        // Image Format
        {
            new Label(composite, SWT.LEFT).setText("Image format:");
            formatField = new Combo(composite, SWT.LEFT);
            List<String> availableFormats = new ArrayList<String>();
            for (ImageFormatProvider provider : Activator.getImageProviders())
            {
                availableFormats.add(provider.getName());
            }
            formatField.setItems(availableFormats.toArray(new String[availableFormats.size()]));
            formatField.addSelectionListener(this);
        }

        // Filename
        {
            new Label(composite, SWT.LEFT).setText("File name:");
            Composite fileNamePanel = new Composite(composite, SWT.NONE);
            fileNamePanel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
            fileNamePanel.setLayout(new GridLayout(2, false));

            fileNameField = new Text(fileNamePanel, SWT.LEFT);
            fileNameField.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
            Button browseButton = new Button(fileNamePanel, SWT.LEFT);
            browseButton.setText("Browse...");
        }

        // Settings
        settingsGroup = new Group(composite, SWT.NONE);
        settingsGroup.setText("Settings");
        settingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        settingsGroup.setLayout(new GridLayout());

        // Update composite and its children
        formatField.select(0);
        formatChanged();

        setControl(composite);
    }

    /** @return the image provider with the given name */
    public static ImageFormatProvider findImageProvider(String name)
    {
        for (ImageFormatProvider provider : Activator.getImageProviders())
        {
            if (provider.getName().equals(name))
            {
                return provider;
            }
        }
        return null;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
        widgetSelected(e);
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        if (e.getSource().equals(formatField))
        {
            formatChanged();
        }
    }

    /** Update the page for the currently selected image format */
    protected void formatChanged()
    {
        ImageFormatProvider imageProvider = findImageProvider(formatField.getText());
        if (imageProvider == null)
        {
            // Should not happen?
            return;
        }
        fileNameField.setText("Image." + imageProvider.getDefaultExtension());

        // Remove previous settings (if any)
        for (Control child : settingsGroup.getChildren())
        {
            child.setVisible(false);
        }

        Composite formatSettings;
        // Update settings for new selected format
        if (settingComposites.containsKey(imageProvider))
        {
            formatSettings = settingComposites.get(imageProvider);
        }
        else
        {
            formatSettings = new Composite(settingsGroup, SWT.NONE);
            imageProvider.getProvider().provideSettings(imageProvider.getID(), formatSettings);
            settingComposites.put(imageProvider, formatSettings);
        }

        settingsGroup.setVisible(formatSettings.getChildren().length > 0);
        formatSettings.setVisible(true);
        settingsGroup.layout(true, true);
    }
}
