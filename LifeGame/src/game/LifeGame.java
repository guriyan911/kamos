package game;

import java.util.HashMap;
import java.util.Map;

public class LifeGame {
	private int height = 0;
	private int width = 0;
	private int generation = 0;
	private Map<String, String> map;

	public void make(int x, int y) {
		this.width = x;
		this.height = y;
		this.map = new HashMap<String, String>();
	}

	public void setCell(int x, int y) {
		map.put(x + "," + y, "■");
	}

	public int getCount(int x, int y) {
		int count = 0;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (map.containsKey(i + "," + j) && !(i == x && j == y)) {
					count++;
				}
			}
		}
		return count;
	}

	public void display() {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (map.containsKey(x + "," + y)) {
					System.out.print("■");
				} else {
					System.out.print("□");
				}
			}
			System.out.print("\n");
		}
	}

	public void nextGeneration() {
		Map<String, String> workMap = new HashMap<String, String>(this.map);
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				int count = getCount(x, y);
				if (count == 3) {
					workMap.put(x + "," + y, "■");
				} else if (count != 2) {
					workMap.remove(x + "," + y);
				}
			}
		}
		this.map = workMap;
	}

	public void setGeneration(int i) {
		this.generation = i;
	}

	public void start() {
		for (int i = 1; i <= this.generation; i++) {
			this.display();
			System.out.println("");
			this.nextGeneration();
		}
	}

}
