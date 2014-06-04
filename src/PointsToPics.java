import java.util.ArrayList;

import processing.core.PVector;

public class PointsToPics {
	
	private KPrez parent;
	private ArrayList<Board> boards;
	private int[] colors;
	private int boardId;

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
	protected void nextDisplay(){
		boardId++;
		if(boardId>=boards.size()) boardId=0;
	}
	protected void update() {
		
	}
	protected void display() {
		
	}
	public void displayScreen(PVector pvector) {
		Board board = boards.get(boardId);
		board.display(pvector);
	}
}
