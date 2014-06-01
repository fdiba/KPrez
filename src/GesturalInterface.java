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
	private PVector rightHand2dOutsider;
	protected boolean isTracked;
	private boolean isQuitting;
	private PVector rightHand2d;
	private PVector leftHand2d;
	private int exitDistance;
	private int timeToExit;
	private int timeToExitMax;
	
	protected HandControl handControl;
	
	public GesturalInterface(KPrez _parent){
		parent = _parent;
		timeToExitMax = 24;
		timeToExit = timeToExitMax;
		handControl = new HandControl(parent);
		takeControl = 0;
		sl_user = 0;
		exitDistance = 42;
	}
	protected void update() {
		
		selectAndTrackUsers();
		handControl.update();
		
		int sceneId = parent.sceneId();
		if(isTracked && sceneId!=0) isQuitting();
		
	}
	private void isQuitting(){
		
		rightHand2d = handControl.rightHand.location.get();
		leftHand2d = handControl.leftHand.location.get();
		
		//float distance = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		float distance = PApplet.dist(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);

		if(distance < exitDistance){
			isQuitting = true;
			timeToExit--;
			
			if (timeToExit <= 0) {
				timeToExit = timeToExitMax;
				isQuitting = false;
				parent.editScene(0);
			}
			
		} else {
			isQuitting = false;
			if(timeToExit < timeToExitMax) timeToExit++;
		}
	}
	protected void display() {
		
		if(isQuitting){
			drawLine();
			handControl.displayExit(timeToExit*3);
		} else {
			handControl.display();
		}
		if(isTakingControl) displayControler(rightHand2dOutsider);
	}
	private void drawLine(){
		parent.strokeWeight(2);
		parent.stroke(255,0,0);
		parent.line(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
	}
	private void displayControler(PVector pVector){
		
		float diam = 35;
		
		parent.rectMode(PApplet.CENTER);
		parent.pushMatrix();
			parent.stroke(255, 0, 0);
			parent.noFill();
			//fill(255, 0, 0, map(takeControl, 0, 75, 50, 255));
			parent.translate(pVector.x, pVector.y);
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
		
		rightHand2dOutsider = new PVector();	
		parent.context.convertRealWorldToProjective(rightHand, rightHand2dOutsider);
		
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
	private void selectAndTrackUsers() {
		
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