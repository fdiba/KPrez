import processing.core.PApplet;
import processing.core.PVector;

public class Bouton {
	
	private KPrez kprez;
	protected int hits;
	protected PVector location;
	protected int width;
	protected int height;
	protected int alpha;
	protected int a_speed;
	protected int couleur;
	
	protected SmartPoint lastSelector;
	
	private boolean isRect;
	
	public Bouton(KPrez _kprez, float _x, float _y, int _width) {
		kprez = _kprez;
		location = new PVector(_x, _y);
		width = _width;
		couleur = kprez.colors.get(3);
		alpha = 0;
		a_speed = 5;
	}
	
	public Bouton(KPrez _kprez, float _x, float _y, int _width, int _height, boolean _isRect) {
		
		kprez = _kprez;
				
		location = new PVector(_x, _y);
		width = _width;
		height = _height;
		
		couleur = kprez.color(255, 133, 18);
		
		alpha = 0;
		a_speed = 5;
		
		isRect = _isRect;
		
	}
	
	protected void testCollision(SmartPoint _sp){
		
		if(!_sp.isTaken){
		
			if(!isRect){
				float distance = PApplet.dist(location.x, location.y, _sp.location.x, _sp.location.y);
				if(distance <= width/2 && kprez.gi.isInPlace()) collisionWith(_sp);
			} else {
				if(_sp.location.x > location.x &&  _sp.location.x < location.x + width &&
				   _sp.location.y > location.y &&  _sp.location.y < location.y + height && kprez.gi.isInPlace()){
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
		kprez.strokeWeight(3);
		kprez.stroke(couleur);
		kprez.fill(255, 255, 255, alpha);
		kprez.ellipse(location.x, location.y, width, width);
	}
}
