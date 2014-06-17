import processing.core.PApplet;
import processing.core.PVector;

public class SliderController {
	private KPrez kprez;
	protected PVector location;
	protected int width;
	public SliderController(KPrez _kprez, PVector _location) {
		kprez = _kprez;
		width = 10;
		location = _location.get();
		location.y += width/2;
		
	}
	protected void update(){
		
	}
	protected void display(){
		
		kprez.rectMode(PApplet.CENTER);
		kprez.noStroke();
		kprez.fill(255);
		kprez.rect(location.x, location.y, width, width);
	}

}
