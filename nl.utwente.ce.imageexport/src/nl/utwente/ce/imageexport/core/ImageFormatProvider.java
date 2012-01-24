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

import java.util.Arrays;
import java.util.List;

import nl.utwente.ce.imageexport.IImageFormatProvider;

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

    /** @return all extensions associated with the image format */
    public String[] getExtensions()
    {
        return (String[]) extensions.toArray();
    }

    public IImageFormatProvider getProvider()
    {
        return provider;
    }

    /** @return true if filename has one of the available extensions */
    public boolean isValidExtension(String filename)
    {
        for (String extension : extensions)
        {
            if (filename.endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }
}
