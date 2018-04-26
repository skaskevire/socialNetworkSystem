package sns.dao.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.MongoClient;

@Configuration
@EnableTransactionManagement
@ComponentScan("sns.dao")
@EnableNeo4jRepositories("sns.dao")
public class PersistenceContextConfig {

  @Bean
  public SessionFactory getSessionFactory() {
    return new SessionFactory(configuration(), "sns.dao");
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
  
  @Bean
  public MongoClient mongoBean()
  {
	 return new MongoClient("localhost", 27017);
  }
  
  @Bean
  public MongoOperations mongoOperations()
  {
	  MongoTemplate mt = new MongoTemplate(new MongoClient(), "users");
     return mt;
  }
}