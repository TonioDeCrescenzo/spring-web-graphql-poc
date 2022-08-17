package poc.spring.avro.graphql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import poc.spring.avro.graphql.Dto;
import poc.spring.avro.graphql.fetcher.MongoDataFetchers;
import poc.spring.avro.graphql.service.AppService;
import poc.spring.avro.graphql.util.GraphQlInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
public class AppController {
  @Autowired
  private AppService service;
  @Autowired
  MongoDataFetchers mongoDataFetcher;
  @Autowired
  private ObjectMapper objectMapper;
  private GraphQlInstance graphQL;
  private static final String GRAPHQL_BY_ID = "byId";
  private static final String GRAPHQL_BY_GTINS = "byGtin";

  @PostConstruct
  public void setGraphQlInstance() {
    // From this map will be created all the graphql requirement
    // The name of each query must be unique
    Set<GraphQlInstance.QueryInfo<?,?>> graphQlQueryMap = Set.of(
        new GraphQlInstance.QueryInfo<>(GRAPHQL_BY_ID, Dto.class, mongoDataFetcher.byKey),
        new GraphQlInstance.QueryInfo<>(GRAPHQL_BY_GTINS, Dto[].class, mongoDataFetcher.byGtinIn)
    );
    
    this.graphQL = new GraphQlInstance(graphQlQueryMap, objectMapper);
  }

  @PostMapping("one/by/{catalogId}/{adeoProductId}")
  public Mono<Dto> getOne(@RequestBody(required = false) String query,
                          @PathVariable String catalogId,
                          @PathVariable String adeoProductId)
      throws GraphQLException{
    // To execute a graphql query is mandatory the list of output fields, if you need all the fields you must write all of them
    // this is a workaround to be able to get all the fields when no field are specified
    if (Objects.isNull(query)){
      return service.getOne(catalogId, adeoProductId);
    }

    // GraphQl can't work with Flux and Mono
    // Reactive data from the DB will be converted in CompletableFuture in the DataFetchers
    // and then again in Mono or Flux in the controller to be the same of the common service return type
    return Mono.justOrEmpty(
          graphQL.executeGraphQlQuery(
              GRAPHQL_BY_ID,
              query,
              Map.of(
                  MongoDataFetchers.ADEO_ID_FIELD,adeoProductId,
                  MongoDataFetchers.CATALOG_ID_FIELD,catalogId
              ), Dto.class));
  }

  @PostMapping("all/by/{catalogId}")
  public Flux<Dto> getAll(@RequestBody(required = false) String query,
                          @PathVariable String catalogId, @RequestParam String gtins)
      throws GraphQLException {
    var listGtin = Arrays.stream(gtins.split(",")).toList();

    if (Objects.isNull(query)){
      return service.getAllByGtins(catalogId, listGtin);
    }

    return Flux.fromArray(
        graphQL.executeGraphQlQuery(
            GRAPHQL_BY_GTINS,
            query,
            Map.of(
                MongoDataFetchers.GTINS_FIELD,listGtin,
                MongoDataFetchers.CATALOG_ID_FIELD,catalogId
            ), Dto[].class));
  }

}
