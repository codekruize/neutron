package org.neutron;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.neutron.app.util.MIDletOutputStreamRedirector;
import org.neutron.app.util.MIDletResourceLoader;
import org.neutron.app.util.MIDletSystemProperties;
import org.neutron.log.Logger;
public final class Injected implements Serializable {

	private static final long serialVersionUID = -1L;
public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	static {
		Logger.addLogOrigin(Injected.class);
	}
private Injected() {
		
	}
	
	private static PrintStream outPrintStream() {
		//return System.out;
		return MIDletOutputStreamRedirector.out;
	}

	private static PrintStream errPrintStream() {
		//return System.err;
		return MIDletOutputStreamRedirector.err;
	}
public static void printStackTrace(Throwable t) {
		Logger.error("MIDlet caught", t);
	}
public static String getProperty(String key) {
		return MIDletSystemProperties.getProperty(key);
	}
public static InputStream getResourceAsStream(Class origClass, String name)  {
		return MIDletResourceLoader.getResourceAsStream(origClass, name);
	}
public static Throwable handleCatchThrowable(Throwable t) {
		Logger.error("MIDlet caught", t);
		return t;
	}
}
