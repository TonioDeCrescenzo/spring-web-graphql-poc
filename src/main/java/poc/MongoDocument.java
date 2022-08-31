package poc;

import com.adeo.cps.kafka.model.V4.merged.MergedProduct;
import com.adeo.cps.kafka.model.V4.merged.ProviderMetadata;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "products")
@CompoundIndex(name = "byCatalogIdAndGtinIndex", def = "{'_id.catalogId': 1, 'product.availableGtin.value': 1}")

public class MongoDocument {
  @Id
  MongoDocumentPK productDocumentPK;

  String messageType;
  String messageDesc;
  String label;
  List<ProviderMetadata> providerMetadata;
  MergedProduct product;
  Instant mergedTimestamp;
  Instant lastProductUpdate;

  public MongoDocument() {
  }

  public MongoDocument(MongoDocumentPK productDocumentPK, String messageType, String messageDesc, String label, List<ProviderMetadata> providerMetadata, MergedProduct product, Instant mergedTimestamp, Instant lastProductUpdate) {
    this.productDocumentPK = productDocumentPK;
    this.messageType = messageType;
    this.messageDesc = messageDesc;
    this.label = label;
    this.providerMetadata = providerMetadata;
    this.product = product;
    this.mergedTimestamp = mergedTimestamp;
    this.lastProductUpdate = lastProductUpdate;
  }

  public MongoDocumentPK getProductDocumentPK() {
    return productDocumentPK;
  }

  public void setProductDocumentPK(MongoDocumentPK productDocumentPK) {
    this.productDocumentPK = productDocumentPK;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public String getMessageDesc() {
    return messageDesc;
  }

  public void setMessageDesc(String messageDesc) {
    this.messageDesc = messageDesc;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public List<ProviderMetadata> getProviderMetadata() {
    return providerMetadata;
  }

  public void setProviderMetadata(List<ProviderMetadata> providerMetadata) {
    this.providerMetadata = providerMetadata;
  }

  public MergedProduct getProduct() {
    return product;
  }

  public void setProduct(MergedProduct product) {
    this.product = product;
  }

  public Instant getMergedTimestamp() {
    return mergedTimestamp;
  }

  public void setMergedTimestamp(Instant mergedTimestamp) {
    this.mergedTimestamp = mergedTimestamp;
  }

  public Instant getLastProductUpdate() {
    return lastProductUpdate;
  }

  public void setLastProductUpdate(Instant lastProductUpdate) {
    this.lastProductUpdate = lastProductUpdate;
  }
}
