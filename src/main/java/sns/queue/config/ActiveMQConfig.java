package sns.queue.config;

import java.net.URI;
import java.util.Arrays;

import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@Configuration
public class ActiveMQConfig {

	public static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	public static final String COMMENT_QUEUE = "use2queue";

	@Bean
	public BrokerService createBrokerService() throws Exception {
		BrokerService broker = new BrokerService();
		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI("tcp://localhost:61616"));
		broker.addConnector(connector);
		broker.setPersistent(true);
		broker.setSchedulerSupport(true);

		return broker;
	}

	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
		connectionFactory.setTrustedPackages(Arrays.asList("sns.resource.rest.entity","java.util"));
		//connectionFactory.setRedeliveryPolicy(redeliveryPolicy());

		return connectionFactory;
	}

	/*@Bean
	public RedeliveryPolicy redeliveryPolicy()
	{
		RedeliveryPolicy rp = new RedeliveryPolicy();
		rp.setRedeliveryDelay(5l);
		rp.setMaximumRedeliveries(-1);
		rp.setQueue(COMMENT_QUEUE);
		rp.setUseExponentialBackOff(false);

		return rp;
	}*/
	//@Bean
	//public JmsComponent userqueue()
	//{
	//	return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory());
	//}
	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setDefaultDestinationName(COMMENT_QUEUE);
		template.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		
		return template;
	}

	@Bean
	public JmsConfiguration jmsConfig() {
		JmsConfiguration jc = new JmsConfiguration();
		jc.setConnectionFactory(connectionFactory());
		jc.setConcurrentConsumers(14);
		return jc;
	}

	@Bean
	public ActiveMQComponent actsivemq() {
		ActiveMQComponent amqc = new ActiveMQComponent();
		amqc.setConfiguration(jmsConfig());

		return amqc;
	}
}