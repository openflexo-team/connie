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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtils {
	private static final String WIN_REG_PROXY_PATH = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
	private static final String WIN_REG_PROXY_ENABLE = "ProxyEnable"; // DWORD
	private static final String WIN_REG_PROXY_SERVER = "ProxyServer";
	private static final String WIN_REG_AUTO_CONFIG_URL = "AutoConfigURL";

	private static final Pattern PROXY_PATTERN = Pattern.compile("PROXY\\s+([a-zA-Z-.0-9]+)(:(\\p{Digit}+))?");

	public static boolean isProxyEnabled() {
		if (ToolBox.isWindows()) {
			return Integer.decode(
					WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_PROXY_ENABLE, WinRegistryAccess.REG_DWORD_TOKEN)) != 0;
		}
		return false;
	}

	public static boolean autoDetectSettingsEnabled() {
		if (ToolBox.isWindows()) {
			return WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_AUTO_CONFIG_URL, WinRegistryAccess.REG_SZ_TOKEN) != null;
		}
		return false;
	}

	public static String[] getHTTPProxyPort(boolean secure) {
		String[] proxyHost = null;
		if (ToolBox.isWindows()) {
			try {
				String proxyServer = WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_PROXY_SERVER,
						WinRegistryAccess.REG_SZ_TOKEN);
				if (proxyServer != null) {
					int defaultPort = secure ? 443 : 80;
					if (proxyServer.indexOf(";") > -1) {
						String[] s = proxyServer.split(";");
						for (String string : s) {
							if (string.toLowerCase().startsWith("https=")) {
								string = string.substring("https=".length());
								proxyHost = ToolBox.getHostPortFromString(string, defaultPort);
								if (secure) {
									break;
								}
							}
							if (string.toLowerCase().startsWith("http=")) {
								string = string.substring("http=".length());
								proxyHost = ToolBox.getHostPortFromString(string, defaultPort);
								if (!secure) {
									break;
								}
							}
						}
					}
					else {
						proxyHost = ToolBox.getHostPortFromString(proxyServer, defaultPort);
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return proxyHost;
	}

	public static List<String[]> getProxiesFromAutoConfigURL(URL autoConfigURL, int defaultPort) {
		List<String[]> proxies = new ArrayList<>();
		if (autoConfigURL == null) {
			return proxies;
		}
		try {
			String pac = ToolBox.getContentAtURL(autoConfigURL);
			if (pac == null) {
				return proxies;
			}
			Matcher m = PROXY_PATTERN.matcher(pac);
			while (m.find()) {
				String proxyHost = m.group(1);
				int port = defaultPort;
				if (m.groupCount() > 2) {
					port = Integer.parseInt(m.group(3));
				}
				proxies.add(new String[] { proxyHost, String.valueOf(port) });
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't read auto config file: " + autoConfigURL.toString());
		}
		return proxies;
	}

	public static URL getAutoConfigURL() {
		URL autoConfigURL = null;
		if (ToolBox.isWindows()) {
			try {
				/*String proxyPath = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections";
				String s ="DefaultConnectionSettings";
				s=WinRegistryAccess.getRegistryValue(proxyPath, s, WinRegistryAccess.REG_BINARY);
				boolean autoDetectNetworkSettings = Integer.parseInt(s.substring(8, 10), 16)%2!=0;
				if (autoDetectNetworkSettings) {
					// OK let's go for WPAD
					autoConfigURL = WPADURL();
				}
				if (autoConfigURL == null) {*/
				String autoConfig = WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_AUTO_CONFIG_URL,
						WinRegistryAccess.REG_SZ_TOKEN);
				if (autoConfig != null) {
					try {
						return new URL(autoConfig);
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return null;
					}
				}
				// }
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return autoConfigURL;
	}

	/**
	 * Implements the WPAD lookup like explained here: http://en.wikipedia.org/wiki/Web_Proxy_Autodiscovery_Protocol This is insane.
	 * 
	 * @return
	 */
	/*
	public static URL WPADURL() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostName();
			while (host.indexOf(".") > -1) {
				try {
					URL attempt = new URL("http://wpad" + host.substring(host.indexOf(".")) + "/wpad.dat");
					try {
						HttpURLConnection conn = (HttpURLConnection) attempt.openConnection();
						int code = conn.getResponseCode();
						if (code >= 200 && code < 300) {
							return attempt;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
				host = host.substring(host.indexOf(".") + 1);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLocalDomain() {
		try {
			InetAddress localaddr = InetAddress.getLocalHost();
			String hostName = localaddr.getHostName();
			// my.local.domain.com
			if (hostName != null) {
				int index = hostName.lastIndexOf('.');
				index = hostName.lastIndexOf('.', index - 1);
				if (index > -1) {
					return hostName.substring(index + 1); // domain.com
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
}
