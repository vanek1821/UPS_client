import java.util.Scanner;

public class Chess {

	public static void main(String argv[]) throws Exception{
		
		String host = "127.0.0.1";
		int port = 10000;
		Scanner sc = new Scanner(System.in);
		String con=null;
		
		
		System.out.println("Welcome to Chess Game");
		System.out.print("Enter your name: ");
		String clientName = sc.nextLine();
		Client client = new Client(host, port);
		
		client.sendMessage("CONNECT;OK;" + clientName + ";");
		client.receiveMessage();
		
		while (client.getState() == State.LOBBY){
			System.out.print("Vytvořit hru? [Y,N]");
			con = sc.nextLine();
			if (con.equals("Y")) {
				client.sendMessage("CREATE_MATCH;");
				client.receiveMessage();
			}
			else if(con.equals("N")){
				System.out.println("Chcete se připojit do hry? [Y,N]");
				con=sc.nextLine();
				if(con.equals("Y")) {
					client.sendMessage("FIND_MATCH;");
				}
				else if(con.equals("N")) {
					System.out.println("Nechci vybrat hru");
				}
			}
		}
		while(true) {}
	}
}
