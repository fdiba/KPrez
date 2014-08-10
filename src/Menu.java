
import processing.core.PVector;
import webodrome.App;
import webodrome.mainctrl.GesturalInterface;

public class Menu {
	
	protected KPrez parent;
	
	private int offset;
	private PVector location;
	
	private MBouton[] menu = new MBouton[6];
		
	public Menu(KPrez _parent){
		
		parent = _parent;
		offset = 25;
		location = new PVector(160, 160);
		
		for (int i=0; i<menu.length; i++){
			
			switch (i) {
			case 0:
				menu[i] = new MBouton(this, new PVector(location.x, location.y), 60, i+1, "2D");
				break;
			case 1:
				menu[i] = new MBouton(this, new PVector(menu[0].location.x + menu[0].width + offset, menu[0].location.y), menu[0].width, i+1, "3D");
				break;
			case 2:
				menu[i] = new MBouton(this, new PVector(menu[0].location.x,  menu[0].location.y + menu[0].width + offset), menu[0].width, i+1, "3D");
				break;
			case 3:
				menu[i] = new MBouton(this, new PVector(menu[0].location.x + menu[0].width + offset,  menu[0].location.y + menu[0].width + offset), menu[0].width, i+1, "3D");
				break;
			case 4:
				menu[i] = new MBouton(this, new PVector(menu[0].location.x + menu[0].width*2 + offset*2,  menu[0].location.y), menu[0].width, i+1, "2D");
				break;
			case 5:
				menu[i] = new MBouton(this, new PVector(menu[0].location.x + menu[0].width*2 + offset*2,  menu[0].location.y + menu[0].width + offset), menu[0].width, i+1, "2D");
				break;
			default:
				break;
			}
		}
	}
	
	protected void testCollision(GesturalInterface gi){
		
		if (App.userIsTracked && parent.gi.isInPlace()) {
			
			for (int i = 0; i < menu.length; i++){
				menu[i].hits = 0;
				menu[i].testCollision(gi, gi.handControl.rightSP);
				menu[i].testCollision(gi, gi.handControl.leftSP);
				menu[i].update(gi);
			}
			
		} else {
			for (int i = 0; i < menu.length; i++){
				menu[i].hits = 0;
				menu[i].testCollision(gi, gi.handControl.cp);
				menu[i].update(gi);
			}
		}	
	}
	protected void display() {
		
		for (int i=0; i<menu.length; i++){
			menu[i].display();
		}
	}
}
