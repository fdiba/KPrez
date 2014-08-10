
import java.util.ArrayList;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;
import webodrome.mainctrl.GesturalInterface;

public class PointsToPics {
	
	private KPrez parent;
	private ArrayList<Board> boards;
	private String[] images = {"ptp1.jpg", "ptp2.jpg"};
	private int boardId;
	private boolean screenAvailable;
	private int timeToBeDisplayed;
	protected int counter;

	public PointsToPics(KPrez _parent){
		
		parent = _parent;
		counter = 48;
		boardId = 0;
		
		boards = new ArrayList<Board>();
		for (int i=0; i < images.length; i++){
			Board board = new Board(parent, images[i]);
			boards.add(board);
		}
	}
	protected void init(){
		counter = 48;
	}
	protected void testScreenDisplay(SimpleOpenNI context, GesturalInterface gi) {
		
		PVector rightHand = gi.getRightHand();
		PVector leftHand = gi.getLeftHand();
		PVector torso = gi.getTorsoPos(context, gi);
		
		float distBetweenHands = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		//PApplet.println(distance);
		
		PVector pointBetweenHands = gi.middlePoint(rightHand, leftHand);
		
		float distFromTorso = PApplet.dist(pointBetweenHands.x, pointBetweenHands.y, pointBetweenHands.z, torso.x, torso.y, torso.z);
		
		if(parent.gi.isInPlace()){
			//PApplet.println("ON");
			if(distBetweenHands > 750 && distBetweenHands > 850 && distFromTorso < 350 &&
			  (rightHand.y - leftHand.y < 10 || leftHand.y - rightHand.y < 10)) {
				if(!screenAvailable){
					screenAvailable = true;
					timeToBeDisplayed = 255;
				} else if (screenAvailable && timeToBeDisplayed < 255){
					timeToBeDisplayed += 5;
				}
			} else {
				endingScreenDisplay();
			}
		} else {
			//PApplet.println("OFF");
			endingScreenDisplay();
		}
	}
	private void endingScreenDisplay(){
		if(timeToBeDisplayed>0) timeToBeDisplayed -= 5;
		if(timeToBeDisplayed <= 0 && screenAvailable){
			screenAvailable = false;
			nextDisplay();
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
		if(counter>0)counter--;
	}
	protected void display() {
		
	}
	public void displayScreen(GesturalInterface gi) {
		
		Board board = boards.get(boardId);
		
		if(timeToBeDisplayed >= 255-55){
			board.setLocation3d(gi.getMiddlePoint());
		}
		board.display(timeToBeDisplayed);
	}
}
