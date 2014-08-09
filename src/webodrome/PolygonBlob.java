package webodrome;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import blobDetection.BlobDetection;

public class PolygonBlob extends Polygon2D {
	
	private Body body;
	
	public PolygonBlob() {
		// TODO Auto-generated constructor stub
	}
	public void create(BlobDetection blobDetection, ArrayList<ArrayList<PVector>> contours){
		
		ArrayList<PVector> userContour = new ArrayList<PVector>();
		int userContourLength = 0;
		
		//select the contour of the user
		for (ArrayList<PVector> contour : contours){
			
			if(contour.size() > userContourLength){	
				userContourLength = contour.size();
				userContour = contour;
			}
			
		}
	    
		if(userContourLength >  1){
	        for (PVector p : userContour) {
	        	add(new Vec2D(p.x, p.y));
	        }
		}
	        
	     
		
	}
	public void createBody(Box2DProcessing box2d){
		
		BodyDef bodyDef = new BodyDef();
		body = box2d.createBody(bodyDef);
		
		// if there are more than 0 points (aka a person on screen)...
		if(getNumPoints() > 0){
			
			Vec2[] verts = new Vec2[getNumPoints()];

			for (int i = 0; i < getNumPoints(); i++) {
				Vec2D v = vertices.get(i);
				verts[i] = box2d.coordPixelsToWorld(v.x, v.y); 
			}
			
			// create a chain from the array of vertices
		    ChainShape chain = new ChainShape();
		    chain.createChain(verts, verts.length);
		    
		    // create fixture in body from the chain (this makes it actually deflect other shapes)
		    body.createFixture(chain, 1);
			
		}
		
	}
	public void destroyBody(Box2DProcessing box2d){
		box2d.destroyBody(body);
	}
}
