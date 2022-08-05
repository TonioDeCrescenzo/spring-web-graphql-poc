package poc.spring.avro.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poc.spring.avro.graphql.Dto;
import poc.spring.avro.graphql.Mapper;
import poc.spring.avro.graphql.repository.AppRepository;

import java.util.List;
import java.util.Objects;

@Service
public class AppService {

  @Autowired
  AppRepository repository;

  @GraphQLQuery(name = "getOneProduct")
  public Dto getOne(@GraphQLArgument(name = "adeoProductId") String adeoProductId){
    return Mapper.modelToDto(
        repository.getOne(adeoProductId)
    );
  }

  @GraphQLQuery(name = "getAllProducts")
  public List<Dto> getAll(@GraphQLArgument(name = "adeoProductId") String adeoProductId){
    return repository.getAll(adeoProductId).stream().map(Mapper::modelToDto).filter(Objects::nonNull).toList();
  }

}
