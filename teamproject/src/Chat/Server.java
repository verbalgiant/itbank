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
	//���� �Ѱ��� ������ Ŭ����
	private static class Client extends Thread{
		//�Ѱ� ���
		private static List<Client> list = new ArrayList<>();
		public static void add(Client c) {
			list.add(c);
		}
		public static void remove(Client c) {
			list.remove(c);
		}
		public static void broadcast(String text) {
			//��~~~�� ����ڿ��� send() ����� ���� text�� ����
			for(Client client : list) {
				client.send(text);
			}
		}

		//���� - ����(Socket), ���(BufferedReader)
		private Socket socket;
		private BufferedReader br;
		private PrintWriter pw;

		//������ - �ݵ�� Socket�� �޾Ƽ� �����ؾ� �Ѵ�
		public Client(Socket socket) throws IOException {
			this.socket = socket;
			this.br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())));

			Client.add(this);//���� �����
		}

		//Ŭ���̾�Ʈ���� �޼����� ������ ���
		public void send(String text) {
			pw.println(text);
			pw.flush();
		}

		//�޼ҵ� - run()
		@Override
		public void run() {
			try {
				while(true) {
					String str = br.readLine();
					if(str == null) break;
					//System.out.println("���� �޼��� : "+str);
					//���� �޼����� ��ü���� �ѷ��ش�
					broadcast(str);
				}

				br.close();
				socket.close();
			}catch(Exception e) {
				//					e.printStackTrace();
				//					�������� ���� �ؾ���
				Client.remove(this);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		//�������� ������ ���� ������ �� �ֵ��� ���α׷� ����
		// -> ����ڰ� ������ ������ �ش� ���Ằ�� while���� �����ǵ��� ������ ó��

		ServerSocket server = new ServerSocket(20000);

		while(true) {
			Socket socket = server.accept();

			//������ ��� Ŭ���̾�Ʈ ����
			Client c = new Client(socket);
			c.setDaemon(true);
			c.start();
		}

		//			server.close();
	}
}
