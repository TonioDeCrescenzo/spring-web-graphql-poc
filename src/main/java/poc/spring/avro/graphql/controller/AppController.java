package poc.spring.avro.graphql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poc.spring.avro.graphql.Dto;
import poc.spring.avro.graphql.service.AppService;
import poc.spring.avro.graphql.util.GraphQlUtils;

import java.util.Map;
import java.util.Objects;

@RestController
public class AppController {
  private final GraphQL graphQL;
  private final AppService service;
  private final ObjectMapper objectMapper;

  @Autowired
  public AppController(final AppService service, final ObjectMapper objectMapper) {
    this.graphQL = GraphQlUtils.initializeGraphQlSchema(service);
    this.service = service;
    this.objectMapper = objectMapper;
  }

  @PostMapping("one/by/{adeoProductId}")
  public Dto getOne(@RequestBody(required = false) String query, @PathVariable String adeoProductId)
      throws GraphQLException{
    // To execute a graphql query is mandatory the list of output fields, if you need all the fields you must write all of them
    if (Objects.isNull(query)){
      return service.getOne(adeoProductId);
    }
    return objectMapper.convertValue(
        GraphQlUtils.executeGraphQlQuery(graphQL, "getOneProduct", query, Map.of("adeoProductId",adeoProductId)),
        Dto.class);
  }

  @PostMapping("all/by/{adeoProductId}")
  public Dto[] getAll(@RequestBody(required = false) String query, @PathVariable String adeoProductId)
      throws GraphQLException {

    if (Objects.isNull(query)){
      return service.getAll(adeoProductId).toArray(Dto[]::new);
    }
    return objectMapper.convertValue(
        GraphQlUtils.executeGraphQlQuery(graphQL, "getAllProducts", query, Map.of("adeoProductId",adeoProductId)),
        Dto[].class);
  }

}
