/*
 * Author: Yuchi Shi
 * */
package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.sound.sampled.*;

public class snakePanel extends JPanel implements KeyListener, ActionListener{
	// all images needed
	ImageIcon instraction = new ImageIcon("instraction.jpg");
	ImageIcon title = new ImageIcon("title.jpg");
	ImageIcon body = new ImageIcon("body.png");
	ImageIcon up = new ImageIcon("up.png");
	ImageIcon down = new ImageIcon("down.png");
	ImageIcon right = new ImageIcon("right.png");
	ImageIcon left = new ImageIcon("left.png");
	ImageIcon body_fever = new ImageIcon("body_fever.png");
	ImageIcon up_fever = new ImageIcon("up_fever.png");
	ImageIcon down_fever = new ImageIcon("down_fever.png");
	ImageIcon right_fever = new ImageIcon("right_fever.png");
	ImageIcon left_fever = new ImageIcon("left_fever.png");
	ImageIcon fruit = new ImageIcon("food.png");
	ImageIcon sfruit = new ImageIcon("superFood.png");
	ImageIcon pfruit = new ImageIcon("poisonedFood.png");

	int length = 3;
	int score = 0;
	int[] snakeX = new int[820];
	int[] snakeY = new int[820];
	int fruitX;
	int fruitY;
	long startTime;
	long endTime;
	long feverStartTime;
	Random r = new Random();
	//  the default direction is toward to right
	direction dir = direction.R;
	fruits fruitType;
	boolean isStart = false;
	boolean isDead = false;
	Timer t = new Timer(100, this);
	int energy = 0;
	boolean isFever = false;
	boolean showInstraction = true;

	
	/**
	 * a constructor that takes no argument and will create an snakePanel object.
	 */
	public snakePanel() {
		initSnake();
		this.addKeyListener(this);
		this.setFocusable(true);
		setLayout(null);
		JButton PauseAndContinue = new JButton("Pause");
		  PauseAndContinue.addMouseListener(new MouseAdapter() {
		   @Override
		   public void mouseClicked(MouseEvent e) {
			if(isDead) {
				return; 
			}
		    isStart = !isStart;
		    //  when user first press the button, disable the instruction.
		    showInstraction = false;
		   }
		  });
		  PauseAndContinue.setBounds(39, 11, 89, 23);
		  PauseAndContinue.setFocusable(false);
		  add(PauseAndContinue);
		t.start();
		playBGM();
	}
	
