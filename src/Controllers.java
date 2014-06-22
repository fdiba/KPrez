
import java.util.ArrayList;

import processing.core.PVector;

public class Controllers {
	protected KPrez kprez;
	protected PVector location;
	protected ArrayList<Slider> sliders;
	
	public Controllers(KPrez _kprez, PVector _location) {
		kprez = _kprez;
		location = _location;
	}
	protected void update(){
		
		if(kprez.mousePressed){
			PVector mousePosition = new PVector(kprez.mouseX, kprez.mouseY);
			for (Slider s: sliders) s.update(mousePosition);
		}
		
		for (Slider s: sliders) s.followMouse();
		
	}
	protected void resetSliders(){
		for (Slider s: sliders) s.reset();
	}
	protected void display(){
		for (Slider s: sliders) s.display();
	}
}