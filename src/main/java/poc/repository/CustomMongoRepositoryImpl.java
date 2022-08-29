package poc.repository;

import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import poc.MongoDocument;
import poc.MongoDocumentPK;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Repository
public class CustomMongoRepositoryImpl implements CustomMongoRepository{
  @Autowired
  ReactiveMongoTemplate template;
  
  @Override
  public Flux<MongoDocument> findAllByGtins(String catalogId, List<String> gtins, @Nullable List<String> projection) {
    var query = Query.query(
        Criteria.where("_id.catalogId").is(catalogId)
            .and("product.availableGtin.value").in(gtins));

    addProjection(query, projection);
    
    return template.find(query, MongoDocument.class);
  }

  @Override
  public Mono<MongoDocument> findByKey(String catalogId, String adeoId, @Nullable List<String> projection) {
    var query = Query.query(
        Criteria.where("_id").is(new MongoDocumentPK(catalogId,adeoId)));

    addProjection(query, projection);

    return template.findOne(query, MongoDocument.class);
  }

  private static void addProjection(Query query, List<String> projection){
    if(Objects.nonNull(projection) && !projection.isEmpty()){
      query.fields().include(projection.toArray(String[]::new));
    }
  }
}
