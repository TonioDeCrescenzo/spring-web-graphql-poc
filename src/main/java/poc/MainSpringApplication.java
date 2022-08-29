package poc;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
public class MainSpringApplication {

  @JsonIgnoreProperties(ignoreUnknown = true)
  interface AvroJsonMixin {
    @JsonIgnore
    Schema getSchema();
    @JsonIgnore
    SpecificData getSpecificData();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return Jackson2ObjectMapperBuilder.json()
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(new Jdk8Module(), new JavaTimeModule())
        .build()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .addMixIn(SpecificRecordBase.class, AvroJsonMixin.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(MainSpringApplication.class);
  }

}
