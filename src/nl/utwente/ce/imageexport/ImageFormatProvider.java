package nl.utwente.ce.imageexport;

import java.util.Arrays;
import java.util.List;

/** Class that holds information of an image format provider */
public class ImageFormatProvider
{
    private final String id;
    private final String name;
    private final List<String> extensions;
    private final IImageFormatProvider provider;

    public ImageFormatProvider(String id, String name, String extensions, IImageFormatProvider provider)
    {
        this.id = id;
        this.name = name;
        this.extensions = Arrays.asList(extensions.split(","));
        this.provider = provider;
    }

    public String getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDefaultExtension()
    {
        return extensions.get(0);
    }

    public IImageFormatProvider getProvider()
    {
        return provider;
    }

    /** @return true if filename has one of the available extensions */
    public boolean isValidExtension(String filename)
    {
        for(String extension: extensions) {
            if(filename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
