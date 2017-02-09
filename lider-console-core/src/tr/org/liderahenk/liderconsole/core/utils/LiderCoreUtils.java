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
package tr.org.liderahenk.liderconsole.core.utils;

import java.text.SimpleDateFormat;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

import tr.org.liderahenk.liderconsole.core.config.ConfigProvider;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LiderCoreUtils {

	private static StandardPBEStringEncryptor enc;

	private static final String ENCRYPTOR_ALGORITHM = "PBEWITHMD5ANDTRIPLEDES";

	public static boolean isInteger(String s) {
		if (s == null || s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), 10) < 0)
				return false;
		}
		return true;
	}

	public static boolean isValidDate(String inDate, String format) {
		if (inDate == null || inDate.isEmpty())
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (Exception pe) {
			return false;
		}
		return true;
	}

	public static String encrypt(String plain) {
		initEncryptor();
		return enc.encrypt(plain);
	}

	public static String decrypt(String encrypted) {
		initEncryptor();
		return enc.decrypt(encrypted);
	}

	public static boolean checkPassword(String inputPassword, String encryptedPassword) {
		initEncryptor();
		return enc.decrypt(encryptedPassword).equals(inputPassword);
	}

	private static void initEncryptor() {
		if (enc == null) {
			enc = new StandardPBEStringEncryptor();
			EnvironmentStringPBEConfig env = new EnvironmentStringPBEConfig();
			env.setAlgorithm(ENCRYPTOR_ALGORITHM);
			env.setPassword(ConfigProvider.getInstance().get(LiderConstants.CONFIG.ENCRYPTOR_PASS));
			enc.setConfig(env);
			enc.initialize();
		}
	}

}
