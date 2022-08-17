package poc.spring.avro.graphql.repository;

import poc.spring.avro.graphql.MongoDocument;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CustomMongoRepository {
  
  Flux<MongoDocument> findAllByGtins(String catalogId, List<String> gtins,List<String> projection);
  
}
