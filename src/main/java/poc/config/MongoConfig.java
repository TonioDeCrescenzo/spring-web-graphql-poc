package poc.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import poc.MongoDocument;
import poc.util.MongoIndexesHelper;
import poc.util.StaticResourceHelper;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "poc.repository")
public class MongoConfig implements InitializingBean, DisposableBean {
  private static final String DB_NAME = "poc";
  private MongodExecutable executable;

  private final Boolean embeddedEnabled;

  @Autowired
  public MongoConfig(@Value("${spring.mongodb.embedded.enable}") String enabled){
    this.embeddedEnabled = Boolean.parseBoolean(enabled);
  }

  @Bean
  public MongoCustomConversions defaultMongoCustomConversions() {
    return new MongoCustomConversions(Collections.emptyList());
  }

  @Bean
  public MongoClient mongoClient(){
    return new ReactiveMongoClientFactory(new ArrayList<>()).createMongoClient(
        MongoClientSettings.builder().applyConnectionString(
            new ConnectionString("mongodb://localhost:27017")
        ).build());
  }

  @Bean
  public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) throws IOException {
    var mongoTemplate = new ReactiveMongoTemplate(mongoClient, DB_NAME);
    MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
    mongoMapping.setCustomConversions(defaultMongoCustomConversions());
    mongoMapping.afterPropertiesSet();

    MongoIndexesHelper.createIndexes(mongoClient, DB_NAME, MongoDocument.class);

    mongoTemplate.save(
        StaticResourceHelper.readJsonResource("ProductDocument.json", MongoDocument.class)
    ).block();

    return mongoTemplate;
  }

  @Override
  @PreDestroy
  public void destroy() {
    if (executable != null)
      executable.stop();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if(Boolean.FALSE.equals(embeddedEnabled)) return;

    MongodConfig mongodConfig = MongodConfig.builder()
        .version(Version.Main.DEVELOPMENT)
        .net(new Net("localhost", 27017, Network.localhostIsIPv6()))
        .build();

    executable = MongodStarter.getDefaultInstance().prepare(mongodConfig);
    executable.start();
  }

}
