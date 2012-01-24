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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = "nl.utwente.ce.imageexport";

    /** The plug-in ID */
    public static final String FORMATPROVIDEREXTENSION_ID = "nl.utwente.ce.imageexport.exportFormatProvider";

    /** The shared instance */
    private static Activator plugin;

    private static List<ImageFormatProvider> imageProviders;

    public Activator()
    {
    }

    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        // Find available exporting format providers
        imageProviders = new ArrayList<ImageFormatProvider>();
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                FORMATPROVIDEREXTENSION_ID);
        for (IConfigurationElement e : config)
        {
            try
            {
                final Object o = e.createExecutableExtension("class");
                if (o instanceof IImageFormatProvider)
                {
                    final String id = e.getAttribute("id");
                    final String name = e.getAttribute("name");
                    final String extensions = e.getAttribute("extensions");
                    imageProviders.add(new ImageFormatProvider(id, name, extensions, (IImageFormatProvider) o));
                }
            } catch (CoreException exception)
            {
                // Could not activate extension: just ignore, so the other will be available..!
                exception.printStackTrace();
            }
        }
        imageProviders = Collections.unmodifiableList(imageProviders);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        imageProviders = null;
        super.stop(context);
    }

    /** @return the shared instance */
    public static Activator getDefault()
    {
        return plugin;
    }

    /** @return a list of {@link IImageFormatProviders} that are available to export images */
    public static List<ImageFormatProvider> getImageProviders()
    {
        return imageProviders;
    }
}
