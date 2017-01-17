
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * 
 * @author gaoyikang
 *
 */

public class MyClient extends Socket {
	public static final String IP = "127.0.0.1";
	public static final int PORT = 8080;

	// 初始化
	public void init() {
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			Socket s = new Socket(IP, PORT);
			// 启动线程读取服务器端发送的数据
			new Thread(new ClientThread(s)).start();
			System.out.println("请输入登录命令，格式为/login + yourname");
			System.out.println("如果您想退出，请输入/quit + yourname ,谢谢！");
			// 控制台向服务器发送数据
			br = new BufferedReader(new InputStreamReader(System.in));
			// String msg = br.readLine();

			// 如果用户输入quit则断开连接
			/*
			 * if(msg.equals("quit")){ ps = new
			 * PrintStream(s.getOutputStream()); ps.println(msg); ps.flush();
			 * s.close(); }
			 */

			ps = new PrintStream(s.getOutputStream());

			String line = null;
			while ((line = br.readLine()) != null) {
				ps.println(line);
			}
			ps.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	public static void main(String[] args) {
		MyClient client1 = new MyClient();
		client1.init();

		System.out.println("你已经断开连接");
	}

}

class ClientThread implements Runnable {
	private BufferedReader br;

	ClientThread(Socket s) {
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
