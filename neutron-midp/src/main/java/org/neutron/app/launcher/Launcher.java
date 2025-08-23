package org.neutron.app.launcher;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import org.neutron.MIDletEntry;
import org.neutron.app.CommonInterface;

public class Launcher extends MIDlet implements CommandListener {

	protected static final Command CMD_LAUNCH = new Command("Start", Command.ITEM, 0);;

	protected static final String NOMIDLETS = "[no midlets]";

	protected CommonInterface common;

	protected List menuList;

	protected static Vector midletEntries = new Vector();

	public Launcher(CommonInterface common) {
		this.common = common;
	}

	public static void addMIDletEntry(MIDletEntry entry) {
		midletEntries.addElement(entry);
	}

	public static void removeMIDletEntries() {
		midletEntries.removeAllElements();
	}

	public MIDletEntry getSelectedMidletEntry() {
		if (menuList != null) {
			int idx = menuList.getSelectedIndex();
			if (!menuList.getString(idx).equals(NOMIDLETS)) {
				return (MIDletEntry) midletEntries.elementAt(idx);
			}
		}

		return null;
	}

	public void destroyApp(boolean unconditional) {
	}

	public void pauseApp() {
	}

	public void startApp() {
		menuList = new List("Launcher", List.IMPLICIT);
		menuList.addCommand(CMD_LAUNCH);
		menuList.setCommandListener(this);

		if (midletEntries.size() == 0) {
			menuList.append(NOMIDLETS, null);
		} else {
			for (int i = 0; i < midletEntries.size(); i++) {
				menuList.append(((MIDletEntry) midletEntries.elementAt(i)).getName(), null);
			}
		}

		Display.getDisplay(this).setCurrent(menuList);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == menuList) {
			if (c == List.SELECT_COMMAND || c == CMD_LAUNCH) {
				MIDletEntry entry = getSelectedMidletEntry();
				if (entry != null) {
					common.initMIDlet(true, entry);
				}
			}
		}
	}

}
