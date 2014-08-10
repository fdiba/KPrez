package webodrome.scene;

import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;
import webodrome.ctrl.Menu;


public class Scene {
	
	protected PApplet pApplet;
	protected int width, height;
	
	public Map<String, Integer> params;
	public Menu menu;
	
	public Scene(Object[][] objects){
		params = new HashMap<String, Integer>();
		createMenu(objects);
	}
	public Scene(){
		menu = null;
	}
	protected void createMenu(Object[][] objects){	
		menu = new Menu(this, new PVector(450, 50), objects);
	}

}
