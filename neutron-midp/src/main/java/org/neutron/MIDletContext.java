package org.neutron;

import javax.microedition.midlet.MIDlet;

import org.neutron.app.launcher.Launcher;
public class MIDletContext {

	private MIDletAccess midletAccess;
	
	public MIDletContext() {
		
	}
	
	public MIDletAccess getMIDletAccess() {
		return midletAccess;
	}
	
	protected void setMIDletAccess(MIDletAccess midletAccess) {
		this.midletAccess = midletAccess;	
	}
	
	public MIDlet getMIDlet() {
		if (midletAccess == null) {
			return null;
		}
		return midletAccess.midlet;
	}
	
	public boolean isLauncher() {
		return (getMIDlet() instanceof Launcher);
	}
}
