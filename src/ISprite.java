import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class ISprite extends Bouton{
	
	private String path;
	private PImage img;
	protected boolean isDragged;
	private int timeToMove;
	private PVector lastLocation;
	private KPrez kprez;
	
	public ISprite(KPrez _kprez, float _x, float _y, String _path){
		
		super(_kprez, _x, _y, 75, 75, true);
		
		kprez = _kprez;
		path = _path;
		img = kprez.loadImage(path);
		width = img.width;
		height = img.height;
		couleur = kprez.colors.get(4); //border
		
		lastLocation = location.get();
	}
	protected void hasBeenSelected(){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			isDragged = true;
			couleur = kprez.colors.get(3);
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
		
		if(distance < 5 || !kprez.gi.isInPlace()){
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
		couleur = kprez.colors.get(4);
	}
	protected void display(){
		kprez.image(img, location.x, location.y);
		
		//pApplet.noFill();
		kprez.fill(kprez.colors.get(0), alpha);
		
		int strokeWeight;
		
		if(isDragged){
			strokeWeight = 3;
		} else {
			strokeWeight = 1;
		}
		
		kprez.strokeWeight(strokeWeight);
		kprez.stroke(couleur);
		kprez.rect(location.x, location.y, width, height);
		
	}
}
