import processing.core.PVector;

public class SmartPoint {
	
	private PVector cp_raw_location;
	private int maxDepth;
	private int actualDepth;
	private int couleur;
	
	protected HandControl parent;
	protected PVector location;
	
	public SmartPoint(HandControl _parent){
		parent = _parent;
		couleur = parent.parent.color(242, 162, 32);
		
		cp_raw_location = new PVector();
		location = cp_raw_location.get();
		
		maxDepth = 5000;
		
	}
	protected void update(int userId, int memberId) {
		
		PVector handVector = new PVector();
		PVector handVector2d = new PVector();
		
		parent.parent.context.getJointPositionSkeleton(userId, memberId, handVector);
		parent.parent.context.convertRealWorldToProjective(handVector, handVector2d);
		
		location.set(handVector2d);
	
	}
	protected void updateClosestPoint() {
		actualDepth = maxDepth;
		
		int[]depthValues = parent.parent.context.depthMap();
		int mapWidth = parent.parent.context.depthWidth();
		int mapHeight = parent.parent.context.depthHeight();
		
		for(int y=0; y<mapHeight; y++){
			for(int x=0; x<mapWidth; x++){
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				if(currentDepthValue>0 && currentDepthValue<actualDepth){
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
		parent.parent.ellipse(location.x, location.y, 20, 20);
	}
}
