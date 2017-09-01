package helloBeans;



import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;

/**
 * Starts up a Camel Context and Registers two instances of beans to act as connectors 
 * as well as a testProducers that randomly connect to them
 * @author mdehning
 *
 */
public class CamelHelloBeansExample {
	public static void main(String[] args) throws Exception{
		CamelContext context = new DefaultCamelContext();
		
		try{
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
			context.addComponent("activemq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
	
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					// Zwei Beans vorher erstellen, 
					ConsumerBean firstBean = new ConsumerBean();
					ConsumerBean secondBean = new ConsumerBean();
					//	Die bean Methode muss nicht zwingend auch einen Parameter konsumieren
					from("activemq:queue:first.queue")
						.bean(firstBean, "talk");
					
					//	Wenn die Methode alleridngs einen konsumiert, wird beim Binden offenbar versucht, das zuzuordnen nach Reihenfolge
					from("activemq:queue:second.queue")
					.bean(secondBean, "talkBack");
					
					//	Von einer Bean zur anderen weiterzuleiten geht über eine direkt, interne Route
					//	Hier wird der Rückgabewert von "talkThis" weitergeleitet - selbst wenn auf Verzögerungen zu warten ist
					from("activemq:queue:third.queue")
					.bean(secondBean, "talkThis")
					.to("direct:next");
					
					from("direct:next")
					.bean(new ConsumerBean(), "talkBack");
				}
			});
			ProducerTemplate template = context.createProducerTemplate();
			context.start();
			
			System.out.println("Started");
			for (int i = 0; i < 100; i++) {
				double t = Math.random(); 
				if(t < 0.3){
				
					template.sendBody("activemq:queue:first.queue", "Test Message " + i);
				} else if(t < 0.7){
				
					template.sendBody("activemq:queue:second.queue", "Test Message " + i);
				} else{
					System.out.println("--- \t"+i+"\t!");
					template.sendBody("activemq:queue:third.queue", "Test Message " + i);
				}
				
	            Thread.sleep((long) (Math.random() * 100));
	         
	            
	        }
			System.out.println("Finished, waiting for Stuff");
			Thread.sleep((long) (10000));
			System.out.println("Shutting Down!");
		} finally{
			context.stop();
		}
	}
}
