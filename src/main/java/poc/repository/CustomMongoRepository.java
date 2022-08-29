package poc.repository;

import poc.MongoDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomMongoRepository {
  Flux<MongoDocument> findAllByGtins(String catalogId, List<String> gtins, List<String> projection);
  Mono<MongoDocument> findByKey(String catalogId, String adeoId, List<String> projection);
}
