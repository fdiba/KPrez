import processing.core.PVector;

public class SmartPoint {
	
	private PVector cp_raw_location;
	private int actualDepth;
	private int couleur;
	private int couleur3d;
	private int alpha;
	
	protected boolean isTaken;
	
	protected KPrez kprez;
	protected PVector location;
	protected PVector location3d;
	
	protected int width;
	
	public SmartPoint(KPrez _kprez){
		kprez = _kprez;
		alpha = 255;
		couleur = kprez.colors.get(3);
		couleur3d = kprez.colors.get(1);
		width = 20;
		cp_raw_location = new PVector();
		location = cp_raw_location.get();
		location3d = new PVector();
		
	}
	protected void take(Bouton btn){
		isTaken = true;
	}
	protected void free(Bouton btn){
		isTaken = false;
	}
	protected void update(PVector _handVector, int _alpha) {
		
		alpha = _alpha;
		
		if (kprez.gi.getWorld() == "2D") {
			PVector handVector2d = new PVector();
			kprez.context.convertRealWorldToProjective(_handVector, handVector2d);
			location.set(handVector2d);
		} else {
			location3d.set(_handVector);
		}
	}
	protected void updateClosestPoint() {
		actualDepth = kprez.gi.getHighestValue();
		
		int[]depthValues = kprez.context.depthMap();
		int mapWidth = kprez.context.depthWidth();
		int mapHeight = kprez.context.depthHeight();
		
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth; x++){
				
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				
				int lowestValue = kprez.gi.getLowestValue();
				
				if(currentDepthValue > lowestValue && currentDepthValue < actualDepth){
					actualDepth = currentDepthValue;
					cp_raw_location.set(x, y);
				}
			}
		}
		
		location.x = (location.x + cp_raw_location.x)/2;
		location.y = (location.y + cp_raw_location.y)/2;
		
	}
	protected void display() {
		
		kprez.noStroke();
		
		if (kprez.gi.getWorld() == "2D") {
			
			if(isTaken){
				kprez.noFill();
			} else {
				kprez.fill(couleur, alpha);
			}
			
			kprez.ellipse(location.x, location.y, width, width);
		} else {
			kprez.pushMatrix();
				kprez.fill(couleur3d, alpha);
				kprez.translate(location3d.x, location3d.y, location3d.z);
				kprez.ellipse(0, 0, width, width);
			kprez.popMatrix();
		}
	}
	protected void displayExit(int timeToExit) {
		kprez.noFill();
		
		if(kprez.gi.getWorld() == "2D"){
			kprez.stroke(kprez.colors.get(1));
			kprez.ellipse(location.x, location.y, width + timeToExit, width + timeToExit);
		} else {
			kprez.pushMatrix();
				kprez.stroke(couleur3d);
				kprez.translate(location3d.x, location3d.y, location3d.z);
				kprez.ellipse(0, 0, width + timeToExit, width+ timeToExit);
			kprez.popMatrix();
		}
	}
}
