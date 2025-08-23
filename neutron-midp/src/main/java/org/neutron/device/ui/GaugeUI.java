package org.neutron.device.ui;

public interface GaugeUI extends ItemUI {

	void setValue(int value);

	int getValue();

	void setMaxValue(int maxValue);

}
