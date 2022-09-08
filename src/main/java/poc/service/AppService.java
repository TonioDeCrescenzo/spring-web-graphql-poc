package poc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poc.Mapper;
import poc.dto.ResponseDto;
import poc.repository.MongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AppService {

  @Autowired
  private MongoRepository repository;

  public Mono<ResponseDto> getByKey(String catalogId, String adeoKey){
    return repository.findByKey(catalogId,adeoKey,null)
        .map(Mapper::modelToDto);
  }

  public Flux<ResponseDto> getByGtins(String catalogId, List<String> gtins){
    return repository.findAllByGtins(catalogId,gtins,null)
        .map(Mapper::modelToDto);
  }

}
