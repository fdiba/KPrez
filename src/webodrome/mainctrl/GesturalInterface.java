package webodrome.mainctrl;

import processing.core.PApplet;
import processing.core.PVector;
import webodrome.App;
import SimpleOpenNI.IntVector;
import SimpleOpenNI.SimpleOpenNI;

public class GesturalInterface {
	
	private boolean isTakingControl;
	private PApplet pApplet;
	private int takeControl;
	protected int userId;
	private int sl_user;
	private PVector rightHand2dOutsider;
	private PVector rightHandOutsider;
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
	
	public HandControl handControl;
		
	public GesturalInterface(PApplet _pApplet, int _lowestValue, int _highestValue, String _world){
		
		pApplet = _pApplet;
		
		initVectors();
		
		lowestValue = _lowestValue;
		highestValue = _highestValue;
		
		timeToExitMax = 24;
		timeToExit = timeToExitMax;
		handControl = new HandControl(pApplet);
		takeControl = 0;
		sl_user = 0;
		world = _world;
	}
	public void setLowestValue(int _lowestValue) {
		lowestValue = _lowestValue;
	}
	public void setHighestValue(int _highestValue) {
		highestValue = _highestValue;
	}
	public int getLowestValue() {
		return lowestValue;
	}
	public int getHighestValue() {
		return highestValue;
	}
	public PVector getTorsoPos(SimpleOpenNI context, GesturalInterface gi){
		context.getJointPositionSkeleton(gi.userId, SimpleOpenNI.SKEL_TORSO, torso);
		return torso;
	}
	public PVector middlePoint(PVector pvector1, PVector pvector2){
		middlePoint = pvector1.get();
		PVector pVector = PVector.sub(pvector2, pvector1);
		pVector.div(2);
		middlePoint.add(pVector);
		return middlePoint;
	}
	public PVector getRightHand(){
		return rightHand;
	}
	public PVector getLeftHand(){
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
	public void update(SimpleOpenNI context) {
		
		selectAndTrackUsers(context);
		
		if(App.userIsTracked){
			context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
			context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
		}
		
		handControl.update(context, this);
		
		if(App.userIsTracked) isInPlace();
		
		int sceneId = App.getSceneId();
		if(App.userIsTracked && sceneId!=0) isQuitting();
	}
	public boolean isInPlace(){
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
	public void setWorld(String _world) {
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
					App.setSceneId(this, 0, "2D");
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
	public void display(SimpleOpenNI context, GesturalInterface gi) {
		
		if(isQuitting){
			drawLine();
			handControl.displayExit(gi, timeToExit*3);
		} else {
			handControl.display(gi);
		}
		if(isTakingControl) displayControler(context);
		
	}
	private void drawLine(){
		pApplet.strokeWeight(2);
		pApplet.stroke(App.colorsPanel[0]);
		if(world == "2D"){
			pApplet.line(rightHand2d.x, rightHand2d.y, leftHand2d.x, leftHand2d.y);
		} else {
			pApplet.line(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		}
	}
	private void displayControler(SimpleOpenNI context){
		
		float diam = 35;
		pApplet.rectMode(PApplet.CENTER);
		
		pApplet.pushMatrix();
		pApplet.stroke(255, 0, 0);
		pApplet.noFill();
		
		if(world == "2D"){
			rightHand2dOutsider = new PVector();	
			context.convertRealWorldToProjective(rightHandOutsider, rightHand2dOutsider);
			pApplet.translate(rightHand2dOutsider.x, rightHand2dOutsider.y);
		} else {
			pApplet.translate(rightHandOutsider.x, rightHandOutsider.y, rightHandOutsider.z);
		}
		
		pApplet.rotate(takeControl);
		pApplet.rect(0, 0, diam, diam);
		pApplet.popMatrix();
		
	}
	private void isTakingControl(SimpleOpenNI context, int _id, int i) {
		
		PVector headOutsider = new PVector();
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_HEAD, headOutsider);
		
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandOutsider);
		
		PVector leftHandOutsider = new PVector();
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_LEFT_HAND, leftHandOutsider);
		
		int distance = 150;
		
		if(rightHandOutsider.y > headOutsider.y + distance && leftHandOutsider.y + distance < headOutsider.y) {

			isTakingControl = true;
			takeControl++;
			
			if(takeControl > 75){
				sl_user = i;
				
				IntVector userList = new IntVector();	
				context.getUsers(userList);
				userId = userList.get(sl_user);
				
				takeControl = 0;
				isTakingControl = false;
			}
			
		} else {
			isTakingControl = false;
		}
	}
	private void selectAndTrackUsers(SimpleOpenNI context) {
		
		IntVector userList = new IntVector();	
		context.getUsers(userList);
		
		if(userList.size() > 0) {
			 
			if(sl_user >= userList.size()) sl_user = 0;
			
			userId = userList.get(sl_user);
			
			if(context.isTrackingSkeleton(userId)) {
				App.userIsTracked = true;
			} else {
				App.userIsTracked = false;
			}
						
			for (int i = 0; i < userList.size(); i++) {
				
				int otherId = userList.get(i);
				
				if(context.isTrackingSkeleton(otherId)) {
					
					if(sl_user != i) isTakingControl(context, otherId, i);
					
				}
			}
		}
	}
	public PVector getMiddlePoint(){
		return middlePoint;
	}
	public void displayMiddlePoint() {
		pApplet.pushMatrix();
			pApplet.noStroke();
			pApplet.fill(App.colorsPanel[0]);
			pApplet.translate(middlePoint.x, middlePoint.y, middlePoint.z);
			pApplet.ellipse(0, 0, 50, 50);
		pApplet.popMatrix();	
	}
}
