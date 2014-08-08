package webodrome;

import webodrome.ctrl.BehringerBCF;
import webodrome.ctrl.Menu;
import webodrome.scene.Scene;

public class App {
	
	public static final boolean BCF2000 = true;
	private static Scene actualScene;
	public static Menu actualMenu;
	public static BehringerBCF behringer;
	
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
