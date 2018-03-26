package Chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	//소켓 한개를 관리할 클래스
	private static class Client extends Thread{
		//총괄 기능
		private static List<Client> list = new ArrayList<>();
		public static void add(Client c) {
			list.add(c);
		}
		public static void remove(Client c) {
			list.remove(c);
		}
		public static void broadcast(String text) {
			//모~~~든 사용자에게 send() 명령을 통해 text를 전송
			for(Client client : list) {
				client.send(text);
			}
		}

		//변수 - 연결(Socket), 통로(BufferedReader)
		private Socket socket;
		private BufferedReader br;
		private PrintWriter pw;

		//생성자 - 반드시 Socket을 받아서 설정해야 한다
		public Client(Socket socket) throws IOException {
			this.socket = socket;
			this.br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())));

			Client.add(this);//나를 등록해
		}

		//클라이언트에게 메세지를 보내는 기능
		public void send(String text) {
			pw.println(text);
			pw.flush();
		}

		//메소드 - run()
		@Override
		public void run() {
			try {
				while(true) {
					String str = br.readLine();
					if(str == null) break;
					//System.out.println("받은 메세지 : "+str);
					//보낸 메세지를 전체에게 뿌려준다
					broadcast(str);
				}

				br.close();
				socket.close();
			}catch(Exception e) {
				//					e.printStackTrace();
				//					오류나면 나를 잊어줘
				Client.remove(this);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		//여러명의 연결을 각각 관리할 수 있도록 프로그램 구성
		// -> 사용자가 접속할 때마다 해당 연결별로 while문이 구동되도록 스레드 처리

		ServerSocket server = new ServerSocket(20000);

		while(true) {
			Socket socket = server.accept();

			//스레드 대신 클라이언트 생성
			Client c = new Client(socket);
			c.setDaemon(true);
			c.start();
		}

		//			server.close();
	}
}
