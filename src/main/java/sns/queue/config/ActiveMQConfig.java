package sns.queue.config;

import java.net.URI;
import java.util.Arrays;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.JmsTransactionManager;

@EnableJms
@Configuration
public class ActiveMQConfig {

	public static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	public static final String COMMENT_QUEUE = "user-queue";

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
	public ActiveMQConnectionFactory connectionFactory(RedeliveryPolicy redeliveryPolicy) {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
		connectionFactory.setTrustedPackages(Arrays.asList("sns.resource.rest.entity", "java.util"));
		connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
		return connectionFactory;
	}

	@Bean
	public RedeliveryPolicy redeliveryPolicy() {
		RedeliveryPolicy rp = new RedeliveryPolicy();
		rp.setMaximumRedeliveries(0);
		rp.setQueue(COMMENT_QUEUE);
		rp.setUseExponentialBackOff(false);
		return rp;
	}

	@Bean
	public JmsConfiguration jmsConfig(ActiveMQConnectionFactory connectionFactory,
			JmsTransactionManager jmsTransactionManager) {
		JmsConfiguration jmsConfig = new JmsConfiguration();
		jmsConfig.setConnectionFactory(connectionFactory);
		jmsConfig.setConcurrentConsumers(14);
		jmsConfig.setAsyncConsumer(true);
		jmsConfig.setTransactionManager(jmsTransactionManager);

		return jmsConfig;
	}

	@Bean
	public JmsTransactionManager jmsTransactionManager(ActiveMQConnectionFactory connectionFactory) {
		JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
		jmsTransactionManager.setConnectionFactory(connectionFactory);
		return jmsTransactionManager;
	}

	@Bean
	public ActiveMQComponent actsivemq(JmsConfiguration jmsConfig) {
		ActiveMQComponent amqc = new ActiveMQComponent();
		amqc.setConfiguration(jmsConfig);
		return amqc;
	}
}