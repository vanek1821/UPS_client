package GUI;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Constants.*;
import Game.*;



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
	
	public Label waitLabel;
	
	public StageController(Stage stage) {
		this.stage = stage;
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
			login();
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
		refreshGamesButton = new Button("Refresh");
		createGameButton = new Button("Create Game");
		joinGameButton = new Button("Join Game");
		
		refreshGamesButton.setOnAction(event -> {
			refreshGames();
		});
		
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
		rightGP.add(refreshGamesButton, 1, 3);
		
		BorderPane bp = new BorderPane();
		bp.setTop(topGP);
		bp.setCenter(centerGP);
		bp.setRight(rightGP);
		
		Scene scene = new Scene(bp, Constants.LOBBY_WIDTH, Constants.LOBBY_HEIGHT);
		
		stage.setScene(scene);	
	}
	
	public void initWaitStage() {
		
		stage.setMinHeight(Constants.LOBBY_HEIGHT);
		stage.setMinWidth(Constants.LOBBY_WIDTH);
		stage.setTitle(Constants.GAME_TITLE);
		
		waitLabel = new Label("Wait for player to join...");
		
		GridPane gp = new GridPane();
		gp.add(waitLabel, 1, 1);
		
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
		
		GridPane boardGP = new GridPane();
		initializeBoard(boardGP);
		
		BorderPane bp = new BorderPane();
		bp.setLeft(boardGP);
		
		Scene scene = new Scene(bp, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
		stage.setScene(scene);	
	}	
	
	/*
	 * POMOCNÉ METODY
	 */
	
	private void initializeBoard(GridPane board) {
		
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

	private void refreshGames() {
		client.sendMessage("FIND_MATCH;");		
	}

	private void login() {
		if(loginField.getText() != null) {
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
}

