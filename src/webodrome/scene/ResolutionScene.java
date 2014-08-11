package webodrome.scene;

import java.util.ArrayList;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import webodrome.App;
import webodrome.ColorPoint;
import webodrome.mainctrl.GesturalInterface;

public class ResolutionScene extends Scene { //scene 4
	
	private PVector[] depthMapRealWorld;
	private PImage rgbImage;
	private boolean hasJumpALine;
	private int spaceBetweenPoints;
	private int color;
	private ArrayList<ColorPoint> cpoints;
	private boolean useColor;
	
	public ResolutionScene(PApplet _pApplet, Object[][] objects){
		super(_pApplet, objects);
		//useColor = true;
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
		drawPointsAndLines(cpoints.size());
	}
	private void updateColorPoints(SimpleOpenNI context, GesturalInterface gi){
		
		spaceBetweenPoints = params.get("resolution");
		int offset = spaceBetweenPoints/2;
		
		cpoints = new ArrayList<ColorPoint>();
		
	    pApplet.strokeWeight(3);
	    
	    int mapWidth = context.depthWidth();
	    
	    int oldRowId = 0;
	    int j=0;
	    
	    int depthMapLength = depthMapRealWorld.length;
	    	    
	    for (int i=0; i < depthMapLength; i+=spaceBetweenPoints) {	
	    
	    	int rowId = i/mapWidth;
	    	
	    	//diagonal effect
	    	if(rowId % 2 == 1){
	    		j = i;		    		
	    	} else {
	    		j = i + offset;
	    	}
	    	
	    	//new line
	    	if(oldRowId != rowId) {
	    		
	    		//----- create space between between each line of dots -----//
	    		if(!hasJumpALine){
	    		
	    			//jump a line
	    			if(i+mapWidth*2*spaceBetweenPoints < depthMapLength){
	    				
	    				i += mapWidth*spaceBetweenPoints;
	    				j = i;
	    				
	    			} else { //get last row
	    				i = depthMapLength - mapWidth;
	    				j = i;
	    			}
	    			
	    		} else {
	    			
	    			//pApplet.stroke(toogleColor());
	    		
	    		}
	    		
	    		hasJumpALine();
	    		//------------------------------------//
	    	}
	    	
	    	oldRowId = rowId;
	    	
	    	PVector currentPoint = depthMapRealWorld[j];
	        if (currentPoint.z > gi.getLowestValue() && currentPoint.z < gi.getHighestValue()) {
	        	
	        	color = rgbImage.pixels[i];
	        	cpoints.add(new ColorPoint(currentPoint, color));
	    
	        } else {
	        	cpoints.add(new ColorPoint(currentPoint, true));
	        }
	    }		
	}
	private void drawPointsAndLines(int arrayLength){
		
		int numberOfPointInOneRow = App.width/spaceBetweenPoints;

		pApplet.strokeWeight(1);
		
		for (int i=1+numberOfPointInOneRow; i<arrayLength-numberOfPointInOneRow; i++) {
			
			ColorPoint actualCP = cpoints.get(i);
			int couleur = 255 << 24 | 255 << 16 | 255 << 8 | 255;
			pApplet.stroke(couleur);
			
			int num = params.get("gridId");
			
			switch (num) {
			case 0:
				displayGrid(actualCP, numberOfPointInOneRow, i);
				break;
			case 1:
				displayDiagonal(actualCP, numberOfPointInOneRow, i);
				break;
			case 2:
				displayGrid(actualCP, numberOfPointInOneRow, i);
				displayDiagonal(actualCP, numberOfPointInOneRow, i);
				break;
			default:
				break;
			}
		}
	}
	private void displayDiagonal(ColorPoint actualCP, int numberOfPointInOneRow, int i){
		
		//----------------- top previous one ------------------------//
		ColorPoint topLeftCP = cpoints.get(i-numberOfPointInOneRow-1);
		if(!actualCP.getIsHidden() && !topLeftCP.getIsHidden()){
			pApplet.line(actualCP.location.x, actualCP.location.y, actualCP.location.z, topLeftCP.location.x, topLeftCP.location.y, topLeftCP.location.z);
		}
		
		//---------------- bottom previous one --------------------//
		ColorPoint bottomLeftCP = cpoints.get(i+numberOfPointInOneRow-1);			
		if(!actualCP.getIsHidden() && !bottomLeftCP.getIsHidden()){
			pApplet.line(actualCP.location.x, actualCP.location.y, actualCP.location.z, bottomLeftCP.location.x, bottomLeftCP.location.y, bottomLeftCP.location.z);
		}
		
	}
	private void displayGrid(ColorPoint actualCP, int numberOfPointInOneRow, int i){
		
		//---------------- previous one --------------------//
		ColorPoint prevCP = cpoints.get(i-1);			
		if(!actualCP.getIsHidden() && !prevCP.getIsHidden()){
			pApplet.line(actualCP.location.x, actualCP.location.y, actualCP.location.z, prevCP.location.x, prevCP.location.y, prevCP.location.z);
		}
		
		//----------------- top one ------------------------//
		ColorPoint topCP = cpoints.get(i-numberOfPointInOneRow);
		if(!actualCP.getIsHidden() && !topCP.getIsHidden()){
			pApplet.line(actualCP.location.x, actualCP.location.y, actualCP.location.z, topCP.location.x, topCP.location.y, topCP.location.z);
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
	private void hasJumpALine(){
		hasJumpALine = !hasJumpALine;
	}
}
