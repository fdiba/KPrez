import processing.core.PApplet;
import processing.core.PImage;

public class Background {
	
	protected String imgType;
	private int lowestValue;
	private int highestValue;
	protected KPrez parent;
	private PImage img;
	
	public Background(KPrez _parent, int _lowestValue, int _highestValue, String _imgType) {
		
		parent = _parent;
		lowestValue = _lowestValue;
		highestValue = _highestValue;
		imgType = _imgType;
	}
	protected void setValues(int _lowestValue, int _highestValue) {
		lowestValue = _lowestValue;
		highestValue = _highestValue;
	}
	protected void setImg(String _imgType) {
		imgType = _imgType;
	}
	protected void update() {
		
		int[] depthValues = parent.context.depthMap();
		
		switch (imgType) {
		case "userImage":
			img = parent.context.userImage();
			break;
		default:
			img = parent.context.depthImage();
			break;
		}
		
		int mapWidth = parent.context.depthWidth();
		int mapHeight = parent.context.depthHeight();
		
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				int i = x + y * mapWidth;
				int currentDepthValue = depthValues[i];
				if (currentDepthValue < lowestValue || currentDepthValue > highestValue) {
					img.pixels[i] = 0;
				}
		    }
		}
	}
	protected void display() {
		parent.noFill();
		parent.stroke(255);
		parent.rectMode(PApplet.CORNER);
		parent.rect(0, 0, img.width, img.height);
		
		parent.image(img, 0, 0);
	}
}
