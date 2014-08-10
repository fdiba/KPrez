package webodrome;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import webodrome.mainctrl.GesturalInterface;

public class ISprite extends webodrome.mainctrl.Bouton {
	
	private String path;
	private PImage img;
	public boolean isDragged;
	private int timeToMove;
	private PVector lastLocation;
	
	public ISprite(PApplet _pApplet, float _x, float _y, String _path, int c){
		
		super(_pApplet, _x, _y, 75, 75, true);
				
		path = _path;
		img = pApplet.loadImage(path);
		width = img.width;
		height = img.height;
		couleur = c; //border
		lastLocation = location.get();
		isAvailable = true;
		spId = 9999;
	}
	protected void hasBeenSelected(GesturalInterface gi){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			isDragged = true;
			isAvailable = false;
			couleur = App.colorsPanel[3];
			alpha = 0;
			lastSelector.take();
			spId = lastSelector.id;
			timeToMove = 250;
			followSmartPoint(gi);
		}		
	}
	public void followSmartPoint(GesturalInterface gi) {
		
		location.x = lastSelector.location.x - width/2;
		location.y = lastSelector.location.y - height/2;
		
		float distance = location.dist(lastLocation);
		//PApplet.println(distance + "    " + timeToMove);
		
		if(distance < 5 || !gi.isInPlace()){
			timeToMove -=5;
		} else {
			timeToMove +=5;
		}
		
		timeToMove = PApplet.constrain(timeToMove, 0, 250);
		
		lastLocation = location.get();
		
		if(timeToMove <=0){
			stopfollowSmartPoint();
		}
	}
	protected void stopfollowSmartPoint() {
		timeToMove = 0;
		lastSelector.free();
		isDragged = false;
		couleur = App.colorsPanel[4];
	}
	public void display(){
		pApplet.image(img, location.x, location.y);
		
		pApplet.fill(App.colorsPanel[0], alpha);
		
		int strokeWeight;
		
		if(isDragged){
			strokeWeight = 3;
		} else {
			strokeWeight = 1;
		}
		
		pApplet.strokeWeight(strokeWeight);
		pApplet.stroke(couleur);
		pApplet.rect(location.x, location.y, width, height);
		
	}
}
