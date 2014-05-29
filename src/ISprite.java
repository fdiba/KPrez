import processing.core.PImage;

public class ISprite extends Bouton{
	
	private String path;
	private PImage img;
	
	public ISprite(KPrez _kprez, float _x, float _y, String _path){
		
		super(_kprez, _x, _y, 150);
		
		path = _path;
		img = pApplet.loadImage(path);
	}
	protected void hasBeenSelected(){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			alpha = 0;
			//declencher une action
		}
		
	}
	protected void display(){
		pApplet.image(img, location.x, location.y);
		
		//pApplet.noFill();
		pApplet.fill(255, 0, 0, alpha);
		pApplet.strokeWeight(3);
		pApplet.stroke(0);
		pApplet.rect(location.x, location.y, img.width, img.height);
		
	}

}
