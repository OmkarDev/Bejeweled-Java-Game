package game;

public class Vector2i {

	int i,j;
	
	public Vector2i(int i,int j) {
		this.i = i;
		this.j = j;
	}
	
	public Vector2i() {
		i = 0;
		j = 0;
	}
	
	public void print() {
		System.out.println(i+" , " + j);
	}
	
}
