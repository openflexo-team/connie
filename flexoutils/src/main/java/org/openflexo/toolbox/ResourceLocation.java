package org.openflexo.toolbox;

import java.io.InputStream;
import java.net.URL;

public class ResourceLocation {
	// Initial requested path
	private String _relativePath;
	// Location of the resource for the given Delegate
	private URL _url; 
	// The Delegate that can resolve this location
	private ResourceLocatorDelegate _delegate;
	
	public ResourceLocation(ResourceLocatorDelegate delegate, String initialPath,
			URL url2) {
			_relativePath = initialPath;
			_url = url2;
			_delegate = delegate;
			
	}
	// TODO: a voir si on peut supprimer à terme
	public InputStream openStream() {
		return _delegate.retrieveResourceAsInputStream(this);
	}

	public URL getURL() {
		return _url;
	}
	
	public void setURL(URL url) {
		_url = url;
	}

	public ResourceLocatorDelegate getDelegate() {
		return _delegate;
	}
	
	public String toString(){
		return "[" + _delegate.toString() + "]" +_url.toString();
		
	}

	public String getRelativePath() {
		return _relativePath;
	}

	// TODO, to be fixed
	public long lastModified() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
