import processing.core.PVector;

public class SoundScene {
	
	private KPrez kprez;
	protected SoundBox[] boxes;
	
	public SoundScene(KPrez _parent) {
		kprez = _parent;
		int[] colors = {kprez.color(255, 0, 0), kprez.color(0, 255, 0), kprez.color(0, 0, 255)};
		PVector[]pvectors = {new PVector(-300, 0, kprez.gi.getLowestValue() + 1000),
							 new PVector(0, 0, kprez.gi.getLowestValue() + 1000),
							 new PVector(300, 0, kprez.gi.getLowestValue() + 1000)};
		
		boxes = new SoundBox[pvectors.length];
		
		for (int i=0; i<pvectors.length; i++) {
		     boxes[i] = new SoundBox(_parent, pvectors[i], colors[i]);
		}
	}
	
	protected void reinit(){
		for (SoundBox b : boxes) b.reinit();
	}
	protected void display(){
		for (SoundBox b : boxes) b.display();		
	}
}