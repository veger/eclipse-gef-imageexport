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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.utwente.ce.imageexport.IImageFormatProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Class to control the plug-in life cycle */
public class ImageExportPlugin extends AbstractUIPlugin
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = "nl.utwente.ce.imageexport";

    /** The format provider extension point ID */
    public static final String FORMATPROVIDEREXTENSION_ID = "nl.utwente.ce.imageexport.exportFormatProvider";

    /** The shared instance */
    private static ImageExportPlugin plugin;

    /** List of available image providers */
    private List<ImageFormatProvider> imageProviders;

    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        imageProviders = null;
        super.stop(context);
    }

    /** @return the shared instance */
    public static ImageExportPlugin getDefault()
    {
        return plugin;
    }

    /** @return a list of {@link IImageFormatProviders} that are available to export images */
    public List<ImageFormatProvider> getImageProviders() throws CoreException
    {
        if (imageProviders == null)
        {
            // Find available exporting format providers
            imageProviders = new ArrayList<ImageFormatProvider>();
            IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    FORMATPROVIDEREXTENSION_ID);
            for (IConfigurationElement element : elements)
            {
                final Object o = element.createExecutableExtension("class");
                if (o instanceof IImageFormatProvider)
                {
                    final String id = element.getAttribute("id");
                    final String name = element.getAttribute("name");
                    final String extensions = element.getAttribute("extensions");
                    imageProviders.add(new ImageFormatProvider(id, name, extensions, (IImageFormatProvider) o));
                }
            }
            imageProviders = Collections.unmodifiableList(imageProviders);
        }
        return imageProviders;
    }

    /** @return the preference store of this plug-in */
    public static IPreferenceStore getPreferences()
    {
        return getDefault().getPreferenceStore();
    }
}
