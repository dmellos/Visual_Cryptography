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


public class KeyGenNEncryptFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel pnlAll = new JPanel();
	private JPanel pnlFile = new JPanel();
	private JPanel pnlScrolls = new JPanel();
	private JPanel pnlScrollKey = new JPanel();
	private JPanel pnlScrollEnc = new JPanel();
	
	private JLabel lblDescr = new JLabel("<html>Add a valid source image (png, jpg or gif, will be converted to b/w) below to generate a key for it and encrypt it.</html>");
	private JLabel lblKey = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JLabel lblEnc = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JTextField tfImage = new JTextField();
	private JButton btnSelectImg = new JButton("Select image");
	private JButton btnEncrypt = new JButton("Generate Key and Encrypt");
	private JButton btnSaveKey = new JButton("Save key to file");
	private JButton btnSaveEnc = new JButton("Save crypt to file");
	private JScrollPane scrKey = new JScrollPane(lblKey);
	private JScrollPane scrEnc = new JScrollPane(lblEnc);
	
	private JFileChooser fileChooser = new JFileChooser();
	private BufferedImage imgKey = null;
	private BufferedImage imgEnc = null;
	File fSrcFile = null;
	
	public KeyGenNEncryptFrame(JFrame parent) {
		// size
		tfImage.setMaximumSize(new Dimension(tfImage.getMaximumSize().width, tfImage.getPreferredSize().height));
		
		// orientation
		lblDescr.setAlignmentX(LEFT_ALIGNMENT);
		pnlFile.setAlignmentX(LEFT_ALIGNMENT);
		pnlScrolls.setAlignmentX(LEFT_ALIGNMENT);
		
		// action listener
		btnSelectImg.addActionListener(this);
		btnEncrypt.addActionListener(this);
		this.btnSaveKey.addActionListener(this);
		btnSaveEnc.addActionListener(this);
		
		tfImage.setEditable(false);
		btnSaveKey.setEnabled(false);
		btnSaveEnc.setEnabled(false);
		
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
		
		pnlFile.setLayout(new BoxLayout(pnlFile, BoxLayout.X_AXIS));
		pnlFile.add(tfImage);
		pnlFile.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlFile.add(btnSelectImg);
		
		pnlScrollKey.setLayout(new BoxLayout(pnlScrollKey, BoxLayout.Y_AXIS));
		pnlScrollKey.add(scrKey);
		pnlScrollKey.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlScrollKey.add(btnSaveKey);
		
		pnlScrollEnc.setLayout(new BoxLayout(pnlScrollEnc, BoxLayout.Y_AXIS));
		pnlScrollEnc.add(scrEnc);
		pnlScrollEnc.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlScrollEnc.add(btnSaveEnc);
		
		pnlScrolls.setLayout(new BoxLayout(pnlScrolls, BoxLayout.X_AXIS));
		pnlScrolls.add(pnlScrollKey);
		pnlScrolls.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlScrolls.add(pnlScrollEnc);
		
		pnlAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlAll.setLayout(new BoxLayout(pnlAll, BoxLayout.Y_AXIS));
		pnlAll.add(lblDescr);
		pnlAll.add(pnlFile);
		pnlAll.add(btnEncrypt);
		pnlAll.add(Box.createVerticalStrut(10));
		pnlAll.add(pnlScrolls);
		
		setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		
		add(pnlAll);
		setSize(500, 500);
		setMinimumSize(new Dimension(384, 253));
		setLocationRelativeTo(parent);
		setTitle("Visual Cryptography - Key & Encryption");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(btnEncrypt.getText())) {
			if (fSrcFile == null || !fSrcFile.exists()) {
				JOptionPane.showMessageDialog(this, "File not found", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			BufferedImage imgSrc = Crypting.loadAndCheckSource(fSrcFile, 0, 0, false);
			if (imgSrc == null) {
				JOptionPane.showMessageDialog(this, fSrcFile.getName() + " is not fit for encryption", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			imgKey = Crypting.generateKey(imgSrc.getWidth(), imgSrc.getHeight());
			imgEnc = Crypting.encryptImage(imgKey, imgSrc);
			
			if (imgKey == null || imgEnc == null) {
				JOptionPane.showMessageDialog(this, "Error while encrypting (should never happen :( )", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			lblKey.setIcon(new ImageIcon(imgKey));
			lblEnc.setIcon(new ImageIcon(imgEnc));
			
			btnSaveKey.setEnabled(true);
			btnSaveEnc.setEnabled(true);
		} else if (e.getActionCommand().equals(btnSaveKey.getText())) {
			if (imgKey == null) return;
			fileChooser.setSelectedFile(new File(""));
		    fileChooser.setDialogTitle("Save key as..");
		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	File f = fileChooser.getSelectedFile();
		    	if (!f.toString().endsWith(".png")) {
		    		f = new File(f.toString() + ".png");
		    	}
		    	try {
					ImageIO.write(imgKey, "png", f);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Could not Save file because: " + e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
		    }
		} else if (e.getActionCommand().equals(btnSaveEnc.getText())) {
			if (imgEnc == null) return;
			fileChooser.setSelectedFile(new File(""));
		    fileChooser.setDialogTitle("Save encrypted image as..");
		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	File f = fileChooser.getSelectedFile();
		    	if (!f.toString().endsWith(".png")) {
		    		f = new File(f.toString() + ".png");
		    	}
		    	try {
					ImageIO.write(imgEnc, "png", f);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Could not Save file because: " + e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
		    }
		} else if (e.getActionCommand().equals(btnSelectImg.getText())) {
			fileChooser.setDialogTitle("Open image..");
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
	        if(aComponent.equals(btnSelectImg)) return btnEncrypt;
	        else if(aComponent.equals(btnEncrypt)) {
	        	if (btnSaveKey.isEnabled()) return btnSaveKey;
	        	if (btnSaveEnc.isEnabled()) return btnSaveEnc;
	        	return btnSelectImg;
	        }
	        else if(aComponent.equals(btnSaveKey) && btnSaveEnc.isEnabled()) return btnSaveEnc;
	        return btnSelectImg;
	    }
	   
	    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(btnSelectImg)) {
	        	if (btnSaveEnc.isEnabled()) return btnSaveEnc;
	        	if (btnSaveKey.isEnabled()) return btnSaveKey;
	        	return btnEncrypt;
	        }
	        else if(aComponent.equals(btnEncrypt)) return btnSelectImg;
	        else if(aComponent.equals(btnSaveKey)) return btnEncrypt;
	        else if(aComponent.equals(btnSaveEnc) && btnSaveKey.isEnabled()) return btnSaveKey;
	        return btnEncrypt;
	    }
	    
	    public Component getDefaultComponent(Container focusCycleRoot) {
	        return btnSelectImg;
	    }
	   
	    public Component getFirstComponent(Container focusCycleRoot) {
	        return btnSelectImg;
	    }
	   
	    public Component getLastComponent(Container focusCycleRoot) {
	        return btnSaveEnc;
	    }
	}
	
}