import java.util.ArrayList;
import java.util.Collections;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PApplet;
import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;

public class PolygonBlob extends Polygon2D {
	
	private Body body;
	
	private int width;
	private int height;
	
	public PolygonBlob() {
		width = 640;
		height = 480;
	}
protected void create(BlobDetection blobDetection){
		
		ArrayList<ArrayList<PVector>> contours = new ArrayList<ArrayList<PVector>>();

	    int selectedContour = 0;
	    int selectedPoint = 0;
	    
	    for (int n = 0; n < blobDetection.getBlobNb(); n++) {
	    	
	    	Blob blob = blobDetection.getBlob(n);
	    	
	    	if (blob != null && blob.getEdgeNb() > 100) {
	    		
	    		ArrayList<PVector> contour = new ArrayList<PVector>();
	    		
	    		for (int m=0; m < blob.getEdgeNb(); m++) {
	    			
	    			EdgeVertex eA = blob.getEdgeVertexA(m);
					EdgeVertex eB = blob.getEdgeVertexB(m);
					
					if (eA != null && eB != null) {
						
						//get next and previous edgeVertexA
			            EdgeVertex fn = blob.getEdgeVertexA((m+1) % blob.getEdgeNb());
			            EdgeVertex fp = blob.getEdgeVertexA((PApplet.max(0, m-1)));
						
			            // calculate distance between vertexA and next and previous edgeVertexA respectively
			            // positions are multiplied by kinect dimensions because the blob library returns normalized values
			            float dn = PApplet.dist(eA.x*width, eA.y*height, fn.x*width, fn.y*height);
			            float dp = PApplet.dist(eA.x*width, eA.y*height, fp.x*width, fp.y*height);
			            
			            if (dn > 5 || dp > 5) { //add param
			            	
			            	if (contour.size() > 0) {
			            		contour.add(new PVector(eB.x*width, eB.y*height)); // add final point
			                    contours.add(contour);
			                    contour = new ArrayList<PVector>();
			                } else {  // if the current contour size is 0 (aka it's a new list) add the point to the list
			                	contour.add(new PVector(eA.x*width, eA.y*height));
			                }
			            } else {  // if both distance are smaller than 15 (aka the points are close) add the point to the list
			            	//contour.add(new PVector(eA.x*width, eA.y*height)); //comment to space out points
			            }
		   
					}
					
	    		}
	    		
	    	}
	    	
		}
	    
	    /* 
	     * at this point in the code we have a list of contours (aka an arrayList of arrayLists of PVectors)
	     *  now we need to sort those contours into a correct polygon. To do this we need two things:
	     *  1. The correct order of contours
	     *  2. The correct direction of each contour
	     */
	    
	    while (contours.size() > 0) {
	    	
	        float distance = 999999999;
	        
	        //if there are already points in the polygon
	        if (getNumPoints() > 0) {
	        
	          Vec2D vecLastPoint = vertices.get(getNumPoints()-1);
	          // use the polygon's last point as a starting point
	          PVector lastPoint = new PVector(vecLastPoint.x, vecLastPoint.y);

	          for (int i=0; i<contours.size(); i++) {
	        	 
	            ArrayList<PVector> c = contours.get(i);
	            
	            PVector fp = c.get(0);
	            PVector lp = c.get(c.size()-1);
	            
	            if (fp.dist(lastPoint) < distance) {
	            	
	              distance = fp.dist(lastPoint);
	              
	              selectedContour = i;
	              selectedPoint = 0;
	              
	            }
	            
	            if (lp.dist(lastPoint) < distance) {
	            	
	              distance = lp.dist(lastPoint);
	              selectedContour = i;
	              selectedPoint = 1;
	              
	            }
	          }
	        } else {  // if the polygon is still empty
	        	
	        	PVector closestPoint = new PVector(width, height);

	            for (int i=0; i<contours.size(); i++) {
	           	
	            	ArrayList<PVector> c = contours.get(i);
	            	
	            	PVector fp = c.get(0);
	            	PVector lp = c.get(c.size()-1);
	            	
	            	// if the first point is in the lowest 5 pixels of the (kinect) screen and more to the left than the current closestPoint
	            	if (fp.y > height-5 && fp.x < closestPoint.x) {
	            		closestPoint = fp;
	            		selectedContour = i;
	            		selectedPoint = 0;
	            	}
	            	
	            	// if the last point is in the lowest 5 pixels of the (kinect) screen and more to the left than the current closestPoint
	            	if (lp.y > height-5 && lp.x < closestPoint.x) {
	            		closestPoint = lp;
	            		selectedContour = i;
	            		selectedPoint = 1;
	            	}
	            }
	        }
	        
	        // add contour to polygon
	        ArrayList<PVector> contour = contours.get(selectedContour);
	        
	        if (selectedPoint > 0) {
	        	Collections.reverse(contour);
	        }
	        
	        for (PVector p : contour) {
	        	add(new Vec2D(p.x, p.y));
	        }
	        
	        contours.remove(selectedContour);
		}
		
	}
	protected void create2(BlobDetection blobDetection){
		
		ArrayList<ArrayList<PVector>> contours = new ArrayList<ArrayList<PVector>>();

	    int selectedContour = 0;
	    int selectedPoint = 0;
	    
	    for (int n = 0; n < blobDetection.getBlobNb(); n++) {
	    	
	    	Blob blob = blobDetection.getBlob(n);
	    	
	    	if (blob != null && blob.getEdgeNb() > 100) {
	    		
	    		ArrayList<PVector> contour = new ArrayList<PVector>();
	    		
	    		for (int m=0; m < blob.getEdgeNb(); m++) {
	    			
	    			EdgeVertex eA = blob.getEdgeVertexA(m);
					EdgeVertex eB = blob.getEdgeVertexB(m);
					
					if (eA != null && eB != null) {
						
						//get next and previous edgeVertexA
			            EdgeVertex fn = blob.getEdgeVertexA((m+1) % blob.getEdgeNb());
			            EdgeVertex fp = blob.getEdgeVertexA((PApplet.max(0, m-1)));
						
			            // calculate distance between vertexA and next and previous edgeVertexA respectively
			            // positions are multiplied by kinect dimensions because the blob library returns normalized values
			            float dn = PApplet.dist(eA.x*width, eA.y*height, fn.x*width, fn.y*height);
			            float dp = PApplet.dist(eA.x*width, eA.y*height, fp.x*width, fp.y*height);
			            
			            if (dn > 5 || dp > 5) { //add param
			            	
			            	if (contour.size() > 0) {
			            		contour.add(new PVector(eB.x*width, eB.y*height)); // add final point
			                    contours.add(contour);
			                    contour = new ArrayList<PVector>();
			                } else {  // if the current contour size is 0 (aka it's a new list) add the point to the list
			                	contour.add(new PVector(eA.x*width, eA.y*height));
			                }
			            } else {  // if both distance are smaller than 15 (aka the points are close) add the point to the list
			            	//contour.add(new PVector(eA.x*width, eA.y*height)); //comment to space out points
			            }
		   
					}
					
	    		}
	    		
	    	}
	    	
		}
	    
	    /* 
	     * at this point in the code we have a list of contours (aka an arrayList of arrayLists of PVectors)
	     *  now we need to sort those contours into a correct polygon. To do this we need two things:
	     *  1. The correct order of contours
	     *  2. The correct direction of each contour
	     */
	    
	    while (contours.size() > 0) {
	    	
	        float distance = 999999999;
	        
	        //if there are already points in the polygon
	        if (getNumPoints() > 0) {
	        
	          Vec2D vecLastPoint = vertices.get(getNumPoints()-1);
	          // use the polygon's last point as a starting point
	          PVector lastPoint = new PVector(vecLastPoint.x, vecLastPoint.y);

	          for (int i=0; i<contours.size(); i++) {
	        	 
	            ArrayList<PVector> c = contours.get(i);
	            
	            PVector fp = c.get(0);
	            PVector lp = c.get(c.size()-1);
	            
	            if (fp.dist(lastPoint) < distance) {
	            	
	              distance = fp.dist(lastPoint);
	              
	              selectedContour = i;
	              selectedPoint = 0;
	              
	            }
	            
	            if (lp.dist(lastPoint) < distance) {
	            	
	              distance = lp.dist(lastPoint);
	              selectedContour = i;
	              selectedPoint = 1;
	              
	            }
	          }
	        } else {  // if the polygon is still empty
	        	
	        	PVector closestPoint = new PVector(width, height);

	            for (int i=0; i<contours.size(); i++) {
	           	
	            	ArrayList<PVector> c = contours.get(i);
	            	
	            	PVector fp = c.get(0);
	            	PVector lp = c.get(c.size()-1);
	            	
	            	// if the first point is in the lowest 5 pixels of the (kinect) screen and more to the left than the current closestPoint
	            	if (fp.y > height-5 && fp.x < closestPoint.x) {
	            		closestPoint = fp;
	            		selectedContour = i;
	            		selectedPoint = 0;
	            	}
	            	
	            	// if the last point is in the lowest 5 pixels of the (kinect) screen and more to the left than the current closestPoint
	            	if (lp.y > height-5 && lp.x < closestPoint.x) {
	            		closestPoint = lp;
	            		selectedContour = i;
	            		selectedPoint = 1;
	            	}
	            }
	        }
	        
	        // add contour to polygon
	        ArrayList<PVector> contour = contours.get(selectedContour);
	        
	        if (selectedPoint > 0) {
	        	Collections.reverse(contour);
	        }
	        
	        for (PVector p : contour) {
	        	add(new Vec2D(p.x, p.y));
	        }
	        
	        contours.remove(selectedContour);
		}
		
	}
	protected void createBody(Box2DProcessing box2d){
		
		BodyDef bodyDef = new BodyDef();
		body = box2d.createBody(bodyDef);
		// if there are more than 0 points (aka a person on screen)...
		if(getNumPoints() > 0){
			
			//Vec2[] verts = new Vec2[getNumPoints()];
			Vec2[] verts = new Vec2[getNumPoints()/2];

		    for (int i = 0; i < getNumPoints()/2; i++) {
			//for (int i = 0; i < getNumPoints(); i++) {
				
		    	Vec2D v = vertices.get(i*2);
		    	//Vec2D v = vertices.get(i);
				verts[i] = box2d.coordPixelsToWorld(v.x, v.y); 
			}
			
			// create a chain from the array of vertices
		    ChainShape chain = new ChainShape();
		    chain.createChain(verts, verts.length);
		    
		    // create fixture in body from the chain (this makes it actually deflect other shapes)
		    body.createFixture(chain, 1);
			
		}
		
	}
	protected void destroyBody(Box2DProcessing box2d){
		box2d.destroyBody(body);
	}
}
