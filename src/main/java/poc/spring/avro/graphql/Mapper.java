package poc.spring.avro.graphql;

import java.util.Objects;

public class Mapper {

  public static Dto modelToDto(MongoDocument model){
    if (Objects.isNull(model)){
      return null;
    }

    return new Dto(
        model.getProductDocumentPK().getCatalogId(),
        model.getProductDocumentPK().getAdeoProductId(),
        model.getMessageType(),
        model.getMessageDesc(),
        model.getLabel(),
        model.getProviderMetadata(),
        model.getProduct(),
        model.getMergedTimestamp(),
        model.getLastProductUpdate()
    );
  }
  
  public static MongoDocument dtoToModel(Dto dto){
    if (Objects.isNull(dto)){
      return null;
    }
    
    return new MongoDocument(
        new MongoDocumentPK(
            dto.getCatalogId(),
            dto.getAdeoProductId()),
        dto.getMessageType(),
        dto.getMessageDesc(),
        dto.getLabel(),
        dto.getProviderMetadata(),
        dto.getProduct(),
        dto.getMergedTimestamp(),
        dto.getLastProductUpdate()
    );
  }

}
