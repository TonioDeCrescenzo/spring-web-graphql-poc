package poc.fetcher;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poc.Mapper;
import poc.dto.ResponseDto;
import poc.repository.MongoRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static poc.graphql.util.QueryNotationMapper.toDotNotation;

@Service
public class MongoDataFetchers {
  
  @Autowired
  MongoRepository repository;
  
  public static final String ADEO_ID_FIELD = "adeoId";
  public static final String CATALOG_ID_FIELD = "catalogId";
  public static final String GTINS_FIELD = "gtins";
  
  public final DataFetcher<CompletableFuture<ResponseDto>> byKey = e ->
      repository.findByKey(
          e.getGraphQlContext().get(CATALOG_ID_FIELD),
          e.getGraphQlContext().get(ADEO_ID_FIELD),
          toDotNotation(e.getSelectionSet())
      ).map(Mapper::modelToDto).toFuture();
  
  public final DataFetcher<CompletableFuture<List<ResponseDto>>> byGtinIn = e ->
      repository.findAllByGtins(
          e.getGraphQlContext().get(CATALOG_ID_FIELD),
          e.getGraphQlContext().get(GTINS_FIELD),
          toDotNotation(e.getSelectionSet())
      ).map(Mapper::modelToDto).collectList().toFuture();

}
