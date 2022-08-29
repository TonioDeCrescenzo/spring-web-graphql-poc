package poc;

import poc.dto.ResponseDto;

import java.util.Objects;

public class Mapper {

  private Mapper(){}

  public static ResponseDto modelToDto(MongoDocument model){
    if (Objects.isNull(model)){
      return null;
    }

    return new ResponseDto(
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
  
  public static MongoDocument dtoToModel(ResponseDto responseDto){
    if (Objects.isNull(responseDto)){
      return null;
    }
    
    return new MongoDocument(
        new MongoDocumentPK(
            responseDto.getCatalogId(),
            responseDto.getAdeoProductId()),
        responseDto.getMessageType(),
        responseDto.getMessageDesc(),
        responseDto.getLabel(),
        responseDto.getProviderMetadata(),
        responseDto.getProduct(),
        responseDto.getMergedTimestamp(),
        responseDto.getLastProductUpdate()
    );
  }

}
