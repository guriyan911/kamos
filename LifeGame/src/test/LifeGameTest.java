package test;

import static org.junit.Assert.assertEquals;
import game.LifeGame;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

public class LifeGameTest {

	LifeGame world = new LifeGame();

	@Before
	public void 設定_盤面サイズ() {
		world.make(10, 10);
	}

	@Test
	public void 取得_座標にセルを設定する1() {
		world.setCell(1, 1);
		world.setCell(8, 8);
	}

	@Test
	public void 取得_座標にセルを設定する2() {
		world.setCell(0, 1);
		world.setCell(1, 1);
		world.setCell(2, 1);
	}

	@Test
	public void セルのまわりの数を返す() {
		取得_座標にセルを設定する1();
		assertEquals(0, world.getCount(1, 1));
		assertEquals(1, world.getCount(8, 7));
	}

	@Test
	public void 誕生_セルの周りに生きたセルが3つ() {
		取得_座標にセルを設定する2();
		assertEquals(3, world.getCount(1, 0));
	}

	@Test
	public void 生存_セル周りに生きたセルが2つ() {
		取得_座標にセルを設定する2();
		assertEquals(2, world.getCount(1, 1));
	}

	@Test
	public void 生存_セルの周りに生きたセルが3つ() {
		取得_座標にセルを設定する2();
		world.setCell(1, 0);
		assertEquals(3, world.getCount(1, 0));
	}

	@Test
	public void 過疎_生きたセルの周りに生きたセルが1つなら死滅する() {
		world.setCell(0, 1);
		assertEquals(1, world.getCount(1, 1));
	}

	@Test
	public void ブリンカー() {
		取得_座標にセルを設定する2();
		assertEquals(3, world.getCount(1, 0));
		world.display();
		world.nextGeneration();
		world.display();
		assertEquals(3, world.getCount(2, 1));
	}

	@Test
	public void 繰り返し世代を設定する() {
		world.setGeneration(10);
	}

	@Test
	public void 表示_世界の表示2() {
		world.setCell(0, 1);
		world.setCell(5, 0);
		world.setCell(5, 1);
		world.setCell(5, 2);
		world.setCell(4, 2);
		world.setCell(3, 1);
		world.setCell(2, 1);
		world.setGeneration(100);
		world.start();
	}
}
