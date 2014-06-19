
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Background {
	
	protected String imgType;
	protected KPrez kprez;
	private PImage img;
	//3D
	private PVector[] depthMapRealWorld;
	private int couleur1;
	private int couleur2;
	private int couleur;
	private boolean hasJumpALine;
	
	public Background(KPrez _kprez, String _imgType) {
		kprez = _kprez;
		imgType = _imgType;
		couleur1 = kprez.color(255);
		couleur2 = kprez.color(127);
		couleur = couleur1;
	}
	protected void setImg(String _imgType) {
		imgType = _imgType;
	}
	protected void update(String _imgType) {
		
		imgType = _imgType;
		
		if(imgType != "3D") {
		
			switch (imgType) {
			case "depthImage":
				img = kprez.context.depthImage();
				break;
			case "userImage":
				img = kprez.context.userImage();
				break;
			default:
				img = kprez.context.depthImage();
				break;
			}
			draw2DBackground();
		} else {
			depthMapRealWorld = kprez.context.depthMapRealWorld();
		}
	}
	private void draw2DBackground(){
		
		int[] depthValues = kprez.context.depthMap();
		int mapWidth = kprez.context.depthWidth();
		int mapHeight = kprez.context.depthHeight();
		
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				if (currentDepthValue < kprez.gi.getLowestValue() || currentDepthValue > kprez.gi.getHighestValue()) {
					img.pixels[i] = 0;
				}
		    }
		}
	}
	private void display3DPoints(){
		
		couleur = couleur1;
		int resolution = kprez.resolution;
		int foo = 10*resolution; //10
		int bar = foo/2; //5
		
		//kprez.pushMatrix();

		kprez.stroke(couleur);
	    kprez.strokeWeight(2);
	    
	    int mapWidth = kprez.context.depthWidth();
	    
	    int oldLineId = 0;
	    int j=0;
	    
	    //PApplet.println(depthMapRealWorld.length + " " + mapWidth*mapHeight);
	    
	    for (int i = 0; i < depthMapRealWorld.length; i += foo) {
	    	
	    	int newLineId = i/mapWidth;
	    	//PApplet.println(newLineId);
	    	
	    	//diagonal effect
	    	if(newLineId % 2 == 1){
	    		j = i;		    		
	    	} else {
	    		j = i+bar;
	    	}
	    	
	    	//new line
	    	if(oldLineId != newLineId) {
	    		
	    		//----- create space between between each line of dots -----//
	    		if(!hasJumpALine){
	    		
	    			//jump a line
	    			if(i+mapWidth*2*resolution < depthMapRealWorld.length){
	    				i += mapWidth*2*resolution;
	    				j = i;
	    			}
	    		} else {
	    			kprez.stroke(toogleColor());
	    		}
	    		hasJumpALine();
	    		//------------------------------------//
	    	}
	    	
	    	oldLineId = newLineId;
	    	
	    	PVector currentPoint = depthMapRealWorld[j];
	        if (currentPoint.z > kprez.gi.getLowestValue() && currentPoint.z < kprez.gi.getHighestValue()) {
	        	kprez.point(currentPoint.x, currentPoint.y, currentPoint.z);
	        	
	        	//---- SCENE 2 FEATURE ----//
	        	if(kprez.sceneId() == 2){
	        		for (SoundBox s: kprez.soundScene.boxes) {
	        		     s.isHit(currentPoint);
	        		}
	        	}
	       
	        }
	    }	
	}
	private void hasJumpALine(){
		hasJumpALine = !hasJumpALine;
	}
	private int toogleColor(){
		if(couleur == couleur1){
			couleur = couleur2;
		} else if(couleur == couleur2){
			couleur = couleur1;
		}
		return couleur;
	}
	protected void display() {
		
		if(imgType != "3D") {
			kprez.noFill();
			kprez.stroke(255);
			kprez.rectMode(PApplet.CORNER);
			kprez.rect(0, 0, img.width, img.height);
			
			kprez.image(img, 0, 0);
		} else {
			display3DPoints();
		}
	}
}
