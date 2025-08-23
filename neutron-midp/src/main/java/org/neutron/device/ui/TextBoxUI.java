package org.neutron.device.ui;

public interface TextBoxUI extends DisplayableUI {
	
	int getCaretPosition();

	String getString();
	
	void setString(String text);
	
	void insert(String text, int position);
	
	void delete(int offset, int length);
	
}
