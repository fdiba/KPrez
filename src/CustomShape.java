import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import shiffman.box2d.Box2DProcessing;
import toxi.geom.Circle;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.processing.ToxiclibsSupport;

public class CustomShape {
	
	private KPrez kprez;
	private float radius;
	private int color;
	private Box2DProcessing box2dProcessing;
	private Body body;
	private Polygon2D polygon2d; //toxi polygon
	private int[] colors = {-1117720,-13683658,-8410437,-9998215,-1849945,-5517090,-4250587,-14178341,-5804972,-3498634};
	
	private ToxiclibsSupport toxiclibsSupport;
	
	public CustomShape(KPrez _kprez, ToxiclibsSupport _toxiclibsSupport, Box2DProcessing _box2dProcessing, float x, float y, float _radius) {
		
		kprez = _kprez;
		box2dProcessing = _box2dProcessing;
		radius = _radius;
		makeBody(x, y);
		//color = kprez.color(255, 0, 0);
		color = colors[(int) kprez.random(0, colors.length-1)];
		toxiclibsSupport = _toxiclibsSupport;
		
	}
	protected boolean done(){
		Vec2 posScreen = box2dProcessing.getBodyPixelCoord(body); //jbox2d
		boolean offscreen = posScreen.y > 480; //----- edit it ---------//
		if(offscreen) {
			box2dProcessing.destroyBody(body);
			return true;
		}
		return false;
	}
	protected void update(PolygonBlob poly){
		
		
		Vec2 posScreen = box2dProcessing.getBodyPixelCoord(body);
		
		Vec2D toxiScreen = new Vec2D(posScreen.x, posScreen.y);
		
		// check if this shape's position is inside the person's polygon
		boolean inBody = poly.containsPoint(toxiScreen);
		
	    if (inBody) {
	    
	    	// find the closest point on the polygon to the current position
	    	Vec2D closestPoint = toxiScreen;
	    	float closestDistance = 9999999;
	      
	    	for (Vec2D v : poly.vertices) {
	    		float distance = v.distanceTo(toxiScreen);
	    		
	    		if (distance < closestDistance) {
	    			closestDistance = distance;
	    			closestPoint = v;
	    		}
	    	}
		
	    	Vec2 contourPos = new Vec2(closestPoint.x, closestPoint.y);
	    	Vec2 posWorld = box2dProcessing.coordPixelsToWorld(contourPos);
	    
	    	float angle = body.getAngle();
	    
	    	body.setTransform(posWorld, angle);
	    }
		
	}
	protected void display(){
		
		Vec2 pos = box2dProcessing.getBodyPixelCoord(body);
		
		kprez.pushMatrix();
		
			kprez.translate(pos.x, pos.y);
			kprez.noStroke();
			kprez.fill(color);
			
			if(radius == -1){ //polygon
				
				float a = body.getAngle();
				kprez.rotate(-a);
				toxiclibsSupport.polygon2D(polygon2d);
				
			} else {
				kprez.ellipse(0, 0, radius*2, radius*2);
			}
		
		kprez.popMatrix();
		
	}
	private void makeBody(float x, float y){
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(box2dProcessing.coordPixelsToWorld(new Vec2(x, y)));
		
		body = box2dProcessing.createBody(bodyDef);
		body.setLinearVelocity(new Vec2(kprez.random(-8, 8), kprez.random(2, 8)));
		body.setAngularVelocity(kprez.random(-5, 5));
		
		if(radius == -1){ //polygon
		
			PolygonShape polygonShape = new PolygonShape();
			polygon2d = new Circle(kprez.random(5, 20)).toPolygon2D((int) kprez.random(3, 6));
			
			Vec2[] vertices = new Vec2[polygon2d.getNumPoints()]; //jbox2d
			
			for (int i = 0; i < vertices.length; i++) {
				Vec2D v = polygon2d.vertices.get(i); //toxi
				vertices[i] = box2dProcessing.vectorPixelsToWorld(new Vec2(v.x, v.y));
			}
			
			polygonShape.set(vertices, vertices.length);
			body.createFixture(polygonShape, 1);
			
		} else { //circle
			
			CircleShape circleShape = new CircleShape(); //jbox2d
			circleShape.m_radius = box2dProcessing.scalarPixelsToWorld(radius);
			
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = circleShape;
			fixtureDef.density = 1;
			fixtureDef.friction = (float) 0.01;
			fixtureDef.restitution = (float) 0.3;
			
			body.createFixture(fixtureDef);
		}
		
	}

}
