package quote.dao;

import org.neo4j.ogm.authentication.Credentials;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("quote.dao")
@EnableNeo4jRepositories("quote.dao")
public class PersistenceContext {

  @Bean
  public SessionFactory getSessionFactory() {
    return new SessionFactory(configuration(), "quote.dao");
  }

  @Bean
  public Neo4jTransactionManager transactionManager() throws Exception {
    return new Neo4jTransactionManager(getSessionFactory());
  }

  @Bean
  public org.neo4j.ogm.config.Configuration configuration() {
	  org.neo4j.ogm.config.Configuration c = new org.neo4j.ogm.config.Configuration();
	  c.driverConfiguration().setURI("http://neo4j:admin@localhost:7474").setDriverClassName(
		      "org.neo4j.ogm.drivers.http.driver.HttpDriver");

	  return c;
  }
}