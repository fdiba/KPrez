import processing.core.PImage;

public class DDScene {
	
	private KPrez parent;
	private String[] paths = {"a.jpg", "b.jpg", "c.jpg"};
	private ISprite[] images;
	
	private PImage image;
	
	private int lowestValue;
	private int highestValue;
	
	public DDScene(KPrez _parent){
		
		parent = _parent;
		images = new ISprite[paths.length];
		
		lowestValue = 610;
		highestValue = 1525;
		
		for(int i=0; i<paths.length; i++){
			
			String path = "assets/" + paths[i];
			images[i] = new ISprite(parent, parent.random(parent.width/2-75), parent.random(parent.height-75), path);
			
		}
	
	}
	protected void update() {
		
		int[] depthValues = parent.context.depthMap();
		image = parent.context.depthImage();
		int mapWidth = parent.context.depthWidth();
		int mapHeight = parent.context.depthHeight();
		
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				if (currentDepthValue < lowestValue || currentDepthValue > highestValue) {
					image.pixels[i] = 0;
				}
		    }
		}
	}
	protected void testCollision(){
		
		if (parent.isTracked) {
			
			for (int i = 0; i < images.length; i++){
				
				if(!images[i].isDragged){
				
					images[i].hits = 0;
					images[i].testCollision(parent.handControl.rightHand);
					images[i].testCollision(parent.handControl.leftHand);
					images[i].update();
				} else {
					images[i].followSmartPoint();
				}
			}
			
		} else {
			for (int i = 0; i < images.length; i++){
				images[i].hits = 0;
				images[i].testCollision(parent.handControl.cp);
				images[i].update();
			}
		}
	}
	protected void display(){
		
		parent.image(image, 0, 0);
		
		for(int i=0; i<paths.length; i++){
			
			images[i].display();
			
		}	
	}
}
