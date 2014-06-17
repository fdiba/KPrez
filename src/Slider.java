import processing.core.PApplet;
import processing.core.PVector;

public class Slider {
	
	private KPrez kprez;
	private PVector location;
	private SliderController sliderCtrl;
	private int width;
	protected boolean dragging;
	private float maxValue;
	private float lowValue;
	private float value;
	private float lowXPos;
	private float maxYPos;
	private String param;
	private int color;
	
	public Slider(KPrez _kprez, PVector _location, String _param, float _lowValue, float _maxValue, int _color) {
		kprez = _kprez;
		location = _location;
		width = 100; 
		sliderCtrl = new SliderController(kprez, new PVector(location.x+width/2, location.y));
		param = _param;
		
		lowValue = _lowValue;
		maxValue = _maxValue;
		
		lowXPos = location.x;
		maxYPos = location.x + width;
		
		color = _color;
	}
	protected void update(PVector mousePosition){
		
		
		
		if(mousePosition.x > sliderCtrl.location.x - sliderCtrl.width/2 && mousePosition.x < sliderCtrl.location.x + sliderCtrl.width/2 &&
		   mousePosition.y > sliderCtrl.location.y - sliderCtrl.width/2 && mousePosition.y <sliderCtrl. location.y + sliderCtrl.width/2){
			
			//PApplet.println("hit");
			dragging = true;
			//sliderCtrl.location.x = mousePosition.x;
		} 
		
	}
	/*protected void editSliderCtrlPosition(int _x){
		sliderCtrl.location.x = location.x + _x;
	}*/
	protected void followMouse(){
		if(dragging) {
			sliderCtrl.location.x = kprez.mouseX;
			if(sliderCtrl.location.x <= lowXPos) {
				sliderCtrl.location.x = lowXPos;
			} else if (sliderCtrl.location.x >= maxYPos) {
				sliderCtrl.location.x = maxYPos;
			}
			editValue();
		}
	}
	private void editValue(){
		
		value = PApplet.map(sliderCtrl.location.x, lowXPos, maxYPos, lowValue, maxValue);
		//PApplet.println(param + ": " + value);
		
		switch (param) {
		case "rotateY":
			kprez.rotateYangle = value;
			break;
		case "rotateX":
			kprez.rotateXangle = value;
			break;
		case "zTrans":
			kprez.zTrans = value;
			break;
		case "xTrans":
			kprez.xTrans = value;
			break;
		default:
			break;
		}
		
	}
	protected void reset(){
		dragging = false;
	}
	protected void display(){
		kprez.rectMode(PApplet.CORNER);
		kprez.noStroke();
		kprez.fill(color);
		kprez.rect(location.x, location.y, width, 10);
		sliderCtrl.display();
	}
}
