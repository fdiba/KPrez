package webodrome.mainctrl;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;
import webodrome.App;

public class SmartPoint {
	
	private PVector cp_raw_location;
	private int actualDepth;
	private int couleur;
	private int couleur3d;
	private int alpha;
	public int id;
	
	protected boolean isTaken;
	
	protected PApplet pApplet;
	public PVector location;
	protected PVector location3d;
	
	protected int width;
	
	public SmartPoint(PApplet _pApplet, int _id){
		
		pApplet = _pApplet;
		
		id = _id;
		alpha = 255;
		couleur = App.colorsPanel[3];
		couleur3d = App.colorsPanel[1];
		width = 20;
		cp_raw_location = new PVector();
		location = cp_raw_location.get();
		location3d = new PVector();
	}
	public void take(){
		isTaken = true;
	}
	public void free(){
		isTaken = false;
	}
	protected void update(SimpleOpenNI context, GesturalInterface gi, PVector _handVector, int _alpha) {
		
		alpha = _alpha;
		
		if (gi.getWorld() == "2D") {
			PVector handVector2d = new PVector();
			context.convertRealWorldToProjective(_handVector, handVector2d);
			location.set(handVector2d);
		} else {
			location3d.set(_handVector);
		}
	}
	protected void updateClosestPoint(SimpleOpenNI context, GesturalInterface gi) {
		
		actualDepth = gi.getHighestValue();
		
		int[]depthValues = context.depthMap();
		int mapWidth = context.depthWidth();
		int mapHeight = context.depthHeight();
		
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth; x++){
				
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				
				int lowestValue = gi.getLowestValue();
				
				if(currentDepthValue > lowestValue && currentDepthValue < actualDepth){
					actualDepth = currentDepthValue;
					cp_raw_location.set(x, y);
				}
			}
		}
		
		location.x = (location.x + cp_raw_location.x)/2;
		location.y = (location.y + cp_raw_location.y)/2;
		
	}
	protected void display(GesturalInterface gi) {
		
		pApplet.noStroke();
		
		if (gi.getWorld() == "2D") {
			
			if(isTaken){
				pApplet.noFill();
			} else {
				pApplet.fill(couleur, alpha);
			}
			
			pApplet.ellipse(location.x, location.y, width, width);
		} else {
			pApplet.pushMatrix();
				pApplet.fill(couleur3d, alpha);
				pApplet.translate(location3d.x, location3d.y, location3d.z);
				pApplet.ellipse(0, 0, width, width);
			pApplet.popMatrix();
		}
	}
	protected void displayExit(GesturalInterface gi, int timeToExit) {
		pApplet.noFill();
		
		if(gi.getWorld() == "2D"){
			pApplet.stroke(App.colorsPanel[1]);
			pApplet.ellipse(location.x, location.y, width + timeToExit, width + timeToExit);
		} else {
			pApplet.pushMatrix();
			pApplet.stroke(couleur3d);
				pApplet.translate(location3d.x, location3d.y, location3d.z);
				pApplet.ellipse(0, 0, width + timeToExit, width+ timeToExit);
			pApplet.popMatrix();
		}
	}
}
