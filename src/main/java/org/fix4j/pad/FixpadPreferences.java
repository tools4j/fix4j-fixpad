package org.fix4j.pad;

import org.fix4j.test.properties.ApplicationProperties;
import org.fix4j.test.properties.ApplicationPropertiesFactory;
import org.fix4j.test.properties.MapPropertySource;
import org.fix4j.test.properties.PropertyKeysAndDefaultValues;

import java.util.Collections;
import java.util.Map;

/**
 * User: ben
 * Date: 22/10/15
 * Time: 6:27 AM
 */
public class FixpadPreferences {
    public static final String FIXPAD_PREF_FIX_FIELD_DELIMITER = "fixpad.fixFieldDelimiter";
    public static final String FIXPAD_PREF_WORD_WRAP_FROM_TEXT = "fixpad.wordWrapFromText";
    private final java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node("fixpad");

    public boolean getIsWordWrap(){
        return preferences.getBoolean(FIXPAD_PREF_WORD_WRAP_FROM_TEXT, false);
    }

    public void setWordWrap(final boolean wordWrap){
        preferences.putBoolean(FIXPAD_PREF_WORD_WRAP_FROM_TEXT, wordWrap);
    }

    public void setFixDelimiter(final String delim){
        if(delim == null || delim.length() == 0){
            preferences.remove(FIXPAD_PREF_FIX_FIELD_DELIMITER);
        } else {
            preferences.put(FIXPAD_PREF_FIX_FIELD_DELIMITER, delim);
        }
        saveFixDelimiterFromJavaSavedPreferencesIntoFix4jProperties();
    }

    public String getDefaultDelimiter(){
        return PropertyKeysAndDefaultValues.FIX_FIELD_DELIM.getDefaultValue();
    }

    public String getFixDelimiter(){
        return preferences.get(FIXPAD_PREF_FIX_FIELD_DELIMITER, getDefaultDelimiter());
    }

    public void saveFixDelimiterFromJavaSavedPreferencesIntoFix4jProperties(){
        final String customDelimiter = preferences.get(FIXPAD_PREF_FIX_FIELD_DELIMITER, null);
        final Map<String, String> customProperties;
        if(customDelimiter != null && customDelimiter.length() > 0){
            customProperties = Collections.singletonMap(PropertyKeysAndDefaultValues.FIX_FIELD_DELIM.getKey(), customDelimiter);
        } else {
            customProperties = Collections.emptyMap();
        }
        final ApplicationProperties properties = new ApplicationPropertiesFactory().createApplicationProperties(new MapPropertySource(customProperties, "fixpad.prefs"));
        ApplicationProperties.Singleton.setInstance(properties);
    }

    public void resetDefaultFixDelimiter() {
        setFixDelimiter(getDefaultDelimiter());
    }
}
