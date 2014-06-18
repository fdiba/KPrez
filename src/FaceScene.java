import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class FaceScene {
	protected KPrez kprez;
	private PVector[] depthMapRealWorld;
	private int color;
	private boolean hasJumpALine;
	private PImage rgbImage;
	private ArrayList<PVector> points;
	private int width;
	private int resolution;
	
	public FaceScene(KPrez _kprez) {
		kprez = _kprez;
		color = kprez.color(255);
		width = 20;
	}
	protected void update(){
		depthMapRealWorld = kprez.context.depthMapRealWorld();
		rgbImage = kprez.context.rgbImage();
		//PApplet.println(depthMapRealWorld.length + " " + rgbImage.pixels.length);
	}
	protected void display(){
		//PApplet.println(kprez.resolution);
		display3DPoints();
		drawLines();
	}
	private void drawLines(){
		
		kprez.strokeWeight(1);
		
		//need to be optimised
		//PApplet.println(points.size());
		for (int i=0; i<points.size(); i++) {
			
			PVector p = points.get(i);
			
		    for (int j=i+1; j<points.size(); j++) {
		    	
		    	PVector np = points.get(j);
		    	
		        float distance = p.dist(np);
		        
		        if (distance > 0 && distance < resolution*35) {
		        	kprez.stroke(kprez.color(255), 150);
		        	kprez.line(p.x, p.y, p.z, np.x, np.y, np.z);
		        }
		    }
		 }
	}
	private void display3DPoints(){
		
		resolution = kprez.resolution;
		int foo = 10*resolution; //10
		int bar = foo/2; //5
		
		points = new ArrayList<PVector>();
		
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
	        	
	        	int foobar = 0;
	        	
	        	if(foobar == 0){
	        		drawPoints(currentPoint);
	        	} else {
	        		drawEllipse(currentPoint);
	        	}
	        	
	        	points.add(currentPoint);
	    
	        }
	    }		
	}
	private void drawPoints(PVector pvector){
		kprez.stroke(color);
    	kprez.point(pvector.x, pvector.y, pvector.z);
	}
	private void drawEllipse(PVector pvector){
		kprez.noStroke();
    	for (int k=0; k<5; k++) {
    		kprez.fill(color, k*50);
    		kprez.pushMatrix();
    			kprez.translate(pvector.x, pvector.y, pvector.z);
    			kprez.ellipse(0, 0, width-2*k, width-2*k);
    		kprez.popMatrix();
    	}
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
