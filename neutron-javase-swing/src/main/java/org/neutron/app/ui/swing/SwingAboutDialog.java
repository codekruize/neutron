package org.neutron.app.ui.swing;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.neutron.app.Main;
import org.neutron.app.util.BuildVersion;
public class SwingAboutDialog extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private JLabel iconLabel;

	private JLabel textLabel;

	public SwingAboutDialog() {

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.ipadx = 10;
		c.ipady = 10;
		c.gridx = 0;
		c.gridy = 0;
		iconLabel = new JLabel();
		add(iconLabel, c);

		iconLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/org/neutron/icon.png"))));

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		textLabel = new JLabel("Neutron");
		textLabel.setFont(new Font("Default", Font.BOLD, 18));
		add(textLabel, c);

		c.gridy = 1;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.WEST;
		add(new JLabel("version: " + BuildVersion.getVersion()), c);

		c.gridy = 2;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel("Copyright (C) 2001-2008 Bartek Teodorczyk & co"), c);

	}
}
