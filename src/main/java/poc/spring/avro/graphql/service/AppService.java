package poc.spring.avro.graphql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poc.spring.avro.graphql.Dto;
import poc.spring.avro.graphql.Mapper;
import poc.spring.avro.graphql.MongoDocument;
import poc.spring.avro.graphql.MongoDocumentPK;
import poc.spring.avro.graphql.repository.MongoRepository;
import poc.spring.avro.graphql.util.StaticResourceHelper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class AppService {
  
  @Autowired
  MongoRepository mongoTemplate;

  // Just to add a document in the embedded mongo instance
  @PostConstruct
  public void saveDataToMongo() throws IOException {
    mongoTemplate.save(
        StaticResourceHelper.readJsonResource("ProductDocument.json", MongoDocument.class)
    ).block();
  }

  public Mono<Dto> getOne(String catalogId, String adeoProductId){
    return mongoTemplate.findById(new MongoDocumentPK(catalogId,adeoProductId)).map(Mapper::modelToDto).filter(Objects::nonNull);
  }

  public Flux<Dto> getAllByGtins(String catalogId, List<String> gtins){
    return mongoTemplate.findAllByGtins(catalogId, gtins,null).map(Mapper::modelToDto).filter(Objects::nonNull);
  }

}
