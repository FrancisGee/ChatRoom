
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



/**
 * 
 * 
 * @author gaoyikang
 *
 */



public class MyServer {
	public static final int PORT = 8080;
	public static List<Socket> socketList = Collections
				.synchronizedList(new ArrayList<Socket>());		//定义一个线程安全的list
	public  static List<String> user_list = new ArrayList<String>(); 			//登录用户集合
	
	//初始化
	public void init(){
		try{
			ServerSocket server = new ServerSocket(PORT);
			System.out.println("服务已经启动，等待客户端连接...");
			// 主线程一直在监听端口
			while(true){
				Socket client = server.accept();
				InetAddress inet = client.getInetAddress();
				System.out.println(inet.getHostAddress()+"上线了");
					socketList.add(client);
				new Thread(new ServerThread(client)).start(); // 启动线程处理客户端请求			
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		MyServer server = new MyServer();
		server.init();
		
		System.out.println("大型测试");

	}
}

class ServerThread implements Runnable{
	private BufferedReader br;
	private Socket s;
	
	ServerThread(Socket s) throws IOException{
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	
	@Override
	public void run(){
		try {
			PrintWriter init = new PrintWriter(s.getOutputStream());
			init.println("Please login");
			init.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String msg =null;
		msg = readClientContext();
		String[] spilted = msg.split(" ");
		String command = spilted[0];
		String  name = spilted[1];
	
		
	if(command.equals("/login")){
			try {
				PrintWriter pw = new PrintWriter(s.getOutputStream());
				pw.println("you have logined");
				pw.flush();
				for(Socket s : MyServer.socketList){
					try{
						PrintWriter pw1 = new PrintWriter(s.getOutputStream());
						pw1.println( name+" has logined ");
						pw1.flush();
						
					}catch (IOException e){
						e.printStackTrace();
					}
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	else if(command.equals("quit")){
		System.out.println("有人要退出");
		for(Socket s : MyServer.socketList){
			try{
				PrintWriter pw = new PrintWriter(s.getOutputStream());
				pw.println("兄弟们，有人下线了");
				pw.flush();
				
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		
	}
	else{
		try {
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.println("Invalid command");
			pw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
		String line = null;
		while((line = readClientContext()) != null){
			/*if(line.equals("login")){
				try {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println("欢迎合法用户");
					pw.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			//将客户端数据广播出去(传递给每个客户端数据)
			for(Socket s : MyServer.socketList){
				try{
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println(line + "我收到你的消息");
					pw.flush();
					
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		
			
		}
//读取客户端数据
	private String readClientContext() {
		try{
			return br.readLine();
		}catch (IOException e){
			MyServer.socketList.remove(s);
			System.out.println("读取客户端数据失败！");
		}
		return null;

	}
		
	}



