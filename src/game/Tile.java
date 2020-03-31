package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Tile {

	public BufferedImage img;
	int num;
	int w = Game.SIZE;
	int x, y;
	Color color = new Color(0, 0, 0);
	public int matches;
	boolean matched = false;
	Game game;
	public ArrayList<Vector2i> matchedVectorsX = new ArrayList<Vector2i>();
	public ArrayList<Vector2i> matchedVectorsY = new ArrayList<Vector2i>();
	boolean fall = false;
	int speed = 8;
	boolean left, right, up, down;
	Instant start, end;
	boolean canFall;
	int reverse = 1;

	Tile(Game game, BufferedImage img, int num) {
		this.game = game;
		this.img = img;
		this.num = num;
	}

	public void update() {
		if (fall) {
			end = Instant.now();
			if (Duration.between(start, end).toMillis() >= 300) {
				fall = false;
				canFall = true;
			}
		}
		if (canFall) {
			if (y < 0) {
				y += speed;
			} else {
				y = 0;
				canFall = false;
				game.checkMatches();
			}
		}
		if (left && game.swap) {
			x -= speed;
			if (Math.abs(x) >= Game.SIZE) {
				left = false;
				x = 0;
				game.swap(game.one, game.two, game.tiles);
				game.swap = false;
				game.checkMatches();
			}
		}
		if (right && game.swap) {
			x += speed;
			if (Math.abs(x) >= Game.SIZE) {
				right = false;
				x = 0;
				game.checkMatches();
			}
		}

		if (up && game.swap) {
			y -= speed;
			if (Math.abs(y) >= Game.SIZE) {
				up = false;
				y = 0;
				game.swap(game.one, game.two, game.tiles);
				game.swap = false;
				game.checkMatches();
			}
		}
		if (down && game.swap) {
			y += speed;
			if (Math.abs(y) >= Game.SIZE) {
				down = false;
				y = 0;
				game.checkMatches();
			}
		}
		/// DOOUBLE SWAP ZONE
		if (left && game.doubleSwap) {
			x = x - reverse * speed;
			if (x > 0) {
				reverse = 1;
				left = false;
				x = 0;
				game.doubleSwap = false;
			}
			if(x < -Game.SIZE) {
				reverse = -1;
			}
		}
		if (right && game.doubleSwap) {
			x = x + reverse * speed;
			if (x < -1) {
				reverse = 1;
				right = false;
				x = 0;
				game.doubleSwap = false;
			}
			System.out.println(Game.SIZE + " , " +x);
			if(x > Game.SIZE) {
				reverse = -1;
			}
		}
//		
//		if (up && game.doubleSwap) {
//			y = y - reverse * speed;
//			if (y > 0) {
//				reverse = 1;
//				up = false;
//				y = 0;
//				game.doubleSwap = false;
//			}else if(y <= -Game.SIZE) {
//				reverse = -1;
//			}
//		}
////		if (down && game.doubleSwap) {
////			y = y + reverse * speed;
////			if (y < 0) {
////				reverse = 1;
////				down = false;
////				y = 0;
////				game.doubleSwap = false;
////			}
////			if(y >= Game.SIZE) {
////				reverse = -1;
////			}
////		}
	}

	public void render(Graphics2D g, int i, int j) {
		g.drawImage(img, (i * Game.SIZE) + x, (j * Game.SIZE) + y, w, w, null);
	}

	public ArrayList<Vector2i> checkNeighboursY(int i, int j, ArrayList<Tile> tiles) {
		matchedVectorsY = new ArrayList<Vector2i>();
		matchedVectorsY.add(new Vector2i(i, j));
		checkUp(i, j, tiles);
		checkDown(i, j, tiles);
		if (matchedVectorsY.size() >= 3) {
			return matchedVectorsY;
		} else {
			return null;
		}
	}

	public boolean checkNeighboursYBool(int i, int j, ArrayList<Tile> tiles) {
		matchedVectorsY = new ArrayList<Vector2i>();
		matchedVectorsY.add(new Vector2i(i, j));
		checkUp(i, j, tiles);
		checkDown(i, j, tiles);
		if (matchedVectorsY.size() >= 3) {
			matchedVectorsY.clear();
			return true;
		} else {
			matchedVectorsY.clear();
			return false;
		}
	}

	public ArrayList<Vector2i> checkNeighboursX(int i, int j, ArrayList<Tile> tiles) {
		matchedVectorsX = new ArrayList<Vector2i>();
		matchedVectorsX.add(new Vector2i(i, j));
		checkLeft(i, j, tiles);
		checkRight(i, j, tiles);
		if (matchedVectorsX.size() >= 3) {
			return matchedVectorsX;
		} else {
			return null;
		}
	}

	public boolean checkNeighboursXBool(int i, int j, ArrayList<Tile> tiles) {
		matchedVectorsX = new ArrayList<Vector2i>();
		matchedVectorsX.add(new Vector2i(i, j));
		checkLeft(i, j, tiles);
		checkRight(i, j, tiles);
		if (matchedVectorsX.size() >= 3) {
			matchedVectorsX.clear();
			return true;
		} else {
			matchedVectorsX.clear();
			return false;
		}
	}

	public void checkLeft(int i, int j, ArrayList<Tile> tiles) {
		if (i > 0) {
			if (tiles.get((i - 1) + j * game.cols).num == num) {
				matchedVectorsX.add(new Vector2i(i - 1, j));
				checkLeft(i - 1, j, tiles);
			} else {
				return;
			}
		}
	}

	public void checkRight(int i, int j, ArrayList<Tile> tiles) {
		if (i < game.cols - 1) {
			if (tiles.get((i + 1) + j * game.cols).num == num) {
				matchedVectorsX.add(new Vector2i(i + 1, j));
				checkRight(i + 1, j, tiles);
			} else {
				return;
			}
		}
	}

	public void checkUp(int i, int j, ArrayList<Tile> tiles) {
		if (j > 0) {
			if (tiles.get(i + (j - 1) * game.cols).num == num) {
				matchedVectorsY.add(new Vector2i(i, j - 1));
				checkUp(i, j - 1, tiles);
			} else {
				return;
			}
		}
	}

	public void checkDown(int i, int j, ArrayList<Tile> tiles) {
		if (j < game.cols - 1) {
			if (tiles.get(i + (j + 1) * game.cols).num == num) {
				matchedVectorsY.add(new Vector2i(i, j + 1));
				checkDown(i, j + 1, tiles);
			} else {
				return;
			}
		}
	}

}
