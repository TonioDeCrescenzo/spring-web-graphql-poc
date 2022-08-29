package poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQLError;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poc.dto.RequestDtos;
import poc.dto.ResponseDto;
import poc.fetcher.MongoDataFetchers;
import poc.graphql.GraphQlInstance;
import poc.graphql.exception.EmptyResultSetException;
import poc.graphql.exception.QuerySelectionException;
import poc.graphql.exception.UnexpectedException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class AppController {

  private final GraphQlInstance graphQL;
  private static final String GRAPHQL_BY_ID = "byId";
  private static final String GRAPHQL_BY_GTINS = "byGtin";

  @Autowired
  public AppController(final MongoDataFetchers mongoDataFetcher, final ObjectMapper objectMapper) {
    // From this map will be created all the graphql requirements
    // The name of each query must be unique
    Set<GraphQlInstance.QueryInfo<?,?>> graphQlQueryMap = Set.of(
        new GraphQlInstance.QueryInfo<>(GRAPHQL_BY_ID, ResponseDto.class, mongoDataFetcher.byKey),
        new GraphQlInstance.QueryInfo<>(GRAPHQL_BY_GTINS, ResponseDto[].class, mongoDataFetcher.byGtinIn)
    );
    this.graphQL = new GraphQlInstance(graphQlQueryMap, objectMapper);
  }

  @PostMapping("one/by/adeoId")
  public ResponseDto getOne(@RequestBody RequestDtos.ItemByAdeoKey dto)
      throws GraphQLException{

    return graphQL.executeGraphQlQuery(
              GRAPHQL_BY_ID,
              dto.getFromResponse(),
              Map.of(
                  MongoDataFetchers.ADEO_ID_FIELD, dto.adeoProductId(),
                  MongoDataFetchers.CATALOG_ID_FIELD, dto.catalogId()),
              ResponseDto.class);
  }

  @PostMapping("all/by/gtins")
  public ResponseDto[] getAll(@RequestBody RequestDtos.ItemByGtins dto)
      throws GraphQLException {

    return graphQL.executeGraphQlQuery(
            GRAPHQL_BY_GTINS,
            dto.getFromResponse(),
            Map.of(
                MongoDataFetchers.GTINS_FIELD, dto.gtins(),
                MongoDataFetchers.CATALOG_ID_FIELD, dto.catalogId()
            ), ResponseDto[].class);
  }

  @ExceptionHandler(QuerySelectionException.class)
  public ResponseEntity<List<String>> graphQlExceptionHandler(QuerySelectionException ex){
    return ResponseEntity.badRequest().body(ex.getErrors());
  }

  @ExceptionHandler(UnexpectedException.class)
  public ResponseEntity<List<GraphQLError>> graphQlUnexpectedException(UnexpectedException ex){
    return ResponseEntity.internalServerError().body(ex.getErrors());
  }

  @ExceptionHandler(EmptyResultSetException.class)
  public ResponseEntity<String> graphQlEmptyResultException(EmptyResultSetException ex){
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No data found!");
  }
}
