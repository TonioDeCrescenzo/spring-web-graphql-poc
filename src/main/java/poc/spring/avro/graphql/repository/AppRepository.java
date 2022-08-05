package poc.spring.avro.graphql.repository;


import com.adeo.cps.kafka.model.V4.ProductUpdated;
import org.springframework.stereotype.Repository;
import poc.spring.avro.graphql.util.StaticResourceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class AppRepository {

 public ProductUpdated getOne(String adeoProductId) {
  try {
   return adeoProductId.equals("111") ?
       StaticResourceHelper.readJsonResource("ProductUpdated.json",ProductUpdated.class) :
       null;
  } catch (IOException e) {
   throw new RuntimeException(e);
  }
 }

 public List<ProductUpdated> getAll(String adeoProductId) {
  ProductUpdated product = null;
  try {
   product = adeoProductId.equals("111") ?
       StaticResourceHelper.readJsonResource("ProductUpdated.json",ProductUpdated.class) :
       null;
  } catch (IOException e) {
   throw new RuntimeException(e);
  }
  return Objects.nonNull(product) ? List.of(product,product,product) : new ArrayList<>();
 }

}
