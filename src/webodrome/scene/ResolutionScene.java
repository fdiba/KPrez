package webodrome.scene;

import java.util.ArrayList;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import webodrome.App;
import webodrome.ColorPoint;
import webodrome.mainctrl.GesturalInterface;

public class ResolutionScene extends Scene {
	
	private PVector[] depthMapRealWorld;
	private PImage rgbImage;
	private boolean hasJumpALine;
	private int resolution;
	private int color;
	private ArrayList<ColorPoint> cpoints;
	private boolean useColor;
	
	public ResolutionScene(PApplet _pApplet, Object[][] objects){
		
		super(_pApplet, objects);
		
	}
	public void pointAndMoveInTheRightDirection(){
		pApplet.translate(params.get("xTrans"), params.get("yTrans"), params.get("zTrans"));
		pApplet.rotateX(PApplet.radians(params.get("rotateXangle")));
		pApplet.rotateY(PApplet.radians(params.get("rotateYangle")));
		pApplet.rotateZ(PApplet.radians(params.get("rotateZangle")));
	}
	public void update(SimpleOpenNI context, GesturalInterface gi){
		depthMapRealWorld = context.depthMapRealWorld();
		rgbImage = context.rgbImage();		
		updateColorPoints(context, gi);
		
		menu.update(pApplet);
	}
	public void display(){
		drawPointsAndLines();
	}
	private void drawPoints(ColorPoint cp){
		pApplet.stroke(cp.color);
		pApplet.point(cp.location.x, cp.location.y, cp.location.z);
	}
	private void drawEllipse(ColorPoint cp){
		pApplet.noStroke();
    	for (int k=0; k<5; k++) {
    		pApplet.fill(cp.color, k*50);
    		pApplet.pushMatrix();
    			pApplet.translate(cp.location.x, cp.location.y, cp.location.z);
    			pApplet.ellipse(0, 0, width-2*k, width-2*k);
    		pApplet.popMatrix();
    	}
	}
	private void drawPointsAndLines(){

		pApplet.strokeWeight(1);
		
		//need to be optimised
		//PApplet.println(points.size());
		for (int i=0; i<cpoints.size(); i++) {
			
			ColorPoint colorPoint = cpoints.get(i);
						
			int foobar = 0;
        	//int foobar = 2;
        	
        	if(foobar == 0){
        		drawPoints(colorPoint);
        	} else if (foobar == 1){
        		drawEllipse(colorPoint);
        	}
			
		    for (int j=i+1; j<cpoints.size(); j++) {
		    	
		    	ColorPoint ncp = cpoints.get(j);
		    	
		        float distance = colorPoint.location.dist(ncp.location);
		        
		        if (distance > 0 && distance < resolution*35) {
		        	
		        	int couleur;
		        	
		        	if(useColor){
		        		couleur = pickColor(colorPoint.color, ncp.color);
		        	} else {
		        		couleur = 255 << 24 | 255 << 16 | 255 << 8 | 255;
		        	}
		        	
		        	pApplet.stroke(couleur, 150);
		        	pApplet.line(colorPoint.location.x, colorPoint.location.y, colorPoint.location.z, ncp.location.x, ncp.location.y, ncp.location.z);
		        }		
		    }
		}
	}
	private int pickColor(int c1, int c2){
		
		//int r = (int) kprez.red(c1);
		int r1 = (c1 >> 16) & 0xFF;
	    int g1 = (c1 >> 8) & 0xFF;
	    int b1 = c1 & 0xFF;
	    
	    int r2 = (c2 >> 16) & 0xFF;
	    int g2 = (c2 >> 8) & 0xFF;
	    int b2 = c2 & 0xFF;
	    
	    int r = (r1+r2)/2;
	    int g = (g1+g2)/2;
	    int b = (b1+b2)/2;
		
		return 255 << 24 | r << 16 | g << 8 | b;
		
	}
	private void updateColorPoints(SimpleOpenNI context, GesturalInterface gi){
		
		resolution = App.resolution;
		int foo = 10*resolution; //10
		int bar = foo/2; //5
		
		cpoints = new ArrayList<ColorPoint>();
		
	    pApplet.strokeWeight(3);
	    
	    int mapWidth = context.depthWidth();
	    
	    int oldLineId = 0;
	    int j=0;
	    	    
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
	    			//pApplet.stroke(toogleColor());
	    		}
	    		hasJumpALine();
	    		//------------------------------------//
	    	}
	    	
	    	oldLineId = newLineId;
	    	
	    	PVector currentPoint = depthMapRealWorld[j];
	        if (currentPoint.z > gi.getLowestValue() && currentPoint.z < gi.getHighestValue()) {
	        	
	        	color = rgbImage.pixels[i];
	        	cpoints.add(new ColorPoint(currentPoint, color));
	    
	        }
	    }		
	}
	private void hasJumpALine(){
		hasJumpALine = !hasJumpALine;
	}
}
