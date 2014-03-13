package org.openflexo.toolbox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class FileResourceLocation extends ResourceLocation {

	private static final Logger logger = Logger.getLogger(FileResourceLocation.class.getPackage().getName());

	// Related File
	private File _file; 

	public FileResourceLocation(ResourceLocatorDelegate delegate,String initialPath,
			URL url2, File file2) {
		super (delegate, initialPath, url2);
		_file = file2;
	}

	public FileResourceLocation(
			ResourceLocatorDelegate delegate, String initialPath, URL url) {
		super (delegate,initialPath, url);
		try {
			setFile(new File(getURL().toURI()));
		} catch (URISyntaxException e) {
			logger.severe("Unable to open file from URL: " + getURL());
			e.printStackTrace();
		}
	}

	public FileResourceLocation(ResourceLocatorDelegate delegate,
			File file) throws MalformedURLException {
		super(delegate,file.getPath(),file.toURI().toURL());
	}

	public File getFile(){
		return _file;
	}

	public void setFile(File f){
		_file = f;
		try {
			setURL(f.toURI().toURL());
		} catch (MalformedURLException e) {
			logger.severe("Unable to assign a new file: " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}

	@Override
	public long lastModified() {
		if (_file != null){
			return _file.lastModified();
		}
		return 0;
	}
}
