package webodrome;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;
import webodrome.mainctrl.GesturalInterface;
import webodrome.scene.Scene;

public class MakeSoundScene extends Scene {
	
	private SoundBox[] boxes;
	
	public MakeSoundScene(PApplet _pApplet, Minim minim, AudioPlayer player, GesturalInterface gi, Object[][] objects) {
		
		super(_pApplet, objects);
		
		PVector[] pvectors = {new PVector(-300, -150, gi.getLowestValue() + 1000),
				 new PVector(0, -150, gi.getLowestValue() + 1000),
				 new PVector(300, -150, gi.getLowestValue() + 1000)};
		
		boxes = new SoundBox[pvectors.length];
		
		for (int i=0; i<pvectors.length; i++) {
		     boxes[i] = new SoundBox(pApplet, minim, player, pvectors[i], App.colorsPanel[i]);
		}
		
	}
	public void update(Minim minim, AudioPlayer player, GesturalInterface gi, PVector[] depthMapRealWorld){
		
		calculateHitsForEachBox(gi, depthMapRealWorld);
		
		for (SoundBox b : boxes) b.update(minim, player);
		
		menu.update(pApplet);
		
	}
	private void calculateHitsForEachBox(GesturalInterface gi, PVector[] depthMapRealWorld){

		for (int i = 0; i < depthMapRealWorld.length; i++) {

			PVector currentPoint = depthMapRealWorld[i];
		    
			if (currentPoint.z > gi.getLowestValue() && currentPoint.z < gi.getHighestValue()) {
		        		
				for (SoundBox s: boxes) s.isHit(currentPoint);
		        
			}
		 	
		}
		
	}
	public void display(){
		for (SoundBox b : boxes) b.display();		
	}
	public void pointAndMoveInTheRightDirection(){
		pApplet.translate(params.get("xTrans"), params.get("yTrans"), params.get("zTrans"));
		pApplet.rotateX(PApplet.radians(params.get("rotateXangle")));
		pApplet.rotateY(PApplet.radians(params.get("rotateYangle")));
		pApplet.rotateZ(PApplet.radians(params.get("rotateZangle")));
	}
	public void resetSoundBox(){
		for (SoundBox b : boxes) b.reset();
	}
}
