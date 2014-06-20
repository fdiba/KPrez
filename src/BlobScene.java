import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;
import toxi.processing.ToxiclibsSupport;
import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;

public class BlobScene {
	
	private KPrez kprez;
	private BlobDetection blobDetection;
	private PImage image;
	private int width;
	private int height;
	
	private Box2DProcessing box2dProcessing;
	private ArrayList<CustomShape> polygons;
	
	private ToxiclibsSupport toxiclibsSupport;
	
	public BlobScene(KPrez _kprez) {
		
		kprez = _kprez;
		image = new PImage(640/5, 480/5); //param /5
		blobDetection = new BlobDetection(image.width, image.height);
		blobDetection.setPosDiscrimination(true);
		blobDetection.setThreshold(0.2f); //param between 0.0f and 1.0f 
		
		width = 640;
		height = 480;
		
		toxiclibsSupport = new ToxiclibsSupport(kprez);
		
		box2dProcessing = new Box2DProcessing(kprez);
		box2dProcessing.createWorld();
		box2dProcessing.setGravity(0, -20);
		
		polygons = new ArrayList<CustomShape>();
		
	}
	protected void update(){
		
		PImage cam = kprez.bgrd.getImg();
		image.copy(cam, 0, 0, cam.width, cam.height, 0, 0, image.width, image.height);
		fastblur(image, 2);
		blobDetection.computeBlobs(image.pixels);
	}
	protected void display(boolean drawBlobs, boolean drawEdges){
	
		Blob blob;
		EdgeVertex eA, eB;
		
		kprez.noFill();
		
		for(int n=0; n<blobDetection.getBlobNb(); n++){
			blob = blobDetection.getBlob(n);
			if(blob != null){ //param && blob.getEdgeNb(); > 100
				
				if(drawEdges){
					kprez.strokeWeight(3);
					kprez.stroke(kprez.colors.get(2));
					
					for(int m=0; m<blob.getEdgeNb(); m++){
						eA = blob.getEdgeVertexA(m);
						eB = blob.getEdgeVertexB(m);
						if(eA != null && eB != null) kprez.line(eA.x*width, eA.y*height, eB.x*width, eB.y*height);
					}	
				}
				
				if(drawBlobs){
					kprez.strokeWeight(1);
					kprez.stroke(kprez.colors.get(0));
					kprez.rect(blob.xMin*width, blob.yMin*height, blob.w*width, blob.h*height);
				}
			
			}
		}
	}
	protected void updateAndDrawBox2D(){
		
		//PApplet.println(kprez.frameRate);
		if (kprez.frameRate > 15) { //param 29
			polygons.add(new CustomShape(kprez, toxiclibsSupport, box2dProcessing, width/2, -50, -1));
		    polygons.add(new CustomShape(kprez, toxiclibsSupport, box2dProcessing, width/2, -50, kprez.random((float) 2.5, 20)));
		}
		
		box2dProcessing.step();
		
		
		//display user
		
		
		//display shapes
		updateAndDisplayShapes();
		
	}
	private void updateAndDisplayShapes(){
		for (int i = polygons.size()-1; i>0; i--) {
			CustomShape customShape = polygons.get(i);
			if(customShape.done()){
				polygons.remove(i);
			} else {
				customShape.update();
				customShape.display();
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
