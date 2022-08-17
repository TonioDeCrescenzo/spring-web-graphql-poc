package poc.spring.avro.graphql;

import java.io.Serializable;
public class MongoDocumentPK implements Serializable {
  String catalogId;
  String adeoProductId;

  public MongoDocumentPK(String catalogId, String adeoProductId) {
    this.catalogId = catalogId;
    this.adeoProductId = adeoProductId;
  }

  public MongoDocumentPK() {
  }

  public String getCatalogId() {
    return catalogId;
  }

  public void setCatalogId(String catalogId) {
    this.catalogId = catalogId;
  }

  public String getAdeoProductId() {
    return adeoProductId;
  }

  public void setAdeoProductId(String adeoProductId) {
    this.adeoProductId = adeoProductId;
  }
}
