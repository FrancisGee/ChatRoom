
public class MessageHelper {
	public static final String WHITESPACE = " ";
		public static class Message{
		//	private final String from;
			private final String to;
			private final String body;
			
			public Message( String to, String body){
	//			this.from = from;
				this.to = to;
				this.body = body;
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
				return  new Message(to,body);
				
				
		
			}
				return null;
			
			
		}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MessageHelper  test = new MessageHelper();
		String str= new String("/to Tom how are you ");
		if(str.startsWith("/to")){
			String[] msg = null;
			msg = str.split(" ");
			String toname = msg[1];
			String body = " ";
			for(int i = 2; i < msg.length; i++){
				body += " "+ msg[i];
			}
			
			System.out.println(toname);
			System.out.println(body);
		}
	}

}
