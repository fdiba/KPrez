import java.util.ArrayList;

import processing.core.PVector;
import webodrome.App;

public class BackgroundControllers extends Controllers {
	
	public BackgroundControllers(KPrez _kprez, PVector _location) {
		
		super( _kprez, _location);
		sliders = new ArrayList<Slider>();
		sliders.add(new Slider(kprez, location, "rotateY", -180, 180, App.colorsPanel[1]));
		sliders.get(sliders.size()-1).initValue(21.6f);
		kprez.rotateYangle = 21.6f;
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15), "rotateX", -180, 180, App.colorsPanel[0]));
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "zTrans", -5000, 5000, App.colorsPanel[2]));
		sliders.get(sliders.size()-1).initValue(300);
		kprez.zTrans = 300;
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "xTrans", -2000, 2000, App.colorsPanel[4]));
		sliders.get(sliders.size()-1).initValue(-520);
		kprez.xTrans = -520;
	}
}
