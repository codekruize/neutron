package org.neutron.microedition.io;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;

import org.neutron.microedition.Implementation;
public class PushRegistryImpl implements PushRegistryDelegate, Implementation {

	public String getFilter(String connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMIDlet(String connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] listConnections(boolean available) {
		// TODO Auto-generated method stub
		return new String[0];
	}

	public long registerAlarm(String midlet, long time) throws ClassNotFoundException, ConnectionNotFoundException {
		// TODO Auto-generated method stub
		throw new ConnectionNotFoundException();
	}

	public void registerConnection(String connection, String midlet, String filter) throws ClassNotFoundException,
			IOException {
		// TODO Auto-generated method stub

	}

	public boolean unregisterConnection(String connection) {
		// TODO Auto-generated method stub
		return false;
	}

}
