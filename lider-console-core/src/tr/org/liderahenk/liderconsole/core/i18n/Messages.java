/*
*
*    Copyright © 2015-2016 Agem Bilişim
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.liderconsole.core.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.utils.LiderCoreUtils;

/**
 * Provides i18n messages for Lider Console Core strings.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class Messages extends NLS {

	private static final Logger logger = LoggerFactory.getLogger(Messages.class);

	private static final String BUNDLE_NAME = "tr.org.liderahenk.liderconsole.core.i18n.messages";

	/**
	 * Target locale is either read from command line arguments or 'tr' by
	 * default. It can be overridden according to Lider locale
	 */
	private static Locale targetLocale = null;
	static {
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		String locale = preferenceStore.getString("lider.locale");
		if (LiderCoreUtils.isEmpty(locale)) {
			locale = "tr";
		}
		targetLocale = locale.contains("-") || locale.contains("_") ? Locale.forLanguageTag(locale)
				: new Locale(locale);
		logger.info("Configuring locale: {}", locale);
	}

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return PropertyResourceBundle.getBundle(BUNDLE_NAME, targetLocale).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Returns a formatted string using the specified message string and
	 * arguments.<br/>
	 * <br/>
	 * 
	 * <b>Example:</b><br/>
	 * messages_tr.properties:<br/>
	 * ROSTER_ONLINE=%s çevrimiçi oldu<br/>
	 * 
	 * usage:<br/>
	 * Messages.getString("ROSTER_ONLINE", dn)
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getString(String key, Object... args) {
		return String.format(getString(key), args);
	}

	public static void setLocale(final String locale) {
		if (locale == null) {
			throw new NullPointerException();
		}
		if (!locale.contains(targetLocale.getLanguage())) {
			targetLocale = locale.contains("-") || locale.contains("_") ? Locale.forLanguageTag(locale)
					: new Locale(locale);
			PlatformUI.getPreferenceStore().setValue("lider.locale", locale);
			logger.info("Setting new locale: {}", locale);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					// Re-launch with the new locale
					PlatformUI.getWorkbench().restart();
				}
			});
		}
		try {
			// Try to set locale as default
			Locale.setDefault(targetLocale);
		} catch (Exception e) {
		}
	}

	public static String getLocale() {
		return "tr";
//		return PlatformUI.getPreferenceStore().getString("lider.locale");
	}

}
