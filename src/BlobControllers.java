import java.util.ArrayList;

import processing.core.PVector;

public class BlobControllers extends Controllers {
	
	private int[] colors = {-8410437, -9998215, -1849945};
	
	public BlobControllers(KPrez _kprez, PVector _location) {
		super(_kprez, _location);
		sliders = new ArrayList<Slider>();
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "frameRateValue", 0, 30, colors[0]));
		sliders.get(0).initValue(15);
		sliders.add(new Slider(kprez, new PVector(location.x, location.y + 15*sliders.size()), "yOffset", 0, 10, colors[1]));
		sliders.get(0).initValue(1);
	}

}
