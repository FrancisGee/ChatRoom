
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 
 * @author gaoyikang
 *
 */

public class MyServer {
	private static final int PORT = 8080;
	public static List<Socket> socketList = Collections.synchronizedList(new ArrayList<Socket>()); // 定义一个线程安全的list
	public static HashSet<String> user_list = new HashSet<String>(); // 登录用户集合
	public static HashMap<String, Socket> map = new HashMap<String, Socket>();
	// 用于对广播等操作进行重构
	public static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

	// 初始化
	public void init() {
		try {
			ServerSocket server = new ServerSocket(PORT);
			System.out.println("服务已经启动，等待客户端连接...");
			// 主线程一直在监听端口
			while (true) {
				Socket client = server.accept();
				InetAddress inet = client.getInetAddress();
				System.out.println(inet.getHostAddress() + "上线了");
				socketList.add(client);
				new Thread(new ServerThread(client)).start(); // 启动线程处理客户端请求
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		MyServer server = new MyServer();
		server.init();

		System.out.println("大型测试");

	}
}

class ServerThread implements Runnable {
	private static final int COMMAND_TO = 1;
	private static final int BROARDCAST = 2;
	private static final int COMMAND_HI = 3;
	private static final int COMMAND_HI_USER = 4;
	private BufferedReader br;
	private Socket s;
	private String name;
	private PrintWriter writer;
	private int isClosed = 0;

	ServerThread(Socket s) throws IOException {
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		sendHello(s);
		writer = new PrintWriter(s.getOutputStream());
	}

	@Override
	public void run() {

		String firstmsg = null;
		firstmsg = readClientContext();
		String[] spilted = firstmsg.split(" ");
		String command = spilted[0];
		String name = spilted[1];

		MyServer.map.put(name, s);
		MyServer.writers.add(writer);

		if (command.equals("/login")) {
			if (!MyServer.user_list.contains(name)) {

				MyServer.user_list.add(name);

				writer.println("you have logined");
				writer.flush();

				// 广播消息(给其他用户)
				for (PrintWriter out : MyServer.writers) {
					if (out != this.writer) {
						out.println(name + " has logined ");
						out.flush();
					}
				}
			} else {
				System.out.println("非法用户想要进来");

				writer.println("Name exist, please choose anthoer name....");
				writer.flush();
			}
		} else if (command.equals("/quit")) {
			System.out.println("有人要退出");
			for (Socket s : MyServer.socketList) {
				try {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println("兄弟们，有人下线了");
					pw.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			writer.println("Invalid command");
			writer.flush();
		}

		// 处理后续消息

		String line = null;
		while ((line = readClientContext()) != null) {
			// 服务器记录日志信息
			System.out.println(line);
			MessageHelper.Message talk = MessageHelper.parseRawMessage(line);
			int flag = talk.getFlag();

			if (flag == COMMAND_TO) {
				// 向某个用户私聊

				// 判断私聊对象名字的合法性
				// 如果该用户不存在
				if (!MyServer.map.containsKey(talk.getTo())) {
					writer.println(talk.getTo() + " is not online ");
					writer.flush();
				}

				else {
					// 拿到这个用户的Socket
					Socket target = MyServer.map.get(talk.getTo());
					if (target.equals(s)) {
						writer.println("Stop talking to yourself");
						writer.flush();
					} else {

						// 对方看到的消息(这个地方没有用PrintWriter来确定对象)
						// 因为我还没有维护用户名字对于的PrintWriter的HashMap
						try {
							PrintWriter pw３ = new PrintWriter(target.getOutputStream());
							pw３.println(name + " 对你说 " + talk.getBody());
							pw３.flush();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						writer.println(" 你对 " + talk.getTo() + " 说 " + talk.getBody());
						writer.flush();
					}
				}
			}
			if (flag == BROARDCAST) {
				// 代表这是一条广播消息
				// 广播消息(给其他用户)
				for (PrintWriter out : MyServer.writers) {
					if (out != this.writer) {
						out.println(name + "说" + ": " + talk.getBody());
						out.flush();
					}
				}

				writer.println("你说: " + talk.getBody());
				writer.flush();
			}
			if(flag == COMMAND_HI){
				//打招呼(无用户参数)
				
				//自己看到的消息
				writer.println(" 你向大家打招呼 "+ talk.getBody());
				writer.flush();
				
				//别人看到的消息
				for (PrintWriter out : MyServer.writers) {
					if (out != this.writer) {
						out.println(name + " 向大家打招呼 , " + ": " + talk.getBody());
						out.flush();
					}
				}
				
			}
		}

	}

	// 读取客户端数据
	private String readClientContext() {
		try {
			return br.readLine();
		} catch (IOException e) {
			MyServer.socketList.remove(s);
			System.out.println("读取客户端数据失败！");
		}
		return null;

	}

	// 给客户端发送欢迎登录消息
	private void sendHello(Socket selfsocket) {
		try {
			writer = new PrintWriter(s.getOutputStream());
			writer.println("Please login....");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
