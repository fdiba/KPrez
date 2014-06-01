import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class ISprite extends Bouton{
	
	private String path;
	private PImage img;
	protected boolean isDragged;
	private int timeToMove;
	private PVector lastLocation;
	
	public ISprite(KPrez _kprez, float _x, float _y, String _path){
		
		super(_kprez, _x, _y, 75, 75, true);
		
		path = _path;
		img = pApplet.loadImage(path);
		width = img.width;
		height = img.height;
		couleur = pApplet.color(232, 187, 131);
		
		lastLocation = location.get();
	}
	protected void hasBeenSelected(){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			isDragged = true;
			couleur = pApplet.color(245, 215, 106);
			alpha = 0;
			lastSelector.take(this);
			timeToMove = 250;
			followSmartPoint();
		}		
	}
	protected void followSmartPoint() {
		location.x = lastSelector.location.x - width/2;
		location.y = lastSelector.location.y - height/2;
		
		float distance = PApplet.dist(location.x, location.y, lastLocation.x, lastLocation.y);
		//PApplet.println(distance + "    " + timeToMove);
		
		if(distance < 5){
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
		lastSelector.free(this);
		isDragged = false;
		couleur = pApplet.color(0);
	}
	protected void display(){
		pApplet.image(img, location.x, location.y);
		
		//pApplet.noFill();
		pApplet.fill(255, 0, 0, alpha);
		
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
