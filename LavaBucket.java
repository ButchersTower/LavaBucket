package LavaBucket;

import javax.swing.JFrame;

public class LavaBucket extends JFrame {
	public LavaBucket() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setContentPane(new Persp());
		// frame.setContentPane(new Height().getPanel());
		frame.setContentPane(new Trees());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setTitle("LavaBucket");
	}

	public static void main(String[] args) {
		new LavaBucket();
	}
}
