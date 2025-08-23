package org.neutron.app;

import java.io.InputStream;
import java.util.ArrayList;

import org.neutron.DisplayComponent;
import org.neutron.MIDletBridge;
import org.neutron.app.ui.Message;
import org.neutron.app.ui.noui.NoUiDisplayComponent;
import org.neutron.app.util.DeviceEntry;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.EmulatorContext;
import org.neutron.device.FontManager;
import org.neutron.device.InputMethod;
import org.neutron.device.impl.DeviceImpl;
import org.neutron.device.j2se.J2SEDevice;
import org.neutron.device.j2se.J2SEDeviceDisplay;
import org.neutron.device.j2se.J2SEFontManager;
import org.neutron.device.j2se.J2SEInputMethod;
import org.neutron.log.Logger;

public class Headless {

	private Common emulator;

	private EmulatorContext context = new EmulatorContext() {

		private DisplayComponent displayComponent = new NoUiDisplayComponent();

		private InputMethod inputMethod = new J2SEInputMethod();

		private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

		private FontManager fontManager = new J2SEFontManager();

		public DisplayComponent getDisplayComponent() {
			return displayComponent;
		}

		public InputMethod getDeviceInputMethod() {
			return inputMethod;
		}

		public DeviceDisplay getDeviceDisplay() {
			return deviceDisplay;
		}

		public FontManager getDeviceFontManager() {
			return fontManager;
		}

		public InputStream getResourceAsStream(Class origClass, String name) {
            return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}
		
		public boolean platformRequest(final String URL) {
			new Thread(new Runnable() {
				public void run() {
					Message.info("MIDlet requests that the device handle the following URL: " + URL);
				}
			}).start();

			return false;
		}
	};

	public Headless() {
		emulator = new Common(context);
	}

	public static void main(String[] args) {
		StringBuffer debugArgs = new StringBuffer();
		ArrayList params = new ArrayList();

		// Allow to override in command line
		// Non-persistent RMS
		params.add("--rms");
		params.add("memory");

		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
			if (debugArgs.length() != 0) {
				debugArgs.append(", ");
			}
			debugArgs.append("[").append(args[i]).append("]");
		}

		if (args.length > 0) {
			Logger.debug("headless arguments", debugArgs.toString());
		}

		Headless app = new Headless();

		DeviceEntry defaultDevice = new DeviceEntry("Default device", null, DeviceImpl.DEFAULT_LOCATION, true, false);

		app.emulator.initParams(params, defaultDevice, J2SEDevice.class);
		app.emulator.initMIDlet(true);
	}

}
