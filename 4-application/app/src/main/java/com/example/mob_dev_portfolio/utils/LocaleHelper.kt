import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    private const val SELECTED_LANGUAGE_KEY = "app_language"

    fun applyLocale(context: Context): Context {
        return context
    }

    fun setAppLocale(context: Context, language: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString(SELECTED_LANGUAGE_KEY, language)
            apply()
        }
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language)
        )
    }

    fun getPersistedLanguage(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SELECTED_LANGUAGE_KEY, "en") ?: "en"
    }
}