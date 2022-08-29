package poc.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import poc.MongoDocument;
import poc.MongoDocumentPK;
import reactor.core.publisher.Mono;

public interface MongoRepository
    extends ReactiveMongoRepository<MongoDocument, MongoDocumentPK>, CustomMongoRepository {
  
  @Query(value = "{ field: ?0 }", fields = "{ ?1 }")
  Mono<MongoDocument> findByFieldk(String field, String projection);
  
}
