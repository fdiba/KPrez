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
	private PVector rightHandOutsider;
	protected boolean isTracked;
	private boolean isQuitting;
	
	private PVector rightHand;
	private PVector leftHand;
	private PVector rightHand2d;
	private PVector leftHand2d;
	
	private int exitDistance;
	private int timeToExit;
	private int timeToExitMax;
	private String world;
	
	protected HandControl handControl;
	private PVector middlePoint;
	private boolean screenAvailable;
	private PVector torso;
	
	public GesturalInterface(KPrez _parent, String _world){
		parent = _parent;
		timeToExitMax = 24;
		timeToExit = timeToExitMax;
		handControl = new HandControl(parent);
		takeControl = 0;
		sl_user = 0;
		world = _world;
		
		initVectors();
	}
	private void initVectors(){
		//-- init elsewhere ------------//
		//middlePoint = new PVector();
		//rightHand = new PVector();
		//leftHand = new PVector();
		
		//-- user ----------------------//
		torso = new PVector();
		
		//-- candidate -----------------//
		rightHandOutsider = new PVector();
	}
	protected void update() {
		selectAndTrackUsers();
		handControl.update();
		int sceneId = parent.sceneId();
		if(isTracked && sceneId!=0) isQuitting();
	}
	protected String getWorld() {
		return world;
	}
	protected void setWorld(String _world) {
		world = _world;
	}
	private void isQuitting(){
		
		float distance;
		
		if(world =="2D"){
			exitDistance = 42;
			rightHand2d = handControl.rightHand.location.get();
			leftHand2d = handControl.leftHand.location.get();			
			distance = PApplet.dist(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
		} else {
			exitDistance = 150;
			rightHand = handControl.rightHand.location3d.get();
			leftHand = handControl.leftHand.location3d.get();
			distance = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		}
		//PApplet.println(distance);
		if(distance < exitDistance){
			isQuitting = true;
			timeToExit--;
			
			if (timeToExit <= 0) {
				timeToExit = timeToExitMax;
				isQuitting = false;
				parent.editScene(0, "2D");
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
		if(isTakingControl) displayControler();
		
	}
	private void drawLine(){
		parent.strokeWeight(2);
		parent.stroke(255,0,0);
		if(world == "2D"){
			parent.line(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
		} else {
			parent.line(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		}
	}
	private void displayControler(){
		
		float diam = 35;
		parent.rectMode(PApplet.CENTER);
		
		parent.pushMatrix();
		parent.stroke(255, 0, 0);
		parent.noFill();
		
		if(world == "2D"){
			rightHand2dOutsider = new PVector();	
			parent.context.convertRealWorldToProjective(rightHandOutsider, rightHand2dOutsider);
			parent.translate(rightHand2dOutsider.x, rightHand2dOutsider.y);
		} else {
			parent.translate(rightHandOutsider.x, rightHandOutsider.y, rightHandOutsider.z);
		}
		
		parent.rotate(takeControl);
		parent.rect(0, 0, diam, diam);
		parent.popMatrix();
		
	}
	private void isTakingControl(int _id, int i) {
		
		PVector headOutsider = new PVector();
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_HEAD, headOutsider);
		
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandOutsider);
		
		PVector leftHandOutsider = new PVector();
		parent.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_LEFT_HAND, leftHandOutsider);
		
		int distance = 150;
		
		if(rightHandOutsider.y > headOutsider.y + distance && leftHandOutsider.y + distance < headOutsider.y) {

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
	public void testScreenDisplay() {
		
		float distBetweenHands = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		//PApplet.println(distance);
		
		parent.context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, torso);
		
		middlePoint = rightHand.get();
		PVector pVector = PVector.sub(leftHand, rightHand);
		pVector.div(2);
		middlePoint.add(pVector);
		
		float distFromTorso = PApplet.dist(middlePoint.x, middlePoint.y, middlePoint.z, torso.x, torso.y, torso.z);
		
		if(distBetweenHands > 750 && distBetweenHands > 850 && distFromTorso < 250 &&
		  (rightHand.y - leftHand.y < 10 || leftHand.y - rightHand.y < 10)) {
			screenAvailable = true;
		} else {
			screenAvailable = false;
		}
	}
	protected PVector getMiddlePoint(){
		return middlePoint;
	}
	protected boolean getScreenAvailable(){
		return screenAvailable;
	}
	public void displayMiddlePoint() {
		parent.pushMatrix();
			parent.noStroke();
			parent.fill(255,0,0);
			parent.translate(middlePoint.x, middlePoint.y, middlePoint.z);
			parent.ellipse(0, 0, 50, 50);
		parent.popMatrix();
		
	}
}