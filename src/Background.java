import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Background {
	
	protected String imgType;
	protected KPrez parent;
	private PImage img;
	//3D
	private PVector[] depthMapRealWorld;
	private int couleur1;
	private int couleur2;
	private int couleur;
	private boolean hasJumpALine;
	
	private int lowestValue;
	private int highestValue;
	
	public Background(KPrez _parent, int _lowestValue, int _highestValue, String _imgType) {
		
		lowestValue = _lowestValue;
		highestValue = _highestValue;
		
		parent = _parent;
		imgType = _imgType;
		couleur1 = parent.color(255, 0, 0);
		couleur2 = parent.color(0,255,0);
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
				img = parent.context.depthImage();
				break;
			case "userImage":
				img = parent.context.userImage();
				break;
			default:
				img = parent.context.depthImage();
				break;
			}
			draw2DBackground();
		} else {
			depthMapRealWorld = parent.context.depthMapRealWorld();
		}
	}
	private void draw2DBackground(){
		
		int[] depthValues = parent.context.depthMap();
		int mapWidth = parent.context.depthWidth();
		int mapHeight = parent.context.depthHeight();
		
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				if (currentDepthValue < lowestValue || currentDepthValue > highestValue) {
					img.pixels[i] = 0;
				}
		    }
		}
	}
	private void display3DPoints(){
		
		parent.pushMatrix();

			parent.stroke(couleur);
		    parent.strokeWeight(2);
		    
		    int mapWidth = parent.context.depthWidth();
		    
		    int oldLineId = 0;
		    int j=0;
		    
		    //PApplet.println(depthMapRealWorld.length + " " + mapWidth*mapHeight);
		    
		    for (int i = 0; i < depthMapRealWorld.length; i += 10) {
		    	
		    	int newLineId = i/mapWidth;
		    	//PApplet.println(newLineId);
		    	
		    	if(newLineId % 2 == 1){
		    		j= i;		    		
		    	} else {
		    		j =i+5;
		    	}
		    	
		    	//new line
		    	if(oldLineId != newLineId) {
		    		
		    		//----- create space between between each line of dots -----//
		    		if(!hasJumpALine){
		    		
		    			//jump a line
		    			if(i+mapWidth*2 < depthMapRealWorld.length){
		    				i+=mapWidth*2;
		    				j=i;
		    			}
		    		} else {
		    			parent.stroke(toogleColor());
		    		}
		    		hasJumpALine();
		    		//------------------------------------//
		    	}
		    	
		    	oldLineId = newLineId;
		    	
		    	PVector currentPoint = depthMapRealWorld[j];
		        if (currentPoint.z > lowestValue && currentPoint.z < highestValue) {
		        	parent.point(currentPoint.x, currentPoint.y, currentPoint.z);
		        }
		      }
	
		parent.popMatrix();
		
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
	protected void setLowestValue(int _lowestValue) {
		lowestValue = _lowestValue;
	}
	protected void setHighestValue(int _highestValue) {
		highestValue = _highestValue;
	}
	protected void display() {
		
		if(imgType != "3D") {
			parent.noFill();
			parent.stroke(255);
			parent.rectMode(PApplet.CORNER);
			parent.rect(0, 0, img.width, img.height);
			
			parent.image(img, 0, 0);
		} else {
			display3DPoints();
		}
	}
}
