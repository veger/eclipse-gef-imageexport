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
package nl.utwente.ce.imageexport.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.utwente.ce.imageexport.core.ImageExportPlugin;
import nl.utwente.ce.imageexport.core.ImageFormatProvider;
import nl.utwente.ce.imageexport.core.PreferenceConstants;
import nl.utwente.ce.imageexport.Utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportImagePage extends WizardPage implements SelectionListener, ModifyListener, MouseListener
{
    static private final String EXPORTIMAGEPAGEID = "export-image-page";

    /** true when a suitable/active editor is available */
    private final boolean hasActiveEditor;

    /** Default file path of the input file of the active editor, or an empty string if not available */
    private String defaultFilePath = "";

    private Combo formatField;

    private Text fileNameField;

    private Button browseButton;

    private Group settingsGroup;

    private Map<ImageFormatProvider, Composite> settingComposites;

    public ExportImagePage(boolean hasActiveEditor)
    {
        this(EXPORTIMAGEPAGEID, hasActiveEditor);
    }

    protected ExportImagePage(String name, boolean hasActiveEditor)
    {
        super(name);
        this.hasActiveEditor = hasActiveEditor;
        setTitle("Export image");
        setDescription("Fill in the settings for the exported image");
    }

    public void setDefaultPath(String path)
    {
        defaultFilePath = path;
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
            for (ImageFormatProvider provider : ImageExportPlugin.getImageProviders())
            {
                availableFormats.add(provider.getName());
            }
            formatField.setItems(availableFormats.toArray(new String[availableFormats.size()]));
            formatField.addSelectionListener(this);

            String imageFormatPreference = ImageExportPlugin.getPreferences().getString(PreferenceConstants.EXPORT_FORMAT);
            int index = availableFormats.indexOf(imageFormatPreference);
            if (index != -1)
            {
                // Select format stored in preferences
                formatField.select(index);
            }
        }

        // Filename
        {
            new Label(composite, SWT.LEFT).setText("File name:");
            Composite fileNamePanel = new Composite(composite, SWT.NONE);
            fileNamePanel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
            fileNamePanel.setLayout(new GridLayout(2, false));

            fileNameField = new Text(fileNamePanel, SWT.LEFT);
            fileNameField.setText(getDefaultFilename());
            fileNameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            fileNameField.addModifyListener(this);
            browseButton = new Button(fileNamePanel, SWT.LEFT);
            browseButton.setText("Browse...");
            browseButton.addMouseListener(this);
        }

        // Settings
        settingsGroup = new Group(composite, SWT.NONE);
        settingsGroup.setText("Settings");
        settingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        settingsGroup.setLayout(new GridLayout());

        // Update composite and its children
        formatChanged();

        setControl(composite);
    }

    /** @return the image provider with the given name */
    public static ImageFormatProvider findImageProvider(String name)
    {
        for (ImageFormatProvider provider : ImageExportPlugin.getImageProviders())
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

    @Override
    public void modifyText(ModifyEvent e)
    {
        if (e.getSource().equals(fileNameField))
        {
            validatePage();
        }
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
        if (e.getSource() == browseButton)
        {
            browseFile();
        }
    }

    /** Update the page for the currently selected image format */
    protected void formatChanged()
    {
        ImageFormatProvider imageProvider = getImageProvider();
        if (imageProvider == null)
        {
            // Should not happen?
            return;
        }

        // Update filename for new format
        String filename = fileNameField.getText();
        if (filename.length() == 0)
        {
            filename = "Image";
        }
        filename = Utils.sanitizePath(new File(filename));

        // Add/replace extensions
        int dot = filename.lastIndexOf('.');
        if (dot != -1)
        {
            filename = filename.substring(0, dot);
        }
        fileNameField.setText(filename + "." + imageProvider.getDefaultExtension());

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
            imageProvider.getProvider().provideSettings(imageProvider.getID(), formatSettings,
                    ImageExportPlugin.getPreferences());
            settingComposites.put(imageProvider, formatSettings);
        }

        settingsGroup.setVisible(formatSettings.getChildren().length > 0);
        formatSettings.setVisible(true);
        settingsGroup.layout(true, true);
    }

    private String getDefaultFilename()
    {
        String filename = ImageExportPlugin.getPreferences().getString(PreferenceConstants.EXPORT_FILENAME);
        if ("".equals(filename))
        {
            filename = defaultFilePath;
        }
        else
        {
            // Use editors file name
            String name = new File(defaultFilePath).getName();
            filename = new File(filename).getParent() + File.separator + name;
        }

        return filename;
    }

    protected void validatePage()
    {
        ImageFormatProvider imageProvider = getImageProvider();

        boolean complete = false;
        try
        {
            if (!hasActiveEditor)
            {
                setErrorMessage("There is not suitable/active editor available. Try again after you opened a GEF based editor and made it active.");
                return;
            }

            String filename = fileNameField.getText();
            if (imageProvider.isValidExtension(filename) == false)
            {
                setErrorMessage("File name has an invalid extension, please change it to '"
                        + imageProvider.getDefaultExtension() + "'");
                return;
            }

            if ("".equals(filename))
            {

            }

            File f = new File(Utils.sanitizePath(new File(filename)));
            if (f.isAbsolute() == false)
            {
                setErrorMessage("Provide an absolute filename");
                return;
            }

            setErrorMessage(null);
            complete = true;
        } finally
        {
            // Update page complete status
            setPageComplete(complete);
        }
    }

    /** Opens a {@link FileDiaglog} and updates {@link #fileNameField} if a new filename was picked */
    private void browseFile()
    {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
        File f = new File(Utils.sanitizePath(new File(fileNameField.getText())));
        fileDialog.setFileName(f.getName());
        fileDialog.setFilterPath(f.getParent());

        // Create filter extension and name
        ImageFormatProvider imageProvider = getImageProvider();
        String filterExtension = createFilterExtension(Arrays.asList(imageProvider.getExtensions()));
        fileDialog.setFilterExtensions(new String[] { filterExtension });
        fileDialog.setFilterNames(new String[] { imageProvider.getName() });

        String filename = fileDialog.open();
        if (filename != null)
        {
            fileNameField.setText(filename);
        }
    }

    /** @return the filter extensions like "*.ext1;*.ext2" */
    public static String createFilterExtension(Collection<String> extensions)
    {
        if (extensions.isEmpty())
        {
            return ""; // Easy!
        }
        Iterator<String> iter = extensions.iterator();
        StringBuilder buffer = new StringBuilder("*.");
        buffer.append(iter.next());
        while (iter.hasNext())
        {
            buffer.append(";*.").append(iter.next());
        }
        return buffer.toString();
    }

    /** @return the filename for the exported image */
    public String getFilename()
    {
        return fileNameField.getText();
    }

    public ImageFormatProvider getImageProvider()
    {
        return findImageProvider(formatField.getText());
    }

    /** Store the preferences of the page, so they can be used as default values the next time */
    public void storePreferences()
    {
        IPreferenceStore store = ImageExportPlugin.getPreferences();
        store.setValue(PreferenceConstants.EXPORT_FORMAT, formatField.getText());
        // Do not store, but update default,
        // so a next time Eclipse is started, it default to the project again
        store.setDefault(PreferenceConstants.EXPORT_FILENAME, getFilename());

        getImageProvider().getProvider().storePreferences(store);
    }
}
