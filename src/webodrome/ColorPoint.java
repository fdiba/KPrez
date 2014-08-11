package webodrome;

import processing.core.PVector;

public class ColorPoint {
	
	public PVector location;
	public int color = 255 << 24 | 255 << 16 | 0 << 8 | 0;
	
	private boolean isHidden;
	
	public ColorPoint(PVector _location, int _color) {
		location = _location;
		color = _color;
	}
	public ColorPoint(PVector _location, boolean _isHidden) { //need to be true;
		location = _location;
		isHidden = _isHidden;
	}
	protected void setIsHidden(boolean value){
		isHidden = value;
	}
	public boolean getIsHidden(){
		return isHidden;
	}
}
