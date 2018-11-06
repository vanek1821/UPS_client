import java.io.*;
import java.net.*;

class Client
{	
	
	private Socket socket;
	private InetAddress adress;
	private OutputStreamWriter osw;
	private BufferedReader br;
	private char[] cbuf = new char[1024];
	private State state;
	
	public Client(String hostName, int port) throws Exception{
		this.socket = new Socket(hostName, port);
		this.adress = socket.getInetAddress();
		this.osw = new OutputStreamWriter(socket.getOutputStream());		
		this.cbuf = new char[16];
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.state = State.START;
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
	
	public void receiveMessage() {
		try {
			br.read(cbuf);
			//System.out.println("přijal jsem něco:");
			System.out.println("přijal jsem " + new String(cbuf));
			decodeMessage(cbuf);
		} catch (IOException e) {
			System.out.println("Receiving message failed.");
		}
	}
	public void decodeMessage(char[] cbuf) {
		String message = new String(cbuf);
		String[] splitMes = message.split(";");
		if(this.state == State.START) {
			if(splitMes[0].equals("CONNECT")) {
				if(splitMes[1].equals("OK")) {
					System.out.println("Uživatel úspěšně připojen");
					this.state = State.LOBBY;
				}
				else if(splitMes[1].equals("FAIL")) {
					System.out.println("Připojení uživatele se nezdařilo.");
				}
			}
		} else if (this.state == State.LOBBY ) {
			if(splitMes[0].equals("CREATE_MATCH")) {
				if(splitMes[1].equals("OK")) {
					System.out.println("Hra byla vytvořena");
					System.out.println("Čekám na hráče");
					this.state = State.ROOM;
				}
			}
 		}
		return;
	}
	
	public State getState() {
		return this.state;
	}
	
	
}
