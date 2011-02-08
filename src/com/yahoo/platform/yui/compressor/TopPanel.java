package com.yahoo.platform.yui.compressor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class TopPanel extends JPanel implements ActionListener {

	/**
	 * Error textarea
	 */
	private JTextArea taError;
	private static final String CLEAR_LOG = "Clear log";

	public TopPanel() {

		super();

		taError = new JTextArea(4, 50);
		taError.setLineWrap(true);
		taError.setEnabled(false);
		taError.setDisabledTextColor(Color.RED);
		taError.setAlignmentX(0.5f);
		taError.setAlignmentY(0.25f);

		JButton button = new JButton(CLEAR_LOG);
		button.addActionListener(this);
		setBackground(Color.darkGray);

		/**
		 * Scrollable text area for input
		 */
		JScrollPane scroller = new JScrollPane(taError);
		scroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroller);
		this.add(button);
	}

	public void actionPerformed(ActionEvent ev) {
		taError.setText("");
	}

	public JTextArea getTextArea() {
		return taError;
	}
}
