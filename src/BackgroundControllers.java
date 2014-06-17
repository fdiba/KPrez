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
		sliders.add(new Slider(kprez, location, "rotateY", -180, 180, kprez.colors.get(1)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15), "rotateX", -180, 180, kprez.colors.get(0)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "zTrans", -5000, 5000, kprez.colors.get(2)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "xTrans", -2000, 2000, kprez.colors.get(4)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "resolution", 1, 9, kprez.colors.get(3)));
		sliders.get(sliders.size()-1).editSliderCtrlPosition(0);
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
