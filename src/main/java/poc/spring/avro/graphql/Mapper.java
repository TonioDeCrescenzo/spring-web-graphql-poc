package poc.spring.avro.graphql;

import com.adeo.cps.kafka.model.V4.ProductUpdated;

import java.util.Objects;

public class Mapper {

  public static Dto modelToDto(ProductUpdated model){
    if (Objects.isNull(model)){
      return null;
    }

    return new Dto(
        model.getCatalogId(),
        model.getAdeoProductId(),
        model.getMessageType(),
        model.getMessageDesc(),
        model.getLabel(),
        model.getProviderMetadata(),
        model.getProduct(),
        model.getMergedTimestamp(),
        model.getLastProductUpdate()
    );
  }

}
