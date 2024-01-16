package minty.io;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;

import minty.io.CanvasImage.GridMode;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JSlider;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Dimension;

public class Main {

    // == attributes ==
	private static final String VER = "0.1.2";
	
    private JFrame frame;
    private JTextField textField;
    private CanvasImage canvas;
    private JScrollPane scrollPaneCanvas; // TODO: doesn't need to be here
    private JComboBox<GridMode> comboBox;
    private JList<AtlasData> list;
    private DefaultListModel<AtlasData> listModel;
    private JCheckBox chckbxDisplayGrid;
    
    // file operations //
    private File filename;
    public File workingDir;
    public JFileChooser fileChooser;
    public final static FileNameExtensionFilter FILE_FILTER = new FileNameExtensionFilter("Images", "png");
    public final static String FILE_EXTENSION_PNG  = ".png";
    
    private String simpleName = "";
    public final static String FILE_EXTENSION_ATLAS = ".atlas";
    private String atlasFilePath = "";
    
    private boolean isFileLoaded = false;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    try {
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if ("Nimbus".equals(info.getName())) {
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} catch (Exception e) {
			    // ignore, just use default L&F //
			}
		    Main window = new Main();
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public Main() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	
	// file operations
	fileChooser = new JFileChooser();
	String directoryProperty = System.getProperty("user.home");
	try {
	    //directoryProperty = new File(DatabaseGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
	    String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String decodedPath = URLDecoder.decode(path, "UTF-8");
	    decodedPath.substring(0, path.lastIndexOf("/")+1);
	    directoryProperty = decodedPath;
	} 
	catch (UnsupportedEncodingException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	workingDir = new File(directoryProperty);
	fileChooser.setCurrentDirectory(workingDir);
	fileChooser.setAcceptAllFileFilterUsed(false);
	
	// swing
	frame = new JFrame();
	frame.setTitle("Spritesheet Atlas Maker " + VER);
	frame.setBounds(0, 0, 800, 600);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocationRelativeTo(null);
	
	JPanel panelSide = new JPanel();
	frame.getContentPane().add(panelSide, BorderLayout.WEST);
	GridBagLayout gbl_panelSide = new GridBagLayout();
	gbl_panelSide.columnWidths = new int[]{0, 0, 0, 0, 0};
	gbl_panelSide.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
	gbl_panelSide.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
	gbl_panelSide.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
	panelSide.setLayout(gbl_panelSide);
		
	Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
	rigidArea.setPreferredSize(new Dimension(200, 20));
	GridBagConstraints gbc_rigidArea = new GridBagConstraints();
	gbc_rigidArea.gridwidth = 4;
	gbc_rigidArea.insets = new Insets(0, 0, 5, 0);
	gbc_rigidArea.gridx = 0;
	gbc_rigidArea.gridy = 0;
	panelSide.add(rigidArea, gbc_rigidArea);
	
	textField = new JTextField();
	//textField.setPreferredSize(new Dimension(300, 25));
	GridBagConstraints gbc_textField = new GridBagConstraints();
	//gbc_textField.insets = new Insets(0, 0, 5, 0);
	gbc_textField.gridwidth = 4;
	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField.gridx = 0;
	gbc_textField.gridy = 1;
	panelSide.add(textField, gbc_textField);
	textField.setColumns(10);
	
	JButton btnSave = new JButton("Save");
	btnSave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			saveBlock();
		}
	});
	GridBagConstraints gbc_btnSave = new GridBagConstraints();
	gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
	gbc_btnSave.insets = new Insets(0, 0, 5, 0);
	gbc_btnSave.gridwidth = 4;
	gbc_btnSave.gridx = 0;
	gbc_btnSave.gridy = 2;
	panelSide.add(btnSave, gbc_btnSave);
	
	listModel = new DefaultListModel<AtlasData>();
	
	JButton btnDelete = new JButton("Delete");
	btnDelete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			deleteEntry();
		}
	});
	
	JScrollPane scrollPane = new JScrollPane();
	GridBagConstraints gbc_scrollPane = new GridBagConstraints();
	gbc_scrollPane.gridwidth = 4;
	gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
	gbc_scrollPane.fill = GridBagConstraints.BOTH;
	gbc_scrollPane.gridx = 0;
	gbc_scrollPane.gridy = 3;
	panelSide.add(scrollPane, gbc_scrollPane);
	list = new JList<AtlasData>(listModel);
	scrollPane.setViewportView(list);
	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	GridBagConstraints gbc_btnDelete = new GridBagConstraints();
	gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
	gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
	gbc_btnDelete.gridwidth = 4;
	gbc_btnDelete.gridx = 0;
	gbc_btnDelete.gridy = 4;
	panelSide.add(btnDelete, gbc_btnDelete);
	
	JToolBar toolBar = new JToolBar();
	toolBar.setFloatable(false);
	frame.getContentPane().add(toolBar, BorderLayout.NORTH);
	
	JButton btnOpen = new JButton("Open File...");
	btnOpen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		    openFile(frame);
		}
	});
	toolBar.add(btnOpen);
	
	JButton btnGen = new JButton("Generate Atlas");
	btnGen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			generateAtlas();
		}
	});
	toolBar.add(btnGen);
	
	Component horizontalStrut = Box.createHorizontalStrut(20);
	toolBar.add(horizontalStrut);
	
	JLabel lblGrid = new JLabel("Grid:");
	toolBar.add(lblGrid);
	
	comboBox = new JComboBox<GridMode>();
	comboBox.setModel(new DefaultComboBoxModel<GridMode>(GridMode.values()));
	comboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			canvas.setGrid();
		}});
	toolBar.add(comboBox);
	
	chckbxDisplayGrid = new JCheckBox("Display Grid");
	toolBar.add(chckbxDisplayGrid);
	
	Component horizontalGlue = Box.createHorizontalGlue();
	toolBar.add(horizontalGlue);
	
	JButton btnHelp = new JButton("Help");
	toolBar.add(btnHelp);
	
	canvas = new CanvasImage(this);
	scrollPaneCanvas = new JScrollPane(canvas);
	scrollPaneCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPaneCanvas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	frame.getContentPane().add(scrollPaneCanvas, BorderLayout.CENTER);
	
	JLabel lblReport = new JLabel("Report");
	frame.getContentPane().add(lblReport, BorderLayout.SOUTH);
	
    }

    private void showMsg(String msg) {
    	JOptionPane.showMessageDialog(frame,
    		    msg,
    		    "Message",
    		    JOptionPane.INFORMATION_MESSAGE);
    }
    private void showWarning(String msg) {
    	JOptionPane.showMessageDialog(frame,
    		    msg,
    		    "Warning",
    		    JOptionPane.WARNING_MESSAGE);
    }
    private void showError(String msg) {
    	JOptionPane.showMessageDialog(frame,
    		    msg,
    		    "Error",
    		    JOptionPane.ERROR_MESSAGE);
    }
    
    private void openFile(JFrame frame) {
	fileChooser.setFileFilter(FILE_FILTER);
	
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			filename = fileChooser.getSelectedFile();
			// System.out.print(filename.getName());
			AtlasData.imageName = filename.getName();
			int lastI = AtlasData.imageName.lastIndexOf(".");
			simpleName = AtlasData.imageName.substring(0, lastI);
			//System.out.print(simpleName);
			BufferedImage image = null;
            try {
            	image = ImageIO.read(filename);
            }
            catch (IOException e) {
                showError("" + e);
                return;
            }
            isFileLoaded = true;
            canvas.setImage(image);
            AtlasData.width = image.getWidth();
            AtlasData.height = image.getHeight();
            listModel.removeAllElements();
            
            // check for atlas
            Path path = Paths.get(filename.toURI());
            atlasFilePath = path.getParent().toString() + "/" + simpleName + FILE_EXTENSION_ATLAS;
            //System.out.print(directory);
            File atlasFile = new File(atlasFilePath);
            
            if (atlasFile.exists()) {
            	//ArrayList<String> parseList = new ArrayList<String>(); 
            	try (Scanner s = new Scanner(atlasFile)) { // BUG: scanner outputs empty strings, solution is to use an arraylist
            		s.useDelimiter("(,|\\s+)");
            		AtlasData.parseHeader(s);
            		
            		// image size has priority
            		AtlasData.width = image.getWidth();
                    AtlasData.height = image.getHeight();
//                    while (s.hasNext()) {
//            			String next = s.next();
//            			//if (next.isEmpty()) continue;
//            			//parseList.add(next);
//            			System.out.print("|"+next+"|");
//            		}
                    
                    ArrayList<AtlasData> data = AtlasData.parseEntries(s);
                    
            		for(AtlasData ad: data) {
            			listModel.addElement(ad);
            		}
//
            	} 
            	catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					showError(e.toString());
					return;
				}
            	//Iterator<String> parseIterator = parseList.iterator();
            	//AtlasData.parseHeader(parseIterator);
            }
            else {
            	System.out.println("No atlas found.");
            }
            
            //scrollPaneCanvas.revalidate();
			// TODO: check if there's an atlas
		}
    }
    
    private void saveBlock() {
    	if (canvas.isSelectMode()) {
    		String name = textField.getText();
    		
    		if (name.isEmpty()) {
    			showWarning("Name cannot be empty.");
    			return;
    		}
    		else if  (name.contains(" ")) {
    			showWarning("Name cannot contain spaces.");
    			return;
    		}
    		
    		AtlasData ad = canvas.getAtlasData(name);
    		listModel.addElement(ad);
    		canvas.changeMouseStateToFree();
    	}
    	else {
    		showWarning("Image need to be selected first.");
    	}
    }
    
    private void generateAtlas()  {
    	if (!isFileLoaded) return;
    	if(!listModel.isEmpty()) {
    		StringBuilder sb = new StringBuilder();
    		sb.append(AtlasData.generateHeader());
    		Enumeration<AtlasData> data = getAtlasData();
    		while(data.hasMoreElements()) {
    			AtlasData ad = data.nextElement();
    			sb.append(ad.generateEntry());
    		}
    		//System.out.print(sb.toString()); // TODO: save to file
    		
    		try (BufferedWriter writer = new BufferedWriter(new FileWriter(atlasFilePath))) {
    			writer.write(sb.toString());
    			showMsg("Atlas created!");
    		} 
    		catch (IOException e) {
				// TODO Auto-generated catch block
				showError(e.toString());
			}
    		
    	}
    	else {
    		showWarning("There's no data to generate.");
    	}
    }
    
    private void deleteEntry() {
    	if (list.getSelectedValue() != null) {
    		
    		listModel.remove(list.getSelectedIndex());
    		
    	}
    }
    
    // TODO: report popup windows
    
    // == public methods ==
    public GridMode getGridMode() {
    	return (GridMode)comboBox.getSelectedItem();
    }
    
    public  Enumeration<AtlasData> getAtlasData() {
    	//listModel.elements().
    	return listModel.elements();
    }
    
    public boolean isDisplayGrid() {
    	return chckbxDisplayGrid.isSelected();
    }
    public AtlasData getSelectedListItem() {
    	return list.getSelectedValue();
    }
}
