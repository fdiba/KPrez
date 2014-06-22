
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.lang.model.element.Element;
import javax.management.monitor.Monitor;

import ddf.minim.*;
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
	private FaceScene fScene;
	
	protected BlobScene bScene;
	protected BlobControllers blobCtrl;
	
	protected Background bgrd;
	protected BackgroundControllers bgrdCtrl;
	
	protected GesturalInterface gi;
	private PointsToPics ptp;
	
	protected float rotateYangle;
	protected float rotateXangle;
	protected float zTrans;
	protected float xTrans;

	//protected float distance;
	protected int frameRateValue = 15;

	//private int[] resolutions = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	private int[] resolutions = {1, 2, 4, 5, 7, 9};
	private int resolutionId;
	protected int resolution;
	
	protected Minim minim;
	protected AudioPlayer player;
	
	protected ArrayList<Integer> colors;
	
	private boolean fs;
	private static Rectangle monitor;

	public static void main(String[] args) {
		
		GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = gEnvironment.getScreenDevices();
		
		
		if(graphicsDevices.length>1){
				
			GraphicsDevice graphicsDevice = graphicsDevices[1];
			GraphicsConfiguration[] gConfigurations = graphicsDevice.getConfigurations();
			monitor = gConfigurations[0].getBounds();
		}
		
		//PApplet.main(KPrez.class.getSimpleName());
		PApplet.main( new String[] { "--display=1", KPrez.class.getSimpleName() });
		//PApplet.main( new String[] { "--present", KPrez.class.getSimpleName() });
		
		//String libPathProperty = System.getProperty("java.library.path");
        //System.out.println(libPathProperty);
		
	}
	public void editScene(int _sceneId, String _mode) {
		sceneId = _sceneId;
		gi.setWorld(_mode);
	}
	protected int sceneId(){
		return sceneId;
	}
	public void setup(){
		
		fs = false;
		
		if(fs){
			size(monitor.width, monitor.height, OPENGL);
		} else {
			size(640, 480, OPENGL);
		}
		
		//size(640, 480, OPENGL);
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
			context.enableRGB();
			
			setColors();
			resolutionId = 1;
			resolution = resolutions[resolutionId];
			
			sceneId = 0;
			//sceneId = 5;
			
			minim = new Minim(this);		
			menu = new Menu(this);
			
			//bureau
			lowestValue = 950;
			highestValue = 2300;
			
			//salon
			//lowestValue = 1700;
			//highestValue = 3300;
			
			//salon capture
			//lowestValue = 1500;
			//highestValue = 2500;
						
			gi = new GesturalInterface(this, lowestValue, highestValue, "2D");
			//gi = new GesturalInterface(this, lowestValue, highestValue, "3D");
			
			bgrd = new Background(this, "userImage");
			bgrdCtrl = new BackgroundControllers(this, new PVector(450, 50));
			
			//scene 1
			ddScene = new DDScene(this);
			//scene 2
			soundScene = new SoundScene(this);
			//scene 3
			ptp = new PointsToPics(this);
			//scene 4
			fScene = new FaceScene(this);
			//scene 4
			bScene = new BlobScene(this);
			blobCtrl = new BlobControllers(this, new PVector(450, 50));
		
			System.out.println("depthmap controllers : UP | DOWN | l to toggle" + "\n" +
							"press n for next resolution");
		}
	}
	private void setColors(){
		colors = new ArrayList<Integer>();
		colors.add(color(240, 65, 50)); //red
		colors.add(color(135, 205, 137)); //green
		colors.add(color(40, 135, 145)); //blue green
		colors.add(color(252, 177, 135)); //orange
		colors.add(color(15, 65, 85)); //dark blue
	}
	public void init() {
		frame.removeNotify();
		frame.setUndecorated(true);
		super.init();
	} 
	public void draw() {
		
		if(fs){
			frame.setLocation(monitor.x, monitor.y);
		} else {
			frame.setLocation(monitor.x+monitor.width/2-width/2, monitor.y+monitor.height/2-height/2);
		}
		frame.setAlwaysOnTop(true); 
		  
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
				soundScene.update(); //2 update
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
		case 4:
			gi.update();
			fScene.update();
			bgrdCtrl.update();
			pushMatrix();
				pointAndMoveInTheRightDirection();
				fScene.display();
				gi.display();
			popMatrix();
			bgrdCtrl.display();
			break;
		case 5:
			gi.update();
			bgrd.update("depthImage");
			
			bScene.updateK(); //1
			blobCtrl.update();
			
			bgrd.display();

			bScene.displayK(false, true);
			blobCtrl.display();			
		
			gi.display();
			
			break;
		case 6:
			gi.update();
			bgrd.update("depthImage");
			bScene.update(); //1

			//bgrd.display();
			bScene.displayK(false, true);
						
			bScene.displayUser();
			
			bScene.updateAndDrawBox2D(); //2
			
			gi.display();
			break;
		default:
			firstScene();
			break;
		}
		oldSceneId = sceneId;
	}
	private void pointAndMoveInTheRightDirection(){
		
		translate(width/2 + xTrans, height/2, zTrans);
		rotateX(radians(180));
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
	protected void toggleValue() {
		switchValue = !switchValue;
	}
	protected void setSelectedValue(int value) {		
		
		if(switchValue){
			lowestValue += value;
			lowestValue = PApplet.constrain(lowestValue, 0, highestValue-100);
			gi.setLowestValue(lowestValue);
			PApplet.println(lowestValue);
		} else {
			highestValue += value;
			highestValue = PApplet.constrain(highestValue, lowestValue+100, 7000);
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
	private void nextResolution(){
		
		resolutionId++;
		if(resolutionId >= resolutions.length) resolutionId = 0;
		resolution = resolutions[resolutionId];
		
	}
	//---- key ----//
	public void keyPressed() {
		if (key == 'l') {
			toggleValue();
		} else if (key == 'r') {
			nextResolution();
		}else if (keyCode == UP) {
			setSelectedValue(+50);
		} else if (keyCode == DOWN) {
			setSelectedValue(-50);
		}
	}
	//---- mouse ----//
	public void mouseReleased(){
		bgrdCtrl.resetSliders();
		blobCtrl.resetSliders();
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
