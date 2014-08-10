package webodrome.mainctrl;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import webodrome.App;

public class HandControl {
	
	private int alpha;
	protected PApplet pApplet;
	
	public SmartPoint rightSP;
	public SmartPoint leftSP;
	public SmartPoint cp;
	
	public HandControl(PApplet _pApplet){
		
		pApplet = _pApplet;
		
		rightSP = new SmartPoint(pApplet, 0);
		leftSP = new SmartPoint(pApplet, 1);
		cp = new SmartPoint(pApplet, 2);
	}
	protected void update(SimpleOpenNI context, GesturalInterface gi) {
		if (gi.getWorld() == "2D") {
			if(App.userIsTracked){
				updateHandsLocation(context, gi);
			} else {
				cp.updateClosestPoint(context, gi);
			}
		} else {	
			if(App.userIsTracked)updateHandsLocation(context, gi);
		}
	}
	private void updateHandsLocation(SimpleOpenNI context, GesturalInterface gi){
		
		if(gi.isInPlace()){
			alpha = 255;
		} else {
			alpha = 75;
		}
		
		rightSP.update(context, gi, gi.getRightHand(), alpha);
		leftSP.update(context, gi, gi.getLeftHand(), alpha);		
	}
	protected void display(GesturalInterface gi) {
		
		if (gi.getWorld() == "2D") {
			if(App.userIsTracked){			
				rightSP.display(gi);
				leftSP.display(gi);
			} else {
				cp.display(gi);
			}
		} else {
			if(App.userIsTracked){			
				rightSP.display(gi);
				leftSP.display(gi);
			}
		}	
	}
	protected void displayExit(GesturalInterface gi, int timeToExit) {		
		rightSP.displayExit(gi, timeToExit);
		leftSP.displayExit(gi, timeToExit);
	}
}
