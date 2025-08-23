package org.neutron.device.impl;

import javax.microedition.lcdui.Command;
public interface SoftButton 
{
	int TYPE_COMMAND = 1;
	int TYPE_ICON = 2;
	
	String getName();
	
	int getType();

  Command getCommand();
  
  void setCommand(Command cmd);
  
  boolean isVisible();
  
  void setVisible(boolean state);
  
  boolean isPressed();
  
  void setPressed(boolean state);
  
  Rectangle getPaintable();
boolean preferredCommandType(Command cmd);

}
