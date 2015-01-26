/**
 * 
 * Copyright (c) 2014, Openflexo
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.xerces.impl.dv.util.Base64;

/**
 * Small utilities to handle HTTP requests
 * @author leildevi
 *
 */
public class HTTPFileUtils {
	
	/**
	 * Get a file from a host uri, and save it into the save path
	 * @param host
	 * @param savePath
	 * @return
	 */
	public static String getFile(String host,String savePath)
	    {
	        InputStream input = null;
	        FileOutputStream writeFile = null;
	        String path = new String();
	        try
	        {
	            URL url = new URL(host);
	            URLConnection connection = url.openConnection();
	            int fileLength = connection.getContentLength();
	
	            if (fileLength == -1)
	            {
	                System.out.println("Invalide URL or file.");
	                return null;
	            }
	
	            input = connection.getInputStream();
	            String fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
	            path = savePath+"/"+fileName;
	            writeFile = new FileOutputStream(path);
	            byte[] buffer = new byte[1024];
	            int read;
	
	            while ((read = input.read(buffer)) > 0)
	                writeFile.write(buffer, 0, read);
	            writeFile.flush();
	        }
	        catch (IOException e)
	        {
	            System.out.println("Error while trying to download the file.");
	            e.printStackTrace();
	        }
	        finally
	        {
	            try
	            {
	                writeFile.close();
	                input.close();
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	        }
			return path;
	    }
	
	/**
	 * Get a GET String response
	 * @param url
	 * @param params
	 * @return
	 */
	public static String getURL(URL url, String params){
	      StringBuilder sb = new StringBuilder();

	      String lineSepatator = null;
	      try
	      {
	    	  lineSepatator = System.getProperty("line.separator");
	      }
	      catch (Exception e)
	      {
	    	  lineSepatator = "\n";
	      }
	 
	      try
	      {
	         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	         connection.setRequestMethod("GET");
	         connection.setAllowUserInteraction(false);
	         connection.setDoOutput(false);

	       /*  String userpass = "anonymous" + ":" + "";
		      String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
		      connection.setRequestProperty ("Authorization", basicAuth);*/
	         
	         InputStream  response = connection.getInputStream();
	         BufferedReader bufReader = new BufferedReader(new InputStreamReader(response));
	         String sLine;
	 
	         while ((sLine = bufReader.readLine()) != null)
	         {
	        	 sb.append(sLine);
	        	 sb.append(lineSepatator);
	         }

	         connection.disconnect();
	      }
	      catch(ConnectException ctx)
	      {
	        ctx.printStackTrace();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	      return sb.toString();
	    }	
}

