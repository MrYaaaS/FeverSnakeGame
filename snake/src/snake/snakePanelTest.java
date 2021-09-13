/*
 * Author: Yuchi Shi
 * */
package snake;

import static org.junit.Assert.*;

import java.awt.Graphics;

import org.junit.Before;
import org.junit.Test;


public class snakePanelTest {
	private snakePanel sp;

	/**
	 * Setting up with create new snakePanel objects.
	 */
  @Before
  public void init() {
	  sp = new snakePanel();  
  }
  
  
  /**
	 * Testing all default variables.
	 */
  @Test
  public void testDefaultVariables() {
	  assertEquals(sp.length, 3);
	  assertEquals(sp.score, 0);
	  assertEquals(sp.energy, 0);
	  assertEquals(sp.dir, direction.R);
	  assertFalse(sp.isStart);
	  assertFalse(sp.isDead);
	  assertFalse(sp.isFever);
	  assertTrue(sp.showInstraction);
  }
  
  
  /**
	 * Testing the initSnake function.
	 */
  @Test
  public void testInitSnake() {
	  sp.initSnake();
	  assertEquals(sp.length, 3);
	  assertEquals(sp.snakeX[0], 100);
	  assertEquals(sp.snakeY[0], 100);
	  assertEquals(sp.snakeX[1], 75);
	  assertEquals(sp.snakeY[1], 100);
	  assertEquals(sp.snakeX[2], 50);
	  assertEquals(sp.snakeY[2], 100);
	  assertEquals(sp.dir, direction.R);
	  assertEquals(sp.score, 0);
  }
  
  /**
	 * Testing the generateNewFruit function.
	 */
  @Test
  public void testGenerateNewFruit() {
	  sp.generateNewFruit();
	  assertTrue(sp.fruitType.equals(fruits.NORMAL)||sp.fruitType.equals(fruits.SUPER)||sp.fruitType.equals(fruits.POISONED));
  }
  
}
