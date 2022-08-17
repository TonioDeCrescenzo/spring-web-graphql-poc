package poc.spring.avro.graphql.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "poc.spring.avro.graphql.repository")
public class MongoConfig implements InitializingBean, DisposableBean {
  private String database = "poc";
  private static MongodExecutable executable = null;
  private static final MongodStarter starter = MongodStarter.getDefaultInstance();


  @Bean
  public MongoCustomConversions defaultMongoCustomConversions() {
    return new MongoCustomConversions(Collections.emptyList());
  }

  @Bean
  public ReactiveMongoTemplate reactiveMongoTemplate() throws Exception {
    var mongoTemplate = new ReactiveMongoTemplate(
        new ReactiveMongoClientFactory(new ArrayList<>()).createMongoClient(
            MongoClientSettings.builder().applyConnectionString(
                new ConnectionString("mongodb://localhost:27017")
            ).build()
        )
        , database);
    MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
    mongoMapping.setCustomConversions(defaultMongoCustomConversions());
    mongoMapping.afterPropertiesSet();

    return mongoTemplate;
  }

  @Override
  @PreDestroy
  public void destroy() throws Exception {
    if (executable != null)
      executable.stop();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    MongodConfig mongodConfig = MongodConfig.builder()
        .version(Version.Main.DEVELOPMENT)
        .net(new Net("localhost", 27017, Network.localhostIsIPv6()))
        .build();

    executable = starter.prepare(mongodConfig);
    executable.start();
  }
}
