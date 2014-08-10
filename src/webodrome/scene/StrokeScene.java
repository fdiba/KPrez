package webodrome.scene;

import webodrome.App;
import webodrome.PolygonBlob;
import webodrome.CustomShape;

import java.util.ArrayList;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;
import toxi.processing.ToxiclibsSupport;

public class StrokeScene extends Scene {
	
	private PImage bImg;
	private BlobDetection blobDetection;
	private ArrayList<ArrayList<PVector>> contours;
	private boolean box2D;
	
	//----- box2D & Toxiclibs ----------//
	private ToxiclibsSupport toxiclibsSupport;
	private Box2DProcessing box2dProcessing;
	private ArrayList<CustomShape> polygons;
	private PolygonBlob polygonBlob;
	
	public StrokeScene(PApplet p, Object[][] objects, boolean _box2D) {
		
		super(objects);
		
		pApplet = p;
		width = App.width;
		height = App.height;
		
		bImg = new PImage(width/2, height/2);
		
		blobDetection = new BlobDetection(bImg.width, bImg.height);
		blobDetection.setPosDiscrimination(true); //find bright areas
		blobDetection.setThreshold(0.2f); //between 0.0f and 1.0f
		
		box2D = _box2D;
		
		if(box2D){
			
			toxiclibsSupport = new ToxiclibsSupport(p);
			box2dProcessing = new Box2DProcessing(p);
			box2dProcessing.createWorld();
			box2dProcessing.setGravity(0, -20); //--------------------------- param
			
			polygons = new ArrayList<CustomShape>();
			
		}
		
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

	}
	public void displayMenu(){
		menu.display(pApplet);
	}
	public void displayMiniature(){
		
		int alpha = 0;
		
		if(menu.showTime > 0){
			alpha = 255;
		} else {
			alpha = 0;
		}
		
		pApplet.tint(255, alpha);
		
		pApplet.image(bImg, 0, 0);
		
		pApplet.tint(255, 255);
	}
	public void update(PImage depthImg){
		
		bImg.copy(depthImg, 0, 0, depthImg.width, depthImg.height, 0, 0, bImg.width, bImg.height);
		fastblur(bImg, params.get("blurRadius"));
		
		createBlackBorders();
		
		blobDetection.computeBlobs(bImg.pixels);
		
		createContours();
		
		if(box2D){
			// WARNING only the biggest shape is displayed
			polygonBlob = new PolygonBlob();
			polygonBlob.create(blobDetection, contours);
			polygonBlob.createBody(box2dProcessing);
			polygonBlob.destroyBody(box2dProcessing);	
		}
		
		menu.update(pApplet);
		
	}
	public void updateAndDrawBox2D(){
		
		if(box2D){
		
			if (pApplet.frameRate > params.get("frameRateValue")) {
				polygons.add(new CustomShape(pApplet, toxiclibsSupport, box2dProcessing, width/2, -50, -1));
			    polygons.add(new CustomShape(pApplet, toxiclibsSupport, box2dProcessing, width/2, -50, pApplet.random((float) 2.5, 20)));
			}
			
			box2dProcessing.step();		
			updateAndDisplayShapes();
		
		}
		
	}
	private void updateAndDisplayShapes(){
		for (int i = polygons.size()-1; i>0; i--) {
			CustomShape customShape = polygons.get(i);
			if(customShape.done()){
				polygons.remove(i);
			} else {
				customShape.update(polygonBlob);
				customShape.display();
			}
		}
	}
	public void displayUser(){
		if(box2D){
			//drawBackground();
			pApplet.noStroke();
			pApplet.fill(pApplet.color(238, 241, 232)); //beige
			toxiclibsSupport.polygon2D(polygonBlob);
		}
	}
	@SuppressWarnings("unused")
	private void drawBackground(){	
		pApplet.noStroke();
		pApplet.rectMode(PApplet.CORNER);
		int c =  (255 << 24) | (47 << 16) | (52 << 8) | 54;
		pApplet.fill(c);
		pApplet.rect(0, 0, width, height);
	}
	private void createBlackBorders(){
		  
	  bImg.loadPixels();
	  
	  int offset = params.get("borderOffset");
	  int color = (0 << 16) | (0 << 8) | 0;
	  
	  //top border
	  for(int j=0; j<offset; j++){
		  for(int i=0; i<bImg.width; i++){
			  bImg.pixels[i+j*bImg.width] = color;    
		  }
	  }
	
	  //right border
	  for(int i=0; i<offset; i++){
		  for(int j=0; j<bImg.height; j++){
			  bImg.pixels[i+j*bImg.width] = color;    
		  }
	  }
	  
	  //bottom border
	  for(int j=bImg.height-offset; j<bImg.height; j++){
		  for(int i=0; i<bImg.width; i++){
			  bImg.pixels[i+j*bImg.width] = color;    
		  }
	  }
	
	  //left border
	  for(int i=bImg.width-offset; i<bImg.width; i++){
		  for(int j=0; j<bImg.height; j++){
			  bImg.pixels[i+j*bImg.width] = color;    
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
