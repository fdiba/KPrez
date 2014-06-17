import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;

@SuppressWarnings("serial")
public class KPrez extends PApplet {
	
	private int lowestValue;
	private int highestValue;
	private boolean switchValue;
	
	protected SimpleOpenNI context;
	protected Menu menu;
	
	private int sceneId;
	private int oldSceneId;
	
	private DDScene ddScene;
	protected SoundScene soundScene;
	
	protected Background bgrd;
	protected BackgroundControllers bgrdCtrl;
	
	protected GesturalInterface gi;
	private PointsToPics ptp;
	private float scale;
		
	protected float rotateYangle;
	protected float rotateXangle;
	
	public static void main(String[] args) {
		
		//PApplet.main(KPrez.class.getSimpleName());
		PApplet.main( new String[] { "--display=1", KPrez.class.getSimpleName() });
		//PApplet.main( new String[] { "--present", KPrez.class.getSimpleName() });
	}
	public void editScene(int _sceneId, String _mode) {
		sceneId = _sceneId;
		gi.setWorld(_mode);
	}
	protected int sceneId(){
		return sceneId;
	}

	public void setup(){
		size(640, 480, OPENGL);
		//frameRate(1);
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
			
			//sceneId = 0;
			sceneId = 2;	
			
			scale = 1.5f;
			
			menu = new Menu(this);
			
			//bureau
			lowestValue = 600;
			highestValue = 2300;
			
			//salon
			//lowestValue = 1700;
			//highestValue = 3300;
			
			//salon capture
			//lowestValue = 1700;
			//highestValue = 2500;
			
			//gi = new GesturalInterface(this, lowestValue, highestValue, "2D");
			gi = new GesturalInterface(this, lowestValue, highestValue, "3D");
			
			bgrd = new Background(this, lowestValue, highestValue, "userImage");
			bgrdCtrl = new BackgroundControllers(this, new PVector(450, 50));
			
			//scene 1
			ddScene = new DDScene(this);
			//scene 2
			soundScene = new SoundScene(this);
			//scene 3
			ptp = new PointsToPics(this);
		
		}
	}
	public void draw() {
		background(0);
		context.update();
		
		translate(width/2-640/2, height/2-480/2);
		
		switch (sceneId) {
		case 0:
			firstScene();
			break;
		case 1:	
			gi.update();
			bgrd.update("depthImage");
			ddScene.testCollision();
			
			bgrd.display();
			ddScene.display();
			gi.display();
			break;
		case 2:
			gi.update();
			soundScene.reinit(); //1
			bgrd.update("3D"); //2
			
			bgrdCtrl.update();
			
			pushMatrix();
				pointAndMoveInTheRightDirection();
				bgrd.display(); //1
				soundScene.display(); //2
				gi.display();
			popMatrix();
			
			bgrdCtrl.display();
			
			break;
		case 3:
			gi.update();
			if (sceneId != oldSceneId) ptp.init();
			if(gi.isTracked && ptp.counter <= 0) ptp.testScreenDisplay();
			bgrd.update("3D");
			bgrdCtrl.update();
			ptp.update();
			
			pushMatrix();
			
				pointAndMoveInTheRightDirection();
				
				bgrd.display();
				//ptp.display();
				gi.display();
				if(ptp.screenAvailable() && gi.isTracked) {
					ptp.displayScreen();
					gi.displayMiddlePoint();
				}
			
			popMatrix();
			bgrdCtrl.display();
			break;
		default:
			firstScene();
			break;
		}
		oldSceneId = sceneId;
	}
	private void pointAndMoveInTheRightDirection(){
		translate(width/2, height/2, -1000);
		rotateX(PApplet.radians(180));
		translate(0, 0, scale * - 1000);
		
		rotateY(radians(rotateYangle));
		rotateX(radians(rotateXangle));
	}
	private void firstScene(){
		gi.update();
		bgrd.update("userImage");
		menu.testCollision();
		
		bgrd.display();
		menu.display();
		gi.display();
	}
	public void keyPressed() {
		if (key == 'l') {
			toggleValue();
		} else if (keyCode == UP) {
			setSelectedValue(+50);
		} else if (keyCode == DOWN) {
			setSelectedValue(-50);
		}
	}
	public void mouseReleased(){
		bgrdCtrl.resetSliders();
	}
	protected void toggleValue() {
		switchValue = !switchValue;
	}
	protected void setSelectedValue(int value) {		
		
		if(switchValue){
			lowestValue += value;
			lowestValue = PApplet.constrain(lowestValue, 0, highestValue-100);
			bgrd.setLowestValue(lowestValue);
			gi.setLowestValue(lowestValue);
			PApplet.println(lowestValue);
		} else {
			highestValue += value;
			highestValue = PApplet.constrain(highestValue, lowestValue+100, 7000);
			bgrd.setHighestValue(highestValue);
			gi.setHighestValue(highestValue);
			PApplet.println(highestValue);
		}
	}
	protected int getSelectedValue() {
		if(switchValue) {
			return lowestValue;
		} else {
			return highestValue;
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
