package Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import enums.Color;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import Constants.*;

public class Field {

	private int row, col;
	private Color color;
	private Piece piece;
	private ImageView view;
	private HBox box;
	private String bgPath;
	private Image bgField;
	
	public Field(HBox box, int row, int col, Color color) {
		this.setBox(box);
		this.setRow(row);
		this.setCol(col);
		this.setColor(color);
		if(color == Color.WHITE) this.bgPath = "images/white_field.png";
		else this.bgPath = "images/black_field.png";
		this.piece = null;
		changeBox();
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		if(piece == null) {
			this.piece = piece;
			changeBox();
		}
		else {
			this.piece = piece;
			changeBox();
		}
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public ImageView getView() {
		return view;
	}

	public void setView(ImageView view) {
		this.view = view;
	}
	private void changeBox() {
		box.getChildren().clear();
		try {
			FileInputStream bgFile = new FileInputStream(bgPath);
			bgField = new Image(bgFile);
			
		} catch (FileNotFoundException e) {
			System.out.println("soubor nenalezen");
		}
		
		view = new ImageView();
		view.setImage(bgField);
		view.setFitHeight(Constants.FIELD_SIZE);
		view.setFitWidth(Constants.FIELD_SIZE);
				
		
		box.getChildren().add(view);
		Group g = new Group();
		g.getChildren().add(view);
		
		if(this.piece == null);
		else {
			ImageView view2 = new ImageView();
			view2 = this.piece.getImageView();
			view2.setBlendMode(BlendMode.SRC_OVER);
			g.getChildren().add(view2);
		}
		box.getChildren().add(g);
	}

	public HBox getBox() {
		return box;
	}

	public void setBox(HBox box) {
		this.box = box;
	}
	
	
}
