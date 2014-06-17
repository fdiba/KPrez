import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class FaceScene {
	protected KPrez kprez;
	private PVector[] depthMapRealWorld;
	private int color;
	private boolean hasJumpALine;
	private PImage rgbImage;
	
	public FaceScene(KPrez _kprez) {
		kprez = _kprez;
		color = kprez.color(255);
	}
	protected void update(){
		depthMapRealWorld = kprez.context.depthMapRealWorld();
		rgbImage = kprez.context.rgbImage();
		//PApplet.println(depthMapRealWorld.length + " " + rgbImage.pixels.length);
	}
	protected void display(){
		//PApplet.println(kprez.resolution);
		display3DPoints();
	}
	private void display3DPoints(){
		
		int resolution = kprez.resolution;
		int foo = 10*resolution; //10
		int bar = foo/2; //5
		
		kprez.pushMatrix();

			//kprez.stroke(color);
		    kprez.strokeWeight(3);
		    //kprez.strokeWeight(1);
		    
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
		    			//kprez.stroke(toogleColor());
		    		}
		    		hasJumpALine();
		    		//------------------------------------//
		    	}
		    	
		    	oldLineId = newLineId;
		    	
		    	PVector currentPoint = depthMapRealWorld[j];
		        if (currentPoint.z > kprez.gi.getLowestValue() && currentPoint.z < kprez.gi.getHighestValue()) {
		        	
		        	color = rgbImage.pixels[i];
		        	kprez.stroke(color);
		        	kprez.point(currentPoint.x, currentPoint.y, currentPoint.z);
		        	
		        	
		    
		        }
		      }
	
		kprez.popMatrix();
		
	}
	private void hasJumpALine(){
		hasJumpALine = !hasJumpALine;
	}
	/*private int toogleColor(){
		if(color == color1){
			color = color2;
		} else if(color == color2){
			color = color1;
		}
		return color;
	}*/
}
