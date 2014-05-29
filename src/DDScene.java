public class DDScene {
	
	private KPrez parent;
	private String[] paths = {"a.jpg", "b.jpg", "c.jpg"};
	private ISprite[] images;
	
	public DDScene(KPrez _parent){
		
		parent = _parent;
		images = new ISprite[paths.length];
		
		for(int i=0; i<paths.length; i++){
			
			String path = "assets/" + paths[i];
			images[i] = new ISprite(parent, parent.random(parent.width/2-75), parent.random(parent.height-75), path);
			
		}
	
	}
	protected void testCollision(){
		
		if (parent.isTracked) {
			
			for (int i = 0; i < images.length; i++){
				images[i].hits = 0;
				images[i].testCollision(parent.handControl.rightHand);
				images[i].testCollision(parent.handControl.leftHand);
				images[i].update();
			}
			
		} else {
			for (int i = 0; i < images.length; i++){
				images[i].hits = 0;
				images[i].testCollision(parent.handControl.cp);
				images[i].update();
			}
		}
	}
	protected void display(){
		
		for(int i=0; i<paths.length; i++){
			
			images[i].display();
			
		}	
	}
}
