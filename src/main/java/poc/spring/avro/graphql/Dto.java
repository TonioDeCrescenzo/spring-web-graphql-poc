package poc.spring.avro.graphql;

import com.adeo.cps.kafka.model.V4.merged.MergedProduct;
import com.adeo.cps.kafka.model.V4.merged.ProviderMetadata;

import java.time.Instant;
import java.util.List;

public class Dto {

  private String catalogId;

  private String adeoProductId;

  private String messageType;

  private String messageDesc;

  private String label;

  private List<ProviderMetadata> providerMetadata;

  private MergedProduct product;

  private Instant mergedTimestamp;

  private Instant lastProductUpdate;

  public Dto() {
  }

  public Dto(String catalogId, String adeoProductId, String messageType,
             String messageDesc, String label, List<ProviderMetadata> providerMetadata,
             MergedProduct product, Instant mergedTimestamp, Instant lastProductUpdate) {
    this.catalogId = catalogId;
    this.adeoProductId = adeoProductId;
    this.messageType = messageType;
    this.messageDesc = messageDesc;
    this.label = label;
    this.providerMetadata = providerMetadata;
    this.product = product;
    this.mergedTimestamp = mergedTimestamp;
    this.lastProductUpdate = lastProductUpdate;
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
