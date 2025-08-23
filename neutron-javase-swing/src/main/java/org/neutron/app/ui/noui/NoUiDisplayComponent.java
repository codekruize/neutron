package org.neutron.app.ui.noui;

import javax.microedition.lcdui.Displayable;

import org.neutron.DisplayAccess;
import org.neutron.DisplayComponent;
import org.neutron.MIDletAccess;
import org.neutron.MIDletBridge;
import org.neutron.app.ui.DisplayRepaintListener;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.device.j2se.J2SEDeviceDisplay;
import org.neutron.device.j2se.J2SEGraphicsSurface;

public class NoUiDisplayComponent implements DisplayComponent {

	private J2SEGraphicsSurface graphicsSurface;

	private DisplayRepaintListener displayRepaintListener;
	
	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public void repaintRequest(int x, int y, int width, int height) 
	{
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		Displayable current = da.getCurrent();
		if (current == null) {
			return;
		}

		Device device = DeviceFactory.getDevice();

		if (device != null) {
			if (graphicsSurface == null) {
				graphicsSurface = new J2SEGraphicsSurface(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight(), false, 0x000000);
			}
					
			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();
			synchronized (graphicsSurface) {
				deviceDisplay.paintDisplayable(graphicsSurface, x, y, width, height);
				if (!deviceDisplay.isFullScreenMode()) {
					deviceDisplay.paintControls(graphicsSurface.getGraphics());
				}
			}

			fireDisplayRepaint(graphicsSurface);
		}	
	}


	private void fireDisplayRepaint(J2SEGraphicsSurface graphicsSurface)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(graphicsSurface);
		}
	}

}
