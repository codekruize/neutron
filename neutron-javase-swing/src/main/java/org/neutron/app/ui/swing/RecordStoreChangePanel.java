package org.neutron.app.ui.swing;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.neutron.app.Common;
import org.neutron.app.util.FileRecordStoreManager;

public class RecordStoreChangePanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private Common common;

	private JComboBox selectStoreCombo = new JComboBox(new String[] { "File record store", "Memory record store" });

	public RecordStoreChangePanel(Common common) {
		this.common = common;

		add(new JLabel("Record store type:"));
		add(selectStoreCombo);
	}

	protected void showNotify() {
		if (common.getRecordStoreManager() instanceof FileRecordStoreManager) {
			selectStoreCombo.setSelectedIndex(0);
		} else {
			selectStoreCombo.setSelectedIndex(1);
		}
	}

	public String getSelectedRecordStoreName() {
		return (String) selectStoreCombo.getSelectedItem();
	}

}
