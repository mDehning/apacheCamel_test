package helloWorld;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.eclipse.EclipsePackageScanClassResolver;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.PackageScanClassResolver;

public class CamelHelloWorldExample {
	public static void main(String[] args) throws Exception{
		PackageScanClassResolver eclipseResolver = new EclipsePackageScanClassResolver();
		CamelContext context = new DefaultCamelContext();
		context.setPackageScanClassResolver(eclipseResolver);
		
		try{
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		
			context.addComponent("activemq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
			
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					// TODO Auto-generated method stub
					from("activemq:queue:test.queue")
					.to("stream:out");
				}
			});
			
			ProducerTemplate template = context.createProducerTemplate();
			context.start();
			for (int i = 0; i < 10; i++) {
	            template.sendBody("activemq:queue:test.queue", "Test Message: " + i);
	            Thread.sleep(100);
	        }
			template.sendBody("activemq:queue:test.queue", "Hello World!!");
			Thread.sleep(1000);
			template.sendBody("activemq:queue:test.queue", "Goodbye World!!");
			Thread.sleep(1000);
			System.out.println("Finished");
		} finally{
			context.stop();
		}
	}
}
