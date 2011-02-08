package com.yahoo.platform.yui.compressor;

import java.awt.event.*;

import javax.swing.*;

public class MyMenuBar extends JMenuBar implements ActionListener {

	private static final String HELP = "Help";
	private static final String ABOUT = "About";
	private static final String OPTIONS = "Config Options";
	private JMenuItem menuItem;

	private JFrame frame;

	public MyMenuBar(JFrame frame) {
		super();
		this.frame = frame;
		// Build the first menu.
		JMenu menu = new JMenu(HELP);
		menu.setMnemonic(KeyEvent.VK_H);
		/**
		 * First menu Item
		 */
		menuItem = new JMenuItem(OPTIONS, KeyEvent.VK_C);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);

		/**
		 * Second menu Item
		 */
		menuItem = new JMenuItem(ABOUT, KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);

	}

	public void actionPerformed(ActionEvent ev) {
		String cmd = ev.getActionCommand();
		if (cmd.equals(OPTIONS)) {
			showHelp();
		} else if (cmd.equals(ABOUT)) {
			showAbout();
		}
	}

	private void showAbout() {

		JEditorPane pane = new JEditorPane();
		pane.setContentType("text/html");
		pane.setEditable(false);
		pane
				.setText("<html>"
						+ "<p>This is a graphical version of YUI Compressor</p>"
						+ "<p>The YUI Compressor library<br>was written and is maintained by:<br>"
						+ "Julien Lecomte &lt;jlecomte@yahoo-inc.com&gt;</p>"
						+ "<p>The CSS portion is a port of Isaac Schlueter's cssmin utility</p>"
						+ "<p>This program was written by<br>Dmitri Snytkine</p>");

		/*
		 * "<p>Latest version, source code and more info available on<br>" +
		 * "<a href='http://www.minify.it'>Minify.it</a></p></html>");
		 */

		JOptionPane.showMessageDialog(frame, pane, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showHelp() {
		JEditorPane pane = new JEditorPane();
		pane.setContentType("text/html");
		pane.setEditable(false);
		pane
				.setText("<html>"
						+ "<dl><dt>Input type Javascript | CSS</dt><dd>This option is required</dd><dt>Verbose</dt><dd>Display informational messages and warnings.</dd></dl><strong>JAVASCRIPT ONLY OPTIONS</strong><dl><dt>Do not obfuscate</dt><dd>Minify only. Do not obfuscate local symbols.</dd><dt>Preserve semicolons</dt><dd>Preserve unnecessary semicolons<br>(such as right before a '}')<br>This option is useful when <br>compressed code has to be run<br>through JSLint<br>(which is the case of YUI for example)</dd><dt>Disable optimizations</dt><dd>Disable all the built-in micro optimizations.</dd></dl></html>");

		JOptionPane.showMessageDialog(frame, pane, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

}
