
import processing.core.PApplet;
import processing.core.PVector;

public class SoundBox {
	private KPrez kprez;
	private PVector location;
	private int width;
	private int couleur;
	private int alpha;
	private int hits;
	private int angleRotation;
	private String sound;
	private int position;
	private int length;
	
	public SoundBox(KPrez _kprez, PVector _location, int _couleur) {
		kprez = _kprez;
		location = _location;
		width = 150;
		couleur = _couleur;
		alpha = 127;
		hits = 0;
		angleRotation = (int) kprez.random(0, 360);
		sound = "assets/" + "kick.wav";
		kprez.player = kprez.minim.loadFile(sound);
	}
	protected void update(){
		angleRotation++;
		alpha = (int) PApplet.map(hits, 0, 40, 0, 255);
		//PApplet.println(hits);
		if(kprez.player.isPlaying()){
			position = kprez.player.position();
		}
		//new sound
		if(alpha >= 255 && position >= length - 100) { //debug
			//PApplet.println("sound");
			kprez.player.close();
			kprez.player = kprez.minim.loadFile(sound);
			kprez.player.play();
			length = kprez.player.length();
		}
		//PApplet.println(position+ " " + length);
	}
	protected void display(){
		kprez.pushMatrix();
			kprez.fill(couleur, alpha);
			kprez.stroke(couleur);
			kprez.translate(location.x, location.y, location.z);
			kprez.rotateY(PApplet.radians(angleRotation));
			kprez.box(width);
		kprez.popMatrix();
	}
	protected void isHit(PVector pvector){
		if (pvector.x > location.x - width/2 && pvector.x < location.x + width/2) {
			if (pvector.y > location.y - width/2 && pvector.y < location.y + width/2) {
				if (pvector.z > location.z - width/2 && pvector.z < location.z + width/2) {
					hits++;
		        }
		    }
		}
	}
	protected void reinit(){
		hits = 0;
	}
}
