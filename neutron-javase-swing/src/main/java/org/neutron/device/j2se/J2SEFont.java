package org.neutron.device.j2se;

import java.awt.Font;

public interface J2SEFont extends org.neutron.device.impl.Font {

	Font getFont();
	
	void setAntialiasing(boolean antialiasing);
	
}
