package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FadeTile {

	int i,j,w=Game.SIZE;
	BufferedImage image;
	Game game;
	int speed = 6;
	
	public FadeTile(Game game,int i,int j,BufferedImage image) {
		this.game = game;
		this.i = i;
		this.j = j;
		this.image = image;
	}
	
	public void update() {
		if(w > 0) {
			w-=speed;
		}
	}
	
	public void render(Graphics2D g) {
		g.drawImage(image, (i*Game.SIZE)+(Game.SIZE-w)/2, (j*Game.SIZE)+(Game.SIZE-w)/2, w,w,null);
	}
	
	
}
