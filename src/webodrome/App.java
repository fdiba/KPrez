package webodrome;

import webodrome.ctrl.BehringerBCF;
import webodrome.ctrl.Menu;
import webodrome.mainctrl.GesturalInterface;
import webodrome.scene.Scene;

public class App {
	
	public final static boolean BCF2000 = true;
	
	//colors : red, green, blue green, orange, dark blue
	public final static int[] colorsPanel = {255 << 24 | 240 << 16 | 65 << 8 | 50,
											 255 << 24 | 135 << 16 | 205 << 8 | 137,
											 255 << 24 | 40 << 16 | 135 << 8 | 145,
											 255 << 24 | 252 << 16 | 177 << 8 | 135,
											 255 << 24 | 15 << 16 | 65 << 8 | 85};
	
	public final static int[] colors = {-8410437,-9998215,-1849945,-5517090,-4250587,-14178341,-5804972,-3498634};
	
	public static int resolution;
	
	private static Scene actualScene;
	
	public static Menu actualMenu;
	public static BehringerBCF behringer;
	
	public static int width = 640;
	public static int height = 480;
	
	public static boolean userIsTracked;

	private static int sceneId = 0;
	
	public App() {
		// TODO Auto-generated constructor stub
	}
	public static int getSceneId(){
		return sceneId;
	}
	public static void setSceneId(GesturalInterface gi, int _sceneId, String _mode) {
		sceneId = _sceneId;
		gi.setWorld(_mode);
	}
	public static Scene getActualScene() {
		return actualScene;
	}
	public static void setActualScene(Scene actualScene) {
		App.actualScene = actualScene;
		App.actualMenu = actualScene.menu;
	}

}
