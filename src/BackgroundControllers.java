import java.util.ArrayList;

import processing.core.PVector;

public class BackgroundControllers {
	private KPrez kprez;
	private PVector location;
	private ArrayList<Slider> sliders;
	
	public BackgroundControllers(KPrez _kprez, PVector _location) {
		kprez = _kprez;
		location = _location;
		sliders = new ArrayList<Slider>();
		sliders.add(new Slider(kprez, location, "rotateY", -90, 90));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15), "rotateX", -90, 90));
	}
	protected void update(){
		
		if(kprez.mousePressed){
			
			PVector mousePosition = new PVector(kprez.mouseX, kprez.mouseY);
			
			//PApplet.println(kprez.random(0, 1000));
			for (Slider s: sliders) {
			     s.update(mousePosition);
			}
			
		}
		
		
		for (Slider s: sliders) {
		     s.followMouse();
		}
		
		
	}
	protected void resetSliders(){
		for (Slider s: sliders) {
		     s.reset();
		}
	}
	protected void display(){
		for (Slider s: sliders) {
		     s.display();
		}
	}
}
