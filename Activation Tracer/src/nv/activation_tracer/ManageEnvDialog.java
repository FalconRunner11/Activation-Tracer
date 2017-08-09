package nv.activation_tracer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class ManageEnvDialog extends JOptionPane implements ActionListener, ItemListener, FocusListener {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	final private String dbConfigFile = "ATDBConfig.txt";
	final private String backupDbConfigFile = "ATDBConfig_Backup.txt";
	final private String connStringDemo = "<host>.snt.bst.bls.com:<port>:<SID>";
	final private String nullFieldString = "null";
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private JDialog dialog;
	private ArrayList<Environment> envDetails = null;
	private JComboBox<String> envComboBox = new JComboBox<String>();
	private JButton deleteButton = new JButton("Delete");
	private JButton newEnvButton = new JButton("New Environment");
	private JTextField envNameTextField = new JTextField(10);
	private JTextField olConnStringTextField = new JTextField(25);
	private JTextField olUsernameTextField = new JTextField(12);
	private JPasswordField olPasswordTextField = new JPasswordField(12);
	private JTextField cimConnStringTextField = new JTextField(25);
	private JTextField cimUsernameTextField = new JTextField(12);
	private JPasswordField cimPasswordTextField = new JPasswordField(12);
	private JTextField alConnStringTextField = new JTextField(25);
	private JTextField alUsernameTextField = new JTextField(12);
	private JPasswordField alPasswordTextField = new JPasswordField(12);
	private JTextField mlConnStringTextField = new JTextField(25);
	private JTextField mlUsernameTextField = new JTextField(12);
	private JPasswordField mlPasswordTextField = new JPasswordField(12);
	private JButton saveButton = new JButton ("Save");
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	//-----------------------------------------------------------------//
	
	/** Create and manage GUI components **/
	
	protected void showMessageDialog(JFrame parentFrame, String title) {
		final JOptionPane pane = new JOptionPane(buildMainPanel(), JOptionPane.ERROR_MESSAGE, JOptionPane.PLAIN_MESSAGE);
		pane.setComponentOrientation((getRootFrame()).getComponentOrientation());
		pane.setMessageType(PLAIN_MESSAGE);
		pane.setOptions(new Object[] {});
		dialog = pane.createDialog(null, title);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent wE) {
				cancelButtonPress();
			}
			public void windowClosed(WindowEvent wE) {
				//Take no additional action once the JDialog is closed.
			}
		});
		dialog.pack();
		dialog.validate();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);
	}
	
	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Environments"));
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.weightx = 1.0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.insets = new Insets(0, 0, 30, 0);
		mainPanel.add(buildSelectionPanel(), mainPanelConstraints);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 1;
		mainPanelConstraints.weightx = 1.0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanelConstraints.insets = new Insets(0, 10, 0, 10);
		mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), mainPanelConstraints);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 2;
		mainPanelConstraints.weightx = 1.0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.insets = new Insets(0, 0, 30, 0);
		mainPanel.add(buildEditPanel(), mainPanelConstraints);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 3;
		mainPanelConstraints.weightx = 1.0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanelConstraints.insets = new Insets(0, 10, 0, 10);
		mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), mainPanelConstraints);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 4;
		mainPanelConstraints.weightx = 1.0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(buildFinishButtonPanel(), mainPanelConstraints);
		return mainPanel;
	}
	
	private JPanel buildSelectionPanel() {
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridBagLayout());
		GridBagConstraints selectionPanelConstraints = new GridBagConstraints();
		JLabel selectionPanelEnvLabel = new JLabel("Select Environment:");
		selectionPanelConstraints.gridx = 0;
		selectionPanelConstraints.gridy = 0;
		selectionPanelConstraints.weightx = 0.0;
		selectionPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		selectionPanelConstraints.insets = new Insets(4, 10, 0, 10);
		selectionPanel.add(selectionPanelEnvLabel, selectionPanelConstraints);
		try {
			loadFromDbConfigFile(dbConfigFile);
		} catch (IOException e) {
			//Auto-generated catch block...handled deeper within program
		}
		buildEnvComboBox(0);
		envComboBox.addItemListener(this);
		envComboBox.setSelectedIndex(0);
		selectionPanelConstraints.gridx = 0;
		selectionPanelConstraints.gridy = 1;
		selectionPanelConstraints.weightx = 0.0;
		selectionPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		selectionPanelConstraints.insets = new Insets(0, 10, 10, 20);
		selectionPanel.add(envComboBox, selectionPanelConstraints);
		deleteButton.addActionListener(this);
		selectionPanelConstraints.gridx = 1;
		selectionPanelConstraints.gridy = 1;
		selectionPanelConstraints.weightx = 1.0;
		selectionPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		selectionPanelConstraints.insets = new Insets(0, 10, 10, 10);
		selectionPanel.add(deleteButton, selectionPanelConstraints);
		newEnvButton.addActionListener(this);
		selectionPanelConstraints.gridx = 0;
		selectionPanelConstraints.gridy = 2;
		selectionPanelConstraints.weightx = 0.0;
		selectionPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		selectionPanelConstraints.insets = new Insets(10, 10, 0, 20);
		selectionPanel.add(newEnvButton, selectionPanelConstraints);
		return selectionPanel;
	}
	
	private JPanel buildEditPanel() {
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new GridBagLayout());
		GridBagConstraints editPanelConstraints = new GridBagConstraints();
		JLabel envNameLabel = new JLabel("Environment:");
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 0;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(4, 10, 0, 10);
		editPanel.add(envNameLabel, editPanelConstraints);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 1;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 10, 10);
		editPanel.add(envNameTextField, editPanelConstraints);
		
		JLabel olConnStringLabel = new JLabel("OL DB Connection String:");
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 2;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 0, 10);
		editPanel.add(olConnStringLabel, editPanelConstraints);
		olConnStringTextField.addFocusListener(this);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 3;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 10, 10);
		editPanel.add(olConnStringTextField, editPanelConstraints);
		JLabel olUsernameLabel = new JLabel("OL DB Username:");
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 2;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(olUsernameLabel, editPanelConstraints);
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 3;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(olUsernameTextField, editPanelConstraints);
		JLabel olPasswordLabel = new JLabel("OL DB Password:");
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 2;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(olPasswordLabel, editPanelConstraints);
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 3;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(olPasswordTextField, editPanelConstraints);
		
		JLabel cimConnStringLabel = new JLabel("CIM DB Connection String:");
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 4;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 0, 10);
		editPanel.add(cimConnStringLabel, editPanelConstraints);
		cimConnStringTextField.addFocusListener(this);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 5;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 10, 10);
		editPanel.add(cimConnStringTextField, editPanelConstraints);
		JLabel cimUsernameLabel = new JLabel("CIM DB Username:");
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 4;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(cimUsernameLabel, editPanelConstraints);
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 5;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(cimUsernameTextField, editPanelConstraints);
		JLabel cimPasswordLabel = new JLabel("CIM DB Password:");
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 4;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(cimPasswordLabel, editPanelConstraints);
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 5;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(cimPasswordTextField, editPanelConstraints);
		
		JLabel alConnStringLabel = new JLabel("AL DB Connection String:");
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 6;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 0, 10);
		editPanel.add(alConnStringLabel, editPanelConstraints);
		alConnStringTextField.addFocusListener(this);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 7;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 10, 10);
		editPanel.add(alConnStringTextField, editPanelConstraints);
		JLabel alUsernameLabel = new JLabel("AL DB Username:");
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 6;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(alUsernameLabel, editPanelConstraints);
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 7;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(alUsernameTextField, editPanelConstraints);
		JLabel alPasswordLabel = new JLabel("AL DB Password:");
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 6;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(alPasswordLabel, editPanelConstraints);
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 7;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(alPasswordTextField, editPanelConstraints);
		
		JLabel mlConnStringLabel = new JLabel("ML DB Connection String:");
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 8;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 0, 10);
		editPanel.add(mlConnStringLabel, editPanelConstraints);
		mlConnStringTextField.addFocusListener(this);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 9;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 10, 10, 10);
		editPanel.add(mlConnStringTextField, editPanelConstraints);
		JLabel mlUsernameLabel = new JLabel("ML DB Username:");
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 8;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(mlUsernameLabel, editPanelConstraints);
		editPanelConstraints.gridx = 1;
		editPanelConstraints.gridy = 9;
		editPanelConstraints.weightx = 0.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(mlUsernameTextField, editPanelConstraints);
		JLabel mlPasswordLabel = new JLabel("ML DB Password:");
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 8;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 0, 10);
		editPanel.add(mlPasswordLabel, editPanelConstraints);
		editPanelConstraints.gridx = 2;
		editPanelConstraints.gridy = 9;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(0, 20, 10, 10);
		editPanel.add(mlPasswordTextField, editPanelConstraints);
		
		saveButton.addActionListener(this);
		editPanelConstraints.gridx = 0;
		editPanelConstraints.gridy = 10;
		editPanelConstraints.weightx = 1.0;
		editPanelConstraints.gridwidth = 1;
		editPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editPanelConstraints.insets = new Insets(10, 10, 0, 10);
		editPanel.add(saveButton, editPanelConstraints);
		
		return editPanel;
	}
	
	private JPanel buildFinishButtonPanel() {
		JPanel finishButtonPanel = new JPanel();
		finishButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		okButton.addActionListener(this);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(10, 10, 10, 10);
		finishButtonPanel.add(okButton, mainPanelConstraints);
		cancelButton.addActionListener(this);
		mainPanelConstraints.gridx = 1;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(10, 10, 10, 10);
		finishButtonPanel.add(cancelButton, mainPanelConstraints);
		return finishButtonPanel;
	}
	
	private void buildEnvComboBox(int index) {
		JComboBox<String> tempComboBox;
		if (envDetails.size() > 0) {
			String[] environments = new String[envDetails.size()];
			for (int i = 0; i < environments.length; i++) {
				environments[i] = envDetails.get(i).getEnvironmentName();
			}
			tempComboBox = new JComboBox<String>(environments);
		}
		else {
			tempComboBox = new JComboBox<String>(new String[] {"NO ENVIRONMENT"});
		}
		envComboBox.setModel(tempComboBox.getModel());
		envComboBox.setPrototypeDisplayValue("XXXXXXXXXX");
		envComboBox.setSelectedIndex(index);
		populateTextFields();
	}
	
	private void populateTextFields() {
		if (envDetails.size() == 0) {
			//No Environments created, nullify and disable textfields
			clearTextFields();
			disableTextFields();
		}
		else {
			//Fill textfields with data from selected Environment
			Environment selectedEnv = envDetails.get(envComboBox.getSelectedIndex());
			envNameTextField.setText(selectedEnv.getEnvironmentName());
			if (selectedEnv.getOlConnString().equals("")) {
				olConnStringTextField.setText(connStringDemo);
			}
			else {
				olConnStringTextField.setText(selectedEnv.getOlConnString());
			}
			olUsernameTextField.setText(selectedEnv.getOlUsername());
			olPasswordTextField.setText(selectedEnv.getOlPassword());
			if (selectedEnv.getCimConnString().equals("")) {
				cimConnStringTextField.setText(connStringDemo);
			}
			else {
				cimConnStringTextField.setText(selectedEnv.getCimConnString());
			}
			cimUsernameTextField.setText(selectedEnv.getCimUsername());
			cimPasswordTextField.setText(selectedEnv.getCimPassword());
			if (selectedEnv.getAlConnString().equals("")) {
				alConnStringTextField.setText(connStringDemo);
			}
			else {
				alConnStringTextField.setText(selectedEnv.getAlConnString());
			}
			alUsernameTextField.setText(selectedEnv.getAlUsername());
			alPasswordTextField.setText(selectedEnv.getAlPassword());
			if (selectedEnv.getMlConnString().equals("")) {
				mlConnStringTextField.setText(connStringDemo);
			}
			else {
				mlConnStringTextField.setText(selectedEnv.getMlConnString());
			}
			mlUsernameTextField.setText(selectedEnv.getMlUsername());
			mlPasswordTextField.setText(selectedEnv.getMlPassword());
			enableTextFields();
		}
	}
	
	private void clearTextFields() {
		envNameTextField.setText(nullFieldString);
		olConnStringTextField.setText(nullFieldString);
		olUsernameTextField.setText(nullFieldString);
		olPasswordTextField.setText(nullFieldString);
		cimConnStringTextField.setText(nullFieldString);
		cimUsernameTextField.setText(nullFieldString);
		cimPasswordTextField.setText(nullFieldString);
		mlConnStringTextField.setText(nullFieldString);
		mlUsernameTextField.setText(nullFieldString);
		mlPasswordTextField.setText(nullFieldString);
		alConnStringTextField.setText(nullFieldString);
		alUsernameTextField.setText(nullFieldString);
		alPasswordTextField.setText(nullFieldString);
	}
	
	private void enableTextFields() {
		envNameTextField.setEditable(true);
		olConnStringTextField.setEditable(true);
		olUsernameTextField.setEditable(true);
		olPasswordTextField.setEditable(true);
		cimConnStringTextField.setEditable(true);
		cimUsernameTextField.setEditable(true);
		cimPasswordTextField.setEditable(true);
		mlConnStringTextField.setEditable(true);
		mlUsernameTextField.setEditable(true);
		mlPasswordTextField.setEditable(true);
		alConnStringTextField.setEditable(true);
		alUsernameTextField.setEditable(true);
		alPasswordTextField.setEditable(true);
	}
	
	private void disableTextFields() {
		envNameTextField.setEditable(false);
		olConnStringTextField.setEditable(false);
		olUsernameTextField.setEditable(false);
		olPasswordTextField.setEditable(false);
		cimConnStringTextField.setEditable(false);
		cimUsernameTextField.setEditable(false);
		cimPasswordTextField.setEditable(false);
		mlConnStringTextField.setEditable(false);
		mlUsernameTextField.setEditable(false);
		mlPasswordTextField.setEditable(false);
		alConnStringTextField.setEditable(false);
		alUsernameTextField.setEditable(false);
		alPasswordTextField.setEditable(false);
	}
	
	//-----------------------------------------------------------------//
	
	/** Implemented methods and sub-methods **/
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			okButtonPress();
		}
		else if (e.getSource() == cancelButton) {
			cancelButtonPress();
		}
		else if (e.getSource() == deleteButton) {
			deleteButtonPress();
		}
		else if (e.getSource() == newEnvButton) {
			newEnvButtonPress();
		}
		else if (e.getSource() == saveButton) {
			saveButtonPress();
		}
	}
	
	private void okButtonPress() {
		boolean badUserDataExists = false;
		for (int i = 0; i < envDetails.size(); i++) {
			badUserDataExists = checkUserInputData(envDetails.get(i));
			if (badUserDataExists) {
				break;
			}
		}
		if (!badUserDataExists) {
			//Return and save changes
			try {
				saveToDbConfigFile(dbConfigFile);
			} catch (IOException e) {
				//Auto-generated catch block...handled deeper within program
			}
			dialog.dispose();
		}
	}
	
	private void cancelButtonPress() {
		//Return without saving any changes
		dialog.dispose();
	}
	
	private void deleteButtonPress() {
		//Remove selected environment envDetails and reset envCombobox
		if (envDetails.size() > 0) {
			envDetails.remove(envComboBox.getSelectedIndex());
			buildEnvComboBox(0);
		}
		else {
			postCustomError("Invalid Selection Error", "No Environment to delete!");
		}
	}
	
	private void newEnvButtonPress() {
		//Add new empty environment to envDetails, update envComboBox, set textfields
		String newEnvName = "New";
		boolean matchFound = false;
		for (int i = 0; ; i++) {
			newEnvName = "New" + i;
			for (int j = 0; j < envDetails.size(); j++) {
				matchFound = false;
				if (newEnvName.equals(envDetails.get(j).getEnvironmentName())) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				break;
			}
		}
		Environment newEnvToAdd = new Environment(newEnvName);
		envDetails.add(newEnvToAdd);
		buildEnvComboBox(envDetails.size() - 1);
//		envComboBox.setSelectedIndex(envDetails.size() - 1);
	}
	
	private void saveButtonPress() {
		if (envDetails.size() == 0) {
			postCustomError("Invalid Selection Error", "No Environment to save!");
		}
		else {
			JTextField[] textFieldList = {envNameTextField, olConnStringTextField, olUsernameTextField, olPasswordTextField, 
										  cimConnStringTextField, cimUsernameTextField, cimPasswordTextField, alConnStringTextField, 
										  alUsernameTextField, alPasswordTextField, mlConnStringTextField, mlUsernameTextField, 
										  mlPasswordTextField};
			String[] stringList = {"Environment", "OL DB Connection String", "OL Username", "OL DB Password", 
								   "CIM DB Connection String", "CIM Username", "CIM DB Password", "AL DB Connection String", 
								   "AL Username", "AL DB Password", "ML DB Connection String", "ML Username", 
								   "ML DB Password",};
			boolean badUserInputExists = false;
			for (int i = 0; i < textFieldList.length; i++) {
				badUserInputExists = checkUserInputTextFields(textFieldList[i], stringList[i]);
				if (badUserInputExists) {
					break;
				}
			}
			if (!badUserInputExists) {
				String tempEnvName = envNameTextField.getText();
				String tempOlConnString = olConnStringTextField.getText();
				String tempOlUsername = olUsernameTextField.getText();
				char[] olPasswordChars = olPasswordTextField.getPassword();
				String tempOlPassword = "";
				for (int i = 0; i < olPasswordChars.length; i++) {
					tempOlPassword += olPasswordChars[i];
				}
				String tempCimConnString = cimConnStringTextField.getText();
				String tempCimUsername = cimUsernameTextField.getText();
				char[] cimPasswordChars = cimPasswordTextField.getPassword();
				String tempCimPassword = "";
				for (int i = 0; i < cimPasswordChars.length; i++) {
					tempCimPassword += cimPasswordChars[i];
				}
				String tempAlConnString = alConnStringTextField.getText();
				String tempAlUsername = alUsernameTextField.getText();
				char[] alPasswordChars = alPasswordTextField.getPassword();
				String tempAlPassword = "";
				for (int i = 0; i < alPasswordChars.length; i++) {
					tempAlPassword += alPasswordChars[i];
				}
				String tempMlConnString = mlConnStringTextField.getText();
				String tempMlUsername = mlUsernameTextField.getText();
				char[] mlPasswordChars = mlPasswordTextField.getPassword();
				String tempMlPassword = "";
				for (int i = 0; i < mlPasswordChars.length; i++) {
					tempMlPassword += mlPasswordChars[i];
				}
				
				boolean matchFound = false;
				for (int i = 0; i < envDetails.size(); i++) {
					if (i == envComboBox.getSelectedIndex()) {
						//Ignore currently selected Environment
						continue;
					}
					else {
						if (tempEnvName.equals(envDetails.get(i).getEnvironmentName())) {
							matchFound = true;
							break;
						}
					}
				}
				if (matchFound) {
					postCustomError("Invalid Selection Error", "Environment with name: \n\"" + tempEnvName + "\"\nalready exists!");
				}
				else {	
					Environment envToSave = new Environment(tempEnvName, tempOlConnString, tempOlUsername, tempOlPassword, 
															tempCimConnString, tempCimUsername, tempCimPassword, tempAlConnString, 
															tempAlUsername, tempAlPassword, tempMlConnString, tempMlUsername, tempMlPassword);
					envDetails.set(envComboBox.getSelectedIndex(), envToSave);
					buildEnvComboBox(envComboBox.getSelectedIndex());
				}
			}
		}
	}
	
	private boolean checkUserInputTextFields(JTextField tf, String s) {
		if (tf.getText().equals("")) {
			postCustomError("Invalid Input Error", "You must specify a value for field \n\"" + s + "\"\n!");
			return true;
		}
		else if (tf.getText().contains("<") || tf.getText().contains(">") || tf.getText().contains("|")) {
			postCustomError("Invalid Input Error", "Field \n\"" + s + "\"\n cannot contain the characters '<', '>', '|' !");
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean checkUserInputData(Environment env) {
		ArrayList<String> envData = new ArrayList<String>();
		envData.add(env.getEnvironmentName());
		envData.add(env.getOlConnString());
		envData.add(env.getOlUsername());
		envData.add(env.getOlPassword());
		envData.add(env.getCimConnString());
		envData.add(env.getCimUsername());
		envData.add(env.getCimPassword());
		envData.add(env.getAlConnString());
		envData.add(env.getAlUsername());
		envData.add(env.getAlPassword());
		envData.add(env.getMlConnString());
		envData.add(env.getMlUsername());
		envData.add(env.getMlPassword());
		for (int i = 0; i < envData.size(); i++) {
			if (envData.get(i).equals("")) {
				postCustomError("Invalid Input Error", "You must save your Environment data before returning!  " + 
						  "If you wish to return without saving, press the Cancel button.");
				return true;
			}
		}
		return false;
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			//Populate all textfields with environment info
			populateTextFields();
		}
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			//Do nothing for deselected item
		}
	}

	public void focusGained(FocusEvent e) {
		//Clear text if connStringDemo is there
		JTextField focusedTextField = (JTextField) e.getComponent();
		if (focusedTextField.getText().equals(connStringDemo)) {
			focusedTextField.setText("");
		}
	}

	public void focusLost(FocusEvent e) {
		//Do nothing for unfocused textfield
	}

	//-----------------------------------------------------------------//
	
	/** File I/O **/
	
	private void loadFromDbConfigFile(String fileString) throws IOException {
		envDetails = new ArrayList<Environment>();
		BufferedReader inStream = null;
		try {
			inStream = new BufferedReader(new FileReader(fileString));
			String nextLine = inStream.readLine();
			while (nextLine != null) {
				ArrayList<String> messageBuilderArray;
				try {
					messageBuilderArray = parseLine(nextLine);
					String tempEnvName = messageBuilderArray.get(0);
					String tempOlConnString = messageBuilderArray.get(1);
					String tempOlUser = messageBuilderArray.get(2);
					String tempOlPass = messageBuilderArray.get(3);
					String tempCimConnString = messageBuilderArray.get(4);
					String tempCimUser = messageBuilderArray.get(5);
					String tempCimPass = messageBuilderArray.get(6);
					String tempAlConnString = messageBuilderArray.get(7);
					String tempAlUser = messageBuilderArray.get(8);
					String tempAlPass = messageBuilderArray.get(9);
					String tempMlConnString = messageBuilderArray.get(10);
					String tempMlUser = messageBuilderArray.get(11);
					String tempMlPass = messageBuilderArray.get(12);
					Environment tempEnvironment = new Environment(tempEnvName, tempOlConnString, tempOlUser, tempOlPass, tempCimConnString,
																  tempCimUser, tempCimPass, tempAlConnString, tempAlUser, tempAlPass,
																  tempMlConnString, tempMlUser, tempMlPass);
					envDetails.add(tempEnvironment);
					nextLine = inStream.readLine();
				} catch (Exception e) {
					postCustomError("Corrupt File Error", "ATDBconfig.txt has become corrupted!\nEnvironment data not loaded.");
					envDetails = new ArrayList<Environment>();
					saveToDbConfigFile(fileString);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			File f = new File(fileString);
			f.createNewFile();
		} catch (IOException e) {
			postCustomError(e.getClass().getSimpleName(), "Error Message: \n" + e.getMessage());
		} finally {
			if (inStream != null) {
				inStream.close();
			}
		}
	}
	
	private void saveToDbConfigFile(String fileString) throws IOException {
		backupDbConfigFile(dbConfigFile, backupDbConfigFile);
		BufferedWriter outStream = null;
		try {
			outStream = new BufferedWriter(new FileWriter(fileString));
			if (envDetails.size() == 0) {
				//No Environment data
				outStream.write("");
			}
			for (int i = 0; i < envDetails.size(); i++) {
				String lineToWrite = reverseParseLine(envDetails.get(i));
				outStream.write(lineToWrite);
				outStream.newLine();
			}
		} catch (IOException e) {
			postCustomError(e.getClass().getSimpleName(), "Error Message: \n" + e.getMessage());
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
	}
	
	private void backupDbConfigFile(String source, String destination) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(destination));
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) > 0) {
				output.write(buffer, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}
	
	private ArrayList<String> parseLine(String lineToParse) throws IOException {
		ArrayList<String> tempMessageInfoArray = new ArrayList<String>();
		while (lineToParse != null && !lineToParse.isEmpty()) {
			String fullTag = lineToParse.substring(lineToParse.indexOf("<"), lineToParse.indexOf(">") + 1);
			String componentType = fullTag.substring(1, fullTag.indexOf("|"));
			String componentData = fullTag.substring(fullTag.indexOf("|") + 1, fullTag.indexOf(">"));
			if (componentType.length() == 0 || componentData.length() == 0) {
				postCustomError("Corrupt File Error", "ATDBconfig.txt has become corrupted!\nEnvironment data not loaded.");
				envDetails = new ArrayList<Environment>();
				saveToDbConfigFile(dbConfigFile);
				break;
			}
			else if (componentType.contains("<") || componentType.contains(">") || componentType.contains("|") ||
					 componentData.contains("<") || componentData.contains(">") || componentData.contains("|")) {
				postCustomError("Corrupt File Error", "ATDBconfig.txt has become corrupted!\nEnvironment data not loaded.");
				envDetails = new ArrayList<Environment>();
				saveToDbConfigFile(dbConfigFile);
				break;
			}
			else {
				tempMessageInfoArray.add(componentData);
				lineToParse = lineToParse.substring(lineToParse.indexOf(">") + 1);
			}
		}
		return tempMessageInfoArray;
	}
	
	private String reverseParseLine(Environment env) {
		String fullString = "";
		fullString += "<EnvName|" + env.getEnvironmentName() + ">";
		fullString += "<OlConnString|" + env.getOlConnString() + ">";
		fullString += "<OlUsername|" + env.getOlUsername() + ">";
		fullString += "<OlPassword|" + env.getOlPassword() + ">";
		fullString += "<CimConnString|" + env.getCimConnString() + ">";
		fullString += "<CimUsername|" + env.getCimUsername() + ">";
		fullString += "<CimPassword|" + env.getCimPassword() + ">";
		fullString += "<AlConnString|" + env.getAlConnString() + ">";
		fullString += "<AlUsername|" + env.getAlUsername() + ">";
		fullString += "<AlPassword|" + env.getAlPassword() + ">";
		fullString += "<MlConnString|" + env.getMlConnString() + ">";
		fullString += "<MlUsername|" + env.getMlUsername() + ">";
		fullString += "<MlPassword|" + env.getMlPassword() + ">";
		return fullString;
	}
	
	//-----------------------------------------------------------------//
	
	/** Error Reporting **/
	
	private void postCustomError(String errorType, String errorMessage) {
		ActivationTracerErrorDialog errorDialog = new ActivationTracerErrorDialog();
		errorDialog.showMessageDialog(dialog, "Encountered Error!", errorType, errorMessage);
	}
	
	//-----------------------------------------------------------------//
	
}
