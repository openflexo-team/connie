/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.toolbox;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.openflexo.rm.BasicResourceImpl;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;

/**
 * @author bmangez
 */

//TODO : maybe it should implements Resource API to ease usage

public class ImageIconResource extends ImageIcon {
	private static final long serialVersionUID = 1L;
	private Resource _location;

	private static String FILE_SUFFIX = ".flexoicon";

	public ImageIconResource(Resource location) {
		super(((BasicResourceImpl) location).getURL());
		_location = location;
	}
	
	private File getResourceFile() throws IOException {
		File resourceFile = null;
		if (_location != null && _location instanceof FileResourceImpl) {
			resourceFile = ((FileResourceImpl)_location).getFile();
		}if (resourceFile == null) {
			resourceFile = File.createTempFile(((FileResourceImpl)_location).getURL().getFile().replace(File.pathSeparatorChar, '_'), FILE_SUFFIX);
		}
		return resourceFile;
	}

	public String getHTMLImg() {
		try {
			return "<img src=\"file:" + getResourceFile().getCanonicalPath() + "\">";
		} catch (Throwable e) {
			return "";
		}
	}
}
