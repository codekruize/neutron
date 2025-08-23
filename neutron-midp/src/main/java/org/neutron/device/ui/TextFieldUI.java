package org.neutron.device.ui;

public interface TextFieldUI extends ItemUI {

	public void setConstraints(int constraints);
	
	public void setString(String text);
	
	public String getString();
	
}
