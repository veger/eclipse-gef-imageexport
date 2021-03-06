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

public class PreferenceConstants
{
    // Do not allow to instantiate this class
    private PreferenceConstants()
    {
    }

    /** Export image format preference */
    public static final String EXPORT_FORMAT = "export format";

    /** Default file name (path). Note: it is not stored in the preferences store */
    public static final String EXPORT_FILENAME = "export filename";
}
