public class HandControl {
	
	private int alpha;
	protected KPrez kprez;
	
	protected SmartPoint rightSP;
	protected SmartPoint leftSP;
	protected SmartPoint cp;
	
	public HandControl(KPrez _kprez){
		
		kprez = _kprez;
		
		rightSP = new SmartPoint(kprez);
		leftSP = new SmartPoint(kprez);
		cp = new SmartPoint(kprez);
	}
	protected void update() {
		if (kprez.gi.getWorld() == "2D") {
			if(kprez.gi.isTracked){
				updateHandsLocation();
			} else {
				cp.updateClosestPoint();
			}
		} else {	
			if(kprez.gi.isTracked)updateHandsLocation();
		}
	}
	private void updateHandsLocation(){
		
		if(kprez.gi.isInPlace()){
			alpha = 255;
		} else {
			alpha = 75;
		}
		
		rightSP.update(kprez.gi.rightHand(), alpha);
		leftSP.update(kprez.gi.leftHand(), alpha);		
	}
	protected void display() {
		
		if (kprez.gi.getWorld() == "2D") {
			if(kprez.gi.isTracked){			
				rightSP.display();
				leftSP.display();
			} else {
				cp.display();
			}
		} else {
			if(kprez.gi.isTracked){			
				rightSP.display();
				leftSP.display();
			}
		}	
	}
	protected void displayExit(int timeToExit) {		
		rightSP.displayExit(timeToExit);
		leftSP.displayExit(timeToExit);
	}
}
