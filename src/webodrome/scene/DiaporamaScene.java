package webodrome.scene;

import java.util.ArrayList;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;
import webodrome.Board;
import webodrome.mainctrl.GesturalInterface;

public class DiaporamaScene extends Scene {
	
	public int counter;
	
	private int boardId;
	private ArrayList<Board> boards;
	private String[] images = {"ptp1.jpg", "ptp2.jpg"};
	private boolean screenAvailable;
	private int timeToBeDisplayed;
	
	public DiaporamaScene(PApplet _pApplet, Object[][] objects){
		
		super(_pApplet, objects);
		
		counter = 48;
		boardId = 0;
		
		boards = new ArrayList<Board>();
		for (int i=0; i < images.length; i++){
			Board board = new Board(pApplet, images[i]);
			boards.add(board);
		}
	}
	public void pointAndMoveInTheRightDirection(){
		pApplet.translate(params.get("xTrans"), params.get("yTrans"), params.get("zTrans"));
		pApplet.rotateX(PApplet.radians(params.get("rotateXangle")));
		pApplet.rotateY(PApplet.radians(params.get("rotateYangle")));
		pApplet.rotateZ(PApplet.radians(params.get("rotateZangle")));
	}
	public void testScreenDisplay(SimpleOpenNI context, GesturalInterface gi) {
		
		PVector rightHand = gi.getRightHand();
		PVector leftHand = gi.getLeftHand();
		PVector torso = gi.getTorsoPos(context, gi);
		
		float distBetweenHands = PApplet.dist(rightHand.x, rightHand.y, rightHand.z, leftHand.x, leftHand.y, leftHand.z);
		
		PVector pointBetweenHands = gi.middlePoint(rightHand, leftHand);
		
		float distFromTorso = PApplet.dist(pointBetweenHands.x, pointBetweenHands.y, pointBetweenHands.z, torso.x, torso.y, torso.z);
		
		if(gi.isInPlace()){

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
	public boolean screenAvailable(){
		return screenAvailable;
	}
	public void update(){
		if(counter>0)counter--;
		menu.update(pApplet);	
	}
	public void displayScreen(GesturalInterface gi) {
		
		Board board = boards.get(boardId);
		
		if(timeToBeDisplayed >= 255-55){
			board.setLocation3d(gi.getMiddlePoint());
		}
		board.display(timeToBeDisplayed);
	}
}
