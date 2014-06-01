import processing.core.PApplet;
import processing.core.PImage;

public class Background {
	
	protected String imgType;
	private int lowestValue;
	private int highestValue;
	private boolean switchValue;
	protected KPrez parent;
	private PImage img;
	
	public Background(KPrez _parent, int _lowestValue, int _highestValue, String _imgType) {
		
		parent = _parent;
		lowestValue = _lowestValue;
		highestValue = _highestValue;
		imgType = _imgType;
	}
	protected void toggleValue() {
		switchValue = !switchValue;
	}
	protected void setSelectedValue(int value) {		
		
		if(switchValue){
			lowestValue += value;
			lowestValue = PApplet.constrain(lowestValue, 0, highestValue-100);
			PApplet.println(lowestValue);
		} else {
			highestValue += value;
			highestValue = PApplet.constrain(highestValue, lowestValue+100, 7000);
			PApplet.println(highestValue);
		}
	}
	protected int getSelectedValue() {
		if(switchValue) {
			return lowestValue;
		} else {
			return highestValue;
		}
	}
	protected void setLowestValue(int _lowestValue) {
		lowestValue = _lowestValue;
	}
	protected int getLowestValue() {
		return lowestValue;
	}
	protected void setHighestValue(int _highestValue) {
		highestValue = _highestValue;
	}
	protected int getHighestValue() {
		return highestValue;
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
