package webodrome.scene;

import webodrome.App;
import webodrome.ISprite;
import processing.core.PApplet;

public class DragAndDropScene extends Scene {
	
	private PApplet pApplet;
	private String[] paths = {"a.jpg", "b.jpg", "c.jpg"};
	private ISprite[] images;
	
	public DragAndDropScene(PApplet p){
		
		super();
		pApplet = p;
		images = new ISprite[paths.length];
		
		for(int i=0; i<paths.length; i++){
			String path = "assets/" + paths[i];
			images[i] = new ISprite(pApplet, 100+100*i, 200, path, App.colorsPanel[4]);
		}
		
	}
	public void testCollision(){
		
	}
	
	public void display(){
		for(int i=0; i<paths.length; i++) images[i].display();
	}

}
