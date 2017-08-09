package nv.activation_tracer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ActivationTracer implements ActionListener, ListSelectionListener {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	final private String versionNumber = "v. 1.32";
	final private String readmeFile = "README - Activation Tracer.docx";
	final private String dbConfigFile = "ATDBConfig.txt";
	final private String backupDbConfigFile = "ATDBConfig_Backup.txt";
	final private String successString = "Done.";
	final private String errorString = "Encountered error: ";
	final private String endSuccessString = "Ended successfully.\n";
	final private String endErrorString = "Ended with error.\n";
	final private int maxProgressLines = 50;
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private ArrayList<ActivationMessage> fullActivationMessageList = null;
	private ArrayList<Environment> envDetails = null;
	private String[] activationStatus = new String[4];
	private JFrame frame = new JFrame();
	private JMenuItem menuBarFileMenuManageEnvItem = null;
	private JMenuItem menuBarFileMenuExitItem = null;
	private JMenuItem menuBarHelpMenuReadmeItem = null;
	private JMenuItem menuBarHelpMenuAboutItem = null;
	private JTextField orderNumberTextField = new JTextField(10);
	private JComboBox<String> envComboBox = new JComboBox<String>();
	private JButton manageEnvButton = new JButton("Manage Environments");
	private JButton getTimingButton = new JButton("Get Timing");
	private JTabbedPane outputPanelTabbedPane = new JTabbedPane();
	private JTextPane progressTextPane = new JTextPane();
	private StyledDocument textPaneDocument;
	private Style normalStyle;
	private Style successStyle;
	private Style errorStyle;
	private Style keywordStyle;
	private Style valueStyle;
	private JTextField orderInfoPanelEnvironmentTextField = new JTextField(10);
	private JTextField orderInfoPanelOrderNumberTextField = new JTextField(10);
	private JTextField orderInfoPanelFullStatusTextField = new JTextField(30);
	private JTextField orderInfoPanelEMSStatusTextField = new JTextField(30);
	private JTextField orderInfoPanelCMSStatusTextField = new JTextField(30);
	private JTextField orderInfoPanelSAMStatusTextField = new JTextField(30);
	private JTextField orderInfoPanelTotalTimeTextField = new JTextField(18);
	private JTextField orderInfoPanelSelectedTimeTextField = new JTextField(18);
	private String[] tableHeaders = {"OL", "CIM", "AL", "ML", "Timestamp"};
	private JTable resultsTable = new JTable();
	private ArrayList<String> progressLines = new ArrayList<String>();
	private SwingWorker<StatusMessage, StatusMessage> processOrderSwingWorker = null;
	private String orderNumberToProcess = null;
	private boolean errorEncountered = false;
	private String errorType = null;
	private Environment envToProcess = null;
	private ArrayList<String> olTaskIDs = null;
	private ArrayList<String> uniqueAlCorrIDs = null;
	private ArrayList<String> alActivationTimestamps = null;
	
	//-----------------------------------------------------------------//
	
	/** Main method and class declaration/initialization**/
	
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ActivationTracer().createAndShowGUI();
				} catch (IOException e) {
					//Auto-generated catch block...handled deeper within program
				}
			}
		});
	}
	
	//-----------------------------------------------------------------//
	
	/** Create and manage GUI components **/
	
	private void createAndShowGUI() throws IOException {
		frame.setTitle("Activation Tracer " + versionNumber);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.gridheight = 1;
		mainPanelConstraints.weightx = 0.0;
		mainPanelConstraints.weighty = 0.0;
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(10, 10, 5, 5);
		mainPanel.add(buildInputPanel(), mainPanelConstraints);
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 1;
		mainPanelConstraints.gridheight = 1;
		mainPanelConstraints.weightx = 1.0;					//Prevents JScrollPane resizing on minimize
		mainPanelConstraints.weighty = 1.0;					//Prevents JScrollPane resizing on minimize
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(5, 10, 10, 5);
		mainPanel.add(buildOutputPanel(), mainPanelConstraints);
		mainPanelConstraints.gridx = 1;
		mainPanelConstraints.gridy = 0;
		mainPanelConstraints.gridheight = 2;
		mainPanelConstraints.weightx = 0.0;
		mainPanelConstraints.weighty = 0.0;
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanelConstraints.insets = new Insets(10, 5, 10, 10);
		mainPanel.add(buildResultsPanel(), mainPanelConstraints);
		frame.getContentPane().add(mainPanel);
		frame.setJMenuBar(buildMenuBar());
		frame.pack();
		frame.validate();
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameDim = frame.getSize();
		int heightWithoutTaskbar = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
		frame.setLocation((screenDim.width - frameDim.width) / 2, (heightWithoutTaskbar - frameDim.height) / 2);		//Centers application
		frame.setVisible(true);
	}
	
	private JPanel buildInputPanel() {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridBagLayout());
		GridBagConstraints inputPanelConstraints = new GridBagConstraints();
		inputPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Input"));
		JLabel enterLabel = new JLabel("Order Number:");
		inputPanelConstraints.gridx = 0;
		inputPanelConstraints.gridy = 0;
		inputPanelConstraints.gridwidth = 2;
		inputPanelConstraints.weightx = 1.0;
		inputPanelConstraints.weighty = 0.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		inputPanelConstraints.insets = new Insets(4, 10, 0, 10);
		inputPanel.add(enterLabel, inputPanelConstraints);
		inputPanelConstraints.gridx = 0;
		inputPanelConstraints.gridy = 1;
		inputPanelConstraints.gridwidth = 2;
		inputPanelConstraints.weightx = 1.0;
		inputPanelConstraints.weighty = 0.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		inputPanelConstraints.insets = new Insets(0, 10, 0, 10);
		inputPanel.add(orderNumberTextField, inputPanelConstraints);
		JLabel environmentLabel = new JLabel("Select Environment:");
		inputPanelConstraints.gridx = 0;
		inputPanelConstraints.gridy = 2;
		inputPanelConstraints.gridwidth = 2;
		inputPanelConstraints.weightx = 1.0;
		inputPanelConstraints.weighty = 0.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		inputPanelConstraints.insets = new Insets(10, 10, 0, 10);
		inputPanel.add(environmentLabel, inputPanelConstraints);
		try {
			loadFromDbConfigFile(dbConfigFile);
		} catch (IOException e) {
			//Auto-generated catch block...handled deeper within program
		}
		buildEnvComboBox(0);
		inputPanelConstraints.gridx = 0;
		inputPanelConstraints.gridy = 3;
		inputPanelConstraints.gridwidth = 1;
		inputPanelConstraints.weightx = 0.0;
		inputPanelConstraints.weighty = 0.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		inputPanelConstraints.insets = new Insets(0, 10, 10, 10);
		inputPanel.add(envComboBox, inputPanelConstraints);
		manageEnvButton.addActionListener(this);
		inputPanelConstraints.gridx = 1;
		inputPanelConstraints.gridy = 3;
		inputPanelConstraints.gridwidth = 1;
		inputPanelConstraints.weightx = 0.0;
		inputPanelConstraints.weighty = 0.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		inputPanelConstraints.insets = new Insets(0, 0, 10, 10);
		inputPanel.add(manageEnvButton, inputPanelConstraints);
		getTimingButton.addActionListener(this);
		inputPanelConstraints.gridx = 0;
		inputPanelConstraints.gridy = 4;
		inputPanelConstraints.gridwidth = 2;
		inputPanelConstraints.weightx = 1.0;
		inputPanelConstraints.weighty = 1.0;
		inputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		inputPanelConstraints.insets = new Insets(5, 10, 10, 10);
		inputPanel.add(getTimingButton, inputPanelConstraints);
		return inputPanel;
	}
	
	private JPanel buildOutputPanel() {
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new GridBagLayout());
		GridBagConstraints outputPanelConstraints = new GridBagConstraints();
		outputPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Output"));
		outputPanelTabbedPane = new JTabbedPane();
		outputPanelTabbedPane.addTab("Progress", buildProgressPanel());
		outputPanelTabbedPane.addTab("Order Info", buildOrderInfoPanel());
		outputPanelConstraints.gridx = 0;
		outputPanelConstraints.gridy = 0;
		outputPanelConstraints.weightx = 1.0;
		outputPanelConstraints.weighty = 1.0;
		outputPanelConstraints.fill = GridBagConstraints.BOTH;
		outputPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		outputPanelConstraints.insets = new Insets(4, 10, 10, 10);
		outputPanel.add(outputPanelTabbedPane, outputPanelConstraints);
		createTextPaneStyles();
		return outputPanel;
	}
	
	private JPanel buildProgressPanel() {
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new GridBagLayout());
		GridBagConstraints progressPanelConstraints = new GridBagConstraints();
		textPaneDocument = progressTextPane.getStyledDocument();
		progressTextPane.setEditable(false);
		JPanel progressTextPaneNoWrapPanel = new JPanel();
		progressTextPaneNoWrapPanel.setLayout(new BorderLayout());
		progressTextPaneNoWrapPanel.add(progressTextPane);
		JScrollPane progressTextAreaScrollPane = new JScrollPane(progressTextPaneNoWrapPanel);
		progressTextAreaScrollPane.setPreferredSize(new Dimension(300, 200));
		progressTextAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		progressTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		progressPanelConstraints.gridx = 0;
		progressPanelConstraints.gridy = 0;
		progressPanelConstraints.weightx = 1.0;
		progressPanelConstraints.weighty = 1.0;
		progressPanelConstraints.fill = GridBagConstraints.BOTH;
		progressPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		progressPanelConstraints.insets = new Insets(4, 10, 10, 10);
		progressPanel.add(progressTextAreaScrollPane, progressPanelConstraints);
		createTextPaneStyles();
		return progressPanel;
	}
	
	private JPanel buildOrderInfoPanel() {
		JPanel orderInfoPanel = new JPanel();
		orderInfoPanel.setLayout(new GridBagLayout());
		GridBagConstraints orderInfoPanelConstraints = new GridBagConstraints();
		JLabel orderInfoPanelEnvironmentLabel = new JLabel("Environment:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 0;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(4, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelEnvironmentLabel, orderInfoPanelConstraints);
		orderInfoPanelEnvironmentTextField.setEditable(false);
		orderInfoPanelEnvironmentTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 1;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelEnvironmentTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelOrderNumberLabel = new JLabel("Order number:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 2;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelOrderNumberLabel, orderInfoPanelConstraints);
		orderInfoPanelOrderNumberTextField.setEditable(false);
		orderInfoPanelOrderNumberTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 3;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelOrderNumberTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelFullStatusLabel = new JLabel("EMS/CMS/SAM Activation Status:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 4;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelFullStatusLabel, orderInfoPanelConstraints);
		orderInfoPanelFullStatusTextField.setEditable(false);
		orderInfoPanelFullStatusTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 5;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelFullStatusTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelEMSStatusLabel = new JLabel("EMS Activation Status:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 6;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelEMSStatusLabel, orderInfoPanelConstraints);
		orderInfoPanelEMSStatusTextField.setEditable(false);
		orderInfoPanelEMSStatusTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 7;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelEMSStatusTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelCMSStatusLabel = new JLabel("CMS Activation Status:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 8;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelCMSStatusLabel, orderInfoPanelConstraints);
		orderInfoPanelCMSStatusTextField.setEditable(false);
		orderInfoPanelCMSStatusTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 9;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelCMSStatusTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelSAMStatusLabel = new JLabel("SAM Activation Status:");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 10;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelSAMStatusLabel, orderInfoPanelConstraints);
		orderInfoPanelSAMStatusTextField.setEditable(false);
		orderInfoPanelSAMStatusTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 11;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelSAMStatusTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelTotalTimeLabel = new JLabel("Time elapsed (Total):");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 12;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelTotalTimeLabel, orderInfoPanelConstraints);
		orderInfoPanelTotalTimeTextField.setEditable(false);
		orderInfoPanelTotalTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 13;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelTotalTimeTextField, orderInfoPanelConstraints);
		JLabel orderInfoPanelSelectedTimeLabel = new JLabel("Time elapsed (Selected Tasks):");
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 14;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 1.0;
		orderInfoPanelConstraints.weighty = 0.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(10, 10, 0, 10);
		orderInfoPanel.add(orderInfoPanelSelectedTimeLabel, orderInfoPanelConstraints);
		orderInfoPanelSelectedTimeTextField.setEditable(false);
		orderInfoPanelSelectedTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		orderInfoPanelConstraints.gridx = 0;
		orderInfoPanelConstraints.gridy = 15;
		orderInfoPanelConstraints.gridwidth = 1;
		orderInfoPanelConstraints.weightx = 0.0;
		orderInfoPanelConstraints.weighty = 1.0;
		orderInfoPanelConstraints.fill = GridBagConstraints.NONE;
		orderInfoPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		orderInfoPanelConstraints.insets = new Insets(0, 10, 10, 10);
		orderInfoPanel.add(orderInfoPanelSelectedTimeTextField, orderInfoPanelConstraints);
		return orderInfoPanel;
	}
		
	private JPanel buildResultsPanel() {
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new GridBagLayout());
		GridBagConstraints resultsPanelConstraints = new GridBagConstraints();
		resultsPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Results"));
		resultsTable.setFont(new Font("Calibri", Font.PLAIN, 11));
		resultsTable.setFillsViewportHeight(true);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		resultsTable.setRowSelectionAllowed(true);
		resultsTable.setColumnSelectionAllowed(false);
		resultsTable.setRowHeight(resultsTable.getRowHeight() * 2);
		ListSelectionModel resultsTableSelectionModel = resultsTable.getSelectionModel();
		resultsTableSelectionModel.addListSelectionListener(this);
		buildResultsTable();
		JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
		resultsScrollPane.setPreferredSize(new Dimension(893, 550));
		resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsPanelConstraints.gridx = 0;
		resultsPanelConstraints.gridy = 0;
		resultsPanelConstraints.gridwidth = 2;
		resultsPanelConstraints.weightx = 1.0;
		resultsPanelConstraints.fill = GridBagConstraints.BOTH;
		resultsPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		resultsPanelConstraints.insets = new Insets(4, 10, 10, 10);
		resultsPanel.add(resultsScrollPane, resultsPanelConstraints);
		return resultsPanel;
	}
		
	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuBarFileMenu = new JMenu("File");
		menuBarFileMenu.setMnemonic(KeyEvent.VK_F);
		menuBarFileMenuManageEnvItem = new JMenuItem("Manage Environments");
		menuBarFileMenuManageEnvItem.addActionListener(this);
		menuBarFileMenu.add(menuBarFileMenuManageEnvItem);
		menuBarFileMenu.addSeparator();
		menuBarFileMenuExitItem = new JMenuItem("Exit");
		menuBarFileMenuExitItem.addActionListener(this);
		menuBarFileMenu.add(menuBarFileMenuExitItem);
		menuBar.add(menuBarFileMenu);
		JMenu menuBarHelpMenu = new JMenu("Help");
		menuBarHelpMenu.setMnemonic(KeyEvent.VK_H);
		menuBarHelpMenuReadmeItem = new JMenuItem("Readme");
		menuBarHelpMenuReadmeItem.addActionListener(this);
		menuBarHelpMenu.add(menuBarHelpMenuReadmeItem);
		menuBarHelpMenuAboutItem = new JMenuItem("About Activation Tracer");
		menuBarHelpMenuAboutItem.addActionListener(this);
		menuBarHelpMenu.add(menuBarHelpMenuAboutItem);
		menuBar.add(menuBarHelpMenu);
		return menuBar;
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
	}
	
	private void buildResultsTable() {
		String[][] tableData;
		if (fullActivationMessageList != null) {
			tableData = new String[fullActivationMessageList.size()][5];
			for (int y = 0; y < fullActivationMessageList.size(); y++) {
				ActivationMessage messageToAdd = fullActivationMessageList.get(y);
				String[] resultsRow = {"", "", "", "", "\n" + messageToAdd.getMessageTimestampString()};
				String resultsCell = messageToAdd.getHostApp() + " " + messageToAdd.getMessageAction() + " " + 
									 messageToAdd.getMessageType() + " " + messageToAdd.getMessageDirection() + " " + 
									 messageToAdd.getExternalApp() + "\n(" + messageToAdd.getMessageTag() + ")";
				if (messageToAdd.getHostApp().equals("OL")) {
					resultsRow[0] = resultsCell;
				}
				else if (messageToAdd.getHostApp().equals("CIM")) {
					resultsRow[1] = resultsCell;
				}
				else if (messageToAdd.getHostApp().equals("AL")) {
					resultsRow[2] = resultsCell;
				}
				else if (messageToAdd.getHostApp().equals("ML")) {
					resultsRow[3] = resultsCell;
				}
				tableData[y] = resultsRow;
			}
		}
		else {
			//fullActivationMessageList is not created yet, display empty table
			tableData = new String[0][5];
		}
		resultsTable.setModel(new ActivationTableModel(tableData, tableHeaders));
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(2).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(4).setPreferredWidth(175);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		resultsTable.getTableHeader().setResizingAllowed(false);
		for (int i = 0; i < resultsTable.getColumnCount(); i++) {
			resultsTable.getColumnModel().getColumn(i).setCellRenderer(new ActivationTableRenderer());
		}
		resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	private void fillTextFields() {
		orderInfoPanelEnvironmentTextField.setText(envToProcess.getEnvironmentName());
		orderInfoPanelOrderNumberTextField.setText(orderNumberToProcess);
		orderInfoPanelFullStatusTextField.setText(activationStatus[0]);
		orderInfoPanelEMSStatusTextField.setText(activationStatus[1]);
		orderInfoPanelCMSStatusTextField.setText(activationStatus[2]);
		orderInfoPanelSAMStatusTextField.setText(activationStatus[3]);
		ActivationMessage startMessage = fullActivationMessageList.get(0);
		ActivationMessage endMessage = fullActivationMessageList.get(fullActivationMessageList.size() - 1);
		orderInfoPanelTotalTimeTextField.setText(startMessage.getElapsedTime(endMessage));
		orderInfoPanelSelectedTimeTextField.setText("(No tasks selected)");
	}
	
	private void createTextPaneStyles() {
		normalStyle = progressTextPane.addStyle("normalStyle", null);
		StyleConstants.setForeground(normalStyle, Color.black);
		StyleConstants.setFontFamily(normalStyle, "monospaced");
		StyleConstants.setFontSize(normalStyle,  11);
		successStyle = progressTextPane.addStyle("successStyle", null);
		StyleConstants.setForeground(successStyle, new Color(20, 140, 10));
		StyleConstants.setFontFamily(successStyle, "monospaced");
		StyleConstants.setFontSize(successStyle,  11);
		errorStyle = progressTextPane.addStyle("errorStyle", null);
		StyleConstants.setForeground(errorStyle, new Color(190, 30, 10));
		StyleConstants.setFontFamily(errorStyle, "monospaced");
		StyleConstants.setFontSize(errorStyle,  11);
		keywordStyle = progressTextPane.addStyle("keywordStyle", null);
		StyleConstants.setForeground(keywordStyle, new Color(130, 0, 130));
		StyleConstants.setFontFamily(keywordStyle, "monospaced");
		StyleConstants.setFontSize(keywordStyle,  11);
		valueStyle = progressTextPane.addStyle("valueStyle", null);
		StyleConstants.setForeground(valueStyle, new Color(0, 150, 200));
		StyleConstants.setFontFamily(valueStyle, "monospaced");
		StyleConstants.setFontSize(valueStyle,  11);
	}
	
	private void postProgressMessage(StatusMessage sm) {
		ArrayList<String> wordsToAdd = sm.getWords();
		ArrayList<String> typesToAdd = sm.getTypes();
		int nestLevel = sm.getNestLevel();
		String fullLine = "";
		String indent = "";
		try {
			for (int i = 0; i < nestLevel; i++) {
				indent += "| ";
			}
			textPaneDocument.insertString(textPaneDocument.getLength(), indent, normalStyle);
			fullLine += indent;
			Style displayStyle;
			for (int i = 0; i < wordsToAdd.size(); i++) {
				if (typesToAdd.get(i).equalsIgnoreCase("normal")) {
					displayStyle = normalStyle;
				}
				else if (typesToAdd.get(i).equalsIgnoreCase("success")) {
					displayStyle = successStyle;
				}
				else if (typesToAdd.get(i).equalsIgnoreCase("error")) {
					displayStyle = errorStyle;
				}
				else if (typesToAdd.get(i).equalsIgnoreCase("keyword")) {
					displayStyle = keywordStyle;
				}
				else if (typesToAdd.get(i).equalsIgnoreCase("value")) {
					displayStyle = valueStyle;
				}
				else {
					displayStyle = normalStyle;
				}
				textPaneDocument.insertString(textPaneDocument.getLength(), wordsToAdd.get(i), displayStyle);
				fullLine += wordsToAdd.get(i);
			}
			textPaneDocument.insertString(textPaneDocument.getLength(), "\n", normalStyle);
			
			fullLine += "\n";
			progressLines.add(fullLine);
			
			//Check size of progressTextPane
			if (progressLines.size() > maxProgressLines) {
				textPaneDocument.remove(0, progressLines.get(0).length());
				progressLines.remove(0);
			}
			
		} catch (BadLocationException e) {
			//Not sure how this might get thrown
			postException(e);
		}
	}
	
	private void clearData() {
		fullActivationMessageList = new ArrayList<ActivationMessage>();
		String[][] tableData = new String[0][5];
		resultsTable.setModel(new ActivationTableModel(tableData, tableHeaders));
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(2).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(175);
		resultsTable.getColumnModel().getColumn(4).setPreferredWidth(175);
		for (int i = 0; i < resultsTable.getColumnCount(); i++) {
			resultsTable.getColumnModel().getColumn(i).setCellRenderer(new ActivationTableRenderer());
		}
		resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		orderInfoPanelEnvironmentTextField.setText("");
		orderInfoPanelOrderNumberTextField.setText("");
		orderInfoPanelFullStatusTextField.setText("");
		orderInfoPanelEMSStatusTextField.setText("");
		orderInfoPanelCMSStatusTextField.setText("");
		orderInfoPanelSAMStatusTextField.setText("");
		orderInfoPanelTotalTimeTextField.setText("");
		orderInfoPanelSelectedTimeTextField.setText("");
	}
	
	//-----------------------------------------------------------------//
	
	/** Implemented methods and sub-methods **/
	
	public void actionPerformed(ActionEvent e) {
		//Handle getTimingButton press
		if (e.getSource() == getTimingButton) {
			getTimingButtonPress();
		}
		else if (e.getSource() == menuBarFileMenuManageEnvItem || e.getSource() == manageEnvButton) {
			manageEnvButtonPress();
		}
		else if (e.getSource() == menuBarFileMenuExitItem) {
			menuBarFileMenuExitItemSelect();
		}
		else if (e.getSource() == menuBarHelpMenuReadmeItem) {
			menuBarHelpMenuReadmeItemSelect();
		}
		else if (e.getSource() == menuBarHelpMenuAboutItem) {
			menuBarHelpMenuAboutItemSelect();
		}
	}
	
	private void getTimingButtonPress() {
		errorEncountered = false;
		outputPanelTabbedPane.setSelectedIndex(0);
		orderNumberToProcess = orderNumberTextField.getText();
		processOrderSwingWorker = new ProcessOrderSwingWorker();
		processOrderSwingWorker.execute();
	}
	
	private void manageEnvButtonPress() {
		ManageEnvDialog manageDialog = new ManageEnvDialog();
		manageDialog.showMessageDialog(frame, "Manage Environments");
		try {
			loadFromDbConfigFile(dbConfigFile);
		} catch (IOException e) {
			//Auto-generated catch block...handled deeper within program
		}
		buildEnvComboBox(0);
	}
	
	private void menuBarFileMenuExitItemSelect() {
		frame.dispose();
	}
	
	private void menuBarHelpMenuReadmeItemSelect() {
		openReadmeDocument();
	}
	
	private void menuBarHelpMenuAboutItemSelect() {
		String aboutString = "Activation Tracer " + versionNumber + " developed by:\nNick Varn\nAssociate Software Engineer" + 
							 "\nTech Mahindra" + "\nFor use within AT&T, BBNMS-LS";
		JOptionPane.showMessageDialog(null, aboutString, "About Activation Tracer", JOptionPane.PLAIN_MESSAGE);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			//Do nothing, wait for user to finish selection
		}
		else {
			//Calculate time elapsed
			if (fullActivationMessageList == null) {
				postCustomError("Data Not Found Error", "No Activation data to display in table.");
				return;
			}
			int[] selectedRows = resultsTable.getSelectedRows();
			if (selectedRows.length == 0) {
				orderInfoPanelSelectedTimeTextField.setText("");
				return;
			}
			ActivationMessage startMessage = fullActivationMessageList.get(selectedRows[0]);
			ActivationMessage endMessage = fullActivationMessageList.get(selectedRows[selectedRows.length - 1]);
			orderInfoPanelSelectedTimeTextField.setText(startMessage.getElapsedTime(endMessage));
		}
	}
	
	//-----------------------------------------------------------------//
	
	/** File I/O **/
	
	private void openReadmeDocument() {
		try {
			Desktop.getDesktop().open(new File(readmeFile));
		} catch (Exception e) {
			postException(e);
		}
	}
	
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
					String tempOlUsername = messageBuilderArray.get(2);
					String tempOlPassword = messageBuilderArray.get(3);
					String tempCimConnString = messageBuilderArray.get(4);
					String tempCimUsername = messageBuilderArray.get(5);
					String tempCimPassword = messageBuilderArray.get(6);
					String tempAlConnString = messageBuilderArray.get(7);
					String tempAlUsername = messageBuilderArray.get(8);
					String tempAlPassword = messageBuilderArray.get(9);
					String tempMlConnString = messageBuilderArray.get(10);
					String tempMlUsername = messageBuilderArray.get(11);
					String tempMlPassword = messageBuilderArray.get(12);
					Environment tempEnvironment = new Environment(tempEnvName, tempOlConnString, tempOlUsername, tempOlPassword, 
																  tempCimConnString, tempCimUsername, tempCimPassword, tempAlConnString, 
																  tempAlUsername, tempAlPassword, tempMlConnString, tempMlUsername, tempMlPassword);
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
			postException(e);
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
	
	/** Order processing **/
	
	private void verifyOrderNumber(String incomingOrderNumber) {
		if (incomingOrderNumber.length() > 10 || incomingOrderNumber.length() < 5) {
			//Invalid order number length
			postCustomError("Invalid Input Error", "Order number:\n\"" + incomingOrderNumber +
					  "\"\n length is invalid.  Order number should between 5 and 10 digits long.");
			errorType = "Invalid Input Error";
			clearData();
			return;
		}
	}
	
	private void setEnvironment(int selectedIndex) {
		envToProcess =  envDetails.get(selectedIndex);
	}
	
	private void getTaskIDsFromOl(Environment env, String orderNumber) {
		String sql = "select taskid from eih_all where ordernumber = '" + orderNumber + "' and taskdefinitionid = 'T10300' " + 
					 "and messagetag = 'Activate Request'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getOlUrl(), env.getOlUsername(),
															  env.getOlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			clearData();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of no matching entry for orderNumber and messagetag = 'Activate Request'
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "EMS/CMS/SAM Activation Request for ordernumber:\n\"" +
					   orderNumber + "\"\ndoes not exist in eih_all table in OL DB.");
			errorType = "Data Not Found Error";
			clearData();
			return;
		}
		//Put taskIDs into ArrayList
		olTaskIDs = new ArrayList<String>();
		for (int i = 0; i < resultFromDB.size(); i++) {
			olTaskIDs.add(resultFromDB.get(i).get(0));
		}
	}
	
	private void getActivationStatusFromOl(Environment env, String taskID) {
		//Fetch Activation Status from OL
		String sql = "select description, errordescription from task where taskid = '" + taskID + "'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getOlUrl(), env.getOlUsername(),
															  env.getOlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		for (int i = 0; i < resultFromDB.size(); i++) {
			if (resultFromDB.get(i).get(0).equalsIgnoreCase("EMS/CMS/SAM Activation")) {
				activationStatus[0] = resultFromDB.get(i).get(1);
			}
			else if (resultFromDB.get(i).get(0).equalsIgnoreCase("EMS Activation")) {
				activationStatus[1] = resultFromDB.get(i).get(1);
			}
			else if (resultFromDB.get(i).get(0).equalsIgnoreCase("CMS Activation")) {
				activationStatus[2] = resultFromDB.get(i).get(1);
			}
			else if (resultFromDB.get(i).get(0).equalsIgnoreCase("SAM Activation")) {
				activationStatus[3] = resultFromDB.get(i).get(1);
			}
			for (int j = 0; j < 4; j++) {
				if (activationStatus[j] == null) {
					activationStatus[j] = "(null)";
				}
			}
		}
	}
	
	private void getActivationFromOl(Environment env, String taskID) {
		//Fetch Activation Request/Response OL <-> CIM info
		String sql = "select taskid, description, starttime, endtime from task where taskid = '" + taskID +
				     "' and description = 'EMS/CMS/SAM Activation'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getOlUrl(), env.getOlUsername(),
															  env.getOlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of no matching entry for taskID and description = 'EMS/CMS/SAM Activation'
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "EMS/CMS/SAM Activation Request for taskID:\n\"" +
					  taskID + "\"\ndoes not exist in task table in OL DB.");
			errorType = "Data Not Found Error";
			return;
		}
		for (int i = 0; i < resultFromDB.size(); i++) {
			//Create and add Activation Request OL -> CIM
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Sent", "OL", "CIM",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
			//Create and add Activation Response OL <- CIM
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Received", "OL", "CIM",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(3)));
		}
	}
	
	private void getActivationFromCim(Environment env, String taskID) {
		//Fetch Activation Request/Response OL <-> CIM and CIM <-> AL info
		String sql = "select taskid, jrequesttype, jrequesttime, jresponsetime, nrequesttype, nrequesttime, nresponsetime from " +
					 "orderhistory where taskid = '" + taskID + "'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getCimUrl(), env.getCimUsername(), env.getCimPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of no matching entry for taskID
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "EMS/CMS/SAM Activation Request/Response for taskID:\n\"" +
					  taskID + "\"\ndoes not exist in orderhistory table in CIM DB.");
			errorType = "Data NotFound Error";
			return;
		}
		for (int i = 0; i < resultFromDB.size(); i++) {
			if (resultFromDB.get(i).get(0) == null || resultFromDB.get(i).get(1) == null || resultFromDB.get(i).get(2) == null || 
				resultFromDB.get(i).get(3) == null || resultFromDB.get(i).get(4) == null || resultFromDB.get(i).get(5) == null || 
				resultFromDB.get(i).get(5) == null) {
				//In case some fields in returned query (like nrequesttime, etc.) are empty
				postCustomError("Data Not Found Error", "Incomplete EMS/CMS/SAM Activation Request/Response data for taskID:\n\"" +
						  taskID + "\"\n in orderhistory table in CIM DB.");
				errorType = "Data NotFound Error";
				return;
			}
			//Create and add Activation Request OL -> CIM
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Received", "CIM", "OL",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
			//Create and add Activation Response OL <- CIM
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Sent", "CIM", "OL",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(3)));
			//Create and add Activation Request CIM -> AL
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Sent", "CIM", "AL",
																resultFromDB.get(i).get(4), resultFromDB.get(i).get(5)));
			//Create and add Activation Response CIM <- AL
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Received", "CIM", "AL",
																resultFromDB.get(i).get(4), resultFromDB.get(i).get(6)));
		}
	}
	
	private void getActivationFromAl(Environment env, String correlationID) {
		//Fetch Activation Request/Response CIM <-> AL and AL <-> ML info
		String sql = "select correlationid, requestname, t_timestamp from externalinterfacehistory where " +
					 "correlationid = '" + correlationID + "'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getAlUrl(), env.getAlUsername(), env.getAlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of no matching entries for correlationid
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "EMS/CMS/SAM Activation Requests/Responses for correlationid:\n\"" +
					  correlationID + "\"\ndoes not exist in externalinterfacehistory table in AL DB.");
			errorType = "Data Not Found Error";
			return;
		}
		for (int i = 0; i < resultFromDB.size(); i++) {
			String requestName = resultFromDB.get(i).get(1);
			if (requestName.equals("ActivationRequestType")) {
				//Create and add Activation Request CIM -> AL
				fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Received", "AL", "CIM",
																	resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
			}
			else if (requestName.equals("ActivationResponseType")) {
				//Create and add Activation Response CIM <- AL
				fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Sent", "AL", "CIM",
																	resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
			}
			else {
				if (requestName.contains("Request")) {
					//Create and add Activation Request AL -> ML
					fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Sent", "AL", "ML",
																		resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
				}
				else if (requestName.contains("Response")) {
					//Create and add Activation Response AL <- ML
					fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Received", "AL",
																		"ML", resultFromDB.get(i).get(1),
																		resultFromDB.get(i).get(2)));
				}
			}
		}
	}
	
	private void getAlCorrIDsFromAl(Environment env, String correlationID) {
		//Fetch ALCorrelationIDs
		String sql = "select alcorrelationid from externalinterfacehistory where correlationid = '" + correlationID +
					 "'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getAlUrl(), env.getAlUsername(), env.getAlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of nonexistent ALcorrelationIDs (nonsensical, this could not happen)
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "ALcorrelationIDs for correlationid:\n\"" +
					  correlationID + "\"\ndo not exist in externalinterfacehistory table in AL DB.");
			errorType = "Data Not Found Error";
			return;
		}
		//Extract unique alCorrelationIDs
		uniqueAlCorrIDs = new ArrayList<String>();
		for (int i = 0; i < resultFromDB.size(); i++) {
			if (uniqueAlCorrIDs.isEmpty()) {
				uniqueAlCorrIDs.add(resultFromDB.get(i).get(0));
			}
			else {
				boolean matchFound = false;
				for (int j = 0; j < uniqueAlCorrIDs.size(); j++) {
					if (resultFromDB.get(i).get(0).equalsIgnoreCase(uniqueAlCorrIDs.get(j))) {
						matchFound = true;
						break;
					}
				}
				if (!matchFound) {
					uniqueAlCorrIDs.add(resultFromDB.get(i).get(0));
				}
			}
		}
	}
	
	//Removed this method and associated timestamp select from ML activation fetching method, as it was causing app to incorrectly exclude some messages 
	//which occurred after timeouts were declared.
