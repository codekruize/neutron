package org.neutron.app.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.neutron.app.util.IOUtils;
import org.neutron.log.Logger;
public class MIDletClassLoader extends URLClassLoader {

	// TODO make this configurable

	public static boolean instrumentMIDletClasses = true;

	public static boolean traceClassLoading = false;

	public static boolean traceSystemClassLoading = false;

	public static boolean enhanceCatchBlock = false;

	private final static boolean debug = false;

	private boolean delegatingToParent = false;

	private boolean findPathInParent = false;

	private InstrumentationConfig config;

	private Set noPreporcessingNames;
private AccessControlContext acc;

	private static class LoadClassByParentException extends ClassNotFoundException {

		public LoadClassByParentException(String name) {
			super(name);
		}

		private static final long serialVersionUID = 1L;

	}

	public MIDletClassLoader(ClassLoader parent) {
		super(new URL[] {}, parent);
		noPreporcessingNames = new HashSet();
		acc = AccessController.getContext();
		config = new InstrumentationConfig();
		config.setEnhanceCatchBlock(enhanceCatchBlock);
		config.setEnhanceThreadCreation(true);
	}

	// public MIDletClassLoader(URL[] urls, ClassLoader parent) {
	// super(urls, parent);
	// noPreporcessingNames = new HashSet();
	// }

	public void configure(MIDletClassLoaderConfig clConfig, boolean forJad) throws MalformedURLException {
		for (Iterator iter = clConfig.appclasspath.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
			while (st.hasMoreTokens()) {
				this.addURL(new URL(IOUtils.getCanonicalFileClassLoaderURL(new File(st.nextToken()))));
			}
		}
		for (Iterator iter = clConfig.appclasses.iterator(); iter.hasNext();) {
			this.addClassURL((String) iter.next());
		}
		int delegationType = clConfig.getDelegationType(forJad);
		this.delegatingToParent = (delegationType == MIDletClassLoaderConfig.DELEGATION_DELEGATING);
		this.findPathInParent = (delegationType == MIDletClassLoaderConfig.DELEGATION_RELAXED);
	}
public void addClassURL(String className) throws MalformedURLException {
		String resource = getClassResourceName(className);
		URL url = getParent().getResource(resource);
		if (url == null) {
			url = this.getResource(resource);
		}
		if (url == null) {
			throw new MalformedURLException("Unable to find class " + className + " URL");
		}
		String path = url.toExternalForm();
		if (debug) {
			Logger.debug("addClassURL ", path);
		}
		addURL(new URL(path.substring(0, path.length() - resource.length())));
	}

	static URL getClassURL(ClassLoader parent, String className) throws MalformedURLException {
		String resource = getClassResourceName(className);
		URL url = parent.getResource(resource);
		if (url == null) {
			throw new MalformedURLException("Unable to find class " + className + " URL");
		}
		String path = url.toExternalForm();
		return new URL(path.substring(0, path.length() - resource.length()));
	}

	public void addURL(URL url) {
		if (debug) {
			Logger.debug("addURL ", url.toString());
		}
		super.addURL(url);
	}
protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (debug) {
			Logger.debug("loadClass", name);
		}
		// First, check if the class has already been loaded
		Class result = findLoadedClass(name);
		if (result == null) {
			try {
				result = findClass(name);
				if (debug && (result == null)) {
					Logger.debug("loadClass not found", name);
				}
			} catch (ClassNotFoundException e) {

				if ((e instanceof LoadClassByParentException) || this.delegatingToParent) {
					if (traceSystemClassLoading) {
						Logger.info("Load system class", name);
					}
					// This will call our findClass again if Class is not found
					// in parent
					result = super.loadClass(name, false);
					if (result == null) {
						throw new ClassNotFoundException(name);
					}
				}
			}
		}
		if (resolve) {
			resolveClass(result);
		}
		return result;
	}
