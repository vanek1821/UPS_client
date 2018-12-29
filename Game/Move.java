package Game;

public class Move {

	private Coordinates first, second;
	
	public Move(Coordinates first, Coordinates second) {
		this.first = first;
		this.second = second;
	}
	public Move(Coordinates first) {
		this.first = first;
	}
	public Move(int sRow, int sCol, int dRow, int dCol) {
		this.first = new Coordinates(sRow, sCol);
		this.second = new Coordinates(dRow, dCol);
	}
	
	public void setFirstMove(int row, int col) {
		this.first = new Coordinates(row, col);
	}
	public void setFirstMove(Coordinates first) {
		this.first = first;
	}
	public void setSecMove(int row, int col) {
		this.second = new Coordinates(row, col);
	}
	public void setSecMove(Coordinates second) {
		this.second = second;
	}
	
	public Coordinates getFirstMove() {
		return this.first;
	}
	public Coordinates getSecMove() {
		return this.second;
	}
	
	public String toString() {
		return first.getRow() + ";" + first.getCol() + ";" + second.getRow() + ";" + second.getCol();
		
	}
}
