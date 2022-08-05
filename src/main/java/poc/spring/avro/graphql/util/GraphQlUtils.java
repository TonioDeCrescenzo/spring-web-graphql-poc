package poc.spring.avro.graphql.util;

import graphql.GraphQL;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.BeanResolverBuilder;
import org.apache.avro.Schema;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQlUtils {

  public static GraphQL initializeGraphQlSchema(Object graphQlSingletonService) {
    // Generate automatically graphql query and mutation
    var schema = new GraphQLSchemaGenerator()
        .withBasePackages("poc.spring.avro.graphql")
        .withOperationsFromSingleton(graphQlSingletonService)
        // From our pojo, exclude autogenerated fields and method from org.apache.avro
        .withNestedResolverBuilders(new BeanResolverBuilder().withFilters(
            member -> {
              var packageName = Strings.EMPTY;
              if (member instanceof Field field){
                packageName = field.getType().getPackageName();
              }
              if(member instanceof Method method){
                packageName = method.getReturnType().getPackageName();
              }
              return !packageName.startsWith(Schema.class.getPackageName());
            }
        )).generate();
    return new GraphQL.Builder(schema).build();
  }

  public static Object executeGraphQlQuery(
      final GraphQL graphQL, String name, String query, Map<String,Object> parameters){

    var arguments = parameters.isEmpty() ? Strings.EMPTY :
        String.format("( %s )",
            parameters.entrySet().stream().map(e -> String.format("%s:\"%s\"",e.getKey(),e.getValue())).collect(Collectors.joining(", ")));

    var result = graphQL.execute(
        String.format("{ %s%s %s }", name,arguments,query)
    );

    if (!result.getErrors().isEmpty()){
      throw new RuntimeException(result.getErrors().toString());

    }else if (result.isDataPresent()){
      return result.<Map<String,Object>>getData().get(name);
    }

    return new HashMap<>();
  }

}