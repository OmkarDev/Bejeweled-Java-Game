
package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import display.Window;

//@author omkar
public class Game extends Window implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 32 * 8;
	public static final int HEIGHT = 32 * 8;
	public static final int SCALE = 2;
	public static final int SIZE = 32 * SCALE;
	ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	ArrayList<Tile> tiles = new ArrayList<Tile>();
	ArrayList<ArrayList<Vector2i>> matchedListsY = new ArrayList<ArrayList<Vector2i>>();
	ArrayList<ArrayList<Vector2i>> matchedListsX = new ArrayList<ArrayList<Vector2i>>();
	int cols = 8;
	boolean selected = false;
	Vector2i selectedTile;
	boolean swap, doubleSwap;
	Vector2i one, two;
	public ArrayList<FadeTile> fadeTiles = new ArrayList<FadeTile>();

	public Game(int width, int height, String title) {
		super(width, height, title);
		try {
			for (int i = 1; i <= 7; i++) {
				InputStream path = getClass().getResourceAsStream("res/" + i + ".png");
				images.add(ImageIO.read(path));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				tiles.add(getRandomTile());
			}
		}
		checkMatches();
	}

	public void update() {
		for (Tile t : tiles) {
			t.update();
		}
		for (FadeTile ft : fadeTiles) {
			ft.update();
			if (ft.w <= 0) {
				fadeTiles.remove(ft);
				break;
			}
		}
	}

	public void render(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, (int) (WIDTH * SCALE), (int) (HEIGHT * SCALE));
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Tile t = tiles.get(i + j * cols);
				t.render(g, i, j);
			}
		}
		for (FadeTile ft : fadeTiles) {
			ft.render(g);
		}
		if (selected) {
			g.setColor(Color.yellow);
			g.setStroke(new BasicStroke(3));
			g.drawRect(selectedTile.i * SIZE, selectedTile.j * SIZE, SIZE, SIZE);
		}
	}

	public void swapAnimation(Vector2i a, Vector2i b) {
		if (b.i < a.i && a.j == b.j) {
			tiles.get(getNum(a)).left = true;
			tiles.get(getNum(b)).right = true;
		}
		if (b.i > a.i && a.j == b.j) {
			tiles.get(getNum(b)).left = true;
			tiles.get(getNum(a)).right = true;
		}
		if (b.j < a.j && a.i == b.i) {
			tiles.get(getNum(a)).up = true;
			tiles.get(getNum(b)).down = true;
		}
		if (b.j > a.j && a.i == b.i) {
			tiles.get(getNum(b)).up = true;
			tiles.get(getNum(a)).down = true;
		}
	}

	public void DoubleSwapAnimation(Vector2i a, Vector2i b) {
		if (b.i < a.i && a.j == b.j) {
			tiles.get(getNum(a)).left = true;
			tiles.get(getNum(b)).right = true;
		}
		if (b.i > a.i && a.j == b.j) {
			tiles.get(getNum(b)).left = true;
			tiles.get(getNum(a)).right = true;
		}
		if (b.j < a.j && a.i == b.i) {
			tiles.get(getNum(a)).up = true;
			tiles.get(getNum(b)).down = true;
		}
		if (b.j > a.j && a.i == b.i) {
			tiles.get(getNum(b)).up = true;
			tiles.get(getNum(a)).down = true;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int i = (int) Math.floor(e.getX() / (SIZE));
		int j = (int) Math.floor(e.getY() / (SIZE));
		if (e.getButton() == 1) {
			if (!selected) {
				selectedTile = new Vector2i(i, j);
				selected = true;
			} else {
				Vector2i two = new Vector2i(i, j);
				Vector2i one = selectedTile;
				if (Math.abs(one.i - two.i) <= 1 && Math.abs(one.j - two.j) <= 1) {
					if (!(Math.abs(one.i - two.i) == 1 && Math.abs(one.j - two.j) == 1)) {
						ArrayList<Tile> newTiles = tiles;
//						if (canSwap(one, two, newTiles)) {
						swapAnimation(one, two);
						swap = true;
						this.one = one;
						this.two = two;
//						}else {
//						doubleSwap = true;
//						DoubleSwapAnimation(one, two);
//						}
					}
				}
				selected = false;
			}
		}
	}

	public boolean canSwap(Vector2i one, Vector2i two, ArrayList<Tile> newtiles) {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		tiles.addAll(newtiles);
		swap(one, two, tiles);
		try {
			if (tiles.get(getNum(two)).checkNeighboursXBool(two.i, two.j, tiles)) {
				return true;
			}
			if (tiles.get(getNum(two)).checkNeighboursYBool(two.i, two.j, tiles)) {
				return true;
			}
		} catch (Exception e) {

		}
//		if (two.i - one.i == -1) {
//			System.out.println("LEFT");
//		}
//		if (two.i - one.i == 1) {
//			System.out.println("RIGHT");
//		}
//		if (two.j - one.j == -1) {
//			System.out.println("UP");
//		}
//		if (two.j - one.j == 1) {
//			System.out.println("DOWN");
//		}
		return false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
		}
	}

	public void checkMatches() {
		checkMatchesX();
		checkMatchesY();
	}

	public void checkMatchesX() {
		matchedListsX.clear();
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < cols; j++) {
				Tile t = tiles.get(i + j * cols);
				if (t.checkNeighboursX(i, j, tiles) != null) {
					ArrayList<Vector2i> arraylist = t.checkNeighboursX(i, j, tiles);
					matchedListsX.add(arraylist);
				}
			}
		}
		for (ArrayList<Vector2i> ml : matchedListsX) {
			Vector2i v = ml.get(0);
			deleteColumn(v.i, v.j, ml.size());
			fallingAnimation(v.i, v.j, 1);
		}
	}

	public void checkMatchesY() {
		matchedListsY.clear();
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < cols; j++) {
				Tile t = tiles.get(i + j * cols);
				if (t.checkNeighboursY(i, j, tiles) != null) {
					ArrayList<Vector2i> arraylist = t.checkNeighboursY(i, j, tiles);
					matchedListsY.add(arraylist);
					j += arraylist.size() - 1;
				}
			}
		}
		for (ArrayList<Vector2i> ml : matchedListsY) {
			for (Vector2i v : ml) {
				deleteColumn(v.i, v.j, ml.size());
			}
			Vector2i v = ml.get(ml.size() - 1);
			fallingAnimation(v.i, v.j, ml.size());
		}
	}

	public void fallingAnimation(int i, int j, int size) {
		for (int y = 0; y <= j; y++) {
			try {
				if (tiles.get(i + y * cols).fall == false) {
					tiles.get(i + y * cols).y -= SIZE * size;
					tiles.get(i + y * cols).fall = true;
					tiles.get(i + y * cols).start = Instant.now();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteColumn(int i, int j, int size) {
		fadeTiles.add(new FadeTile(this, i, j, tiles.get(i + j * cols).img));
		for (int y = 0; y <= j; y++) {
			if (y - 1 >= 0) {
				swap(new Vector2i(i, j), new Vector2i(i, y - 1), tiles);
			}
		}
		tiles.set(i, getRandomTile());
	}

	public Tile getRandomTile() {
		int num = randomRange(0, 6);
		return new Tile(this, images.get(num), num);
	}

	public int getNum(Vector2i a) {
		return a.i + a.j * cols;
	}

	public void swap(Vector2i a, Vector2i b, ArrayList<Tile> tiles) {
		Collections.swap(tiles, getNum(a), getNum(b));
	}

	public int randomRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static void main(String[] args) {
		Game game = new Game((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE), "Bejeweled");
		game.addMouseListener(game);
		game.addKeyListener(game);
		game.display();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}