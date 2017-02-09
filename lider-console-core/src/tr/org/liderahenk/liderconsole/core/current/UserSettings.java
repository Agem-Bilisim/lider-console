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
package tr.org.liderahenk.liderconsole.core.current;

/**
 * Application wide user settings class.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class UserSettings {

	private UserSettings() {
	}

	private static String dn = null;
	private static String password = null;
	private static String uid = null;

	public static String getDn() {
		return dn;
	}

	public static void setDn(String dn) {
		UserSettings.dn = dn;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		UserSettings.password = password;
	}

	public static String getUid() {
		return uid;
	}

	public static void setUid(String uid) {
		UserSettings.uid = uid;
	}

	public static void reset() {
		dn = null;
		password = null;
		uid = null;
	}

}
