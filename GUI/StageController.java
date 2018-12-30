package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Constants.*;
import Game.*;
import enums.ClientState;
import enums.Color;
import enums.PieceType;



public class StageController{
	private MyClient client;
	private Stage stage;
	
	public Label gameName;
	public Label loginLabel;
	public TextField loginField;
	public Button loginButton;
	
	public ListView<String> gameList;
	public Label lobbyLabel;
	public Label chooseGame;
	public Button refreshGamesButton;
	public Button createGameButton;
	public Button joinGameButton;
	
	public Label rowLabel;
	public Label rowIndic;
	public Label colLabel;
	public Label colIndic;
	public Image whiteField;
	public Image blackField;
	public Button yieldButton;
	
	public Label waitLabel;
	public Button stopWaitButton;
	
	public boolean firstClicked;
	public String message;
	
	public Label resultLabel;
	public Button replayButton;
	public Button backToLobbyButton;
	
	private GridPane boardGP;
	
	public Field[][] fields;
		
	public StageController(Stage stage) {
		this.stage = stage;
		this.fields = new Field[Constants.CHESS_SIZE+1][Constants.CHESS_SIZE+1];
		this.stage.setOnCloseRequest(event -> {
			client.sendMessage("EXIT;");
			System.exit(0);
		});
	}
	
