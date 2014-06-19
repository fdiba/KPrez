
import java.util.ArrayList;
import processing.core.PImage;
import processing.core.PVector;

public class FaceScene {
	protected KPrez kprez;
	private PVector[] depthMapRealWorld;
	private int color;
	private boolean hasJumpALine;
	private PImage rgbImage;
	private ArrayList<ColorPoint> cpoints;
	private int width;
	private int resolution;
	private boolean useColor;
	
	public FaceScene(KPrez _kprez) {
		kprez = _kprez;
		color = kprez.color(255);
		width = 20;
		useColor = false;
	}
	protected void update(){
		depthMapRealWorld = kprez.context.depthMapRealWorld();
		rgbImage = kprez.context.rgbImage();
		//PApplet.println(depthMapRealWorld.length + " " + rgbImage.pixels.length);
		
		updateColorPoints();
	}
	protected void display(){
		//PApplet.println(kprez.resolution);
		//display3DPoints();
		drawPointsAndLines();
	}
	private void drawPointsAndLines(){
		
		kprez.strokeWeight(1);
		
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
		        		couleur = kprez.color(255);
		        	}
		        	
		        	kprez.stroke(couleur, 150);
		        	kprez.line(colorPoint.location.x, colorPoint.location.y, colorPoint.location.z, ncp.location.x, ncp.location.y, ncp.location.z);
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
		
		return kprez.color(r, g, b);
		
	}
	private void updateColorPoints(){
		
		resolution = kprez.resolution;
		int foo = 10*resolution; //10
		int bar = foo/2; //5
		
		cpoints = new ArrayList<ColorPoint>();
		
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
	        	cpoints.add(new ColorPoint(currentPoint, color));
	    
	        }
	    }		
	}
	private void drawPoints(ColorPoint cp){
		kprez.stroke(cp.color);
    	kprez.point(cp.location.x, cp.location.y, cp.location.z);
	}
	private void drawEllipse(ColorPoint cp){
		kprez.noStroke();
    	for (int k=0; k<5; k++) {
    		kprez.fill(cp.color, k*50);
    		kprez.pushMatrix();
    			kprez.translate(cp.location.x, cp.location.y, cp.location.z);
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
