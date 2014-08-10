package webodrome.mainctrl;

import processing.core.PApplet;
import processing.core.PVector;
import webodrome.App;

public class Bouton {
	
	protected PApplet pApplet;
	public int hits;
	public PVector location;
	public int width;
	protected int height;
	protected int alpha;
	protected int a_speed;
	protected int couleur;
	
	protected SmartPoint lastSelector;
	
	private boolean isRect;
	protected boolean isAvailable;
	protected int spId;
	
	public Bouton(PApplet _pApplet, PVector loc, int _width) { //cercle
		pApplet = _pApplet;	
		location = loc.get();
		width = _width;
		couleur = App.colorsPanel[3];
		alpha = 0;
		a_speed = 5;
		isAvailable = true;
	}
	public Bouton(PApplet _pApplet, float _x, float _y, int _width, int _height, boolean _isRect) {
		pApplet = _pApplet;
		location = new PVector(_x, _y);
		width = _width;
		height = _height;
		couleur = pApplet.color(255, 133, 18);
		alpha = 0;
		a_speed = 5;
		isRect = _isRect;
	}
	
	public void testCollision(GesturalInterface gi, SmartPoint _sp){
		
		if(!_sp.isTaken){
			
			float distance = PApplet.dist(location.x, location.y, _sp.location.x, _sp.location.y);
			
			if(!isRect){
				if(distance <= width/2 && gi.isInPlace()) collisionWith(_sp);
			} else {
				//drag and drop scene
				if(_sp.location.x > location.x &&  _sp.location.x < location.x + width &&
				   _sp.location.y > location.y &&  _sp.location.y < location.y + height && gi.isInPlace() && isAvailable){
					collisionWith(_sp);
				} else if (distance >= width && _sp.id == spId && !isAvailable) {
					isAvailable = true;
				}
			}
		}
	}
	protected void collisionWith(SmartPoint _sp){
		hits++;
		lastSelector = _sp;
	}
	protected void deactivate(){
		if(alpha>0)alpha -= a_speed;
		if(alpha<0)alpha = 0;
	}
	public void update(GesturalInterface gi){ //watch class that extend it!!
		
		if(hits>0) {
			hasBeenSelected(gi);
		} else {
			deactivate();
		}		
	}
	protected void hasBeenSelected(GesturalInterface gi){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			alpha = 0;
			//declencher une action
		}
	}
	protected void display() {
		pApplet.strokeWeight(3);
		pApplet.stroke(couleur);
		pApplet.fill(255, 255, 255, alpha);
		pApplet.ellipse(location.x, location.y, width, width);
	}
}

