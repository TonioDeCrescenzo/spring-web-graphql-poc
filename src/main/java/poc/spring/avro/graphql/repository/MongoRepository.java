package poc.spring.avro.graphql.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import poc.spring.avro.graphql.MongoDocument;
import poc.spring.avro.graphql.MongoDocumentPK;
import reactor.core.publisher.Mono;

@Repository
public interface MongoRepository
    extends ReactiveMongoRepository<MongoDocument, MongoDocumentPK>, CustomMongoRepository {
  
  @Query(value = "{ field: ?0 }", fields = "{ ?1 }")
  Mono<MongoDocument> findByFieldk(String field, String projection);
  
}
