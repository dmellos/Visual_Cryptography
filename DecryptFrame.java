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

public class DecryptFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel pnlAll = new JPanel();
	private JPanel pnlKeyFile = new JPanel();
	private JPanel pnlEncFile = new JPanel();
	private JPanel pnlScrolls = new JPanel();
	private JPanel pnlScrollOverlay = new JPanel();
	private JPanel pnlScrollClean = new JPanel();
	
	private JLabel lblDescr = new JLabel("<html>Enter a key file and an encrypted image below to decrypt it. You could also decrypt it by printing the key and the encrypted image on transparent paper and overlaying them manually.</html>");
	private JLabel lblOverlay = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JLabel lblClean = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JTextField tfKey = new JTextField();
	private JTextField tfEncr = new JTextField();
	private JButton btnSelectKey = new JButton("Select keyfile");
	private JButton btnSelectEncr = new JButton("Select encrypted image");
	private JButton btnDecrypt = new JButton("Decrypt");
	private JButton btnSaveOverlay = new JButton("Save overlayed image to file");
	private JButton btnSaveClean = new JButton("Save decrypted image to file");
	private JScrollPane scrOverlay = new JScrollPane(lblOverlay);
	private JScrollPane scrClean = new JScrollPane(lblClean);
	
	private JFileChooser fileChooser = new JFileChooser();
	private BufferedImage imgOverlay = null;
	private BufferedImage imgClean = null;
	File fKeyFile = null;
	File fEncrFile = null;
	
	public DecryptFrame(JFrame parent) {
		// size
		tfKey.setMaximumSize(new Dimension(tfKey.getMaximumSize().width, tfKey.getPreferredSize().height));
		tfEncr.setMaximumSize(new Dimension(tfEncr.getMaximumSize().width, tfEncr.getPreferredSize().height));
		int iButMaxWidth = (btnSelectKey.getPreferredSize().width > btnSelectEncr.getPreferredSize().width) ?
							btnSelectKey.getPreferredSize().width : btnSelectEncr.getPreferredSize().width;
		btnSelectKey.setPreferredSize(new Dimension(iButMaxWidth, btnSelectKey.getPreferredSize().height));
		btnSelectEncr.setPreferredSize(new Dimension(iButMaxWidth, btnSelectEncr.getPreferredSize().height));
		
		
		// orientation
		lblDescr.setAlignmentX(LEFT_ALIGNMENT);
		pnlKeyFile.setAlignmentX(LEFT_ALIGNMENT);
		pnlEncFile.setAlignmentX(LEFT_ALIGNMENT);
		pnlScrolls.setAlignmentX(LEFT_ALIGNMENT);
		
		// action listener
		btnSelectKey.addActionListener(this);
		btnSelectEncr.addActionListener(this);
		btnDecrypt.addActionListener(this);
		btnSaveOverlay.addActionListener(this);
		btnSaveClean.addActionListener(this);
		
		tfKey.setEditable(false);
		tfEncr.setEditable(false);
		btnSaveOverlay.setEnabled(false);
		btnSaveClean.setEnabled(false);
		
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File arg0) {
				if (arg0.isDirectory()) return true;
				if (arg0.getName().endsWith(".png")) return true;
				return false;
			}

			public String getDescription() {
				return "Image (*.png)";
			}
		});
		
		pnlKeyFile.setLayout(new BoxLayout(pnlKeyFile, BoxLayout.X_AXIS));
		pnlKeyFile.add(tfKey);
		pnlKeyFile.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlKeyFile.add(btnSelectKey);
		
		pnlEncFile.setLayout(new BoxLayout(pnlEncFile, BoxLayout.X_AXIS));
		pnlEncFile.add(tfEncr);
		pnlEncFile.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlEncFile.add(btnSelectEncr);
		
		pnlScrollOverlay.setLayout(new BoxLayout(pnlScrollOverlay, BoxLayout.Y_AXIS));
		pnlScrollOverlay.add(scrOverlay);
		pnlScrollOverlay.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlScrollOverlay.add(btnSaveOverlay);
		
		pnlScrollClean.setLayout(new BoxLayout(pnlScrollClean, BoxLayout.Y_AXIS));
		pnlScrollClean.add(scrClean);
		pnlScrollClean.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlScrollClean.add(btnSaveClean);
		
		pnlScrolls.setLayout(new BoxLayout(pnlScrolls, BoxLayout.X_AXIS));
		pnlScrolls.add(pnlScrollOverlay);
		pnlScrolls.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlScrolls.add(pnlScrollClean);
		
		pnlAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlAll.setLayout(new BoxLayout(pnlAll, BoxLayout.Y_AXIS));
		pnlAll.add(lblDescr);
		pnlAll.add(pnlKeyFile);
		pnlAll.add(pnlEncFile);
		pnlAll.add(btnDecrypt);
		pnlAll.add(Box.createVerticalStrut(10));
		pnlAll.add(pnlScrolls);
		
		setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		
		add(pnlAll);
		setSize(500, 500);
		setMinimumSize(new Dimension(384, 253));
		setLocationRelativeTo(parent);
		setTitle("Visual Cryptography - Decrypt Image");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(btnDecrypt.getText())) {
			if (fKeyFile == null || !fKeyFile.exists() || fEncrFile == null || !fEncrFile.exists()) {
				JOptionPane.showMessageDialog(this, "File not found", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			BufferedImage imgKey = Crypting.loadAndCheckEncrFile(fKeyFile);
			if (imgKey == null) {
				JOptionPane.showMessageDialog(this, fKeyFile.getName() + " is not a valid key file", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			BufferedImage imgEnc = Crypting.loadAndCheckEncrFile(fEncrFile);
			if (imgEnc == null) {
				JOptionPane.showMessageDialog(this, fEncrFile.getName() + " is not an encrypted image", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			imgOverlay = Crypting.overlayImages(imgKey, imgEnc);
			if (imgOverlay == null) {
				JOptionPane.showMessageDialog(this, "Decryption failed - key and encrypted image not the same size?", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			imgClean = Crypting.decryptImage(imgOverlay);
			if (imgClean == null) {
				JOptionPane.showMessageDialog(this, "Decryption failed - key and encrypted image not the same size?", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}

			lblOverlay.setIcon(new ImageIcon(imgOverlay));
			lblClean.setIcon(new ImageIcon(imgClean));
			
			
			btnSaveOverlay.setEnabled(true);
			btnSaveClean.setEnabled(true);
		} else if (e.getActionCommand().equals(btnSaveOverlay.getText())) {
			if (imgOverlay == null) return;
			fileChooser.setSelectedFile(new File(""));
		    fileChooser.setDialogTitle("Save overlay as..");
		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	File f = fileChooser.getSelectedFile();
		    	if (!f.toString().endsWith(".png")) {
		    		f = new File(f.toString() + ".png");
		    	}
		    	try {
					ImageIO.write(imgOverlay, "png", f);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Could not Save file because: " + e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
		    }
		} else if (e.getActionCommand().equals(btnSaveClean.getText())) {
			if (imgClean == null) return;
			fileChooser.setSelectedFile(new File(""));
		    fileChooser.setDialogTitle("Save decrypted image as..");
		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	File f = fileChooser.getSelectedFile();
		    	if (!f.toString().endsWith(".png")) {
		    		f = new File(f.toString() + ".png");
		    	}
		    	try {
					ImageIO.write(imgClean, "png", f);
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
		} else if (e.getActionCommand().equals(btnSelectEncr.getText())) {
			fileChooser.setDialogTitle("Open source image..");
		    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	if (!fileChooser.getSelectedFile().exists()) return;
		    	if (!fileChooser.getSelectedFile().getName().endsWith(".png")) return;
		    	fEncrFile = fileChooser.getSelectedFile();
		    	tfEncr.setText(fEncrFile.toString());
		    }
		}
	}

	class MyFocusTraversalPolicy extends FocusTraversalPolicy {
	    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(btnSelectKey)) return btnSelectEncr;
	        else if(aComponent.equals(btnSelectEncr)) return btnDecrypt;
	        else if(aComponent.equals(btnDecrypt)) {
	        	if (btnSaveOverlay.isEnabled()) return btnSaveOverlay;
	        	if (btnSaveClean.isEnabled()) return btnSaveClean;
	        	return btnSelectKey;
	        }
	        else if(aComponent.equals(btnSaveOverlay) && btnSaveClean.isEnabled()) return btnSaveClean;
	        return btnSelectKey;
	    }
	   
	    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(btnSelectKey)) {
	        	if (btnSaveClean.isEnabled()) return btnSaveClean;
	        	if (btnSaveOverlay.isEnabled()) return btnSaveOverlay;
	        	return btnDecrypt;
	        }
	        else if(aComponent.equals(btnSelectEncr)) return btnSelectKey;
	        else if(aComponent.equals(btnDecrypt)) return btnSelectEncr;
	        else if(aComponent.equals(btnSaveOverlay)) return btnDecrypt;
	        else if(aComponent.equals(btnSaveClean) && btnSaveOverlay.isEnabled()) return btnSaveOverlay;
	        return btnDecrypt;
	    }
	    
	    public Component getDefaultComponent(Container focusCycleRoot) {
	        return btnSelectKey;
	    }
	   
	    public Component getFirstComponent(Container focusCycleRoot) {
	        return btnSelectKey;
	    }
	   
	    public Component getLastComponent(Container focusCycleRoot) {
	        return btnSaveClean;
	    }
	}
	
}
