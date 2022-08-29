package poc.util;

import com.mongodb.MongoCommandException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

public class MongoIndexesHelper {

  private MongoIndexesHelper() {}

  public static final String INDEX_KEY_SPECS_CONFLICT = "IndexKeySpecsConflict";

  public static void createIndexes(MongoClient mongoClient, String databaseName, Class<?>... documentClasses) {
    for (Class<?> documentClass : documentClasses) {
      for (CompoundIndex index : documentClass.getAnnotationsByType(CompoundIndex.class)) {
        var document = documentClass.getAnnotation(Document.class);
        if (Objects.nonNull(document)) {
          MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(databaseName)
              .getCollection(document.collection());
          try {
            createIndex(collection, index);
          } catch (MongoCommandException e) {
            handleIndexCreationException(e, collection, index);
          }
        }
      }
    }
  }

  private static void handleIndexCreationException(MongoCommandException e,
      MongoCollection<org.bson.Document> collection, CompoundIndex index) {
    if (INDEX_KEY_SPECS_CONFLICT.equals(e.getErrorCodeName())) {
      collection.dropIndex(index.name());
      createIndex(collection, index);
    }
  }

  private static void createIndex(MongoCollection<org.bson.Document> collection, CompoundIndex index) {

    collection.createIndex(Indexes.compoundIndex(BsonDocument.parse(index.def())),
        new IndexOptions().name(index.name())).subscribe(new Subscriber<>() {
          @Override
          public void onSubscribe(Subscription subscription) {
            // not needed
          }

          @Override
          public void onNext(String s) {
            // not needed
          }

          @Override
          public void onError(Throwable throwable) {
            // not needed
          }

          @Override
          public void onComplete() {
            // not needed
          }
        });
  }

}
