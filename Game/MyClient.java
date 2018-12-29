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
	private ConnectionThread ct;
	private ClientState state;
	private StageController stageController;
	private Color color;
	private boolean turn;
	private Move move;
	private int port;
	private String addressString;
	private boolean connection;
	
	public MyClient(String adress, int port, StageController sc) throws Exception{
		this.setPort(port);
		this.setAddressString(adress);
		this.state = ClientState.START;
		this.stageController = sc;
		sc.setClient(this);
		this.turn = false;
		this.setMove(null);
		this.connection = true;
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
	public Color getColor() {
		return this.color;
	}
	public boolean isTurn() {
		return this.turn;
	}
	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAddressString() {
		return addressString;
	}

	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}

	public boolean isConnection() {
		return connection;
	}

	public void setConnection(boolean connection) {
		this.connection = connection;
	}
	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;
		this.osw = new OutputStreamWriter(socket.getOutputStream());		
		this.rt = new ReaderThread(this.socket, this);
		rt.start();
		this.ct = new ConnectionThread(this);
		ct.start();
	}
}
