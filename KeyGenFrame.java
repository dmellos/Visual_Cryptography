import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

public class KeyGenFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel pnlAll = new JPanel();
	private JPanel pnlRes = new JPanel();
	
	private JLabel lblDescr = new JLabel("<html>The entered resolution below is the largest resolution an image to be encrypted with this key can have " +
			"(The generated key will be twice as large).</html>");
	private JLabel lblWidth = new JLabel("Width:");
	private JLabel lblHeight = new JLabel("Height:");
	private JLabel lblImg = new JLabel(new ImageIcon(), JLabel.CENTER);
	private JFormattedTextField tfWidth = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField tfHeight = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JButton btnGenerate = new JButton("Generate Key");
	private JButton btnSave = new JButton("Save key to file");
	private JScrollPane scrImage = new JScrollPane(lblImg);

	private BufferedImage imgKey = null;
	JFileChooser fileChooser = new JFileChooser();
	
	public KeyGenFrame(JFrame parent) {
		// size
		tfWidth.setMaximumSize(new Dimension(tfWidth.getMaximumSize().width, tfWidth.getPreferredSize().height));
		tfHeight.setMaximumSize(new Dimension(tfWidth.getMaximumSize().width, tfWidth.getPreferredSize().height));
		
		// orientation
		lblDescr.setAlignmentX(LEFT_ALIGNMENT);
		pnlRes.setAlignmentX(LEFT_ALIGNMENT);
		scrImage.setAlignmentX(LEFT_ALIGNMENT);
		btnSave.setAlignmentX(LEFT_ALIGNMENT);
		
		// action listener
		btnGenerate.addActionListener(this);
		btnSave.addActionListener(this);
		tfWidth.addActionListener(this);
		tfHeight.addActionListener(this);
		
		// default value
		tfWidth.setText("200");
		tfHeight.setText("200");
		btnSave.setEnabled(false);
		
		fileChooser.setDialogTitle("Save as..");
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
		
		pnlRes.setLayout(new BoxLayout(pnlRes, BoxLayout.X_AXIS));
		pnlRes.add(lblWidth);
		pnlRes.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlRes.add(tfWidth);
		pnlRes.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlRes.add(lblHeight);
		pnlRes.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlRes.add(tfHeight);
		pnlRes.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlRes.add(btnGenerate);
		
		pnlAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlAll.setLayout(new BoxLayout(pnlAll, BoxLayout.Y_AXIS));
		pnlAll.add(lblDescr);
		pnlAll.add(pnlRes);
		pnlAll.add(Box.createVerticalStrut(10));
		pnlAll.add(scrImage);
		pnlAll.add(btnSave);
		
		setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		
		add(pnlAll);
		setSize(500, 500);
		setMinimumSize(new Dimension(384, 253));
		setLocationRelativeTo(parent);
		setTitle("Visual Cryptography - Generate Key");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(btnGenerate.getText())) {
			imgKey = Crypting.generateKey(Integer.parseInt(tfWidth.getText()), Integer.parseInt(tfHeight.getText()));
			lblImg.setIcon(new ImageIcon(imgKey));
			btnSave.setEnabled(true);
		} else if (e.getActionCommand().equals(btnSave.getText())) {
			if (imgKey == null) return;
			fileChooser.setSelectedFile(new File(""));
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
		} else {
			// tfWidth or tfHeight
			btnGenerate.doClick();
		}
	}

	class MyFocusTraversalPolicy extends FocusTraversalPolicy {
	    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(tfWidth)) return tfHeight;
	        else if(aComponent.equals(tfHeight)) return btnGenerate;
	        else if(aComponent.equals(btnGenerate) && btnSave.isEnabled()) return btnSave;
	        return tfWidth;
	    }
	   
	    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
	        if(aComponent.equals(tfWidth) && btnSave.isEnabled()) return btnSave;
	        else if(aComponent.equals(tfHeight)) return tfWidth;
	        else if(aComponent.equals(btnGenerate)) return tfHeight;
	        return btnGenerate;
	    }
	    
	    public Component getDefaultComponent(Container focusCycleRoot) {
	        return tfWidth;
	    }
	   
	    public Component getFirstComponent(Container focusCycleRoot) {
	        return tfWidth;
	    }
	   
	    public Component getLastComponent(Container focusCycleRoot) {
	        return btnSave;
	    }
	}
	
}
