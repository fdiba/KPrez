import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class PointsToPics {
	
	private KPrez parent;
	private ArrayList<Board> boards;
	private int[] colors;
	private int boardId;
	private boolean screenAvailable;
	private int timeToBeDisplayed;

	public PointsToPics(KPrez _parent){
		
		parent = _parent;
		
		colors = new int[2];
		colors[0] = parent.color(0, 0, 255);
		colors[1] = parent.color(0, 255, 0);
		boardId = 0;
		
		boards = new ArrayList<Board>();
		for (int i=0; i < colors.length; i++){
			Board board = new Board(parent, 640, 480, colors[i]);
			boards.add(board);
		}
	}
	protected void testScreenDisplay() {
		
		PVector rightHand = parent.gi.rightHand();
		PVector leftHand = parent.gi.leftHand();
		PVector torso = parent.gi.torso();
		
		float distBetweenHands = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		//PApplet.println(distance);
		
		PVector pointBetweenHands = parent.gi.middlePoint(rightHand, leftHand);
		
		float distFromTorso = PApplet.dist(pointBetweenHands.x, pointBetweenHands.y, pointBetweenHands.z, torso.x, torso.y, torso.z);
		
		if(distBetweenHands > 750 && distBetweenHands > 850 && distFromTorso < 250 &&
		  (rightHand.y - leftHand.y < 10 || leftHand.y - rightHand.y < 10)) {
			if(!screenAvailable){
				screenAvailable = true;
				timeToBeDisplayed = 255;
			} else if (screenAvailable && timeToBeDisplayed < 255){
				timeToBeDisplayed += 5;
			}
		} else {
			timeToBeDisplayed -= 5;
			if(timeToBeDisplayed <= 0 && screenAvailable){
				screenAvailable = false;
				nextDisplay();
			}
		}
	}
	private void nextDisplay(){
		boardId++;
		if(boardId>=boards.size()) boardId=0;
	}
	protected boolean screenAvailable(){
		return screenAvailable;
	}
	protected void update() {
		
	}
	protected void display() {
		
	}
	public void displayScreen(PVector pvector) {
		Board board = boards.get(boardId);
		board.display(pvector, timeToBeDisplayed);
	}
}
