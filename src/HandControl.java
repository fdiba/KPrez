public class HandControl {
	
	private int alpha;
	protected KPrez parent;
	
	protected SmartPoint rightSP;
	protected SmartPoint leftSP;
	protected SmartPoint cp;
	
	public HandControl(KPrez _parent){
		
		parent = _parent;
		
		rightSP = new SmartPoint(this);
		leftSP = new SmartPoint(this);
		cp = new SmartPoint(this);
	}
	protected void update() {
		if (parent.gi.getWorld() == "2D") {
			if(parent.gi.isTracked){
				updateHandsLocation();
			} else {
				cp.updateClosestPoint();
			}
		} else {	
			if(parent.gi.isTracked)updateHandsLocation();
		}
	}
	private void updateHandsLocation(){
		
		if(parent.gi.isInPlace()){
			alpha = 255;
		} else {
			alpha = 75;
		}
		
		rightSP.update(parent.gi.rightHand(), alpha);
		leftSP.update(parent.gi.leftHand(), alpha);		
	}
	protected void display() {
		
		if (parent.gi.getWorld() == "2D") {
			if(parent.gi.isTracked){			
				rightSP.display();
				leftSP.display();
			} else {
				cp.display();
			}
		} else {
			if(parent.gi.isTracked){			
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
