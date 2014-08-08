package webodrome.scene;

import java.util.ArrayList;
import java.util.HashMap;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import webodrome.ctrl.Menu;

public class StrokeScene extends Scene {
	
	private PImage bImg;
	private BlobDetection blobDetection;
	private ArrayList<ArrayList<PVector>> contours;
	
	public StrokeScene(PApplet p, int w, int h) {
		
		PApplet.println("init StrokeScene");
		
		params = new HashMap<String, Integer>();
		params.put("blurRadius", 0);
		params.put("edgeMinNumber", 0);
		params.put("distMin", 0);
		
		createMenu();
		
		pApplet = p;
		width = w;
		height = h;
		
		bImg = new PImage(width/2, height/2);
		
		blobDetection = new BlobDetection(bImg.width, bImg.height);
		blobDetection.setPosDiscrimination(true); //find bright areas
		blobDetection.setThreshold(0.2f); //between 0.0f and 1.0f
		
	}
	private void createMenu(){	
		
		Object[][] objects = { {"blurRadius", 1, 200, menu.colors[0], 0, 0, 2},
							   {"edgeMinNumber", 0, 500, menu.colors[1], 0, 1, 375},
							   {"distMin", 0, 100, menu.colors[2], 0, 2, 10} };
		
		menu = new Menu(this, new PVector(450, 50), objects);
		
		
	}
	public void display(){
		
		pApplet.strokeWeight(2);
	    pApplet.stroke(0, 255, 0);
	    pApplet.noFill();
		
		for(int i=0; i<contours.size(); i++){
		    
		    ArrayList<PVector> contour = contours.get(i);
		    
		    pApplet.beginShape();
		        
		    for(int j=0; j<contour.size(); j++){
		      
		      PVector v = contour.get(j);
		      pApplet.vertex(v.x, v.y);
		      	
		    }
		    
		    pApplet.endShape(PApplet.CLOSE);
		}
		
		menu.display(pApplet);

	}
	public void displayMiniature(){
		pApplet.image(bImg, 0, 0);
	}
	public void update(PImage depthImg){
		
		bImg.copy(depthImg, 0, 0, depthImg.width, depthImg.height, 0, 0, bImg.width, bImg.height);
		fastblur(bImg, params.get("blurRadius"));
		
		createBlackBorder();
		
		blobDetection.computeBlobs(bImg.pixels);
		createContours();
		
		menu.update(pApplet);
		
	}
	private void createBlackBorder(){
		  
	  bImg.loadPixels();
	  
	  for(int j=bImg.height-1; j<bImg.height; j++){
		  
		  for(int i=0; i<bImg.width; i++){
			  bImg.pixels[i+j*bImg.width] = pApplet.color(0);    
		  }
	    
	  }
	  
	  bImg.updatePixels();
	  
	}

	private void createContours() {
		
		Blob blob;
		EdgeVertex eA, eB;
	  
	  contours = new ArrayList<ArrayList<PVector>>();
	  
	  for(int n=0; n<blobDetection.getBlobNb(); n++){
		  
		  blob = blobDetection.getBlob(n);
	    
		  if(blob != null && blob.getEdgeNb() > params.get("edgeMinNumber")){
	      
			  ArrayList<PVector> contour = new ArrayList<PVector>();

			  for(int i=0; i<blob.getEdgeNb(); i++){
				  
				  eA = blob.getEdgeVertexA(i);
			      eB = blob.getEdgeVertexB(i);
			        
			      if(i==0){
			    	  
			    	  contour.add(new PVector(eA.x*width, eA.y*height));
			    	  
			      } else {
			          
			          PVector v = contour.get(contour.size()-1);
			          float distance = PApplet.dist(eB.x*width, eB.y*height, v.x, v.y);
			          if(distance> params.get("distMin"))contour.add(new PVector(eB.x*width, eB.y*height));
			    
			      }
			    
			  }
	      
	      if(contour.size()>2) contours.add(contour);
	    
	    }
	    
	  }
		
	}
	private void fastblur(PImage img, int radius){
		
		if (radius<1) return;
	
		int w = img.width;
		int h = img.height;
	  
		int wm = w-1;
		int hm = h-1;
		int wh = w*h;
	  
		int div = radius+radius+1;
		
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		
		int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
		
		int vmin[] = new int[PApplet.max(w,h)];
		int vmax[] = new int[PApplet.max(w,h)];
		
		int[] pix = img.pixels;
		
		int dv[] = new int[256*div]; //?!
	  
		for (i=0; i < 256*div; i++) dv[i]=(i/div);

		yw = yi = 0;

		for (y=0; y < h; y++){
	    
			rsum = gsum = bsum = 0;
	    
			for(i = -radius; i <= radius; i++){
	      
				p = pix[yi + PApplet.min(wm, PApplet.max(i,0))];
				rsum += (p & 0xff0000) >> 16;
				gsum += (p & 0x00ff00) >> 8;
      			bsum += p & 0x0000ff;
			}
	    
			for (x=0; x < w; x++){

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				if(y == 0){
					
					vmin[x] = PApplet.min(x + radius + 1, wm);
	        		vmax[x] = PApplet.max(x - radius, 0);
				}
	      
				p1=pix[yw+vmin[x]];
				p2=pix[yw+vmax[x]];
	
				rsum += ((p1 & 0xff0000)-(p2 & 0xff0000)) >> 16;
		      	gsum += ((p1 & 0x00ff00)-(p2 & 0x00ff00)) >> 8;
		      	bsum += (p1 & 0x0000ff)-(p2 & 0x0000ff);
		      	yi++;
			}
			
			yw += w;
		}

		for (x=0; x < w; x++){
	    
			rsum = gsum = bsum = 0;
			yp = -radius*w;
	    
			for(i = -radius; i <= radius; i++){
				
				yi = PApplet.max(0,yp)+x;
			    rsum += r[yi];
			    gsum += g[yi];
			    bsum += b[yi];
			    yp += w;
			}
	    
			yi = x;
	    
			for (y=0; y < h; y++){
	      
				pix[yi] = 0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
	      
				if(x == 0){	
					vmin[y] = PApplet.min(y + radius + 1,hm)*w;
					vmax[y] = PApplet.max(y - radius, 0)*w;
				}
	      
				p1 = x + vmin[y];
				p2 = x + vmax[y];

			    rsum += r[p1] - r[p2];
			    gsum += g[p1] - g[p2];
			    bsum += b[p1] - b[p2];

			    yi+=w;
			}
		}
	}
}
