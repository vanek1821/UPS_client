package Game;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import enums.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ReaderThread extends Thread{

	private BufferedReader br;
	private char[] cbuf = new char[1024];
	private MyClient client;
	
	public ReaderThread(Socket socket, MyClient client) throws IOException {
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.client = client;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				br.read(cbuf);
				if(cbuf!=null) {
					System.out.println("přijal jsem: " + new String(cbuf) );
					decodeMessage(cbuf);
				}
				else {
					System.out.println("Spadlo spojení kokot");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void decodeMessage(char[] cbuf) {
		String message = new String(cbuf);
		String[] splitMes = message.split(";");
		if(client.getState() == ClientState.START) {
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
				String[] gameIDs = splitMes[1].split("-");
				ObservableList<String> games = FXCollections.observableArrayList();
				for (String s: gameIDs) {
					if(!(s.isEmpty())) {
						games.add(s);
						System.out.println("Můžete se připojit ke hře s ID:" + s);
					}
				}
				Platform.runLater(() ->{ 
					client.getStageController().gameList.setItems(games);
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
				});
			}
		}
		return;
	}

	
}
