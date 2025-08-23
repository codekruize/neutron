package org.neutron.device.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.neutron.device.DeviceDisplay;

public interface DeviceDisplayImpl extends DeviceDisplay {

	Image createSystemImage(URL url) throws IOException;
Button createButton(int skinVersion, String name, Shape shape, int keyCode, String keyboardKeys,
			String keyboardChars, Hashtable inputToChars, boolean modeChange);
SoftButton createSoftButton(int skinVersion, String name, Shape shape, int keyCode, String keyName,
			Rectangle paintable, String alignmentName, Vector commands, Font font);

	SoftButton createSoftButton(int skinVersion, String name, Rectangle paintable, Image normalImage, Image pressedImage);
void setNumColors(int i);
void setIsColor(boolean b);

	void setNumAlphaLevels(int i);
void setBackgroundColor(Color color);
void setForegroundColor(Color color);
void setDisplayRectangle(Rectangle rectangle);
void setDisplayPaintable(Rectangle rectangle);
void setMode123Image(PositionedImage object);
void setModeAbcLowerImage(PositionedImage object);
void setModeAbcUpperImage(PositionedImage object);

	boolean isResizable();

	void setResizable(boolean state);

}
