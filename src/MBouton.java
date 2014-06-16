import processing.core.PApplet;

public class MBouton extends Bouton {
	private KPrez kprez;
	private int sceneId;
	private String mode;
	
	public MBouton(Menu _parent, float _x, float _y, int _radius, int _sceneId, String _mode){
		super(_parent.parent, _x, _y, _radius);
		kprez = _parent.parent;
		sceneId = _sceneId;
		mode = _mode;
		a_speed = 10;
	}

	protected void hasBeenSelected(){
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			alpha = 0;
			kprez.editScene(sceneId, mode);
		}
	}
	protected void display() {
		float rad = PApplet.radians(PApplet.map(alpha, 0, 255, 0, 360));
		kprez.strokeWeight(3);
		kprez.stroke(couleur);
		kprez.fill(255);
		kprez.arc(location.x, location.y, width, width, 0-PApplet.HALF_PI, rad-PApplet.HALF_PI);
		kprez.noFill();
		kprez.ellipse(location.x, location.y, width, width);
	}
}
