
import SimpleOpenNI.IntVector;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

public class GesturalInterface {
	
	private boolean isTakingControl;
	private KPrez kprez;
	private int takeControl;
	protected int userId;
	private int sl_user;
	private PVector rightHand2dOutsider;
	private PVector rightHandOutsider;
	protected boolean isTracked;
	private boolean isQuitting;
	
	private PVector rightHand;
	private PVector leftHand;
	private PVector middlePoint;
	private PVector torso;
	
	private PVector rightHand2d;
	private PVector leftHand2d;
	
	private int exitDistance;
	private int timeToExit;
	private int timeToExitMax;
	private String world;
	
	private boolean isInPlace;
	private int lowestValue;
	private int highestValue;
	
	protected HandControl handControl;
		
	public GesturalInterface(KPrez _kprez, int _lowestValue, int _highestValue, String _world){
		
		kprez = _kprez;
		
		initVectors();
		
		lowestValue = _lowestValue;
		highestValue = _highestValue;
		
		timeToExitMax = 24;
		timeToExit = timeToExitMax;
		handControl = new HandControl(kprez);
		takeControl = 0;
		sl_user = 0;
		world = _world;
	}
	protected void setLowestValue(int _lowestValue) {
		lowestValue = _lowestValue;
	}
	protected void setHighestValue(int _highestValue) {
		highestValue = _highestValue;
	}
	protected int getLowestValue() {
		return lowestValue;
	}
	protected int getHighestValue() {
		return highestValue;
	}
	protected PVector torso(){
		kprez.context.getJointPositionSkeleton(kprez.gi.userId, SimpleOpenNI.SKEL_TORSO, torso);
		return torso;
	}
	protected PVector middlePoint(PVector pvector1, PVector pvector2){
		middlePoint = pvector1.get();
		PVector pVector = PVector.sub(pvector2, pvector1);
		pVector.div(2);
		middlePoint.add(pVector);
		return middlePoint;
	}
	protected PVector rightHand(){
		return rightHand;
	}
	protected PVector leftHand(){
		return leftHand;
	}
	private void initVectors(){
		//-- init elsewhere ------------//
		//middlePoint = new PVector();
		
		//-- user ----------------------//
		rightHand = new PVector();
		leftHand = new PVector();
		torso = new PVector();
		
		//-- candidate -----------------//
		rightHandOutsider = new PVector();
	}
	protected void update() {
		selectAndTrackUsers();
		
		if(isTracked){
			kprez.context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
			kprez.context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
		}
		
		handControl.update();
		
		if(isTracked) isInPlace();
		
		int sceneId = kprez.sceneId();
		if(isTracked && sceneId!=0) isQuitting();
	}
	protected boolean isInPlace(){
		if(rightHand.z >= lowestValue && rightHand.z <= highestValue && leftHand.z >= lowestValue && leftHand.z <= highestValue){
			isInPlace =  true;
		} else {
			isInPlace = false;
		}
		return isInPlace;
	}
	protected String getWorld() {
		return world;
	}
	protected void setWorld(String _world) {
		world = _world;
	}
	private void isQuitting(){
		
		float distance;
		
		if(isInPlace){
		
			if(world =="2D"){
				exitDistance = 42;
				rightHand2d = handControl.rightSP.location.get();
				leftHand2d = handControl.leftSP.location.get();			
				distance = PApplet.dist(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
			} else {
				exitDistance = 150;
				distance = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
			}
			//PApplet.println(distance);
			if(distance < exitDistance){
				isQuitting = true;
				timeToExit--;
				
				if (timeToExit <= 0) {
					timeToExit = timeToExitMax;
					isQuitting = false;
					kprez.editScene(0, "2D");
				}
				
			} else {
				notQuitting();
			}
		} else {
			notQuitting();
		}
	}
	private void notQuitting(){
		isQuitting = false;
		if(timeToExit < timeToExitMax) timeToExit++;
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
		kprez.strokeWeight(2);
		kprez.stroke(kprez.colors.get(0));
		if(world == "2D"){
			kprez.line(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
		} else {
			kprez.line(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		}
	}
	private void displayControler(){
		
		float diam = 35;
		kprez.rectMode(PApplet.CENTER);
		
		kprez.pushMatrix();
		kprez.stroke(255, 0, 0);
		kprez.noFill();
		
		if(world == "2D"){
			rightHand2dOutsider = new PVector();	
			kprez.context.convertRealWorldToProjective(rightHandOutsider, rightHand2dOutsider);
			kprez.translate(rightHand2dOutsider.x, rightHand2dOutsider.y);
		} else {
			kprez.translate(rightHandOutsider.x, rightHandOutsider.y, rightHandOutsider.z);
		}
		
		kprez.rotate(takeControl);
		kprez.rect(0, 0, diam, diam);
		kprez.popMatrix();
		
	}
	private void isTakingControl(int _id, int i) {
		
		PVector headOutsider = new PVector();
		kprez.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_HEAD, headOutsider);
		
		kprez.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandOutsider);
		
		PVector leftHandOutsider = new PVector();
		kprez.context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_LEFT_HAND, leftHandOutsider);
		
		int distance = 150;
		
		if(rightHandOutsider.y > headOutsider.y + distance && leftHandOutsider.y + distance < headOutsider.y) {

			isTakingControl = true;
			takeControl++;
			
			if(takeControl > 75){
				sl_user = i;
				
				IntVector userList = new IntVector();	
				kprez.context.getUsers(userList);
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
		kprez.context.getUsers(userList);
		
		if(userList.size() > 0) {
			 
			if(sl_user >= userList.size()) sl_user = 0;
			
			userId = userList.get(sl_user);
			
			if(kprez.context.isTrackingSkeleton(userId)) {
				isTracked = true;
			} else {
				isTracked = false;
			}
						
			for (int i = 0; i < userList.size(); i++) {
				
				int otherId = userList.get(i);
				
				if(kprez.context.isTrackingSkeleton(otherId)) {
					
					if(sl_user != i) isTakingControl(otherId, i);
					
				}
			}
		}
	}
	protected PVector getMiddlePoint(){
		return middlePoint;
	}
	public void displayMiddlePoint() {
		kprez.pushMatrix();
			kprez.noStroke();
			kprez.fill(kprez.colors.get(0));
			kprez.translate(middlePoint.x, middlePoint.y, middlePoint.z);
			kprez.ellipse(0, 0, 50, 50);
		kprez.popMatrix();	
	}
}