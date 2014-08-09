package webodrome;

import webodrome.ctrl.BehringerBCF;
import webodrome.ctrl.Menu;
import webodrome.scene.Scene;

public class App {
	
	public final static boolean BCF2000 = true;
	public final static int[] colors = {-8410437,-9998215,-1849945,-5517090,-4250587,-14178341,-5804972,-3498634};
	
	private static Scene actualScene;
	public static Menu actualMenu;
	public static BehringerBCF behringer;
	
	public static int width = 640;
	public static int height = 480;
	
	public App() {
		// TODO Auto-generated constructor stub
	}
	public static Scene getActualScene() {
		return actualScene;
	}
	public static void setActualScene(Scene actualScene) {
		App.actualScene = actualScene;
		App.actualMenu = actualScene.menu;
	}

}
