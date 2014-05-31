import SimpleOpenNI.IntVector;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

public class GesturalInterface {
	
	private boolean isTakingControl;
	private KPrez parent;
	private int takeControl;
	protected int userId;
	private int sl_user;
	private PVector rightHand2d;
	protected boolean isTracked;
	
	public GesturalInterface(KPrez _parent){
		parent = _parent;
		takeControl = 0;
		sl_user = 0;
	}
	protected void update() {
		
		
	}
	protected void display() {
		if(isTakingControl) displayControler();
	}
	private void displayControler(){
		
		float diam = 35;
		
		parent.rectMode(PApplet.CENTER);
		parent.pushMatrix();
		parent.stroke(255, 0, 0);
		parent.noFill();
			//fill(255, 0, 0, map(takeControl, 0, 75, 50, 255));
			parent.translate(rightHand2d.x, rightHand2d.y);
			parent.rotate(takeControl);
			parent.rect(0, 0, diam, diam);
			parent.popMatrix();
		
	}
	private void isTakingControl(int _id, int i) {
		
		PVector head = new PVector();
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_HEAD, head);
		PVector rightHand = new PVector();
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
		PVector leftHand = new PVector();
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
		
		rightHand2d = new PVector();	
		parent.context.convertRealWorldToProjective(rightHand, rightHand2d);
		
		int distance = 150;
						
		if(rightHand.y > head.y + distance && leftHand.y + distance < head.y) {

			isTakingControl = true;
			takeControl++;
			
			if(takeControl > 75){
				sl_user = i;
				
				IntVector userList = new IntVector();	
				parent.context.getUsers(userList);
				userId = userList.get(sl_user);
				
				takeControl = 0;
				isTakingControl = false;
			}
		} else {
			isTakingControl = false;
		}
	}
	protected void selectAndTrackUsers() {
		
		IntVector userList = new IntVector();	
		parent.context.getUsers(userList);
		
		if(userList.size() > 0) {
			 
			if(sl_user >= userList.size()) sl_user = 0;
			
			userId = userList.get(sl_user);
			
			if(parent.context.isTrackingSkeleton(userId)) {
				isTracked = true;
			} else {
				isTracked = false;
			}
						
			for (int i = 0; i < userList.size(); i++) {
				
				int otherId = userList.get(i);
				
				if(parent.context.isTrackingSkeleton(otherId)) {
					
					if(sl_user != i) isTakingControl(otherId, i);
					
				}
			}
		}
	}
}