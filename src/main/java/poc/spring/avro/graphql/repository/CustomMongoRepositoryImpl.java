package poc.spring.avro.graphql.repository;

import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import poc.spring.avro.graphql.MongoDocument;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@Repository
public class CustomMongoRepositoryImpl implements CustomMongoRepository{
  
  @Autowired
  ReactiveMongoTemplate template;
  
  @Override
  public Flux<MongoDocument> findAllByGtins(String catalogId, List<String> gtins, @Nullable List<String> projectionFields) {
    var query = Query.query(
        Criteria.where("_id.catalogId").is(catalogId)
            .and("product.availableGtin.value").in(gtins));
    
    if(Objects.nonNull(projectionFields) && !projectionFields.isEmpty()){
      query.fields().include(projectionFields.toArray(String[]::new));
    }
    
    return template.find(query, MongoDocument.class);
  }
}
