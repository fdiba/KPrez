import SimpleOpenNI.SimpleOpenNI;

public class HandControl {
	
	protected KPrez parent;
	
	protected SmartPoint rightHand;
	protected SmartPoint leftHand;
	protected SmartPoint cp;
	
	public HandControl(KPrez _parent){
		
		parent = _parent;
		
		rightHand = new SmartPoint(this);
		leftHand = new SmartPoint(this);
		cp = new SmartPoint(this);
	}
	protected void update() {
		if(parent.isTracked){
			rightHand.update(parent.userId, SimpleOpenNI.SKEL_RIGHT_HAND);
			leftHand.update(parent.userId, SimpleOpenNI.SKEL_LEFT_HAND);
		} else {
			cp.updateClosestPoint();
		}
	}
	protected void display() {
	
		if(parent.isTracked){			
			rightHand.display();
			leftHand.display();
		} else {
			cp.display();
		}	
	}
}
