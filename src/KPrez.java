import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

@SuppressWarnings("serial")
public class KPrez extends PApplet {
	
	protected SimpleOpenNI context;
	protected Menu menu;
	protected HandControl handControl;
	
	private int sceneId;
	private DDScene ddScene;
	
	private Background bgrd;
	
	
	protected GesturalInterface gi;
	
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
			
			gi = new GesturalInterface(this);
			
			menu = new Menu(this);
			handControl = new HandControl(this);
			bgrd = new Background(this, 610, 8025, "userImage");
						
			//scene 1
			ddScene = new DDScene(this);
		
		}
	}
	public void draw() {
		background(0);
		context.update();
		
		translate(width/2-640/2, height/2-480/2);
		
		gi.selectAndTrackUsers();
		
		switch (sceneId) {
		case 0:
			firstScene();
			break;
		case 1:
			handControl.update();
			
			if(bgrd.imgType != "depthImage") bgrd.imgType = "depthImage";
			
			bgrd.update();
			gi.update();
			ddScene.testCollision();
			
			bgrd.display();
			ddScene.display();
			handControl.display();
			gi.display();
			
			break;
		default:
			firstScene();
			break;
		}
	}
	private void firstScene(){
		handControl.update();			
		bgrd.update();
		gi.update();
		menu.testCollision();
		
		bgrd.display();
		menu.display();
		handControl.display();
		gi.display();
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
