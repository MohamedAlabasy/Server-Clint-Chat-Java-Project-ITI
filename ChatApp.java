import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatApp extends JFrame implements Runnable {

	private Socket link;
	private PrintWriter outputStream;
	private Scanner inputStream;
	private int port = 7777;
	JTextArea ta;

	public ChatApp() throws IOException {
		String str = "127.128.0.1";
		// connect to server
		InetAddress host = null;
		try {
			host = InetAddress.getByName(str);
		} catch (UnknownHostException e1) {
			System.out.println("Host not found");
		}
		link = null;
		try {
			link = new Socket(host, port);
			link.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("not found");
		}
		inputStream = new Scanner(link.getInputStream());
		outputStream = new PrintWriter(link.getOutputStream());

		// start new thread to listen from server
		// one runnable, two threads... in which cases is this legal?
		Thread t = new Thread(this);
		t.start();

		this.setLayout(new FlowLayout());
		ta = new JTextArea(20, 50);
		JScrollPane scroll = new JScrollPane(ta);
		JTextField tf = new JTextField(40);
		JButton okButton = new JButton("Send");

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!(tf.getText().isEmpty())) {
					outputStream.println(tf.getText());
					outputStream.flush();
					tf.setText("");
				}
			}
		});
		add(scroll);
		add(tf);
		add(okButton);
	}

	public static void main(String args[]) throws IOException {
		ChatApp ui = new ChatApp();
		ui.setSize(600, 500);
		ui.setResizable(true);
		ui.setVisible(true);
	}

	public void run() {
		while (true) {
			if (inputStream.hasNextLine()) {
				ta.append(inputStream.nextLine() + "\n");
			}
		}
	}
}