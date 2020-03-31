package display;

import java.awt.*;
import java.awt.image.BufferStrategy;

import javax.swing.*;

public abstract class Window extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	
	public int width,height;
	private String title;
	private JFrame frame;
	private int fps = 30;
	public Thread thread;
	boolean running = true;
	
	public Window(int width,int height,String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		frame = new JFrame();
		setFocusable(true);
		requestFocus();
	}
	
	public synchronized void start() {
		thread = new Thread(this,"Pacman");
		thread.start();
	}
	
	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void update();
	public abstract void render(Graphics2D g);
	
	public void run() {
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / fps;
		double delta = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				update();
				renderWindow();
				Toolkit.getDefaultToolkit().sync();
				delta--;
			}
		}
	}
	
	public void renderWindow() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		render(g);
		g.dispose();
		bs.show();
	}
	
	public void display() {
		frame.setResizable(false);
		frame.setTitle(title);
		frame.add(this);
		frame.pack();
		frame.setSize(width, height+frame.getInsets().top);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		start();
	}
	
}
