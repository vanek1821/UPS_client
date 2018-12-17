package Game;
import java.io.*;
import java.net.*;

import GUI.StageController;
import enums.ClientState;
import enums.*;

public class MyClient
{	
	
	private Socket socket;
	private InetAddress adress;
	private OutputStreamWriter osw;
	private ReaderThread rt;
	private ClientState state;
	private StageController stageController;
	private Color color;
	
	public MyClient(String hostName, int port, StageController sc) throws Exception{
		this.socket = new Socket(hostName, port);
		this.adress = socket.getInetAddress();
		this.osw = new OutputStreamWriter(socket.getOutputStream());		
		this.rt = new ReaderThread(this.socket, this);
		rt.start();
		this.state = ClientState.START;
		this.stageController = sc;
		sc.setClient(this);
	}
	
	public void sendMessage(String message) {
		try {
			System.out.println("Posílám zprávu: " + message);
			osw.write(message);
			osw.flush();
		} catch (IOException e) {
			System.out.println("Sending message failed.");
		}
	}

	
	public ClientState getState() {
		return this.state;
	}
	
	public void setClientState(ClientState state) {
		this.state = state;
	}
	public StageController getStageController() {
		return this.stageController;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
