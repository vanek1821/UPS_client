package Game;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ConnectionThread extends Thread{
	private MyClient client;
	
	public ConnectionThread(MyClient client) throws IOException {
		this.client = client;
	}
	
	@Override
	public void run() {
		Socket socket = new Socket();
		while(true) {
			if(client.isConnection()) {
				client.setConnection(false);
				try {
					ConnectionThread.sleep(10000);
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted");
				}
			}
			else {
				break;				
			}
		}
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("Connection lost");
			alert.setContentText("You lost connection");
			alert.showAndWait();
			alert.setOnCloseRequest(event -> {
				System.exit(0);
			});
		});
	}
}

