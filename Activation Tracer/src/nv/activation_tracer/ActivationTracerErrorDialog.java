package nv.activation_tracer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class ActivationTracerErrorDialog extends JOptionPane {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private JDialog dialog;
	
	//-----------------------------------------------------------------//
	
	/** Create and manage GUI components **/
	
	protected void showMessageDialog(JFrame parentFrame, String title, String errorType, String errorMessage) {
		final JOptionPane pane = new JOptionPane(buildMainPanel(errorType, errorMessage), JOptionPane.ERROR_MESSAGE, JOptionPane.PLAIN_MESSAGE);
		pane.setComponentOrientation((getRootFrame()).getComponentOrientation());
		pane.setMessageType(PLAIN_MESSAGE);
		dialog = pane.createDialog(null, title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		dialog.validate();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);
	}
	
	protected void showMessageDialog(JDialog parentDialog, String title, String errorType, String errorMessage) {
		final JOptionPane pane = new JOptionPane(buildMainPanel(errorType, errorMessage), JOptionPane.ERROR_MESSAGE, JOptionPane.PLAIN_MESSAGE);
		pane.setComponentOrientation((getRootFrame()).getComponentOrientation());
		pane.setMessageType(PLAIN_MESSAGE);
		dialog = pane.createDialog(null, title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		dialog.validate();
		dialog.setLocationRelativeTo(parentDialog);
		dialog.setVisible(true);
	}
	
	public void showMessageDialog(JFrame parentFrame, String title, Exception exception) {
		final JOptionPane pane = new JOptionPane(buildMainPanel(exception.getClass().getSimpleName(), "Error Message: \n" + exception.getMessage()), JOptionPane.ERROR_MESSAGE, JOptionPane.PLAIN_MESSAGE);
		pane.setComponentOrientation((getRootFrame()).getComponentOrientation());
		pane.setMessageType(PLAIN_MESSAGE);
		dialog = pane.createDialog(null, title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		dialog.validate();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);		
	}
	
	private static JPanel buildMainPanel(String errorType, String errorMessage) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Error Info"));
		JLabel errorTypeLabel = new JLabel("Error Type:  " + errorType);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(5, 5, 0, 5);
		mainPanel.add(errorTypeLabel, mainPanelConstraints);
		JTextArea textArea = new JTextArea(errorMessage);
		textArea.setColumns(30);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane textAreaScrollPane = new JScrollPane(textArea);
		textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaScrollPane.setPreferredSize(new Dimension(300, 150));
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 1;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(0, 5, 0, 5);
		mainPanel.add(textAreaScrollPane, mainPanelConstraints);
		return mainPanel;
	}
	
	//-----------------------------------------------------------------//
	
}
