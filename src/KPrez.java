
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;

import ddf.minim.*;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;
import themidibus.MidiBus;
import webodrome.App;
import webodrome.ctrl.BehringerBCF;
import webodrome.scene.StrokeScene;

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

	protected StrokeScene strokeScene;
	protected StrokeScene box2DScene;
	
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
	protected int yOffset = 1;

	//private int[] resolutions = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	private int[] resolutions = {1, 2, 4, 5, 7, 9};
	private int resolutionId;
	protected int resolution;
	
	protected Minim minim;
	protected AudioPlayer player;
	
	protected ArrayList<Integer> colors;
	
	private boolean fs;
	private static Rectangle monitor;
	
	private BehringerBCF behringer;
	private MidiBus midiBus;
	

	public static void main(String[] args) {		
		
		//------- opencv -----------------------//
		/*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());*/
		
		GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = gEnvironment.getScreenDevices();
		
		if(graphicsDevices.length>1){
			GraphicsDevice graphicsDevice = graphicsDevices[1];
			GraphicsConfiguration[] gConfigurations = graphicsDevice.getConfigurations();
			monitor = gConfigurations[0].getBounds();
			PApplet.main( new String[] { "--display=1", KPrez.class.getSimpleName() });
			//PApplet.main( new String[] { "--present", KPrez.class.getSimpleName() });
		} else {
			GraphicsDevice graphicsDevice = graphicsDevices[0];
			GraphicsConfiguration[] gConfigurations = graphicsDevice.getConfigurations();
			monitor = gConfigurations[0].getBounds();
			PApplet.main(KPrez.class.getSimpleName());
		}
		
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
			resolutionId = 0;
			resolution = resolutions[resolutionId];
			
			sceneId = 0;
			//sceneId = 6;
			
			minim = new Minim(this);		
			
			//------------- behringerBCF2000 -----------//
			
			if(App.BCF2000){		
				MidiBus.list();
			    midiBus = new MidiBus(this, "BCF2000", "BCF2000");
			    behringer = new BehringerBCF(midiBus);
			    App.behringer = behringer;
			}
			  
			//------------------------------------------//
			
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
			
			if (sceneId != oldSceneId) {
				oldSceneId = sceneId;
			}
			
			
			firstScene();
			break;
		case 1:	
			
			if (sceneId != oldSceneId) {
				oldSceneId = sceneId;
			}
			
			
			gi.update();
			bgrd.update("depthImage");
			ddScene.testCollision();
			
			bgrd.display();
			ddScene.display();
			gi.display();
			break;
		case 2:
			
			if (sceneId != oldSceneId) {
				oldSceneId = sceneId;
			}
			
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
			
			if (sceneId != oldSceneId) {
				oldSceneId = sceneId;
			}
			
			gi.update();
				
			if (sceneId != oldSceneId) {
				ptp.init();
			}
			
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
			
			
			if (sceneId != oldSceneId) {	
				oldSceneId = sceneId;
			}
			
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
			
			if (sceneId != oldSceneId) {
				
				Object[][] objects = { {"blurRadius", 1, 200, App.colors[0], 0, 0, 2},
						   {"edgeMinNumber", 0, 1000, App.colors[1], 0, 1, 375},
						   {"distMin", 1, 100, App.colors[2], 0, 2, 10},
						   {"borderOffset", 0, 100, App.colors[3], 0, 3, 1} };
				
				strokeScene = new StrokeScene(this, objects, false);
				App.setActualScene(strokeScene);
								
				oldSceneId = sceneId;
			}
			
			gi.update();
			bgrd.update("depthImage");
			
			strokeScene.update(bgrd.getImg()); //1
						
			bgrd.display();
			
			strokeScene.display();
			strokeScene.displayMenu();
			strokeScene.displayMiniature();
		
			gi.display();
			
			break;
			
		case 6:
			
			if (sceneId != oldSceneId) {
				
				Object[][] objects = { {"blurRadius", 1, 200, App.colors[0], 0, 0, 2},
						   {"edgeMinNumber", 0, 1000, App.colors[1], 0, 1, 375},
						   {"distMin", 1, 100, App.colors[2], 0, 2, 10},
						   {"borderOffset", 0, 100, App.colors[3], 0, 3, 1},
						   {"frameRateValue", 0, 30, App.colors[4], 0, 4, 17} };
				
				box2DScene = new StrokeScene(this, objects, true);
				App.setActualScene(box2DScene);
				
				oldSceneId = sceneId;
			}
			
			gi.update();
			bgrd.update("depthImage");
			
			box2DScene.update(bgrd.getImg()); //1
			
			box2DScene.displayUser();
			
			box2DScene.updateAndDrawBox2D();
			
			box2DScene.displayMenu();

			box2DScene.displayMiniature();
			
			gi.display();
			
			break;
			
		default:
			firstScene();
			break;
		}
		
		
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
	//------------- MIDI ------------------//
	public void midiMessage(MidiMessage message, long timestamp, String bus_name) {
	  
	   int channel = message.getMessage()[0] & 0xFF;
	   int number = message.getMessage()[1] & 0xFF;
	   int value = message.getMessage()[2] & 0xFF;
	   
	   //PApplet.println("bus " + bus_name + " | channel " + channel + " | num " + number + " | val " + value);
	   if(App.BCF2000) behringer.midiMessage(channel, number, value);
	   
	}
	//---- mouse ----//
	public void mouseReleased(){
		
		bgrdCtrl.resetSliders();
		
		switch (sceneId) {
		case 5:
			strokeScene.menu.resetSliders();
			break;
		case 6:
			box2DScene.menu.resetSliders();
			break;
		default:
			break;
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