	public void initLoginStage() {
	
		stage.setMinHeight(Constants.LOGIN_HEIGHT);
		stage.setMinWidth(Constants.LOGIN_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		gameName = new Label("Welcome");
		loginLabel = new Label("Enter your name:");
		loginField = new TextField();
		loginButton = new Button("Login");
		
		loginButton.setOnAction(event -> {
			try {
				login();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		GridPane gp = new GridPane();
		gp.setHgap(5);
		gp.setVgap(5);
		gp.add(gameName, 3, 1);
		gp.add(loginLabel, 2, 3);
		gp.add(loginField, 3, 3);
		gp.add(loginButton, 3, 4);
		
		BorderPane bp = new BorderPane();
		bp.setCenter(gp);
		
		Scene scene = new Scene(bp, Constants.LOGIN_WIDTH, Constants.LOGIN_HEIGHT);
		
		stage.setScene(scene);	
	}
	
	public void initLobbyStage() {
		
		stage.setMinHeight(Constants.LOBBY_HEIGHT);
		stage.setMinWidth(Constants.LOBBY_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		gameList = new ListView<String>();
		gameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		gameList.setEditable(false);
		gameList.setCellFactory(TextFieldListCell.forListView());
		
		
		lobbyLabel = new Label("Welcome to Lobby");
		chooseGame = new Label("Choose Game:");
		createGameButton = new Button("Create Game");
		joinGameButton = new Button("Join Game");
		

		
		createGameButton.setOnAction(event -> {
			createGame();
		});
		
		joinGameButton.setOnAction(event -> {
			joinGame();
		});
		
		
		GridPane topGP = new GridPane();
		topGP.setVgap(1);
		topGP.setHgap(1);
		topGP.add(lobbyLabel, 1, 1);
		
		GridPane centerGP = new GridPane();
		centerGP.setVgap(1);
		centerGP.setHgap(1);
		centerGP.add(gameList, 1, 1);
		
		GridPane rightGP = new GridPane();
		rightGP.setVgap(1);
		rightGP.setVgap(3);
		rightGP.add(createGameButton, 1, 1);
		rightGP.add(joinGameButton, 1, 2);
		
		BorderPane bp = new BorderPane();
		bp.setTop(topGP);
		bp.setCenter(centerGP);
		bp.setRight(rightGP);
		
		Scene scene = new Scene(bp, Constants.LOBBY_WIDTH, Constants.LOBBY_HEIGHT);
		
		stage.setScene(scene);	
	}
	public void initResultStage(boolean result) {
		stage.setMinHeight(Constants.LOBBY_HEIGHT);
		stage.setMinWidth(Constants.LOBBY_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		if(result) {
			resultLabel = new Label("WINNER... You killed opponents king.");
		} else {
			resultLabel = new Label("LOSER... Your king got killed.");
		}
		replayButton = new Button("Replay");
		backToLobbyButton = new Button("Back to Lobby");
		
		replayButton.setOnAction(event -> {
			replayYes();
		});
		
		backToLobbyButton.setOnAction(event -> {
			replayNo();
		});
		
		GridPane lGP = new GridPane();
		lGP.add(resultLabel, 1, 1);
		
		GridPane rGP = new GridPane();
		rGP.add(replayButton, 1, 1);
		rGP.add(backToLobbyButton, 1, 2);
		
		BorderPane bp = new BorderPane();
		bp.setLeft(lGP);
		bp.setRight(rGP);
		
		Scene scene = new Scene(bp, Constants.LOGIN_WIDTH, Constants.LOGIN_HEIGHT);
		stage.setScene(scene);
	}
	public void initWaitStage() {
		
		stage.setMinHeight(Constants.LOBBY_HEIGHT);
		stage.setMinWidth(Constants.LOBBY_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		waitLabel = new Label("Wait for player to join...");
		
		stopWaitButton = new Button("Back");
		stopWaitButton.setOnAction(event -> {
			stopWait();
		});
		
		
		GridPane gp = new GridPane();
		gp.add(waitLabel, 1, 1);
		gp.add(stopWaitButton, 1, 2);
		
		BorderPane bp = new BorderPane();
		bp.setCenter(gp);
		
		Scene scene = new Scene(bp, Constants.LOGIN_WIDTH, Constants.LOGIN_HEIGHT);
		stage.setScene(scene);	
	}
	public void initGameStage() {
		
		stage.setMinHeight(Constants.GAME_HEIGHT);
		stage.setMinWidth(Constants.GAME_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		waitLabel = new Label("You are in game");
		
		boardGP = new GridPane();
		initializeBoard();
		initializePieces();
		drawBoard(boardGP);
		
		GridPane rightGP = new GridPane();
		rightGP.setVgap(2);
		rightGP.setHgap(2);
		rowLabel = new Label("Row: ");
		rowIndic = new Label();
		colLabel = new Label("Col: ");
		colIndic = new Label();
		
		yieldButton = new Button("Yield");
		yieldButton.setOnAction(event -> {
			yield();
		});
		
		
		rightGP.add(rowLabel, 1, 1);
		rightGP.add(rowIndic, 2, 1);
		rightGP.add(colLabel, 1, 2);
		rightGP.add(colIndic, 2, 2);
		rightGP.add(yieldButton, 1, 3);
		
		BorderPane bp = new BorderPane();
		bp.setCenter(boardGP);
		bp.setRight(rightGP);
		
		Scene scene = new Scene(bp, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
		stage.setScene(scene);	
	}	
	
	

	/*
	 * POMOCNÉ METODY
	 */
	private void drawBoard(GridPane boardGP) {
		int row, col;
		boardGP.getChildren().clear();
		for(int i = 0; i < Constants.CHESS_SIZE; i++) {
			for(int j = 1; j <= Constants.CHESS_SIZE; j++) {
				
				row = Constants.CHESS_SIZE - i;
				col = j;
				boardGP.add(fields[row][col].getBox(), j, i);
			}
		}
		
	}
	private void initializePieces() {
		//kings
		fields[1][5].setPiece(new Piece(PieceType.KING, Color.WHITE));
		fields[8][5].setPiece(new Piece(PieceType.KING, Color.BLACK));
		//queens
		fields[1][4].setPiece(new Piece(PieceType.QUEEN, Color.WHITE));
		fields[8][4].setPiece(new Piece(PieceType.QUEEN, Color.BLACK));
		//bishops
	    fields[1][3].setPiece(new Piece(PieceType.BISHOP, Color.WHITE));
	    fields[1][6].setPiece(new Piece(PieceType.BISHOP, Color.WHITE));
	    fields[8][3].setPiece(new Piece(PieceType.BISHOP, Color.BLACK));
	    fields[8][6].setPiece(new Piece(PieceType.BISHOP, Color.BLACK));
	    //knights
	    fields[1][2].setPiece(new Piece(PieceType.KNIGHT, Color.WHITE));
	    fields[1][7].setPiece(new Piece(PieceType.KNIGHT, Color.WHITE));
	    fields[8][2].setPiece(new Piece(PieceType.KNIGHT, Color.BLACK));
	    fields[8][7].setPiece(new Piece(PieceType.KNIGHT, Color.BLACK));
	    //rooks
	    fields[1][1].setPiece(new Piece(PieceType.ROOK, Color.WHITE));
	    fields[1][8].setPiece(new Piece(PieceType.ROOK, Color.WHITE));
	    fields[8][1].setPiece(new Piece(PieceType.ROOK, Color.BLACK));
	    fields[8][8].setPiece(new Piece(PieceType.ROOK, Color.BLACK));
	    
	    for (int i = 1; i <= Constants.CHESS_SIZE; i++) {
			fields[2][i].setPiece(new Piece(PieceType.PAWN, Color.WHITE));
			fields[7][i].setPiece(new Piece(PieceType.PAWN, Color.BLACK));
		}
	}
	private void initializeBoard() {

		for(int i = 0; i < Constants.CHESS_SIZE; i++) {
			for(int j = 1; j <= Constants.CHESS_SIZE; j++) {
				HBox box = new HBox();
				box.setMaxSize(Constants.FIELD_SIZE, Constants.FIELD_SIZE);
				
				Color fieldColor = null;
				
				final int row = Constants.CHESS_SIZE - i;
				final int col = j;
				
				box.setOnMouseClicked(event -> 
				{
					fieldClicked(row, col);
				});
				
				if((i+j) % 2 != 0) {
					fieldColor = Color.WHITE;
				}
				else {
					fieldColor = Color.BLACK;
				}
				
				fields[row][col] = new Field(box, row, col, fieldColor);
			}			
		}
	}
	
	public void clearBoard() {
		for (int i = 1; i <= Constants.CHESS_SIZE; i++) {
			for (int j = 1; j <= Constants.CHESS_SIZE; j++) {
				fields[i][j].setPiece(null);
			}
		}
	}
	public void addPiece(String row, String col, String color, String type) {
		Color pieceColor;
		PieceType pieceType = null;
		if(color.equals("W")) pieceColor = Color.WHITE;
		else pieceColor = Color.BLACK;
		switch (type) {
		case "P":
			pieceType = PieceType.PAWN;
			break;
		case "Q":
			pieceType = PieceType.QUEEN;
			break;
		case "K":
			pieceType = PieceType.KING;
			break;
		case "B":
			pieceType = PieceType.BISHOP;
			break;
		case "R":
			pieceType = PieceType.ROOK;
			break;
		case "N":
			pieceType = PieceType.KNIGHT;
			break;

		}
		fields[Integer.valueOf(row)][Integer.valueOf(col)].setPiece(new Piece(pieceType, pieceColor));
		drawBoard(boardGP);
		
	}
	public void fieldClicked(int row, int col) {
		rowIndic.setText("" + row + "");
		colIndic.setText("" + col + "");
		
		if(!client.isTurn()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("Opponent playing");
			alert.setContentText("Please wait for your opponent to play.");
			alert.show();
			return;
		}
		
		if(!firstClicked) {
			client.setMove(new Move(new Coordinates(row, col)));
			firstClicked = true;
		}
		else {
			client.getMove().setSecMove(new Coordinates(row, col));
			message = "MOVE;" + client.getMove().toString() + ";";
			client.sendMessage(message);
			firstClicked = false;
			message = "";
		}
		
	}
	public void move(Move move) {
		fields[move.getSecMove().getRow()][move.getSecMove().getCol()].setPiece(fields[move.getFirstMove().getRow()][move.getFirstMove().getCol()].getPiece());
		fields[move.getFirstMove().getRow()][move.getFirstMove().getCol()].setPiece(null);
		drawBoard(boardGP);
	}
	/*
	 * REAKCE NA TLAČÍTKA
	 */
	
	private void joinGame() {
		String s = gameList.getSelectionModel().getSelectedItem();
		if(s != null) {
			client.sendMessage("JOIN_MATCH;"+ s+";");
		}
	}

	private void createGame() {
		client.sendMessage("CREATE_MATCH;");
		
	}

	private void login() throws UnknownHostException, IOException {
		if(loginField.getText() != null) {
			client.setSocket(new Socket(client.getAddressString(), client.getPort()));
			String clientName = loginField.getText();
			client.sendMessage("CONNECT;OK;" + clientName + ";");
		}
		
		
	}
	public Stage getStage() {
		return this.stage;
	}
	public void setClient(MyClient client) {
		this.client = client;
	}

	public void replayYes() {
		client.sendMessage("REPLAY_Y;");
	}
	public void replayNo() {
		client.sendMessage("REPLAY_N;");
		backToLobby();
		
	}
	public void stopWait() {
		client.sendMessage("STOP_WAIT;");
		backToLobby();
	}
	public void yield(){
		client.sendMessage("YIELD;");
		backToLobby();
	}
	public void backToLobby() {
		client.setClientState(ClientState.LOBBY);
		client.setColor(null);
		client.setMove(null);
		client.getStageController().initLobbyStage();
	}
}

