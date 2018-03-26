package Chat;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
class Window extends JFrame{
	//	배치할 구성요소(컴포넌트)를 멤버필드로 구현한 뒤 사용

	//	기존에는 Component를 Frame에 직접 배치했었는데 이러면 관리효율이 떨어진다
	//	Panel을 만들어서 Component를 배치할 수 있도록 설정할 수 있다(ContentPane)
	private JPanel mainPanel = new JPanel();
	
	//	메뉴
	//	JMenuBar - JMenu - JMenuItem / JCheckBoxMenuItem / JRadioButtonMenuItem
	private JButton enter = new JButton("전송");
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private JScrollPane sp = new JScrollPane(ta);

	private boolean	 flag;

	//main에 하던 설정들을 생성자에서 진행
	public Window() throws Exception {
		this.display();//화면 구성 관련 처리
		this.event();//이벤트 관련 처리
		this.menu();//메뉴 관련 처리

		this.setTitle("GUI테스트");
		this.setSize(500, 400);
		//		위치를 운영체제가 결정하도록 하자
		this.setLocationByPlatform(true);
		//		상단부분이 나오지 않도록 설정
		//		this.setUndecorated(true);
		this.setResizable(false);
		this.setVisible(true);
		ta.setEditable(false);
		sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());

		InetAddress inet = InetAddress.getByName("192.168.0.133");
		Socket socket = new Socket(inet, 20000);

		//연결되자마자 별도의 수신 스레드를 구동
		Thread t = new Thread() {
			@Override
			public void run() {
				//받는 코드를 이곳에 작성
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					while(true) {
						String ret = br.readLine();
						if(flag && !ret.equals("")) {
							ta.append(ret+"\n");
							sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
						}
						flag=false;
					}
				}catch(Exception e) {
					System.exit(-1);
				}
			}
		};
		t.setDaemon(true);
		t.start();

		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream())));

		while(true) {
			String input = tf.getText();
			pw.println(input);
			pw.flush();
		}
	}

	private void display() {
		//mainPanel을 기본 패널로 설정
		this.setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tf, "South");
		mainPanel.add(enter, "East");
		mainPanel.add(sp, "Center");
	}

	private void event(){
		tf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					flag=true;
					tf.setText("");
				}
			}
		});

		enter.addActionListener(e->{
			flag=true;
			tf.setText("");
		});

	}

	private void menu() {

	}
}

public class Client {
	public static void main(String[] args) throws Exception {
		Window window = new Window();
	}
}

