import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


public class EncryptFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel pnlAll = new JPanel();
	private JPanel pnlKeyFile = new JPanel();
	private JPanel pnlImgFile = new JPanel();
	
	private JLabel lblDescr = new JLabel("<html>Add a valid key file and a valid source image (png, jpg or gif, will be converted to b/w, not larger than half the keyfile) below to encrypt the source image.</html>");
	private JLabel lblImg = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JTextField tfKey = new JTextField();
	private JTextField tfImage = new JTextField();
	private JButton btnSelectKey = new JButton("Select keyfile");
	private JButton btnSelectImage = new JButton("Select image");
	private JButton btnEncrypt = new JButton("Encrypt");
	private JButton btnSave = new JButton("Save encrypted image to file");
	private JScrollPane scrImage = new JScrollPane(lblImg);
	
	private JFileChooser fileChooser = new JFileChooser();
	private BufferedImage imgEncr = null;
	File fKeyFile = null;
	File fSrcFile = null;
	
	public EncryptFrame(JFrame parent) {
		// size
		tfKey.setMaximumSize(new Dimension(tfKey.getMaximumSize().width, tfKey.getPreferredSize().height));
		tfImage.setMaximumSize(new Dimension(tfImage.getMaximumSize().width, tfImage.getPreferredSize().height));
		int iButMaxWidth = (btnSelectKey.getPreferredSize().width > btnSelectImage.getPreferredSize().width) ?
							btnSelectKey.getPreferredSize().width : btnSelectImage.getPreferredSize().width;
		btnSelectKey.setPreferredSize(new Dimension(iButMaxWidth, btnSelectKey.getPreferredSize().height));
		btnSelectImage.setPreferredSize(new Dimension(iButMaxWidth, btnSelectImage.getPreferredSize().height));
		
		
		// orientation
		lblDescr.setAlignmentX(LEFT_ALIGNMENT);
		pnlKeyFile.setAlignmentX(LEFT_ALIGNMENT);
		pnlImgFile.setAlignmentX(LEFT_ALIGNMENT);
		scrImage.setAlignmentX(LEFT_ALIGNMENT);
		btnSave.setAlignmentX(LEFT_ALIGNMENT);
		
		// action listener
		btnSelectKey.addActionListener(this);
		btnSelectImage.addActionListener(this);
		btnEncrypt.addActionListener(this);
		btnSave.addActionListener(this);
		
		tfKey.setEditable(false);
		tfImage.setEditable(false);
		btnSave.setEnabled(false);
		
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File arg0) {
				if (arg0.isDirectory()) return true;
				if (arg0.getName().toLowerCase().endsWith(".png")) return true;
				if (arg0.getName().toLowerCase().endsWith(".jpg")) return true;
				if (arg0.getName().toLowerCase().endsWith(".gif")) return true;
				return false;
			}

			public String getDescription() {
				return "Image";
			}
		});
		
		pnlKeyFile.setLayout(new BoxLayout(pnlKeyFile, BoxLayout.X_AXIS));
		pnlKeyFile.add(tfKey);
		pnlKeyFile.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlKeyFile.add(btnSelectKey);
		
		pnlImgFile.setLayout(new BoxLayout(pnlImgFile, BoxLayout.X_AXIS));
		pnlImgFile.add(tfImage);
		pnlImgFile.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlImgFile.add(btnSelectImage);
		
		pnlAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlAll.setLayout(new BoxLayout(pnlAll, BoxLayout.Y_AXIS));
		pnlAll.add(lblDescr);
		pnlAll.add(pnlKeyFile);
		pnlAll.add(pnlImgFile);
		pnlAll.add(btnEncrypt);
		pnlAll.add(Box.createVerticalStrut(10));
		pnlAll.add(scrImage);
		pnlAll.add(btnSave);
		
		setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		
		add(pnlAll);
		setSize(500, 500);
		setMinimumSize(new Dimension(384, 253));
		setLocationRelativeTo(parent);
		setTitle("Visual Cryptography - Encrypt Image");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(btnEncrypt.getText())) {
			if (fKeyFile == null || !fKeyFile.exists() || fSrcFile == null || !fSrcFile.exists()) {
				JOptionPane.showMessageDialog(this, "File not found", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			BufferedImage imgKey = Crypting.loadAndCheckEncrFile(fKeyFile);
			if (imgKey == null) {
				JOptionPane.showMessageDialog(this, fKeyFile.getName() + " is not a valid key file", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			BufferedImage imgSrc = Crypting.loadAndCheckSource(fSrcFile, imgKey.getWidth() / 2, imgKey.getHeight() / 2, true);
			if (imgSrc == null) {
				JOptionPane.showMessageDialog(this, fSrcFile.getName() + " is not fit for encryption", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			imgEncr = Crypting.encryptImage(imgKey, imgSrc);
			if (imgSrc == null) {
				JOptionPane.showMessageDialog(this, "Could not encrypt file. You should never see this :(", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			lblImg.setIcon(new ImageIcon(imgEncr));
			btnSave.setEnabled(true);
			
			
		} else if (e.getActionCommand().equals(btnSave.getText())) {
			if (imgEncr == null) return;
			fileChooser.setSelectedFile(new File(""));
		    fileChooser.setDialogTitle("Save as..");
		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	File f = fileChooser.getSelectedFile();
		    	if (!f.toString().endsWith(".png")) {
		    		f = new File(f.toString() + ".png");
		    	}
		    	try {
					ImageIO.write(imgEncr, "png", f);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Could not Save file because: " + e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
		    }
		} else if (e.getActionCommand().equals(btnSelectKey.getText())) {
			fileChooser.setDialogTitle("Open keyfile..");
		    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	if (!fileChooser.getSelectedFile().exists()) return;
		    	if (!fileChooser.getSelectedFile().getName().endsWith(".png")) return;
		    	fKeyFile = fileChooser.getSelectedFile();
		    	tfKey.setText(fKeyFile.toString());
		    }
		} else if (e.getActionCommand().equals(btnSelectImage.getText())) {
			fileChooser.setDialogTitle("Open source image..");
		    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	if (!fileChooser.getSelectedFile().exists()) return;
		    	//if (!fileChooser.getSelectedFile().getName().endsWith(".png")) return;
		    	fSrcFile = fileChooser.getSelectedFile();
		    	tfImage.setText(fSrcFile.toString());
		    }
		}
	}

	class MyFocusTraversalPolicy extends FocusTraversalPolicy {
	    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(btnSelectKey)) return btnSelectImage;
	        else if(aComponent.equals(btnSelectImage)) return btnEncrypt;
	        else if(aComponent.equals(btnEncrypt) && btnSave.isEnabled()) return btnSave;
	        return btnSelectKey;
	    }
	   
	    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(btnSelectKey) && btnSave.isEnabled()) return btnSave;
	        else if(aComponent.equals(btnSelectImage)) return btnSelectKey;
	        else if(aComponent.equals(btnEncrypt)) return btnSelectImage;
	        return btnEncrypt;
	    }
	    
	    public Component getDefaultComponent(Container focusCycleRoot) {
	        return btnSelectKey;
	    }
	   
	    public Component getFirstComponent(Container focusCycleRoot) {
	        return btnSelectKey;
	    }
	   
	    public Component getLastComponent(Container focusCycleRoot) {
	        return btnSave;
	    }
	}
	
}