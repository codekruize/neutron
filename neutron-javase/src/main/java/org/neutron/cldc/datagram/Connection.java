package org.neutron.cldc.datagram;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.UDPDatagramConnection;

import org.neutron.microedition.io.ConnectionImplementation;
public class Connection implements DatagramConnection, UDPDatagramConnection, ConnectionImplementation {
public final static String PROTOCOL = "datagram://";
private DatagramSocket socket;
private String address;

	public void close() throws IOException {
		socket.close();
	}

	public int getMaximumLength() throws IOException {
		return Math.min(socket.getReceiveBufferSize(), socket.getSendBufferSize());
	}

	public int getNominalLength() throws IOException {
		return getMaximumLength();
	}

	public void send(Datagram dgram) throws IOException {
		socket.send(((DatagramImpl) dgram).getDatagramPacket());
	}

	public void receive(Datagram dgram) throws IOException {
		socket.receive(((DatagramImpl) dgram).getDatagramPacket());
	}

	public Datagram newDatagram(int size) throws IOException {
		return newDatagram(size, address);
	}

	public Datagram newDatagram(int size, String addr) throws IOException {
		if (!addr.startsWith(PROTOCOL)) {
			throw new IllegalArgumentException("Invalid Protocol " + addr);
		}
		Datagram datagram = new DatagramImpl(size);
		datagram.setAddress(addr);
		return datagram;
	}

	public Datagram newDatagram(byte[] buf, int size) throws IOException {
		return newDatagram(buf, size, address);
	}

	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
		if (!addr.startsWith(PROTOCOL)) {
			throw new IllegalArgumentException("Invalid Protocol " + addr);
		}
		Datagram datagram = new DatagramImpl(buf, size);
		datagram.setAddress(addr);
		return datagram;
	}

	public String getLocalAddress() throws IOException {
		InetAddress address = socket.getInetAddress();
		if (address == null) {
address = InetAddress.getLocalHost();
		} else {
address = socket.getLocalAddress();
		}
		return address.getHostAddress();
	}

	public int getLocalPort() throws IOException {
		return socket.getLocalPort();
	}

	public javax.microedition.io.Connection openConnection(String name, int mode, boolean timeouts) throws IOException {
		if (!org.neutron.cldc.http.Connection.isAllowNetworkConnection()) {
			throw new IOException("No network");
		}
		if (!name.startsWith(PROTOCOL)) {
			throw new IOException("Invalid Protocol " + name);
		}
		// TODO currently we ignore the mode
		address = name.substring(PROTOCOL.length());
		int port = -1;
		int index = address.indexOf(':');
		if (index == -1) {
			throw new IllegalArgumentException("Port missing");
		}
		String portToParse = address.substring(index + 1);
		if (portToParse.length() > 0) {
			port = Integer.parseInt(portToParse);
		}
		if (index == 0) {
			// server mode
			if (port == -1) {
				socket = new DatagramSocket();
			} else {
				socket = new DatagramSocket(port);
			}
		} else {
			// client mode
			if (port == -1) {
				throw new IllegalArgumentException("Port missing");
			}
			String host = address.substring(0, index);
			socket = new DatagramSocket();
			socket.connect(InetAddress.getByName(host), port);
		}
		return this;
	}
}
