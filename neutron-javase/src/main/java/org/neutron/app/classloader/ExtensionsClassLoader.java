package org.neutron.app.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;

import org.neutron.app.util.IOUtils;
import org.neutron.log.Logger;
public class ExtensionsClassLoader extends URLClassLoader {

	private final static boolean debug = false;
private AccessControlContext acc;
    
	public ExtensionsClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		acc = AccessController.getContext();
	}
	
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	public void addClasspath(String classpath) {
		StringTokenizer st = new StringTokenizer(classpath, ";");
		while (st.hasMoreTokens()) {
			try {
				String path = st.nextToken();
				if (path.startsWith("file:")) {
					addURL(new URL(path));	
				} else {
					addURL(new URL(IOUtils.getCanonicalFileURL(new File(path))));
				}
			} catch (MalformedURLException e) {
				throw new Error(e);
			}
		}
	}
public URL getResource(final String name) {
		try {
			URL url = (URL) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return findResource(name);
				}
			}, acc);
			if (url != null) {
				return url;
			}
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.error("Unable to find resource " + name + " ", e);
			}
		}
		return super.getResource(name);
	}

}
