package helloBeans;

import java.io.Serializable;

public class ConsumerBean implements Serializable{
	private static int index = 0;
	private static final long serialVersionUID = 1L;
	
	
	private String name;
	public ConsumerBean(){
		// Bean serializer
		this.setName("Consumer "+(index++));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void talk(){
		System.out.println(this.getName() +":\t Hello! I got a message!");
	}
	
	public void talkBack(String thing){
		System.out.println(String.format("%s got:\t\t %s", this.getName(), thing));
	}
	
	public String talkThis(String thing){
		System.out.println(String.format("%s uses:\t\t %s", this.getName(), thing));
		thing = String.format("%s - %s - %s", thing, thing, thing);
		try{
			Thread.sleep(200);
		} catch(InterruptedException e){}
		return thing;
	}
}
