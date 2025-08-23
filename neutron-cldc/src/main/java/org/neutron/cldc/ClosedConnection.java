package org.neutron.cldc;

import java.io.IOException;

import javax.microedition.io.Connection;
public interface ClosedConnection {

	Connection open(String name) throws IOException;

}