//	private void getActivationTimestampsFromAl(Environment env, String alCorrelationID) {
//		//Fetch Activation Request/Response timestamps for each unique alCorrelationID
//		String sql = "select t_timestamp from externalinterfacehistory where alcorrelationid = '" + alCorrelationID + 
//					 "' and (requestname = 'ActivationRequestType' or requestname = 'ActivationResponseType') order by t_timestamp";
//		ActivationTracerQuery atq = new ActivationTracerQuery(env.getAlUrl(), env.getAlUsername(), env.getAlPassword(), sql);
//		//Catch exception from query
//		if (atq.getException() != null) {
//			Exception e = atq.getException();
//			postException(e);
//			errorType = e.getClass().getSimpleName();
//			return;
//		}
//		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
//		//Error handling in case of no matching alCorrelationIDs for requestname = ActivationRequestType || ActivationResponseType
//		if (resultFromDB.size() == 0) {
//			postCustomError("Data Not Found Error", "ActivationRequestType/ActivationResonpseType alCorrelationid:\n\"" +
//					  alCorrelationID + "\"\ndoes not exist in externalinterfacehistory table in AL DB.");
//			errorType = "Data Not Found Error";
//			return;
//		}
//		alActivationTimestamps = new ArrayList<String>();
//		for (int i = 0; i < resultFromDB.size(); i++) {
//			//Add timestamp without AM/PM to alActivationTimestamps
//			alActivationTimestamps.add(resultFromDB.get(i).get(0));
//		}
//	}
	
	private void getActivationFromMl(Environment env, String correlationID, ArrayList<String> timestamps) {
		//Fetch Activation Request/Response AL <-> ML info
		//Removed this select, as it was causing app to incorrectly exclude some messages which occurred after timeouts were declared.
//		String sql = "select correlationid, tasktype, tasktimestamp, timeofstate from tasks where correlationid = '" +
//					  correlationID + "' and tasktimestamp between to_timestamp('" + timestamps.get(0) + "', " +
//					  "'YYYY-MM-DD HH24.MI.SS.FF') and to_timestamp('" + timestamps.get(1) + "', 'YYYY-MM-DD HH24.MI.SS.FF')";
		String sql = "select correlationid, tasktype, tasktimestamp, timeofstate from tasks where correlationid = '" +
				  correlationID + "'";
		ActivationTracerQuery atq = new ActivationTracerQuery(env.getMlUrl(), env.getMlUsername(), env.getMlPassword(), sql);
		//Catch exception from query
		if (atq.getException() != null) {
			Exception e = atq.getException();
			postException(e);
			errorType = e.getClass().getSimpleName();
			return;
		}
		ArrayList<ArrayList<String>> resultFromDB = atq.getResult();
		//Error handling in case of no matching correlationIDs
		if (resultFromDB.size() == 0) {
			postCustomError("Data Not Found Error", "EMS/CMS/SAM Activation Requests/Responses for correlationid:\n\"" +
					  correlationID + "\"\ndoes not exist in tasks table in ML DB.");
			errorType = "Data Not Found Error";
			return;
		}
		for (int i = 0; i < resultFromDB.size(); i++) {
			//Create and add Activation Request AL -> ML
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Request", "Received", "ML", "AL",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(2)));
			//Create and add Activation Response AL <- ML
			fullActivationMessageList.add(new ActivationMessage(resultFromDB.get(i).get(0), "Response", "Sent", "ML", "AL",
																resultFromDB.get(i).get(1), resultFromDB.get(i).get(3)));
		}
	}
	
	//-----------------------------------------------------------------//
	
	/** Data processing **/
	
	private void sortMessages() {
		ArrayList<ActivationMessage> sortedList = new ArrayList<ActivationMessage>();
		for (int i = 0; i < fullActivationMessageList.size(); i++) {
			boolean addedMessage = false;
			
			if (sortedList.size() == 0) {
				sortedList.add(fullActivationMessageList.get(i));
				continue;
			}
			
			for (int j = 0; j < sortedList.size(); j++) {
				if (fullActivationMessageList.get(i).getMessageTimestampDate().isBefore(sortedList.get(j).getMessageTimestampDate())) {
					sortedList.add(j, fullActivationMessageList.get(i));
					addedMessage = true;
					break;
				}
				else {
				}
			}
			if (!addedMessage) {
				sortedList.add(fullActivationMessageList.get(i));
			}
						
		}
		fullActivationMessageList = sortedList;
	}
	
	//-----------------------------------------------------------------//
	
	/** Error reporting **/
	
	private void postCustomError(String errorType, String errorMessage) {
		ActivationTracerErrorDialog errorDialog = new ActivationTracerErrorDialog();
		errorDialog.showMessageDialog(frame, "Encountered Error!", errorType, errorMessage);
		errorEncountered = true;
	}
	
	private void postException(Exception e) {
		ActivationTracerErrorDialog errorDialog = new ActivationTracerErrorDialog();
		errorDialog.showMessageDialog(frame, "Encountered Error!", e);
		errorEncountered = true;
	}
	
	//-----------------------------------------------------------------//
	
	/** Swing Worker **/
	
	private class ProcessOrderSwingWorker extends SwingWorker<StatusMessage, StatusMessage> {
		
		public StatusMessage doInBackground() throws Exception {
			
			fullActivationMessageList = new ArrayList<ActivationMessage>();
			StatusMessage status;
			if (envDetails.size() == 0) {
				//If no Environments exist
				postCustomError("Invalid Selection Error", "No Environment data available!");
				status = new StatusMessage(0);
				status.addWord(errorString + "Invalid Selection Error", "error");
				publish(status);
				status = new StatusMessage(0);
				status.addWord(endErrorString, "error");
				return status;
			}
			
			
			/** Begin Order Processing **/
			status = new StatusMessage(0);
			status.addWord("Processing activation tasks for ", "normal");
			status.addWord("ordernumber ", "keyword");
			status.addWord(orderNumberToProcess, "value");
			status.addWord("...", "normal");
			publish(status);
			
			
			/** Verify Order Number **/
			status = new StatusMessage(1);
			status.addWord("Verifying ", "normal");
			status.addWord("ordernumber ", "keyword");
			status.addWord(orderNumberToProcess, "value");
			status.addWord("...", "normal");
			publish(status);
			verifyOrderNumber(orderNumberToProcess);
			if (errorEncountered) {
				status = new StatusMessage(1);
				status.addWord(errorString + errorType, "error");
				publish(status);
				status = new StatusMessage(0);
				status.addWord(endErrorString, "error");
				return status;
			}
			status = new StatusMessage(1);
			status.addWord(successString, "normal");
			publish(status);
			
			
			/** Set Environment Details **/
			status = new StatusMessage(1);
			status.addWord("Setting ", "normal");
			status.addWord("environment ", "keyword");
			status.addWord("to ", "normal");
			status.addWord(envDetails.get(envComboBox.getSelectedIndex()).getEnvironmentName(), "value");
			status.addWord("...", "normal");
			publish(status);
			setEnvironment(envComboBox.getSelectedIndex());
			status = new StatusMessage(1);
			status.addWord(successString, "normal");
			publish(status);
			
			
			/** Fetch EMS/CMS/SAM Activation TaskIDs for Order from OL eih_all Table **/
			status = new StatusMessage(1);
			status.addWord("Fetching activation ", "normal");
			status.addWord("taskid", "keyword");
			status.addWord("'s for ", "normal");
			status.addWord("ordernumber ", "keyword");
			status.addWord(orderNumberToProcess, "value");
			status.addWord(" from ", "normal");
			status.addWord("OL DB", "value");
			status.addWord("...", "normal");
			publish(status);
			getTaskIDsFromOl(envToProcess, orderNumberToProcess);
			if (errorEncountered) {
				status = new StatusMessage(1);
				status.addWord(errorString + errorType, "error");
				publish(status);
				status = new StatusMessage(0);
				status.addWord(endErrorString, "error");
				return status;
			}
			
			
			/** Begin Processing Activation Tasks for orderNumber **/
			for (int i = 0; i < olTaskIDs.size(); i++) {
				String processingTaskID = olTaskIDs.get(i);
				
				
				/** Fetch Activation status from OL DB **/
				status = new StatusMessage(2);
				status.addWord("Fetching activation status for ", "normal");
				status.addWord("taskid ", "keyword");
				status.addWord(processingTaskID, "value");
				status.addWord(" from ", "normal");
				status.addWord("OL DB", "value");
				status.addWord("...", "normal");
				publish(status);
				getActivationStatusFromOl(envToProcess, processingTaskID);
				if (errorEncountered) {
					status = new StatusMessage(2);
					status.addWord(errorString + errorType, "error");
					publish(status);
					status = new StatusMessage(0);
					status.addWord(endErrorString, "error");
					return status;
				}
				status = new StatusMessage(2);
				status.addWord(successString, "normal");
				publish(status);
				
				
				/** Fetch Activation data from OL DB **/
				status = new StatusMessage(2);
				status.addWord("Fetching activation requests and responses OL <-> CIM for ", "normal");
				status.addWord("taskid ", "keyword");
				status.addWord(processingTaskID, "value");
				status.addWord(" from ", "normal");
				status.addWord("OL DB", "value");
				status.addWord("...", "normal");
				publish(status);
				getActivationFromOl(envToProcess, processingTaskID);
				if (errorEncountered) {
					status = new StatusMessage(2);
					status.addWord(errorString + errorType, "error");
					publish(status);
					status = new StatusMessage(0);
					status.addWord(endErrorString, "error");
					return status;
				}
				status = new StatusMessage(2);
				status.addWord(successString, "normal");
				publish(status);
				
				
				/** Fetch Activation data from CIM DB **/
				status = new StatusMessage(2);
				status.addWord("Fetching activation requests and responses OL <-> CIM and CIM <-> AL for ", "normal");
				status.addWord("taskid ", "keyword");
				status.addWord(processingTaskID, "value");
				status.addWord(" from ", "normal");
				status.addWord("CIM DB", "value");
				status.addWord("...", "normal");
				publish(status);
				getActivationFromCim(envToProcess, processingTaskID);
				if (errorEncountered) {
					status = new StatusMessage(2);
					status.addWord(errorString + errorType, "error");
					publish(status);
					status = new StatusMessage(0);
					status.addWord(endErrorString, "error");
					return status;
				}
				status = new StatusMessage(2);
				status.addWord(successString, "normal");
				publish(status);
				
				
				/** Fetch Activation data from AL DB **/
				status = new StatusMessage(2);
				status.addWord("Fetching activation requests and responses CIM <-> AL and AL <-> ML for ", "normal");
				status.addWord("correlationid ", "keyword");
				status.addWord(processingTaskID, "value");
				status.addWord(" from ", "normal");
				status.addWord("AL DB", "value");
				status.addWord("...", "normal");
				publish(status);
				getActivationFromAl(envToProcess, processingTaskID);
				if (errorEncountered) {
					status = new StatusMessage(2);
					status.addWord(errorString + errorType, "error");
					publish(status);
					status = new StatusMessage(0);
					status.addWord(endErrorString, "error");
					return status;
				}
				status = new StatusMessage(2);
				status.addWord(successString, "normal");
				publish(status);
				
				
				/** Fetch alCorrelationIDs from AL DB **/
				status = new StatusMessage(2);
				status.addWord("Fetching ", "normal");
				status.addWord("alcorrelationid", "keyword");
				status.addWord("'s for ", "normal");
				status.addWord("correlationid ", "keyword");
				status.addWord(processingTaskID, "value");
				status.addWord(" from ", "normal");
				status.addWord("AL DB", "value");
				status.addWord("...", "normal");
				publish(status);
				getAlCorrIDsFromAl(envToProcess, processingTaskID);
				if (errorEncountered) {
					status = new StatusMessage(2);
					status.addWord(errorString + errorType, "error");
					publish(status);
					status = new StatusMessage(0);
					status.addWord(endErrorString, "error");
					return status;
				}
				
				
				
				for (int j = 0; j < uniqueAlCorrIDs.size(); j++) {
					String processingAlCorrelationID = uniqueAlCorrIDs.get(j);
					
					//Removed this method and associated timestamp select from ML activation fetching method, as it was causing app to incorrectly exclude some messages 
					//which occurred after timeouts were declared.
//					/** Fetch Timestamps for Activation Request/Response from AL DB **/
//					status = new StatusMessage(3);
//					status.addWord("Fetching ", "normal");
//					status.addWord("timestamp", "keyword");
//					status.addWord("'s for activation requests and responses for ", "normal");
//					status.addWord("alcorrelationid ", "keyword");
//					status.addWord(processingAlCorrelationID, "value");
//					status.addWord(" from ", "normal");
//					status.addWord("AL DB", "value");
//					status.addWord("...", "normal");
//					publish(status);
//					getActivationTimestampsFromAl(envToProcess, processingAlCorrelationID);
//					if (errorEncountered) {
//						status = new StatusMessage(3);
//						status.addWord(errorString + errorType, "error");
//						publish(status);
//						status = new StatusMessage(0);
//						status.addWord(endErrorString, "error");
//						return status;
//					}
//					status = new StatusMessage(3);
//					status.addWord(successString, "normal");
//					publish(status);
					
					
					/** Fetch Activation data from ML DB **/
					status = new StatusMessage(3);
					status.addWord("Fetching activation requests and responses AL <-> ML for ", "normal");
					status.addWord("correlationid ", "keyword");
					status.addWord(processingAlCorrelationID, "value");
					status.addWord(" from ", "normal");
					status.addWord("ML DB", "value");
					status.addWord("...", "normal");
					publish(status);
					getActivationFromMl(envToProcess, processingAlCorrelationID, alActivationTimestamps);
					if (errorEncountered) {
						status = new StatusMessage(3);
						status.addWord(errorString + errorType, "error");
						publish(status);
						status = new StatusMessage(0);
						status.addWord(endErrorString, "error");
						return status;
					}
					status = new StatusMessage(3);
					status.addWord(successString, "normal");
					publish(status);
				}
				
				
				/** Finish Process Activation Tasks for taskID **/
				status = new StatusMessage(2);
				status.addWord(successString, "normal");
				publish(status);
				
			}
			
			/** Finish Processing Activation Tasks for orderNumber **/
			status = new StatusMessage(1);
			status.addWord(successString, "normal");
			publish(status);
			
			
			/** Finish Order Processing **/
			status = new StatusMessage(0);
			status.addWord(successString, "normal");
			publish(status);
			
			status = new StatusMessage(0);
			status.addWord(endSuccessString, "success");
			return status;
			
		}
		
		public void process(List<StatusMessage> chunks) {
			for (StatusMessage msg : chunks) {
				postProgressMessage(msg);
			}
		}	
		
		public void done() {
			StatusMessage msg;
			try {
				msg = get();
				postProgressMessage(msg);
			} catch (InterruptedException e) {
				//Not sure how this might get thrown
				postException(e);
			} catch (ExecutionException e) {
				//Not sure how this might get thrown
				postException(e);
			}
			if (fullActivationMessageList.size() > 0) {
				sortMessages();
				buildResultsTable();
				fillTextFields();
				outputPanelTabbedPane.setSelectedIndex(1);
			}
		}
		
	}
	
	//-----------------------------------------------------------------//
	
	/** Cell Renderer **/
	@SuppressWarnings("serial")
	private class ActivationTableRenderer extends JTextArea implements TableCellRenderer {
		
		public ActivationTableRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, 
													   int row, int column) {
			setFont(table.getFont());
			if (isSelected) {
				setBackground(new Color(184, 207, 229));
			}
			else {
				setBackground(Color.WHITE);
			}
			setText(value.toString());
			return this;
		}
		
	}
	
	//-----------------------------------------------------------------//
	
}
