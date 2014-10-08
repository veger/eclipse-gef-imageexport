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
package nl.utwente.ce.imageexport;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

/**
 * Interface to provide an image format, must be implemented by a class that provides a certain image format for the
 * ImageExport plugin
 */
public interface IImageFormatProvider
{
    /**
     * @param formatID is the ID of the format for which the settings are requested
     * @param figure is the figure to export
     * @param store is the {@link IPreferenceStore} to get the (default) preferences from
     */
    void provideSettings(String formatID, Composite container, IPreferenceStore store);

    /**
     * Store the settings that are specific for this image provider
     * @param store is the {@link IPreferenceStore} to store the preferences into
     */
    void storePreferences(IPreferenceStore store);

    /** Export the image */
    void exportImage(String formatID, String filename, IFigure figure) throws Throwable;
}
