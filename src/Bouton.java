import processing.core.PApplet;
import processing.core.PVector;

public class Bouton {
	
	protected PApplet pApplet;
	protected int hits;
	protected PVector location;
	protected int width;
	protected int height;
	protected int alpha;
	protected int a_speed;
	protected int couleur;
	
	protected SmartPoint lastSelector;
	
	private boolean isRect;
	
	public Bouton(PApplet _pApplet, float _x, float _y, int _width) {
		
		pApplet = _pApplet;
				
		location = new PVector(_x, _y);
		width = _width;
				
		couleur = pApplet.color(255, 133, 18);
		
		alpha = 0;
		a_speed = 5;
		
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
	
	protected void testCollision(SmartPoint _sp){
		
		if(!_sp.isTaken){
		
			if(!isRect){
				float distance = PApplet.dist(location.x, location.y, _sp.location.x, _sp.location.y);
				if(distance <= width) collisionWith(_sp);
			} else {
				if(_sp.location.x > location.x &&  _sp.location.x < location.x + width &&
				   _sp.location.y > location.y &&  _sp.location.y < location.y + height){
					collisionWith(_sp);
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
	protected void update(){
		
		if(hits>0) {
			hasBeenSelected();
		} else {
			deactivate();
		}		
	}
	protected void hasBeenSelected(){
		
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
