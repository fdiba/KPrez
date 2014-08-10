package webodrome;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Board {
	
	private PApplet pApplet;
	private int color;
	private String imgName;
	private PImage img;
	private PVector location3d;
	
	public Board(PApplet _pApplet, String _imgName){
		
		pApplet = _pApplet;
		color = App.colorsPanel[3];
		imgName = "assets/" + _imgName;
		img = pApplet.loadImage(imgName);
		float scale = 1.5f;
		img.resize((int) (img.width*scale), (int) (img.height*scale));
		
	}
	public void setLocation3d(PVector _location3d){
		location3d = _location3d;
	}
	public void display(int alpha){
		pApplet.rectMode(PApplet.CENTER);
		pApplet.strokeWeight(4);
		pApplet.strokeCap(PApplet.SQUARE);
		pApplet.stroke(color, alpha);
		pApplet.noFill();
		pApplet.translate(location3d.x, location3d.y,location3d.z);
		
		pApplet.pushMatrix();	
			pApplet.rotateX(PApplet.radians(180));
			pApplet.image(img, -img.width/2, -img.height/2);
			pApplet.rect(0, 0, img.width, img.height);	
		pApplet.popMatrix();
		
		
	}
}
