import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel lblVisualCrypt = new JLabel("Visual Cryptography");
	private JLabel lblVersion = new JLabel("<html>Version 1.0</html>");
	private JLabel lblCreator = new JLabel("<html>Selwyn Dmello, Nevin Dabre and Ansley Rodrigues</html>");
	private JLabel lblSource = new JLabel("<html>Using the encryption technique proposed by Naor and Shamir in 1994.</html>");
	private JLabel lblDescryption = new JLabel("<html>Visual Cryptography is a special encryption technique to hide information in images in such a way that it can be decrypted by the human vision if the correct key image is used. Visual Cryptography uses two transparent images. One image contains random pixels (the key) and the other image contains the secret information. It is impossible to retrieve the secret information from one of the images - both are required to reveal the information. When the random image contains truely random pixels it can be seen as a One-time Pad system and will offer unbreakable encryption.</html>");
	private JLabel lblDescSource = new JLabel("<html></html>");
	private JLabel lblWeb = new JLabel("<html></html>");
	
	public AboutDialog(JFrame parent) {
		setTitle("About");
		((JPanel)this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		lblVisualCrypt.setFont(new Font("Serif", Font.BOLD, 36));
		lblVisualCrypt.setForeground(Color.RED);
		
		setSize(500, 330);
		setLocationRelativeTo(parent);
		setResizable(false);
		
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		this.add(lblVisualCrypt);
		this.add(lblVersion);
		this.add(lblCreator);
		this.add(lblSource);
		this.add(Box.createVerticalStrut(10));
		this.add(lblDescryption);
		this.add(Box.createVerticalStrut(10));
		this.add(lblDescSource);
		this.add(lblWeb);
		
		setVisible(true);
	}
}