public URL getResource(final String name) {
		try {
			return (URL) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					URL url = findResource(name);
					if ((url == null) && delegatingToParent && (getParent() != null)) {
						url = getParent().getResource(name);
					}
					return url;
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.error("Unable to find resource " + name + " ", e);
			}
			return null;
		}
	}
public InputStream getResourceAsStream(String name) {
		final URL url = getResource(name);
		if (url == null) {
			return null;
		}

		try {
			return (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					return url.openStream();
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.debug("Unable to find resource for class " + name + " ", e);
			}
			return null;
		}

	}

	public boolean classLoadByParent(String className) {
if (className.startsWith("java.")) {
			return true;
		}
if (className.startsWith("sun.reflect.")) {
			return true;
		}
if (className.startsWith("javax.microedition.")) {
			return true;
		}
		if (className.startsWith("com.nokia.mid.")) {
			return true;
		}
		if (className.startsWith("javax.")) {
			return true;
		}
		if (noPreporcessingNames.contains(className)) {
			return true;
		}
		return false;
	}
public void disableClassPreporcessing(Class klass) {
		disableClassPreporcessing(klass.getName());
	}

	public void disableClassPreporcessing(String className) {
		noPreporcessingNames.add(className);
	}

	public static String getClassResourceName(String className) {
		return className.replace('.', '/').concat(".class");
	}

	protected Class findClass(final String name) throws ClassNotFoundException {
		if (debug) {
			Logger.debug("findClass", name);
		}
		if (classLoadByParent(name)) {
			throw new LoadClassByParentException(name);
		}
		InputStream is;
		try {
			is = (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws ClassNotFoundException {
					return getResourceAsStream(getClassResourceName(name));
				}
			}, acc);

			// Relax ClassLoader behavior
			if ((is == null) && (this.findPathInParent)) {
				boolean classFound;
				try {
					addClassURL(name);
					classFound = true;
				} catch (MalformedURLException e) {
					classFound = false;
				}
				if (classFound) {
					is = (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws ClassNotFoundException {
							return getResourceAsStream(getClassResourceName(name));
						}
					}, acc);
				}
			}
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.debug("Unable to find resource for class " + name + " ", e);
			}
			throw new ClassNotFoundException(name, e.getCause());
		}

		if (is == null) {
			if (debug) {
				Logger.debug("Unable to find resource for class", name);
			}
			throw new ClassNotFoundException(name);
		}
		byte[] byteCode;
		int byteCodeLength;
		try {
			if (traceClassLoading) {
				Logger.info("Load MIDlet class", name);
			}
			if (instrumentMIDletClasses) {
				byteCode = ClassPreprocessor.instrument(is, config);
				byteCodeLength = byteCode.length;
			} else {
				final int chunkSize = 1024 * 2;
				// No class or data object must be bigger than 16 Kilobyte
				final int maxClassSizeSize = 1024 * 16;
				byteCode = new byte[chunkSize];
				byteCodeLength = 0;
				do {
					int retrived;
					try {
						retrived = is.read(byteCode, byteCodeLength, byteCode.length - byteCodeLength);
					} catch (IOException e) {
						throw new ClassNotFoundException(name, e);
					}
					if (retrived == -1) {
						break;
					}
					if (byteCode.length + chunkSize > maxClassSizeSize) {
						throw new ClassNotFoundException(name, new ClassFormatError(
								"Class object is bigger than 16 Kilobyte"));
					}
					byteCodeLength += retrived;
					if (byteCode.length == byteCodeLength) {
						byte[] newData = new byte[byteCode.length + chunkSize];
						System.arraycopy(byteCode, 0, newData, 0, byteCode.length);
						byteCode = newData;
					} else if (byteCode.length < byteCodeLength) {
						throw new ClassNotFoundException(name, new ClassFormatError("Internal read error"));
					}
				} while (true);
			}
		} finally {
			try {
				is.close();
			} catch (IOException ignore) {
			}
		}
		if ((debug) && (instrumentMIDletClasses)) {
			Logger.debug("instrumented ", name);
		}
		return defineClass(name, byteCode, 0, byteCodeLength);
	}
}
