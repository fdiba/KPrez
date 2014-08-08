
public class DDScene {
	
	private KPrez kprez;
	private String[] paths = {"a.jpg", "b.jpg", "c.jpg"};
	private ISprite[] images;
	
	public DDScene(KPrez _kprez){
		
		kprez = _kprez;
		images = new ISprite[paths.length];

		for(int i=0; i<paths.length; i++){
			String path = "assets/" + paths[i];
			//images[i] = new ISprite(kprez, 100+100*i, 100, path);
			images[i] = new ISprite(kprez, 100+100*i, 200, path);
		}
	
	}
	protected void testCollision(){
		
		if (kprez.gi.isTracked) {
			
			for (int i = 0; i < images.length; i++){
				
				if(!images[i].isDragged){
				
					images[i].hits = 0;
					images[i].testCollision(kprez.gi.handControl.rightSP);
					images[i].testCollision(kprez.gi.handControl.leftSP);
					images[i].update();
				} else {
					images[i].followSmartPoint();
				}
			}
			
		} else {
			for (int i = 0; i < images.length; i++){
				images[i].hits = 0;
				images[i].testCollision(kprez.gi.handControl.cp);
				images[i].update();
			}
		}
	}
	protected void display(){
		for(int i=0; i<paths.length; i++) images[i].display();
	}
}
