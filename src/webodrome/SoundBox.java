package webodrome;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class SoundBox {
	
	private PApplet pApplet;
	private int hits;
	private PVector location;
	private int width;
	private int couleur;
	private int alpha;
	private int angleRotation;
	
	public SoundBox(PApplet _pApplet, Minim minim, AudioPlayer player, PVector _location, int _color){
		
		pApplet = _pApplet;
		
		location = _location;
		width = 150;
		couleur = _color;
		alpha = 127;
		hits = 0;
		angleRotation = (int) pApplet.random(0, 360);
		
	}
	protected void update(Minim minim, AudioPlayer player){
		
		angleRotation++;
		alpha = (int) PApplet.map(hits, 0, 40, 0, 255);
		
		if(alpha >= 255 && !player.isPlaying()){
			player.play();
		} else if(!player.isPlaying()){
			player.rewind();
		}
			
		
	}
	protected void display(){
		pApplet.pushMatrix();
			pApplet.fill(couleur, alpha);
			pApplet.stroke(couleur);
			pApplet.translate(location.x, location.y, location.z);
			pApplet.rotateY(PApplet.radians(angleRotation));
			pApplet.box(width);
		pApplet.popMatrix();
	}
	protected void reset(){
		hits = 0;
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
}
