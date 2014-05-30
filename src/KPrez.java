import SimpleOpenNI.IntVector;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

@SuppressWarnings("serial")
public class KPrez extends PApplet {
	
	protected SimpleOpenNI context;
	protected Menu menu;
	protected HandControl handControl;
	
	private int sl_user;
	//private IntVector userList;
	private int sceneId;
	private DDScene ddScene;
	private int takeControl;
	
	protected int userId;
	
	public boolean isTracked;
	
	private Background bgrd;
	private boolean isTakingControl;
	private PVector rightHand2d;
	
	public static void main(String[] args) {
		
		PApplet.main(KPrez.class.getSimpleName());
		//PApplet.main( new String[] { "--present", KPrez.class.getSimpleName() });
	}
	public void editScene(int _sceneId) {
		sceneId = _sceneId;
	}
	public void setup(){
		size(640*2, 520);
		//size(displayWidth, displayHeight);
		context = new SimpleOpenNI(this);
		if (context.isInit() == false) {
			println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
		    exit();
		    return;
		} else {
		
			context.setMirror(true);
			context.enableDepth();		
			context.enableUser();
			
			sceneId = 0;
			//sceneId = 1;
			
			menu = new Menu(this);
			handControl = new HandControl(this);
			bgrd = new Background(this, 610, 2025, "userImage");
			
			//userList = new IntVector();
			takeControl = 0;
			sl_user = 0;
			
			//scene 1
			ddScene = new DDScene(this);
		
		}
		
	}
	public void draw() {
		background(0);
		context.update();
		
		translate(width/2-640/2, height/2-480/2);
		
		selectAndTrackUsers();
		
		switch (sceneId) {
		case 0:
			firstScene();
			break;
		case 1:
			handControl.update();
			
			if(bgrd.imgType != "depthImage") bgrd.imgType = "depthImage";
			
			bgrd.update();
			ddScene.testCollision();
			
			bgrd.display();
			ddScene.display();
			handControl.display();
			if(isTakingControl)displayControler();
			break;
		default:
			firstScene();
			break;
		}
	}
	private void firstScene(){
		handControl.update();			
		bgrd.update();
		menu.testCollision();
		
		bgrd.display();
		menu.display();
		handControl.display();
		if(isTakingControl)displayControler();
	}
	private void selectAndTrackUsers() {
		
		IntVector userList = new IntVector();	
		context.getUsers(userList);
		
		if(userList.size() > 0) {
			 
			if(sl_user >= userList.size()) sl_user = 0;
			
			userId = userList.get(sl_user);
			
			if(context.isTrackingSkeleton(userId)) {
				isTracked = true;
			} else {
				isTracked = false;
			}
						
			for (int i = 0; i < userList.size(); i++) {
				
				int otherId = userList.get(i);
				
				if(context.isTrackingSkeleton(otherId)) {
					
					if(sl_user != i) isTakingControl(otherId, i);
					
				}
			}
		}
	}
	private void isTakingControl(int _id, int i) {
		
		PVector head = new PVector();
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_HEAD, head);
		PVector rightHand = new PVector();
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
		PVector leftHand = new PVector();
		context.getJointPositionSkeleton(_id, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
		
		rightHand2d = new PVector();	
		context.convertRealWorldToProjective(rightHand, rightHand2d);
		
		int distance = 150;
						
		if(rightHand.y > head.y + distance && leftHand.y + distance < head.y) {

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
	private void displayControler(){
		
		float diam = 35;
		
		rectMode(CENTER);
		pushMatrix();
			stroke(255, 0, 0);
			noFill();
			//fill(255, 0, 0, map(takeControl, 0, 75, 50, 255));
			translate(rightHand2d.x, rightHand2d.y);
			rotate(takeControl);
			rect(0, 0, diam, diam);
		popMatrix();
		
	}
	
	// SimpleOpenNI events
	public void onNewUser(SimpleOpenNI curContext, int userId) {
	  println("onNewUser - userId: " + userId);
	  println("\tstart tracking skeleton");
	  curContext.startTrackingSkeleton(userId);
	}

	public void onLostUser(SimpleOpenNI curContext, int userId) {
	  println("onLostUser - userId: " + userId);
	}

	public void onVisibleUser(SimpleOpenNI curContext, int userId) {
	  //println("onVisibleUser - userId: " + userId);
	}

}
