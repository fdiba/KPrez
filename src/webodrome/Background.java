package webodrome;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import webodrome.mainctrl.GesturalInterface;

public class Background {
	
	private PApplet pApplet;
	protected String imgType;
	private PImage img;
	//3D
	public PVector[] depthMapRealWorld;
	private int couleur1;
	private int couleur2;
	private int couleur;
	private boolean hasJumpALine;
	
	public Background(PApplet _pApplet, String _imgType) {
		pApplet = _pApplet;
		imgType = _imgType;
		couleur1 = pApplet.color(255);
		couleur2 = pApplet.color(127);
		couleur = couleur1;
	}
	protected void setImg(String _imgType) {
		imgType = _imgType;
	}
	public void update(SimpleOpenNI context, GesturalInterface gi, String _imgType) {
		
		imgType = _imgType;
		
		if(imgType != "3D") {
		
			switch (imgType) {
			case "depthImage":
				img = context.depthImage();
				drawDepthImg(context, gi, 0);
				break;
			case "userImage":
				img = context.userImage();
				draw2DBackground(context, gi);
				break;
			case "rgbImage":
				img = context.rgbImage();
				draw2DBackground(context, gi);
				break;
			default:
				img = context.depthImage();
				drawDepthImg(context, gi, 0);
				break;
			}
		} else {
			depthMapRealWorld = context.depthMapRealWorld();
		}
	}
	public PImage getImg(){
		return img;
	}
	private void drawDepthImg(SimpleOpenNI context, GesturalInterface gi, int threshold){
		
		int[] depthValues = context.depthMap();
		int mapWidth = context.depthWidth();
		int mapHeight = context.depthHeight();
		
		int cValue;
		int lValue = gi.getLowestValue();
		int hValue = gi.getHighestValue();
		
		for (int x = 0; x < mapWidth; x++) {
			
			for (int y = 0; y < mapHeight; y++) {
				
				int pixId = x + y * mapWidth;
				int currentDepthValue = depthValues[pixId];
 
				if(currentDepthValue >= lValue && currentDepthValue <= hValue){

					cValue = (int) PApplet.map(currentDepthValue, lValue, hValue, 255, threshold);
					img.pixels[pixId] = (255 << 24) | (cValue << 16) | (cValue << 8) | cValue;
				      
				} else {

					cValue = 0;
					img.pixels[pixId] = (0 << 24) | (cValue << 16) | (cValue << 8) | cValue;
	    
				}
				
		    }
		}
		
	}
	private void draw2DBackground(SimpleOpenNI context, GesturalInterface gi){
		
		int[] depthValues = context.depthMap();
		int mapWidth = context.depthWidth();
		int mapHeight = context.depthHeight();
		
		int cValue;
		int lValue = gi.getLowestValue();
		int hValue = gi.getHighestValue();
		
		for (int x = 0; x < mapWidth; x++) {
			
			for (int y = 0; y < mapHeight; y++) {
				
				int pixId = x + y * mapWidth;
				int currentDepthValue = depthValues[pixId];
 
				if(currentDepthValue >= lValue && currentDepthValue <= hValue){

					/*cValue = (int) PApplet.map(currentDepthValue, lValue, hValue, 255, 0);
					img.pixels[pixId] = (255 << 24) | (cValue << 16) | (cValue << 8) | cValue;*/
				      
				} else {

					cValue = 0;
					img.pixels[pixId] = (0 << 24) | (cValue << 16) | (cValue << 8) | cValue;
	    
				}
				
		    }
		}
	}
	private void display3DPoints(SimpleOpenNI context, GesturalInterface gi){
		
		couleur = couleur1;
		
		int spaceBetweenPoints = App.getActualScene().params.get("resolution");
		int foo = 10*spaceBetweenPoints;
		int offset = foo/2;

		pApplet.stroke(couleur);
		pApplet.strokeWeight(2);
	    
	    int mapWidth = context.depthWidth();
	    
	    int oldLineId = 0;
	    int j=0;
	    
	    int depthMapLength = depthMapRealWorld.length;
	    
	    for (int i = 0; i < depthMapLength; i += foo) {
	    	
	    	int newLineId = i/mapWidth;
	    	//PApplet.println(newLineId);
	    	
	    	//diagonal effect
	    	if(newLineId % 2 == 1){
	    		j = i;		    		
	    	} else {
	    		j = i+offset;
	    	}
	    	
	    	//new line
	    	if(oldLineId != newLineId) {
	    		
	    		//----- create space between between each line of dots -----//
	    		if(!hasJumpALine){
	    		
	    			//jump a line
	    			if(i+mapWidth*2*spaceBetweenPoints < depthMapLength){
	    				
	    				i += mapWidth*2*spaceBetweenPoints;
	    				j = i;
	    				
	    			} else { //get last row
	    				i = depthMapLength - mapWidth;
	    				j = i;
	    			}
	    			
	    		} else {
	    			pApplet.stroke(toogleColor());
	    		}
	    		hasJumpALine();
	    		//------------------------------------//
	    	}
	    	
	    	oldLineId = newLineId;
	    	
	    	PVector currentPoint = depthMapRealWorld[j];
	        if (currentPoint.z > gi.getLowestValue() && currentPoint.z < gi.getHighestValue()) {
	    
	        	pApplet.point(currentPoint.x, currentPoint.y, currentPoint.z);
	      
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
	public void display(SimpleOpenNI context, GesturalInterface gi) {
		
		if(imgType != "3D") {
			pApplet.noFill();
			pApplet.stroke(255);
			pApplet.rectMode(PApplet.CORNER);
			pApplet.rect(0, 0, img.width, img.height);
			pApplet.image(img, 0, 0);
		} else {
			display3DPoints(context, gi);
		}
	}
}
