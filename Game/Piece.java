package Game;

import enums.Color;
import enums.PieceType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import Constants.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Piece {

	private PieceType type;
	private Color color;
	private ImageView view;
	
	public Piece(PieceType type, Color color){
		this.type = type;
		this.color = color;
		this.view = new ImageView();
		try {
			view.setImage(this.getImage());
			view.setFitWidth(Constants.FIELD_SIZE);
			view.setFitHeight(Constants.FIELD_SIZE);
		} catch (Exception e) {
			System.out.println("File not found");
		}
	}
	
	public ImageView getImageView() {
		return this.view;
	}
	
	public Image getImage() throws FileNotFoundException {
		switch (color) {
		case WHITE:
			switch (type) {
			case KING:
				return new Image(new FileInputStream(Constants.W_KING_FILE));

			case QUEEN:
				return new Image(new FileInputStream(Constants.W_QUEEN_FILE));
			
			case BISHOP:
				return new Image(new FileInputStream(Constants.W_BISHOP_FILE));
			
			case ROOK:
				return new Image(new FileInputStream(Constants.W_ROOK_FILE));
		
			case KNIGHT:
				return new Image(new FileInputStream(Constants.W_KNIGHT_FILE));
			
			case PAWN:
				return new Image(new FileInputStream(Constants.W_PAWN_FILE));
						
				}
			break;
		
		case BLACK:
			switch (type) {
			case KING:
				return new Image(new FileInputStream(Constants.B_KING_FILE));
			
				
			case QUEEN:
				return new Image(new FileInputStream(Constants.B_QUEEN_FILE));
			
			
			case BISHOP:
				return new Image(new FileInputStream(Constants.B_BISHOP_FILE));
			

			case ROOK:
				return new Image(new FileInputStream(Constants.B_ROOK_FILE));
			

			case KNIGHT:
				return new Image(new FileInputStream(Constants.B_KNIGHT_FILE));
			
			case PAWN:
				return new Image(new FileInputStream(Constants.B_PAWN_FILE));
						
				}
			break;
		}
		return null;
	}
}
