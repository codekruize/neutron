package org.neutron.device;

import java.io.InputStream;

import javax.microedition.io.ConnectionNotFoundException;

import org.neutron.DisplayComponent;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.FontManager;
import org.neutron.device.InputMethod;

public interface EmulatorContext {

	DisplayComponent getDisplayComponent();

	InputMethod getDeviceInputMethod();

	DeviceDisplay getDeviceDisplay();

	FontManager getDeviceFontManager();
	
	InputStream getResourceAsStream(Class origClass, String name);

	boolean platformRequest(final String URL) throws ConnectionNotFoundException;
	
}
