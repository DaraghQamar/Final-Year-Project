import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JButton;

import java.awt.Panel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

//import net.proteanit.sql.DbUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;


public class DatabaseManagementUI{

	private JFrame frame;
	
	//Global Variables
	Methods mtd = new Methods();
	Connection c = null;
	Statement stmt = null;
	private JTextField inputFilePath;
	private JTextField inputFolderPath;
	private JTextField textNumberOfImages;
	private JTable table;
	private final DefaultTableModel tableModel = new DefaultTableModel();
	private JTextField textTablesSelected;
	

	//Launch the application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatabaseManagementUI window = new DatabaseManagementUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Create the application
	public DatabaseManagementUI() {
		initialize();
	}
	
	//Initialize the contents of the frame
	private void initialize() {
		
		//*********************************************** 	User Interface Set Up	 ***********************************************
		
		frame = new JFrame();
		frame.setBounds(100, 100, 984, 645);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		JPanel ControlPanel_main = new JPanel();
		ControlPanel_main.setForeground(SystemColor.inactiveCaption);
		ControlPanel_main.setBackground(Color.LIGHT_GRAY);
		ControlPanel_main.setBounds(0, 0, 349, 616);
		frame.getContentPane().add(ControlPanel_main);
		ControlPanel_main.setLayout(null);
		
		JPanel ControlPanel_header = new JPanel();
		ControlPanel_header.setBackground(Color.DARK_GRAY);
		ControlPanel_header.setBounds(0, 0, 349, 77);
		ControlPanel_main.add(ControlPanel_header);
		ControlPanel_header.setLayout(null);
		
		JLabel lblControlPanel = new JLabel("Control Panel");
		lblControlPanel.setForeground(SystemColor.inactiveCaption);
		lblControlPanel.setBackground(SystemColor.activeCaption);
		lblControlPanel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblControlPanel.setBounds(10, 11, 145, 55);
		ControlPanel_header.add(lblControlPanel);
		
		JPanel Dashboard_main = new JPanel();
		Dashboard_main.setBounds(349, 0, 629, 616);
		frame.getContentPane().add(Dashboard_main);
		Dashboard_main.setLayout(null);
		
		Panel Dashboard_header = new Panel();
		Dashboard_header.setBackground(Color.DARK_GRAY);
		Dashboard_header.setBounds(0, 0, 629, 77);
		Dashboard_main.add(Dashboard_header);
		Dashboard_header.setLayout(null);
		
		JLabel lblDashboard = new JLabel("Dashboard");
		lblDashboard.setBounds(10, 11, 142, 55);
		lblDashboard.setForeground(SystemColor.inactiveCaption);
		lblDashboard.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblDashboard.setBackground(SystemColor.activeCaption);
		Dashboard_header.add(lblDashboard);
		
		
		//**********************************************************************************************************************************
		
		
		
		//*********************************************** 	Code for Event Extraction dashboard	 ***********************************************
		
		JPanel ExentExtractionDash = new JPanel();
		ExentExtractionDash.setBackground(new Color(255, 99, 71));
		ExentExtractionDash.setBounds(10, 83, 609, 522);
		ExentExtractionDash.setVisible(false);
		Dashboard_main.add(ExentExtractionDash);
		ExentExtractionDash.setLayout(null);
		
		JLabel lblNumberOfEvents = new JLabel("Number of events to extract:");
		lblNumberOfEvents.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNumberOfEvents.setBounds(51, 416, 188, 19);
		ExentExtractionDash.add(lblNumberOfEvents);
		
		textNumberOfImages = new JTextField();
		textNumberOfImages.setBounds(249, 417, 75, 20);
		ExentExtractionDash.add(textNumberOfImages);
		textNumberOfImages.setColumns(10);
		
		JLabel lblTablesToUse = new JLabel("Tables to use: ");
		lblTablesToUse.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTablesToUse.setBounds(51, 391, 89, 14);
		ExentExtractionDash.add(lblTablesToUse);
		
		textTablesSelected = new JTextField();
		textTablesSelected.setBounds(150, 385, 420, 20);
		ExentExtractionDash.add(textTablesSelected);
		textTablesSelected.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(249, 100, 324, 263);
		ExentExtractionDash.add(scrollPane);
		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				String tableClick = (table.getValueAt(row, 0).toString());
				textTablesSelected.setText(tableClick);
			}
		});
		scrollPane.setViewportView(table);
		
		JButton btnGenerateImages = new JButton("Generate Events");
		btnGenerateImages.setBounds(51, 464, 170, 23);
		ExentExtractionDash.add(btnGenerateImages);
		
		JLabel lblSelectBiometricTables = new JLabel("Select Biometric Tables:");
		lblSelectBiometricTables.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSelectBiometricTables.setBounds(51, 100, 178, 20);
		ExentExtractionDash.add(lblSelectBiometricTables);
		
		JButton btnLoadTable = new JButton("Load Table");
		btnLoadTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					mtd.connectDB();
					DefaultTableModel T;
					T = mtd.viewTables(table);
					table.setModel(T);
					mtd.closeConnection();
			}
		});
		btnLoadTable.setBounds(51, 131, 115, 29);
		ExentExtractionDash.add(btnLoadTable);
		
		JButton btnEventExtraction = new JButton("Event Extraction");
		btnEventExtraction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ExentExtractionDash.setVisible(true);
			}
		});
		btnEventExtraction.setToolTipText("View all tables in database");
		btnEventExtraction.setBackground(UIManager.getColor("Button.darkShadow"));
		btnEventExtraction.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnEventExtraction.setForeground(UIManager.getColor("Button.darkShadow"));
		btnEventExtraction.setBounds(53, 135, 236, 46);
		ControlPanel_main.add(btnEventExtraction);
		
		JButton btnCloseEventExtraction = new JButton("CLOSE");
		btnCloseEventExtraction.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnCloseEventExtraction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnCloseEventExtraction.isEnabled()) {
					ExentExtractionDash.setVisible(false);
					textNumberOfImages.setText(null);
					textTablesSelected.setText(null);
				}
			}
		});
		btnCloseEventExtraction.setBackground(SystemColor.control);
		btnCloseEventExtraction.setBounds(530, 11, 69, 23);
		ExentExtractionDash.add(btnCloseEventExtraction);
		
		JLabel lblEventExtraction = new JLabel("Event Extraction");
		lblEventExtraction.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblEventExtraction.setBounds(249, 11, 212, 50);
		ExentExtractionDash.add(lblEventExtraction);
		
		//**********************************************************************************************************************************
		
		
		
		//*********************************************** 	Code for Biometric dashboard	 ***********************************************

		
		JPanel BiometricDash = new JPanel();
		BiometricDash.setVisible(false);
		BiometricDash.setBackground(SystemColor.activeCaption);
		BiometricDash.setBounds(10, 83, 609, 517);
		Dashboard_main.add(BiometricDash);
		BiometricDash.setLayout(null);
		
		JLabel lblUploadingFiles = new JLabel("Uploading files... ");
		lblUploadingFiles.setToolTipText("This may take a couple minutes depending on size of files.");
		lblUploadingFiles.setBounds(158, 253, 128, 35);
		BiometricDash.add(lblUploadingFiles);
		lblUploadingFiles.setVisible(false);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblStatus.setBounds(158, 213, 128, 29);
		BiometricDash.add(lblStatus);
		
		JButton btnUploadBiometricResponse = new JButton("Upload Biometric Response");
		btnUploadBiometricResponse.setToolTipText("Upload a file to the database");
		btnUploadBiometricResponse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					BiometricDash.setVisible(true);
			}
		});
		btnUploadBiometricResponse.setForeground(UIManager.getColor("Button.darkShadow"));
		btnUploadBiometricResponse.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnUploadBiometricResponse.setBackground(UIManager.getColor("Button.darkShadow"));
		btnUploadBiometricResponse.setBounds(53, 292, 236, 46);
		ControlPanel_main.add(btnUploadBiometricResponse);
		
		inputFilePath = new JTextField();
		inputFilePath.setToolTipText("Please ensure file path does not contain \"/\" as only \"\\\" or \"//\" is accepted");
		inputFilePath.setBounds(158, 122, 409, 29);
		BiometricDash.add(inputFilePath);
		
		JButton btnCloseBiometricDash = new JButton("CLOSE");
		btnCloseBiometricDash.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnCloseBiometricDash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnCloseBiometricDash.isEnabled()) {
					BiometricDash.setVisible(false);
					inputFilePath.setText(null);
					lblUploadingFiles.setVisible(false);
				}
			}
		});
		btnCloseBiometricDash.setBackground(SystemColor.control);
		btnCloseBiometricDash.setBounds(530, 11, 69, 23);
		BiometricDash.add(btnCloseBiometricDash);
		
		JLabel lblPleaseInputFile = new JLabel("Please Input File path:");
		lblPleaseInputFile.setBackground(SystemColor.activeCaption);
		lblPleaseInputFile.setBounds(10, 129, 138, 14);
		BiometricDash.add(lblPleaseInputFile);
		
		JLabel lblBiometricHeader = new JLabel("Biometric Response Upload");
		lblBiometricHeader.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblBiometricHeader.setBounds(170, 11, 260, 50);
		BiometricDash.add(lblBiometricHeader);
		
		JButton btnUpload = new JButton("Upload Folder");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String inFilePath = inputFilePath.getText();
				mtd.connectDB();
				if(inFilePath != null && !inFilePath.isEmpty()) {
					lblUploadingFiles.setVisible(true);
					mtd.createBiometricTable(inFilePath.replace("\"", ""));
					lblUploadingFiles.setText("Upload Complete");
				}
				mtd.closeConnection();
			}
		});
		btnUpload.setBounds(158, 162, 128, 23);
		BiometricDash.add(btnUpload);
		
		//**********************************************************************************************************************************
		
		
		
		//************************************************ 	Code for SnapCam dashboard	 ***************************************************
		
		JPanel SnapCamDash = new JPanel();
		SnapCamDash.setVisible(false);
		SnapCamDash.setBackground(SystemColor.info);
		SnapCamDash.setBounds(10, 83, 609, 517);
		Dashboard_main.add(SnapCamDash);
		SnapCamDash.setLayout(null);
		
		JButton btnUploadSnapcamImages = new JButton("Upload SnapCam Images");
		btnUploadSnapcamImages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					SnapCamDash.setVisible(true);
			}
		});
		btnUploadSnapcamImages.setToolTipText("Upload a folder of images to the database");
		btnUploadSnapcamImages.setForeground(UIManager.getColor("Button.darkShadow"));
		btnUploadSnapcamImages.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnUploadSnapcamImages.setBackground(UIManager.getColor("Button.darkShadow"));
		btnUploadSnapcamImages.setBounds(53, 449, 236, 46);
		ControlPanel_main.add(btnUploadSnapcamImages);
		
		JButton btnCloseSnapCamDash = new JButton("CLOSE");
		btnCloseSnapCamDash.setBackground(SystemColor.control);
		btnCloseSnapCamDash.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnCloseSnapCamDash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SnapCamDash.setVisible(false);
				inputFolderPath.setText(null);
			}
		});
		btnCloseSnapCamDash.setBounds(530, 11, 69, 23);
		SnapCamDash.add(btnCloseSnapCamDash);
		
		JLabel lblPleaseInputFolder = new JLabel("Please Input Folder path:");
		lblPleaseInputFolder.setBounds(10, 122, 176, 27);
		SnapCamDash.add(lblPleaseInputFolder);
		
		inputFolderPath = new JTextField();
		inputFolderPath.setToolTipText("Please ensure file path does not contain \"/\" as only \"\\\" or \"//\" is accepted");
		inputFolderPath.setBounds(158, 122, 409, 28);
		SnapCamDash.add(inputFolderPath);
		
		JButton btnUploadFolder = new JButton("Upload Folder");
		btnUploadFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inFolderPath = inputFolderPath.getText();
				mtd.connectDB();
				if(inFolderPath != null && !inFolderPath.isEmpty()) {
					mtd.createImageTable(inFolderPath);
				}
				mtd.closeConnection();
			}
		});
		btnUploadFolder.setBounds(159, 162, 131, 23);
		SnapCamDash.add(btnUploadFolder);
		
		JLabel lblSnapCamHeader = new JLabel("SnapCam Upload");
		lblSnapCamHeader.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblSnapCamHeader.setBounds(218, 11, 212, 50);
		SnapCamDash.add(lblSnapCamHeader);
		
		//************************************************  Handle active dashboards  ******************************************************
		
		ExentExtractionDash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				BiometricDash.setVisible(false);
				SnapCamDash.setVisible(false);
			}
		});
		
		BiometricDash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				ExentExtractionDash.setVisible(false);
				SnapCamDash.setVisible(false);
			}
		});
		
		SnapCamDash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				ExentExtractionDash.setVisible(false);
				BiometricDash.setVisible(false);
			}
		});
	}
}
