import java.util.ArrayList;

import processing.core.PVector;

public class BackgroundControllers extends Controllers {
	
	public BackgroundControllers(KPrez _kprez, PVector _location) {
		
		super( _kprez, _location);
		sliders = new ArrayList<Slider>();
		sliders.add(new Slider(kprez, location, "rotateY", -180, 180, kprez.colors.get(1)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15), "rotateX", -180, 180, kprez.colors.get(0)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "zTrans", -5000, 5000, kprez.colors.get(2)));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "xTrans", -2000, 2000, kprez.colors.get(4)));
	}
}
