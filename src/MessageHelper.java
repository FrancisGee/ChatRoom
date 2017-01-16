
public class MessageHelper {
	public static final String WHITESPACE = " ";
		public static class Message{
		//	private final String from;
			private final String to;
			private final String body;
			private final int flag;			//用于区分不同的命令				
			
			public Message( String to, String body, int flag){
	//			this.from = from;
				this.to = to;
				this.body = body;
				this.flag = flag;
			}
			
		/*	public String getFrom(){
				return this.from;
			}*/
			
			public String getTo(){
				return this.to;
			}
			
			public String getBody(){
				return this.body;
			}
			
			public int getFlag(){
				return this.flag;
			}
			
		}
		
		//Raw message format: From user1 to user2 : message body
	public static Message parseRawMessage(String raw){
			if(raw.startsWith("/to")){
				String[] msg = null;
				msg = raw.split(" ");
				String to = msg[1];
				String body = " ";
				for(int i = 2; i < msg.length; i++){
					body += " "+ msg[i];
				}
				
				int  flag = 1;													//flag为１代表/to命令
				
				
				return  new Message(to,body,flag);
				
				
		
			}
			if(raw.startsWith("//hi")){
				String[] msg = null;
				msg = raw.split(" ");
				if(msg.length == 1){
					String body = "Hi, 大家好 ! 我来咯~";
					String to = null;
					int flag = 3;
					return new Message(to,body,flag);
				}
				if(msg.length ==  2){
					String to = msg[1];
					String body = "Hi，你好啊~";
					int flag = 4;
					return new Message(to,body,flag);
					
				}
			}
			if(raw.equals("/who")){
				String to = null;
				String body = raw;
				int flag = 5;
				return new Message(to,body,flag);
			}
			if(raw.startsWith("/history")){
				String[] msg = null;
				msg = raw.split(" ");
				if(msg.length == 1){
					String to = null;
					String body = raw;
					int flag = 6;
					return new Message(to,body,flag);
				}
				if(msg.length == 3){
					String to = null;
					String body = " ";
					for(int i = 1; i < msg.length; i++){
						body += " "+ msg[i];	
					}
					int flag = 7;
					return new Message(to,body,flag);
					}
				}
			if(raw.equals("/quit")){
				String to = null;
				String body = null;
				int flag = 8;
				return new Message(to,body,flag);
			}
			
			
			else{
				String body = raw;
				String to = null;
				int flag = 2;													//flag为２代表广播消息
				return new Message(to,body,flag);
			}
				
		}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MessageHelper  test = new MessageHelper();
		String str= new String("/quit");
		Message msg = test.parseRawMessage(str);
		System.out.println(msg.body);
		System.out.println(msg.flag);
		
		
	}

}
