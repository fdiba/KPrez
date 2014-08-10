
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.sound.midi.MidiMessage;

import ddf.minim.*;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;
import themidibus.MidiBus;
import webodrome.App;
import webodrome.Background;
import webodrome.MakeSoundScene;
import webodrome.ctrl.BehringerBCF;
import webodrome.mainctrl.GesturalInterface;
import webodrome.scene.DiaporamaScene;
import webodrome.scene.DragAndDropScene;
import webodrome.scene.ResolutionScene;
import webodrome.scene.StrokeScene;

@SuppressWarnings("serial")
public class KPrez extends PApplet {
	
	private int lowestValue;
	private int highestValue;
	private boolean switchValue;
	
	protected SimpleOpenNI context;
	protected Menu menu;
	
	private int oldSceneId;
	
	//--------- scenes --------------//
	protected DragAndDropScene dragAndDropScene; //scene 1
	protected MakeSoundScene makeSoundScene; //scene 2
	protected DiaporamaScene diaporamaScene; //scene 3
	protected ResolutionScene resolutionScene; //scene 4
	protected StrokeScene strokeScene; //scene 5
	protected StrokeScene box2DScene; //scene 6
	
	protected Background bgrd;
	
	protected GesturalInterface gi;
	
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
	
	
	protected Minim minim;
	protected AudioPlayer player;
	
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
			
			resolutionId = 0;
			App.resolution = resolutions[resolutionId];
			
			minim = new Minim(this);
			String sound = "assets/" + "kick.wav";
			player = minim.loadFile(sound);
			
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
		
