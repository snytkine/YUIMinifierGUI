package com.yahoo.platform.yui.compressor;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class GuiCompressor implements ActionListener {
	/**
	 * Input textarea
	 * 
	 */
	private JTextArea ta;
	/**
	 * Result textarea
	 */
	private JTextArea taResult;

	/**
	 * Main Window
	 */
	private JFrame frame;
	/**
	 * Input area label
	 */
	private JLabel label;
	/**
	 * Result label appears above the result textarea
	 */
	private JLabel resLabel;

	private ButtonGroup group;
	/**
	 * Top panel has our log viewer
	 */
	private TopPanel topPanel;

	/**
	 * Length of input string
	 */
	private int inputLen = 0;

	private final Map<String, JCheckBox> checkBoxes = new HashMap<String, JCheckBox>();

	private static final String MINIFY = "Minify";
	private static final String TO_CLIPBOARD = "Copy result to Clipboard";
	private static final String FROM_CLIPBOARD = "Paste input from Clipboard";
	private static final String CLEAR = "Clear all";
	private static final String ORIGINAL = "Original: ";
	private static final String COMPRESSED = "Compressed: ";
	private static final String VERBOSE = "Verbose";
	private static final String DISABLE_OPTIMIZATION = "Disable optimization";
	private static final String PRESERVE_SEMI = "Preserve Semicolons";
	private static final String NO_OBFUSCATE = "Do not obfuscate";
	private static final String CSS = "CSS";
	private static final String JS = "JavaScript";

	/**
	 * 
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the 
     * event-dispatching thread.
     *
	 * @param args
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GuiCompressor gui = new GuiCompressor();
				gui.init();
			}
		});
	}

	public void init() {
		makeFrame();
		makeTopPanel();
		makeInputPanel();
		makeResultPanel();
		makeBottomPanel();
		frame.setVisible(true);
	}

	/**
	 * Make initial Window
	 */
	private void makeFrame() {
		frame = new JFrame("JS and CSS Compressor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(new MyMenuBar(frame));
		frame.setContentPane(createContentPane());
		frame.setSize(760, 700);
	}

	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		return contentPane;
	}

	private void makeTopPanel() {
		topPanel = new TopPanel();
		frame.getContentPane().add(BorderLayout.NORTH, topPanel);
	}

	/**
	 * Make panel with scrollable textarea for the input text
	 * 
	 */
	private void makeInputPanel() {

		label = new JLabel();
		label.setText(ORIGINAL);

		ta = new JTextArea(15, 30);
		ta.setLineWrap(true);

		JButton button = new JButton(FROM_CLIPBOARD);
		button.addActionListener(this);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(label);

		/**
		 * Scrollable text area for input
		 */
		JScrollPane scroller = new JScrollPane(ta);
		scroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftPanel.add(scroller);
		leftPanel.add(button);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		frame.getContentPane().add(BorderLayout.WEST, leftPanel);
	}

	/**
	 * Make panel with scrollable input area for result textarea
	 */
	private void makeResultPanel() {

		taResult = new JTextArea(15, 30);
		taResult.setLineWrap(false);
		taResult.setEnabled(false);
		taResult.setDisabledTextColor(Color.BLACK);

		resLabel = new JLabel();
		resLabel.setText(COMPRESSED);

		JButton button = new JButton(TO_CLIPBOARD);
		button.addActionListener(this);

		/**
		 * Right area for result area
		 */
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(resLabel);

		/**
		 * Scroller 2 is scrollable text area for result
		 */
		JScrollPane scroller2 = new JScrollPane(taResult);
		scroller2
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller2
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightPanel.add(scroller2);
		rightPanel.add(button);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.getContentPane().add(BorderLayout.CENTER, rightPanel);
	}

	private void makeBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.add(makeTypeButtons());
		panel.add(makeOptions());
		panel.add(makeButtons());
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
	}

	private JPanel makeTypeButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		TitledBorder titled = BorderFactory.createTitledBorder("Input type");
		panel.setBorder(titled);

		JRadioButton jsButton = new JRadioButton(JS);
		jsButton.setMnemonic(KeyEvent.VK_J);
		jsButton.setActionCommand(JS);
		jsButton.setSelected(true);
		jsButton.addActionListener(this);

		JRadioButton cssButton = new JRadioButton(CSS);
		cssButton.setMnemonic(KeyEvent.VK_C);
		cssButton.setActionCommand(CSS);
		cssButton.addActionListener(this);

		group = new ButtonGroup();
		group.add(jsButton);
		group.add(cssButton);

		panel.add(jsButton);
		panel.add(cssButton);

		return panel;
	}

	private JPanel makeOptions() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		TitledBorder titled = BorderFactory.createTitledBorder("Options");
		panel.setBorder(titled);

		panel.add(addCheckBox(VERBOSE));
		panel.add(addCheckBox(NO_OBFUSCATE));
		panel.add(addCheckBox(PRESERVE_SEMI));
		panel.add(addCheckBox(DISABLE_OPTIMIZATION));

		return panel;
	}

	private JPanel makeButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton button = new JButton(MINIFY);
		button.addActionListener(this);

		JButton button2 = new JButton(CLEAR);
		button2.addActionListener(this);
		panel.add(button);
		panel.add(button2);

		return panel;
	}

	/**
	 * Creates JCheckbox and adds it to checkBoxes Map
	 * 
	 * @param String
	 *            s name of checkBox
	 * @return object JCheckBox
	 */
	private JCheckBox addCheckBox(String s) {
		JCheckBox check = new JCheckBox(s);
		checkBoxes.put(s, check);
		return check;
	}

	public void actionPerformed(ActionEvent ev) {

		String cmd = ev.getActionCommand();
		if (cmd.equals(FROM_CLIPBOARD)) {
			fromClipboard();
		} else if (cmd.equals(MINIFY)) {
			minify();
		} else if (cmd.equals(CLEAR)) {
			clearAll();
		} else if (cmd.equals(TO_CLIPBOARD)) {
			toClipboard();
		} else if (cmd.equals(CSS)) {
			disableJSOptions();
		} else if (cmd.equals(JS)) {
			enableJSOptions();
		}
	}

	/**
	 * Disable checkboxes that have to do with JavaScript only options
	 */
	private void disableJSOptions() {
		checkBoxes.get(DISABLE_OPTIMIZATION).setEnabled(false);
		checkBoxes.get(NO_OBFUSCATE).setEnabled(false);
		checkBoxes.get(PRESERVE_SEMI).setEnabled(false);
	}

	/**
	 * Enable checkboxes for Javascript options
	 */
	private void enableJSOptions() {
		checkBoxes.get(DISABLE_OPTIMIZATION).setEnabled(true);
		checkBoxes.get(NO_OBFUSCATE).setEnabled(true);
		checkBoxes.get(PRESERVE_SEMI).setEnabled(true);
	}

	/**
	 * Clear both text areas and reset labels
	 */
	private void clearAll() {
		ta.setText("");
		taResult.setText("");
		topPanel.getTextArea().setText("");
		label.setText(ORIGINAL);
		resLabel.setText(COMPRESSED);
	}

	private void showError(String s) {
		JOptionPane.showMessageDialog(frame, s, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Paste from system clipboard into the input textArea
	 */
	private void fromClipboard() {
		Clipboard clipboard = frame.getToolkit().getSystemClipboard();
		Transferable clipData = clipboard.getContents(clipboard);
		if (clipData != null) {
			try {
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String s = (String) (clipData
							.getTransferData(DataFlavor.stringFlavor));
					inputLen = s.length();
					// ta.replaceSelection(s);
					ta.setText(null);
					ta.setText(s);
					label.setText(ORIGINAL + inputLen + " bytes");
				}
			} catch (UnsupportedFlavorException ufe) {
				topPanel.getTextArea().setText("Unsupported data: " + ufe);
			} catch (IOException ioe) {
				topPanel.getTextArea().setText("Data not available: " + ioe);
			}
		} else {
			topPanel
					.getTextArea()
					.setText(
							"[WARNING] Input is empty. You should copy script to clipboard then click on [Paste from Clipboard] button\n");
		}
	}

	/**
	 * Copy from result textarea into system clipboard Also highlight all text,
	 * just for visual effect
	 */
	private void toClipboard() {
		Clipboard clipboard = frame.getToolkit().getSystemClipboard();
		taResult.setEnabled(true);
		taResult.requestFocus();
		taResult.selectAll();

		String selection = taResult.getSelectedText();
		StringSelection data = new StringSelection(selection);
		clipboard.setContents(data, data);
	}

	/**
	 * Main method that performs minification
	 * 
	 */
	private void minify() {

		StringReader reader = new StringReader(new String(ta.getText()));
		StringWriter out = new StringWriter();

		boolean munge = !checkBoxes.get(NO_OBFUSCATE).isSelected();
		boolean verbose = checkBoxes.get(VERBOSE).isSelected();
		boolean semi = checkBoxes.get(PRESERVE_SEMI).isSelected();
		boolean noopt = checkBoxes.get(DISABLE_OPTIMIZATION).isSelected();
		boolean isJS = (group.getSelection().getActionCommand() == JS);
		int linebreak = 2000;

		try {
			if (isJS) {
				JavaScriptCompressor compressor = new JavaScriptCompressor(
						reader, new MyErrorHandler());

				compressor
						.compress(out, linebreak, munge, verbose, semi, noopt);
			} else {
				CssCompressor compressor = new CssCompressor(reader);
				compressor.compress(out, linebreak);
			}
			String input;
			String res = out.toString();
			taResult.setText(res);
			int len = res.length();

			input = ta.getText();
			inputLen = input.length();
			label.setText(ORIGINAL + inputLen + " bytes");

			String label = COMPRESSED + len + " bytes";
			if (inputLen > 0) {
				int ratio = 100 - (len * 100 / inputLen);
				label = label + "  Compression: " + ratio + "%";
			}

			resLabel.setText(label);

		} catch (EvaluatorException e) {
			showError(e.getMessage());
		} catch (IOException e) {
			String err = "IO Exception: " + e.getMessage();
			showError(err);
		} catch (Exception e) {
			String err = "Exception: " + e.getMessage();
			showError(err);
		}

	}

	/**
	 * Handle Errors and Warning by displaying then in special textarea
	 * 
	 * @author Dmitri Snytkine
	 * 
	 */
	public class MyErrorHandler implements ErrorReporter {

		@Override
		public void error(String message, String sourceName, int line,
				String lineSource, int lineOffset) {

			if (line >= 0) {
				message = message + " on line: " + line + " column: "
						+ lineOffset;
			}

			topPanel.getTextArea().append("\n[ERROR] " + message);
		}

		@Override
		public EvaluatorException runtimeError(String message,
				String sourceName, int line, String lineSource, int lineOffset) {
			return new EvaluatorException(message);
		}

		@Override
		public void warning(String message, String sourceName, int line,
				String lineSource, int lineOffset) {
			if (line >= 0) {
				message = message + " on line: " + line + " column: "
						+ lineOffset;
			}

			topPanel.getTextArea().append("\n[WARNING] " + message);
		}
	}
}
