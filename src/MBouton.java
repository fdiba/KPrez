public class MBouton extends Bouton {
	private KPrez kprez;
	private int sceneId;
	
	public MBouton(Menu _parent, float _x, float _y, int _radius, int _sceneId){
		
		super(_parent.parent, _x, _y, _radius);
		
		kprez = _parent.parent;
		
		sceneId = _sceneId;

	}

	protected void hasBeenSelected(){
		
		if(alpha < 255){
			alpha += a_speed;
		} else if (alpha >= 255){
			alpha = 0;
			kprez.editScene(sceneId);
		}
	}
	
}
