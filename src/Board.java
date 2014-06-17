import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Board {
	
	private KPrez parent;
	private int couleur;
	private String imgName;
	private PImage img;
	private PVector location3d;
	
	public Board(KPrez _parent, String _imgName) {
		parent = _parent;
		couleur = parent.colors.get(3);
		imgName = "assets/" + _imgName;
		img = parent.loadImage(imgName);
		float scale = 1.5f;
		img.resize((int) (img.width*scale), (int) (img.height*scale));
	}
	protected void update(){
		
	}
	protected void setLocation3d(PVector _location3d){
		location3d = _location3d;
	}
	protected void display(int alpha){
		parent.rectMode(PApplet.CENTER);
		//parent.noStroke();
		//parent.fill(couleur, alpha);
		parent.strokeWeight(4);
		parent.strokeCap(PApplet.SQUARE);
		parent.stroke(couleur, alpha);
		parent.noFill();
		parent.translate(location3d.x, location3d.y,location3d.z);
		
		parent.pushMatrix();
		
			parent.rotateX(PApplet.radians(180));
			parent.image(img, -img.width/2, -img.height/2);
			parent.rect(0, 0, img.width, img.height);
		
		parent.popMatrix();
		
		
	}
}
