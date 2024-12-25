package config;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.demo.query.repo")
public class EmbeddedMongoConfig {

  private MongodExecutable mongodExecutable;

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    MongodStarter starter = MongodStarter.getDefaultInstance();
    MongodConfig mongodConfig =
        MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(
                new de.flapdoodle.embed.mongo.config.Net(
                    "localhost", 27017, Network.localhostIsIPv6()))
            .build();

    mongodExecutable = starter.prepare(mongodConfig);
    mongodExecutable.start();

    return new MongoTemplate(
        new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/test"));
  }

  @PreDestroy
  public void cleanUp() {
    mongodExecutable.stop();
  }
}
