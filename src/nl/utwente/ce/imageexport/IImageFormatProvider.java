package nl.utwente.ce.imageexport;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface to provide an image format, must be implemented by a class that provides a certain image for the
 * ImageExport plugin
 */
public interface IImageFormatProvider
{

    /**
     * @param formatID is the ID of the format for which the settings are requested
     * @return a {@link Composite} containing all settings of the format or null if no settings are present
     */
    Composite provideSettings(String formatID);

}
