package poc.spring.avro.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLScalarType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poc.spring.avro.graphql.Dto;
import poc.spring.avro.graphql.Mapper;
import poc.spring.avro.graphql.MongoDocumentPK;
import poc.spring.avro.graphql.repository.MongoRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MongoDataFetchers {
  
  @Autowired
  MongoRepository repository;
  
  public static final String ADEO_ID_FIELD = "adeoId";
  public static final String CATALOG_ID_FIELD = "catalogId";
  public static final String GTINS_FIELD = "gtins";
  
  public final DataFetcher<CompletableFuture<Dto>> byKey = e ->
      repository.findById(
          new MongoDocumentPK(
              e.getGraphQlContext().get(CATALOG_ID_FIELD),
              e.getGraphQlContext().get(ADEO_ID_FIELD)
          )
      ).map(Mapper::modelToDto).toFuture();
  
  public final DataFetcher<CompletableFuture<List<Dto>>> byGtinIn = e ->
      repository.findAllByGtins(
          e.getGraphQlContext().get(CATALOG_ID_FIELD),
          e.getGraphQlContext().get(GTINS_FIELD),
          graphQlSelectionToMongoProjection(e.getSelectionSet())
      ).map(Mapper::modelToDto).collectList().toFuture();
  
  // This will work only if the required fields have the same path both in dto and model
  // this is not a problem for adeoId and catalogId also if they are in the mongo document pk ( not like in the dto )
  // because mongo will read always the id data
  private static List<String> graphQlSelectionToMongoProjection(DataFetchingFieldSelectionSet fields){
    return fields.getFields().stream()
        .filter(f -> f.getType() instanceof GraphQLScalarType)
        .map(f -> f.getQualifiedName().replace("/","."))
        .toList();
  }
  
}