			System.out.println("depthmap controllers : UP | DOWN | l to toggle" + "\n" +
							"press r for next resolution");
		}
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
		
		int sceneId = App.getSceneId();
		
		switch (sceneId) {
		case 0:
			scene0(); //accueil
			break;
		case 1:
			scene1(); //drag and drop scene
			break;
		case 2:
			scene2(); //sound scene			
			break;
		case 3:
			scene3(); //diaporama scene
			break;
		case 4:
			scene4(); //user resolution scene
			break;
		case 5:
			scene5(); //contour scene
			break;
		case 6:
			scene6(); //box2D scene
			break;
		case 7:
			scene7(); //test sound scene
			break;
		default:
			scene0();
			break;
		}
			
	}
	private void targetCloudOfPoints(){
		translate(width/2, height/2, 0);
		rotateX(radians(180));
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
		App.resolution = resolutions[resolutionId];
	}
	//--------------------------------------//
	//------------ Scenes ------------------//
	//--------------------------------------//
	private void scene0(){ //accueil
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			oldSceneId = sceneId;
		}
		
		gi.update(context);
		bgrd.update(context, gi, "userImage");
		menu.testCollision(gi);
		
		bgrd.display(context, gi);
		menu.display();
		gi.display(context);
	}
	private void scene1(){ //drag and drop scene
		
		gi.update(context);
		bgrd.update(context, gi, "depthImage");
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			dragAndDropScene = new DragAndDropScene(this);
			App.setActualScene(dragAndDropScene);
			
			oldSceneId = sceneId;
		}
		
		//-------------- draw ------------------//

		dragAndDropScene.testCollision(gi);
		
		bgrd.display(context, gi);
		
		dragAndDropScene.display();
		
		//--------------------------------//
		
		gi.display(context);
		
	}
	private void scene2(){ //sound scene
		
		gi.update(context);
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"xTrans", -5000, 5000, App.colorsPanel[0], 0, 0, -520},
					   {"yTrans", -5000, 5000, App.colorsPanel[1], 0, 1, 0},
					   {"zTrans", -5000, 5000, App.colorsPanel[2], 0, 2, 300},
					   {"rotateXangle", -360, 360, App.colorsPanel[0], 0, 3, 0},
					   {"rotateYangle", -360, 360, App.colorsPanel[1], 0, 4, 21},
					   {"rotateZangle", -360, 360, App.colorsPanel[2], 0, 5, 0} };
			
			makeSoundScene = new MakeSoundScene(this, minim, player, gi, objects);
			App.setActualScene(makeSoundScene);
			
			
			oldSceneId = sceneId;
		}
		
		//-------------- draw ------------------//
		
		makeSoundScene.resetSoundBox();

		bgrd.update(context, gi, "3D"); //2
		
		pushMatrix();
		
			targetCloudOfPoints();
			makeSoundScene.pointAndMoveInTheRightDirection();
			
			makeSoundScene.update(minim, player, gi, bgrd.depthMapRealWorld);
			
			makeSoundScene.display();
			bgrd.display(context, gi);
			gi.display(context);
			
		popMatrix();
		
		//--------------------------------//
		
		App.getActualScene().displayMenu();
		
	}
	private void scene3(){ //diaporama scene
		
		gi.update(context);
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"xTrans", -5000, 5000, App.colorsPanel[0], 0, 0, -520},
					   {"yTrans", -5000, 5000, App.colorsPanel[1], 0, 1, 0},
					   {"zTrans", -5000, 5000, App.colorsPanel[2], 0, 2, 300},
					   {"rotateXangle", -360, 360, App.colorsPanel[0], 0, 3, 0},
					   {"rotateYangle", -360, 360, App.colorsPanel[1], 0, 4, 21},
					   {"rotateZangle", -360, 360, App.colorsPanel[2], 0, 5, 0} };
			
			diaporamaScene = new DiaporamaScene(this, objects);
			App.setActualScene(diaporamaScene);
			
			oldSceneId = sceneId;
						
		}
		
		//-------------- draw ------------------//
		
		if(App.userIsTracked && diaporamaScene.counter <= 0) diaporamaScene.testScreenDisplay(context, gi);
		
		bgrd.update(context, gi, "3D");
				
		diaporamaScene.update();
		
		pushMatrix();
		
			targetCloudOfPoints();
			diaporamaScene.pointAndMoveInTheRightDirection();
			
			bgrd.display(context, gi);
			
			gi.display(context);
			
			if(diaporamaScene.screenAvailable() && App.userIsTracked) {
				diaporamaScene.displayScreen(gi);
				//gi.displayMiddlePoint();
			}
		
		popMatrix();
		
		//--------------------------------//
		
		App.getActualScene().displayMenu();
		
	}
	private void scene4(){ //user resolution scene
		
		gi.update(context);
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"xTrans", -5000, 5000, App.colorsPanel[0], 0, 0, -520},
					   {"yTrans", -5000, 5000, App.colorsPanel[1], 0, 1, 0},
					   {"zTrans", -5000, 5000, App.colorsPanel[2], 0, 2, 300},
					   {"rotateXangle", -360, 360, App.colorsPanel[0], 0, 3, 0},
					   {"rotateYangle", -360, 360, App.colorsPanel[1], 0, 4, 21},
					   {"rotateZangle", -360, 360, App.colorsPanel[2], 0, 5, 0} };
			
			resolutionScene = new ResolutionScene(this, objects);
			App.setActualScene(resolutionScene);
			
			oldSceneId = sceneId;
		}
		
		//-------------- draw ------------------//
				
		resolutionScene.update(context, gi);
				
		pushMatrix();
		
			targetCloudOfPoints();
			resolutionScene.pointAndMoveInTheRightDirection();
			
			resolutionScene.display();
			gi.display(context);
		
		popMatrix();
		
		//--------------------------------//
		
		App.getActualScene().displayMenu();
		
	}
	private void scene5(){ //contour scene
		
		gi.update(context);
		bgrd.update(context, gi,"depthImage");
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"blurRadius", 1, 200, App.colors[0], 0, 0, 2},
					   {"edgeMinNumber", 0, 1000, App.colors[1], 0, 1, 100},
					   {"distMin", 1, 100, App.colors[2], 0, 2, 10},
					   {"borderOffset", 0, 100, App.colors[3], 0, 3, 1} };
			
			strokeScene = new StrokeScene(this, objects, false);
			App.setActualScene(strokeScene);
							
			oldSceneId = sceneId;
		}
		
		//-------------- draw ------------------//
		
		strokeScene.update(bgrd.getImg()); //1
					
		bgrd.display(context, gi);
		
		strokeScene.display();
		App.getActualScene().displayMenu();
		strokeScene.displayMiniature();
		
		//--------------------------------//
	
		gi.display(context);
		
	}
	private void scene6(){ //box2D scene
		
		gi.update(context);
		bgrd.update(context, gi, "depthImage");
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"blurRadius", 1, 200, App.colors[0], 0, 0, 2},
					   {"edgeMinNumber", 0, 1000, App.colors[1], 0, 1, 100},
					   {"distMin", 1, 100, App.colors[2], 0, 2, 10},
					   {"borderOffset", 0, 100, App.colors[3], 0, 3, 1},
					   {"frameRateValue", 0, 30, App.colors[4], 0, 4, 12} };
			
			box2DScene = new StrokeScene(this, objects, true);
			App.setActualScene(box2DScene);
			
			oldSceneId = sceneId;
		}
		
		//-------------- draw ------------------//
		
		box2DScene.update(bgrd.getImg()); //1
		
		box2DScene.displayUser();
		
		box2DScene.updateAndDrawBox2D();
		
		App.getActualScene().displayMenu();

		box2DScene.displayMiniature();
		
		//--------------------------------//
		
		gi.display(context);
		
	}
	private void scene7(){ //test scene
		
		gi.update(context);
		
		//-------------- init ------------------//
		
		int sceneId = App.getSceneId();
		if (sceneId != oldSceneId) {
			
			Object[][] objects = { {"xTrans", -5000, 5000, App.colorsPanel[0], 0, 0, -520},
					   {"yTrans", -5000, 5000, App.colorsPanel[1], 0, 1, 0},
					   {"zTrans", -5000, 5000, App.colorsPanel[2], 0, 2, 300},
					   {"rotateXangle", -360, 360, App.colorsPanel[0], 0, 3, 0},
					   {"rotateYangle", -360, 360, App.colorsPanel[1], 0, 4, 21},
					   {"rotateZangle", -360, 360, App.colorsPanel[2], 0, 5, 0} };
			
			diaporamaScene = new DiaporamaScene(this, objects);
			App.setActualScene(diaporamaScene);
			
			oldSceneId = sceneId;
						
		}
		
		//-------------- draw ------------------//
		
		if(App.userIsTracked && diaporamaScene.counter <= 0) diaporamaScene.testScreenDisplay(context, gi);
		
		bgrd.update(context, gi, "3D");
				
		diaporamaScene.update();
		
		pushMatrix();
		
			targetCloudOfPoints();
			diaporamaScene.pointAndMoveInTheRightDirection();
			
			bgrd.display(context, gi);
			
			gi.display(context);
			
			if(diaporamaScene.screenAvailable() && App.userIsTracked) {
				diaporamaScene.displayScreen(gi);
				//gi.displayMiddlePoint();
			}
		
		popMatrix();
		
		//--------------------------------//
		
		App.getActualScene().displayMenu();
		
	}
	//------------- key -------------//
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
	   if(App.BCF2000 && App.getActualScene().menu != null) behringer.midiMessage(channel, number, value);
	   
	}
	//------------- mouse -------------//
	public void mouseReleased(){
		
		int sceneId = App.getSceneId();
		if(sceneId > 1)App.getActualScene().menu.resetSliders();		
		
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
