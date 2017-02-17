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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Provides i18n messages for Lider Console Core strings.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "tr.org.liderahenk.liderconsole.core.i18n.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
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

	public static String getLanguage() {
		String property = System.getProperty("nl");
		String nl = RESOURCE_BUNDLE.getLocale() != null ? RESOURCE_BUNDLE.getLocale().getLanguage() : null;
		return nl == null ? "tr" : nl;
	}

}
