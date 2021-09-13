/*
 * Author: Yuchi Shi
 * */
package snake;

import javax.swing.JFrame;

public class snakeDriver {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setBounds(300, 200, 900, 720);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new snakePanel());
		frame.setVisible(true);
	}

}

