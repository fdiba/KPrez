import processing.core.PVector;

public class Menu {
	
	protected KPrez parent;
	
	private int offset;
	private PVector location;
	
	private MBouton[] menu = new MBouton[3];
		
	public Menu(KPrez _parent){
		
		parent = _parent;
		offset = 25;
		location = new PVector(260, 160);
		
		for (int i=0; i<menu.length; i++){
			
			switch (i) {
			case 0:
				menu[i] = new MBouton(this, location.x, location.y, 60, i+1, "2D");
				break;
			case 1:
				menu[i] = new MBouton(this, menu[0].location.x + menu[0].width + offset, menu[0].location.y, menu[0].width, i+1, "3D");
				break;
			case 2:
				menu[i] = new MBouton(this, menu[0].location.x,  menu[0].location.y + menu[0].width + offset, menu[0].width, i+1, "3D");
				break;
			default:
				break;
			}
		}
	}
	
	protected void testCollision(){
		
		if (parent.gi.isTracked && parent.gi.isInPlace()) {
			
			for (int i = 0; i < menu.length; i++){
				menu[i].hits = 0;
				menu[i].testCollision(parent.gi.handControl.rightSP);
				menu[i].testCollision(parent.gi.handControl.leftSP);
				menu[i].update();
			}
			
		} else {
			for (int i = 0; i < menu.length; i++){
				menu[i].hits = 0;
				menu[i].testCollision(parent.gi.handControl.cp);
				menu[i].update();
			}
		}	
	}
	protected void display() {
		
		for (int i=0; i<menu.length; i++){
			menu[i].display();
		}
	}
}
