
import processing.core.PApplet;
import processing.core.PVector;
import webodrome.App;
import webodrome.mainctrl.Bouton;
import webodrome.mainctrl.GesturalInterface;

public class MBouton extends Bouton {
	private int sceneId;
	private String mode;
	
	public MBouton(Menu _parent, PVector loc, int _radius, int _sceneId, String _mode){
		super(_parent.parent, loc, _radius);
		sceneId = _sceneId;
		mode = _mode;
		a_speed = 10;
	}
	protected void hasBeenSelected(GesturalInterface gi){		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			alpha = 0;
			App.setSceneId(gi, sceneId, mode);
		}
	}
	protected void display() {
		float rad = PApplet.radians(PApplet.map(alpha, 0, 255, 0, 360));
		pApplet.strokeWeight(3);
		pApplet.stroke(couleur);
		pApplet.fill(255);
		pApplet.arc(location.x, location.y, width, width, 0-PApplet.HALF_PI, rad-PApplet.HALF_PI);
		pApplet.noFill();
		pApplet.ellipse(location.x, location.y, width, width);
	}
}
