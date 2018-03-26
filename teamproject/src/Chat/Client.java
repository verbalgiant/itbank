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
	//	��ġ�� �������(������Ʈ)�� ����ʵ�� ������ �� ���

	//	�������� Component�� Frame�� ���� ��ġ�߾��µ� �̷��� ����ȿ���� ��������
	//	Panel�� ���� Component�� ��ġ�� �� �ֵ��� ������ �� �ִ�(ContentPane)
	private JPanel mainPanel = new JPanel();
	
	//	�޴�
	//	JMenuBar - JMenu - JMenuItem / JCheckBoxMenuItem / JRadioButtonMenuItem
	private JButton enter = new JButton("����");
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private JScrollPane sp = new JScrollPane(ta);

	private boolean	 flag;

	//main�� �ϴ� �������� �����ڿ��� ����
	public Window() throws Exception {
		this.display();//ȭ�� ���� ���� ó��
		this.event();//�̺�Ʈ ���� ó��
		this.menu();//�޴� ���� ó��

		this.setTitle("GUI�׽�Ʈ");
		this.setSize(500, 400);
		//		��ġ�� �ü���� �����ϵ��� ����
		this.setLocationByPlatform(true);
		//		��ܺκ��� ������ �ʵ��� ����
		//		this.setUndecorated(true);
		this.setResizable(false);
		this.setVisible(true);
		ta.setEditable(false);
		sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());

		InetAddress inet = InetAddress.getByName("192.168.0.133");
		Socket socket = new Socket(inet, 20000);

		//������ڸ��� ������ ���� �����带 ����
		Thread t = new Thread() {
			@Override
			public void run() {
				//�޴� �ڵ带 �̰��� �ۼ�
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
		//mainPanel�� �⺻ �гη� ����
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

