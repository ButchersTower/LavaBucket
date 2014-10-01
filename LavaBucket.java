package lavaBucket;

import javax.swing.JFrame;

public class LavaBucket extends JFrame {
	public LavaBucket() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Isometric());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		// frame.setLocationRelativeTo(null);
		frame.setTitle("LavaBucket");
	}

	public static void main(String[] args) {
		new LavaBucket();
	}
}
