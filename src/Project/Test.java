package Project;

import javax.swing.*;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import java.awt.*;

public class Test {
	public static void main(String[] args) {
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		MainFrame main = new MainFrame(1000, 1000, capabilities);
		JFrame frame = new JFrame("GUI Project");
		frame.getContentPane().add(main, BorderLayout.CENTER);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.setResizable(true);
		frame.setVisible(true);
		main.requestFocus();
	}
}
