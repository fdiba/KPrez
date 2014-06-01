import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

@SuppressWarnings("serial")
public class KPrez extends PApplet {
	
	protected SimpleOpenNI context;
	protected Menu menu;
	
	private int sceneId;
	private DDScene ddScene;
	
	protected Background bgrd;
	
	protected GesturalInterface gi;
	private PointsToPics ptp;
	
	public static void main(String[] args) {
		
		//PApplet.main(KPrez.class.getSimpleName());
		PApplet.main( new String[] { "--display=1", KPrez.class.getSimpleName() });
		//PApplet.main( new String[] { "--present", KPrez.class.getSimpleName() });
	}
	public void editScene(int _sceneId) {
		sceneId = _sceneId;
	}
	protected int sceneId(){
		return sceneId;
	}

	public void setup(){
		size(640*2, 520, OPENGL);
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
			
			//bureau
			bgrd = new Background(this, 600, 2300, "userImage");
			//salon
			//bgrd = new Background(this, 1700, 3300, "userImage");
						
			//scene 1
			ddScene = new DDScene(this);
			ptp = new PointsToPics(this);
		
		}
	}
	public void draw() {
		background(0);
		context.update();
		
		translate(width/2-640/2, height/2-480/2);
		
		gi.update();
		
		switch (sceneId) {
		case 0:
			firstScene();
			break;
		case 1:	
			bgrd.update("depthImage");
			ddScene.testCollision();
			
			bgrd.display();
			ddScene.display();
			gi.display();
			break;
		case 3:
			bgrd.update("3D");
			ptp.update();
			
			bgrd.display();
			ptp.display();
			break;
		default:
			firstScene();
			break;
		}
	}
	private void firstScene(){
		bgrd.update("userImage");
		menu.testCollision();
		
		bgrd.display();
		menu.display();
		gi.display();
	}
	public void keyPressed() {
		if (key == 'l') {
			bgrd.toggleValue();
		} else if (keyCode == UP) {
			bgrd.setSelectedValue(+50);
		} else if (keyCode == DOWN) {
			bgrd.setSelectedValue(-50);
		}
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
