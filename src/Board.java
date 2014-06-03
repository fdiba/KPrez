import processing.core.PApplet;
import processing.core.PVector;

public class Board {
	
	private KPrez parent;
	private int width;
	private int height;
	private int couleur;
	
	public Board(KPrez _parent, int _width, int _height, int _couleur) {
		parent = _parent;
		width = _width;
		height = _height;
		couleur = _couleur;
	}
	protected void update(){
		
	}
	protected void display(PVector location3d){
		parent.rectMode(PApplet.CENTER);
		parent.noStroke();
		parent.fill(couleur);
		parent.translate(location3d.x, location3d.y,location3d.z);
		parent.rect(0, 0, width, height);
	}
}
