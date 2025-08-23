package org.neutron.microedition.io;

import java.io.IOException;

import javax.microedition.io.Connection;
public interface ConnectionImplementation {
	
	public Connection openConnection(String name, int mode, boolean timeouts) throws IOException;
	
}