	public void paintComponent(Graphics g) {
		// instruction will only show when user first enter the game.
		if(showInstraction) {
			instraction.paintIcon(this, g, 0, 0);
		} else {
		super.paintComponent(g);
		this.setBackground(Color.LIGHT_GRAY);
		title.paintIcon(this, g, 20, 11);
		g.fillRect(20, 75, 850, 600);
		g.setColor(Color.YELLOW);
		// showing the current score and energy at the board.
		g.drawString("Score: " + this.score, 700, 30);
		g.drawString("Energy: ", 700, 50);
		drawEnergyBar(energy,g);
		g.setColor(Color.RED);
		g.setFont(new Font("Courier", Font.BOLD + Font.ITALIC, 16));
		// when has enough energy, tell user to press "f" enter fever mode.
		if(energy == 6) {
			g.drawString("Press F to enter fever mode! ", 550, 100);
		}
			
		// Check the dir and is in fever mode or not, draw different directions of heads. 
		if(!isFever) {
			if(dir == direction.R) {
				right.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.L) {
				left.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.U) {
				up.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.D) {
				down.paintIcon(this, g, snakeX[0], snakeY[0]);
			} 
			
			for(int i = 1; i < length; i++) {
				body.paintIcon(this, g, snakeX[i], snakeY[i]);
			}
		} else {
			if(dir == direction.R) {
				right_fever.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.L) {
				left_fever.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.U) {
				up_fever.paintIcon(this, g, snakeX[0], snakeY[0]);
			} else if(dir == direction.D) {
				down_fever.paintIcon(this, g, snakeX[0], snakeY[0]);
			} 
			
			for(int i = 1; i < length; i++) {
				body_fever.paintIcon(this, g, snakeX[i], snakeY[i]);
			}
		}
		
		//  Draw different fruits.
		if(this.fruitType == fruits.POISONED) {
			pfruit.paintIcon(this, g, fruitX, fruitY);
		} else if(this.fruitType == fruits.SUPER) {
			sfruit.paintIcon(this, g, fruitX, fruitY);
		} else {
			fruit.paintIcon(this, g, fruitX, fruitY);
		}
		
		//  print out the game status.
		if(this.isDead) {
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 40));
		g.drawString("Game Over", 340, 350);
		g.drawString("Press Enter to Restart.", 250, 420);
		}}
	}
	
	
	/**
     * Reset the snake to the original place and length, then clear the score and energy.
     */
	public void initSnake() {
		// Default snake has 2 body nodes.
		length = 3;
		snakeX[0] = 100;
		snakeY[0] = 100;
		snakeX[1] = 75;
		snakeY[1] = 100;
		snakeX[2] = 50;
		snakeY[2] = 100;
		this.dir = direction.R;
		score = 0;
		energy = 0;
		generateNewFruit();
	}

	
	/**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     * @param e the event to be processed
     */
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	
	 /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     * @param e the event to be processed
     */
	@Override
	public void keyPressed(KeyEvent e) {
		this.setFocusable(true);
		int keyCode = e.getKeyCode();
		//  receiving user's input and change the head direction/isStart.
		if (keyCode == KeyEvent.VK_LEFT) {
			if(this.dir == direction.R) {return;}
			this.dir = direction.L;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			if(this.dir == direction.L) {return;}
			this.dir = direction.R;
		} else if (keyCode == KeyEvent.VK_UP) {
			if(this.dir == direction.D) {return;}
			this.dir = direction.U;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			if(this.dir == direction.U) {return;}
			this.dir = direction.D;
		} else if (keyCode == KeyEvent.VK_ENTER) {
			this.isDead = false;
			initSnake();
		} 
		//  Enter fever mode, reset the energy to 0 and let snake move 3 times fast.
		// Fever mode will continues for 5 seconds.
		if(energy >= 6 && keyCode == KeyEvent.VK_F) {
			this.isFever = true;
			energy = 0;
			t.setDelay(30);
			this.feverStartTime = System.currentTimeMillis();
		}

	}
	
	
	/**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     * @param e the event to be processed
     */
	@Override
	public void keyReleased(KeyEvent e) {

	}

	
	/**
     * Invoked when an action occurs.
     * @param e the event to be processed
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(this.isStart && !isDead) {
		for(int i = length - 1; i > 0; i--) {
			snakeX[i] = snakeX[i - 1];
			snakeY[i] = snakeY[i - 1];
		}
		
		if(this.dir == direction.L) {
			snakeX[0] = snakeX[0] - 25;
			if(snakeX[0] < 25) {
				snakeX[0] = 850;
			}
		} else if(this.dir == direction.R) {
			snakeX[0] = snakeX[0] + 25;
			if(snakeX[0] > 850) {
				snakeX[0] = 25;
			}
		} else if(this.dir == direction.U) {
			snakeY[0] = snakeY[0] - 25;
			if(snakeY[0] < 75) {
				snakeY[0] = 650;
			}
		} else if(this.dir == direction.D) {
			snakeY[0] = snakeY[0] + 25;
			if(snakeY[0] > 670) {
				snakeY[0] = 75;
			}
		}

		//  In NORMAL MODE
		//  Eat normal fruit will earn 10 points and add 1 node to snake's body.
		//  Eat super fruit will earn 50 points and add 3 node to snake's body.
		//  Eat poisoned fruit will deduct 100 points and add the length of snake will not change.
		if(isFever == false) {
			if(snakeX[0] == fruitX && snakeY[0] == fruitY) {
				if(this.fruitType == fruits.NORMAL) {
					length++;
					energy++;
					if(energy > 6) {
						energy = 6;
					}
					score = score + 10;
					generateNewFruit();
				} else if(this.fruitType == fruits.SUPER) {
					length = length + 3;
					energy = 6;
					if(energy > 6) {
						energy = 6;
					}
					score = score + 50;
					generateNewFruit();
				} else {
					energy = 0;
					score = score - 100;
					generateNewFruit();
				}
				eatSound();

			}
		} else {
			//  In FEVER MODE
			//  Eat normal fruit will earn 30 points and add 1 node to snake's body.
			//  Eat super fruit will earn 200 points and add 3 node to snake's body.
			//  Eat poisoned fruit will kill the snake.
			if(snakeX[0] == fruitX && snakeY[0] == fruitY) {
				if(this.fruitType == fruits.NORMAL) {
					length++;
					score = score + 30;
					generateNewFruit();
				} else if(this.fruitType == fruits.SUPER) {
					length = length + 3;
					score = score + 300;
					generateNewFruit();
				} else {
					this.isDead = true;
				}
				eatSound();
			}
		}
		
		// Fever mode will continues for 5 seconds.
		if((System.currentTimeMillis() - feverStartTime) / 1000 >= 5 && isFever == true) {
			isFever = false;
			t.setDelay(100);
		}
		// super/poisoned fruits will only appear for 3 seconds.
		if((System.currentTimeMillis() - this.startTime) / 1000 >= 3 && (this.fruitType == fruits.POISONED || this.fruitType == fruits.SUPER)) {
			generateNewFruit();
		}
		
		for(int i = 1; i < length; i++) {
			if(snakeX[i] == snakeX[0] && snakeY[i] == snakeY[0]) {
				isDead = true;
			}
		}
		repaint();
		}
		t.start();
		
	}
	
	/**
	   * A method to randomly generate one type of fruit on board.
	   */
	public void generateNewFruit() {
		// Fruits can only generate at the game area.
		int ftype = this.r.nextInt(10);
		startTime = System.currentTimeMillis();
		
		//  The possibility of popping up super fruit/poisoned fruit is 10% each.
		if(ftype < 8) {
			this.fruitType = fruits.NORMAL;
		} else if(ftype == 8) {
			this.fruitType = fruits.SUPER;
		} else if(ftype == 9) {
			this.fruitType = fruits.POISONED;
		}
		fruitX = 25 + 25 * r.nextInt(34);
		fruitY = 75 + 25 * r.nextInt(24);
	}
	
	/**
	   * A method to present energy bar.
	   */
	public void drawEnergyBar(int e, Graphics g) {
		int startX = 750;
		for(int i = 0; i < e; i++) {
			g.drawString("o  ", startX, 50);
			startX += 12;
		}
		for(int i = 0; i < 6 - e; i++) {
			g.drawString("-  ", startX, 50);
			startX += 12;
		}
		
	}
	
	
	/**
	   * Play the background music repeatedly.
	   */
	private void playBGM() {
		 File s = new File("bgm.wav");
		    try{
		        Clip clip = AudioSystem.getClip();
		        clip.open(AudioSystem.getAudioInputStream(s));
		        clip.loop(Clip.LOOP_CONTINUOUSLY);
		    } catch (Exception e){
		        e.printStackTrace();
		    }
	}
	
	
	/**
	   * Play the eat sound once.
	   */
	private void eatSound() {
		 File s = new File("eat.wav");
		    try{
		        Clip clip = AudioSystem.getClip();
		        clip.open(AudioSystem.getAudioInputStream(s));
		        clip.start();
		    } catch (Exception e){
		        e.printStackTrace();
		    }
	}
}
