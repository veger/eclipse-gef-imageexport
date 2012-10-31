package nl.utwente.ce.imageexport.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferencesInitializer extends AbstractPreferenceInitializer
{
    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Activator.getPreferences();
        store.setDefault(PreferenceConstants.EXPORT_FORMAT, "PNG Image");
    }
}
