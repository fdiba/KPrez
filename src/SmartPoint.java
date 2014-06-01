import processing.core.PVector;

public class SmartPoint {
	
	private PVector cp_raw_location;
	private int actualDepth;
	private int couleur;
	private int couleur3d;
	protected boolean isTaken;
	
	protected HandControl parent;
	protected PVector location;
	protected PVector location3d;
	
	protected int width;
	
	public SmartPoint(HandControl _parent){
		parent = _parent;
		couleur = parent.parent.color(242, 162, 32);
		couleur3d = parent.parent.color(131, 232, 159);
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
	protected void update(int userId, int memberId) {
		
		if (parent.parent.gi.getWorld() == "2D") {
			PVector handVector = new PVector();
			PVector handVector2d = new PVector();
			parent.parent.context.getJointPositionSkeleton(userId, memberId, handVector);
			parent.parent.context.convertRealWorldToProjective(handVector, handVector2d);
			location.set(handVector2d);
		} else {
			PVector handVector = new PVector();
			parent.parent.context.getJointPositionSkeleton(userId, memberId, handVector);
			location3d.set(handVector);
		}
	}
	protected void updateClosestPoint() {
		actualDepth = parent.parent.bgrd.getHighestValue();
		
		int[]depthValues = parent.parent.context.depthMap();
		int mapWidth = parent.parent.context.depthWidth();
		int mapHeight = parent.parent.context.depthHeight();
		
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth; x++){
				
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				
				int lowestValue = parent.parent.bgrd.getLowestValue();
				
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
		
		parent.parent.noStroke();
		
		if (parent.parent.gi.getWorld() == "2D") {
			
			
			if(isTaken){
				parent.parent.noFill();
			} else {
				parent.parent.fill(couleur);
			}
			
			parent.parent.ellipse(location.x, location.y, width, width);
		} else {
			parent.parent.pushMatrix();
				parent.parent.fill(couleur3d);
				parent.parent.translate(location3d.x, location3d.y, location3d.z);
				parent.parent.ellipse(0, 0, width, width);
			parent.parent.popMatrix();
		}
	}
	protected void displayExit(int timeToExit) {
		parent.parent.noFill();
		
		if(parent.parent.gi.getWorld() == "2D"){
			parent.parent.stroke(couleur);
			parent.parent.ellipse(location.x, location.y, width + timeToExit, width + timeToExit);
		} else {
			parent.parent.pushMatrix();
				parent.parent.stroke(couleur3d);
				parent.parent.translate(location3d.x, location3d.y, location3d.z);
				parent.parent.ellipse(0, 0, width + timeToExit, width+ timeToExit);
			parent.parent.popMatrix();
		}
	}
}
