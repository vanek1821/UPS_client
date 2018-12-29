package Game;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.sun.xml.internal.ws.client.ClientTransportException;

import enums.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class ReaderThread extends Thread{

	private BufferedReader br;
	private String cbuf;// = new char[1024];
	private MyClient client;
	
	public ReaderThread(Socket socket, MyClient client) throws IOException {
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.client = client;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				//cbuf = new char[cbuf.length];
				cbuf = br.readLine();
				if(cbuf!=null) {
					System.out.println("přijal jsem: " + new String(cbuf) );
					decodeMessage(cbuf);
				}
				else {
					//System.out.println("Spadlo spojení");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void decodeMessage(String cbuf) {
		String message = new String(cbuf);
		String[] splitMes = message.split(";");
		if(splitMes[0].equals("PING") && client.getState()!=ClientState.START) {
			client.setConnection(true);
			client.sendMessage("PING;");
		}
		else if(client.getState() == ClientState.START) {
			if(splitMes[0].equals("CONNECT")) {
				if(splitMes[1].equals("OK")) {
					System.out.println("Uživatel úspěšně připojen");
					client.setClientState(ClientState.LOBBY);
					Platform.runLater(() -> {
						client.getStageController().initLobbyStage();
						client.sendMessage("FIND_MATCH;");
					});
				}
				else if(splitMes[1].equals("FAIL")) {
					System.out.println("Připojení uživatele se nezdařilo.");
				}
			}
			else if(splitMes[0].equals("RECONNECT")) {
				if(splitMes[1].equals("LOBBY")) {
					client.setClientState(ClientState.LOBBY);
					Platform.runLater(() -> {
						client.getStageController().initLobbyStage();
						client.sendMessage("FIND_MATCH;");
					});
				}
				else if(splitMes[1].equals("ROOM")) {
					Platform.runLater(() -> {
						client.setClientState(ClientState.ROOM);
						client.getStageController().initWaitStage();
					});
				}
				else if(splitMes[1].equals("GAME")) {
					Platform.runLater(() -> {
						client.setClientState(ClientState.GAME);
						if(splitMes[2].equals("Y")) client.setTurn(true);
						else client.setTurn(false);
						client.getStageController().initGameStage();
						client.getStageController().clearBoard();
					});
				}
				else if(splitMes[1].equals("REPLAY")) {
					Platform.runLater(()-> {
						client.setClientState(ClientState.REPLAY);
						if(splitMes[2].equals("W")) client.getStageController().initResultStage(true);
						else client.getStageController().initResultStage(false);
					});
				}
			}
		} 
		else if (client.getState() == ClientState.LOBBY ) {
			if(splitMes[0].equals("CREATE_MATCH")) {
				if(splitMes[1].equals("OK")) {
					System.out.println("Hra byla vytvořena");
					System.out.println("Vyčkejte na hráče");
					client.setClientState(ClientState.ROOM);
					Platform.runLater(() -> {
						client.getStageController().initWaitStage();
					});
				}
			}
			else if (splitMes[0].equals("FOUND_MATCH")) {
				ObservableList<String> games = FXCollections.observableArrayList();
				if(splitMes.length>1) {
					String[] gameIDs = splitMes[1].split("-");
					for (String s: gameIDs) {
						if(!(s.isEmpty())) {
							games.add(s);
							System.out.println("Můžete se připojit ke hře s ID:" + s);
						}
					}
				}
				else {
						games.clear();
					}
				Platform.runLater(() ->{ 
					client.getStageController().gameList.setItems(games);
					client.getStageController().gameList.refresh();
				});
			}
			else if (splitMes[0].equals("JOIN_MATCH")) {
				if(splitMes[1].equals("OK")) {
					System.out.println("Jste připojen do hry kundo");
					client.setClientState(ClientState.GAME);
					Platform.runLater(() -> {
						System.out.println("Vytvarim gamestage");
						client.getStageController().initGameStage();
						client.setColor(Color.BLACK);
						client.setTurn(false);
					});
				}
				else if(splitMes[1].equals("FAIL")) {
					System.out.println("Do hry se nelze připojit. Je buď obsazena nebo neexistuje.");
				}
			}
 		}
		else if (client.getState() == ClientState.ROOM) {
			if(splitMes[0].equals("JOIN_MATCH")) {
				System.out.println("Jste připojen do hry");
				client.setClientState(ClientState.GAME);
				Platform.runLater(() -> {
					System.out.println("Vytvarim gamestage");
					client.getStageController().initGameStage();
					client.setColor(Color.WHITE);
					client.setTurn(true);
				});
			}
		}
		else if(client.getState() == ClientState.GAME) {
			if(splitMes[0].equals("MOVE")) {
				if(splitMes[1].equals("OK")) {
					Platform.runLater(() -> {
						client.getStageController().move(new Move(Integer.valueOf(splitMes[2]),Integer.valueOf(splitMes[3]),Integer.valueOf(splitMes[4]),Integer.valueOf(splitMes[5])));	
					});
					if(client.isTurn()) {
						client.setTurn(false);
					}
					else {
						client.setTurn(true);
					}
				}
				else {
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setHeaderText("Wrong move");
						alert.setContentText("You can not make this move.");
						alert.show();
						return;
					});
				}
			}
			else if(splitMes[0].equals("GAME")) {
				if(splitMes[1].equals("WIN")) {
					Platform.runLater(()->{
						client.getStageController().initResultStage(true);
						client.setClientState(ClientState.REPLAY);
					});
				}
				else {
					Platform.runLater(()->{
						client.getStageController().initResultStage(false);
						client.setClientState(ClientState.REPLAY);
					});
				}
			}
			else if(splitMes[0].equals("YIELD")){
				client.setClientState(ClientState.LOBBY);
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setHeaderText("Yield");
					alert.setContentText("Your opponent left the game. You WIN");
					alert.setOnCloseRequest(event-> {
						client.getStageController().backToLobby();
					});
					alert.show();
					return;
				});
			}
			else if(splitMes[0].equals("LEFT")) {
				Platform.runLater(() -> {
					client.setClientState(ClientState.LOBBY);
					Alert alert = new Alert(AlertType.WARNING);
					alert.setHeaderText("Disconnected");
					alert.setContentText("Your opponent was disconnected from server.");
					alert.setOnCloseRequest(event-> {
						client.getStageController().backToLobby();
					});
					alert.show();
					return;
				});
			}
			else if(splitMes[0].equals("PIECE")) {
				String[] pieces = splitMes[1].split("-");
				Platform.runLater(() -> {
					client.getStageController().addPiece(pieces[0], pieces[1], pieces[2], pieces[3]);
				});
			}
		}
		else if(client.getState() == ClientState.REPLAY) {
			if(splitMes[0].equals("REPLAY")) {
				if(splitMes[1].equals("YES")) {
					client.setClientState(ClientState.GAME);
					if(client.getColor() == Color.WHITE) {
						client.setTurn(true);
					}
					else {
						client.setTurn(false);
					}
					Platform.runLater(()->{
						client.getStageController().initGameStage();
					});
					
				}
				else if(splitMes[1].equals("NO")) {
					client.setClientState(ClientState.LOBBY);
					client.setColor(null);
					client.setMove(null);
					Platform.runLater(()->{
						client.getStageController().initLobbyStage();
					});
				}
			}
		}
		
		return;
	}

	
}
