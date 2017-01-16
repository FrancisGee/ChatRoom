
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
	public static List<Socket> socketList = Collections
				.synchronizedList(new ArrayList<Socket>());		//定义一个线程安全的list
	public  static HashSet<String> user_list = new HashSet<String>(); 			//登录用户集合
	public static HashMap<String,Socket> map = new HashMap<String,Socket>();
	
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
	private String name;
	private PrintWriter writer;
	private int isClosed = 0;
	
	ServerThread(Socket s) throws IOException{
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		sendHello(s);
	}
	
	@Override
	public void run(){
		
		
		String firstmsg =null;
		firstmsg = readClientContext();
		String[] spilted = firstmsg.split(" ");
		String command = spilted[0];
		String  name = spilted[1];
		
		
		MyServer.map.put(name, s);
		

	
		
	if(command.equals("/login")){
		if(!MyServer.user_list.contains(name)){
			
			MyServer.user_list.add(name);
			
			try {
				PrintWriter pw = new PrintWriter(s.getOutputStream());
				pw.println("you have logined");
				pw.flush();
				
				
				//广播消息(给其他用户)
				for(Socket s : MyServer.socketList){
					if(s != this.s){
					try{
						PrintWriter pw1 = new PrintWriter(s.getOutputStream());
						pw1.println( name+" has logined ");
						pw1.flush();
						
					}catch (IOException e){
						e.printStackTrace();
					}
				}
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		else{
			try {
				System.out.println("非法用户想要进来");
				PrintWriter pw = new PrintWriter(s.getOutputStream());
				pw.println("Name exist, please choose anthoer name....");
				pw.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	else if(command.equals("/quit")){
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
	
	//处理后续消息
	
		String line = null;
		while((line = readClientContext()) != null){
			//服务器记录日志信息
			System.out.println(line);
			MessageHelper.Message talk = MessageHelper.parseRawMessage(line);
			int flag = talk.getFlag();
			
			if(flag == 1){
			//向某个用户私聊
			//拿到这个用户的Socket
			Socket target = MyServer.map.get(talk.getTo());
			
			
			//对方看到的消息
				try {
					PrintWriter pw３= new PrintWriter(target.getOutputStream());
					pw３.println(name+ " 对你说 "+ talk.getBody());
					pw３.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//自己看到的消息
				try {
					PrintWriter pw8= new PrintWriter(s.getOutputStream());
					pw8.println(" 你对 "+ talk.getTo()+ " 说 "+ talk.getBody());
					pw8.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(flag == 2){
				//代表这是一条广播消息
				//广播消息(给其他用户)
				for(Socket s : MyServer.socketList){
					if(s != this.s){
					try{
						PrintWriter pw1 = new PrintWriter(s.getOutputStream());
						pw1.println(name + "说"+": "+talk.getBody());
						pw1.flush();
						
					}catch (IOException e){
						e.printStackTrace();
					}
				}
				}
				
				//自己看到的消息
				try {
					PrintWriter pw6 = new PrintWriter(s.getOutputStream());
					pw6.println("你说: "+ talk.getBody());
					pw6.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			
			//将客户端数据广播出去(传递给每个客户端数据)
			//广播程序已经调通
		/*	for(Socket s : MyServer.socketList){
				try{
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println(name +"说了"+line + "这句话");
					pw.flush();
					
				}catch (IOException e){
					e.printStackTrace();
				}
			}*/
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
		
	
	//给客户端发送欢迎登录消息
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



