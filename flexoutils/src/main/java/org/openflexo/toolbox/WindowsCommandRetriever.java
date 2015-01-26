/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.toolbox;

import java.io.IOException;

import org.openflexo.toolbox.WinRegistryAccess.ConsoleReader;

/**
 * @author gpolet
 * 
 */
public class WindowsCommandRetriever {
	/**
	 * 
	 * @param extension
	 *            the file extension (with or without the preceding '.')
	 * @return the command to execute for the specified <code>extension</code> or null if there are no associated command
	 */
	public static String commandForExtension(String extension) {
		String regKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\" + extension;
		String fileType = WinRegistryAccess.getRegistryValue(regKey, "ProgID", WinRegistryAccess.REG_SZ_TOKEN);
		if (fileType == null) {
			StringBuilder sb = new StringBuilder("cmd /C assoc ");
			sb.append(extension.startsWith(".") ? extension : "." + extension);

			ConsoleReader reader;
			try {
				Process process = Runtime.getRuntime().exec(sb.toString());
				reader = new ConsoleReader(process.getInputStream());
				reader.start();
				process.waitFor();
				reader.join();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
			String result = reader.getResult();
			if (result.indexOf("=") > -1) {
				fileType = result.substring(result.indexOf("=") + 1).trim();
			}
		}
		if (fileType == null) {
			return null;
		}
		return getCommandForFileType(fileType);
	}

	public static String getCommandForFileType(String fileType) {
		String path = "HKEY_CLASSES_ROOT\\" + fileType + "\\shell\\open\\command";
		return WinRegistryAccess.getRegistryValue(path, null, WinRegistryAccess.REG_SZ_TOKEN);
	}
}
