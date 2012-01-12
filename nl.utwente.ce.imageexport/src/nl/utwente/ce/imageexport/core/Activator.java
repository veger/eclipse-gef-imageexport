package nl.utwente.ce.imageexport.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.utwente.ce.imageexport.IImageFormatProvider;

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
            final Object o = e.createExecutableExtension("class");
            if (o instanceof IImageFormatProvider)
            {
                final String id = e.getAttribute("id");
                final String name = e.getAttribute("name");
                final String extensions = e.getAttribute("extensions");
                imageProviders.add(new ImageFormatProvider(id, name, extensions, (IImageFormatProvider) o));
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
