import processing.core.PVector;

public class SmartPoint {
	
	private PVector cp_raw_location;
	private int actualDepth;
	private int couleur;
	protected boolean isTaken;
	
	protected HandControl parent;
	protected PVector location;
	
	protected int width;
	
	public SmartPoint(HandControl _parent){
		parent = _parent;
		couleur = parent.parent.color(242, 162, 32);
		width = 20;
		cp_raw_location = new PVector();
		location = cp_raw_location.get();
		
	}
	protected void take(Bouton btn){
		isTaken = true;
	}
	protected void free(Bouton btn){
		isTaken = false;
	}
	protected void update(int userId, int memberId) {
		
		PVector handVector = new PVector();
		PVector handVector2d = new PVector();
		
		parent.parent.context.getJointPositionSkeleton(userId, memberId, handVector);
		parent.parent.context.convertRealWorldToProjective(handVector, handVector2d);
		//work in 2D only for now !!!!
		location.set(handVector2d);
	
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
		parent.parent.fill(couleur);
		parent.parent.ellipse(location.x, location.y, width, width);
	}
	protected void displayExit(int timeToExit) {
		parent.parent.noFill();
		parent.parent.stroke(couleur);
		parent.parent.ellipse(location.x, location.y, width + timeToExit, width + timeToExit);
	}
}
