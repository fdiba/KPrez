package webodrome.scene;

import webodrome.App;
import webodrome.ISprite;
import webodrome.mainctrl.GesturalInterface;
import processing.core.PApplet;

public class DragAndDropScene extends Scene {
	
	private String[] paths = {"a.jpg", "b.jpg", "c.jpg"};
	private ISprite[] images;
	
	public DragAndDropScene(PApplet _pApplet){
		
		super(_pApplet);
		images = new ISprite[paths.length];
		
		for(int i=0; i<paths.length; i++){
			String path = "assets/" + paths[i];
			images[i] = new ISprite(pApplet, 100+100*i, 200, path, App.colorsPanel[4]);
		}
		
	}
	public void testCollision(GesturalInterface gi){
		
		if (App.userIsTracked) {
					
			for (int i = 0; i < images.length; i++){
						
				if(!images[i].isDragged){
					images[i].hits = 0;
					images[i].testCollision(gi, gi.handControl.rightSP);
					images[i].testCollision(gi, gi.handControl.leftSP);
					images[i].update(gi);
				} else {
					images[i].followSmartPoint(gi);			
				}	
			}
			
		} else {
			
			for (int i = 0; i < images.length; i++){
				images[i].hits = 0;
				images[i].testCollision(gi, gi.handControl.cp);
				images[i].update(gi);
			}
		}
	}
	public void display(){
		for(int i=0; i<paths.length; i++) images[i].display();
	}

}
