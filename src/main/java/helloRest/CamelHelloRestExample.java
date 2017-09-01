package helloRest;

import java.io.InputStream;
import java.io.OutputStream;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;

public class CamelHelloRestExample {
	public static void main(String[] args) throws Exception{
		CamelContext context = new DefaultCamelContext();
		
		try{
			//ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
//			RestletComponent r = new RestletComponent();
//			
//			context.addComponent("restlet", new RestletComponent());
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					restConfiguration().component("restlet").host("localhost").port("8585").bindingMode(RestBindingMode.auto);
					rest("/say")
						.get("/hello").to("direct:hello");
					
					from("direct:hello")
						.process(new Processor() {
							
							@Override
							public void process(Exchange exchange) throws Exception {
								System.out.println("Hello World! Nice to meet you!");
								
							}
						});
				}
			});
			
			context.start();
			while(true){
				try{
					// Run, run, run forever
					Thread.sleep(50000);
				} catch(InterruptedException e){}
			}
		} finally{
			context.stop();
		}
	}
}
