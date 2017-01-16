
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
		String str= new String("今天请谁吃饭来着");
		Message msg = test.parseRawMessage(str);
		System.out.println(msg.body);
		System.out.println(msg.flag);
		
		
	}

}
