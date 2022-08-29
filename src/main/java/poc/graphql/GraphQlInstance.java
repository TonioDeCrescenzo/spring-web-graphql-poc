package poc.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import org.apache.avro.Schema;
import poc.graphql.exception.EmptyResultSetException;
import poc.graphql.exception.UnexpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static poc.graphql.util.QueryNotationMapper.toGraphNotation;

public class GraphQlInstance {
  public record QueryInfo<T,S>(String name, Class<T> outputType, DataFetcher<S> dataFetcher){
  }
  
  private final ObjectMapper objectMapper;
  private final Map<String,GraphQLObjectType> customOutputTypes = new HashMap<>();
  private final GraphQL instance;

  public GraphQlInstance(Set<QueryInfo<?,?>> queryInfo, ObjectMapper objectMapper){
    this.objectMapper = objectMapper;
    
    // create GRAPHQL OBJECTS starting from the query output type defined in the query info
    queryInfo.stream()
        .map(QueryInfo::outputType)
        .collect(Collectors.toSet())
        .forEach(this::createAndRegisterGraphQlObject);
    
    // define GRAPHQL QUERY name and return type of each one requested
    // Each query will be a field of the graphql Data schema
    var queryObjectType = GraphQLObjectType.newObject()
        .name("Data")
        .fields(
            queryInfo.stream().map(query ->
                GraphQLFieldDefinition.newFieldDefinition()
                    .name(query.name())
                    .type(getOutputTypeReference(query.outputType()))
                    .build()
            ).toList()
        ).build();
    
    // Assign the DATA FETCHER to each query
    GraphQLCodeRegistry codeRegistry = newCodeRegistry()
        .dataFetchers(
            "Data",
            queryInfo.stream()
                .collect(Collectors.toMap(QueryInfo::name,QueryInfo::dataFetcher))
        ).build();
    
    // Build GRAPHQL SCHEMA using graphql objects, query and data fetcher defined
    GraphQLSchema schema = GraphQLSchema.newSchema()
        .additionalTypes(Set.copyOf(customOutputTypes.values()))
        .query(queryObjectType)
        .codeRegistry(codeRegistry)
        .build();
    // Build GRAPH INSTANCE from the defined schema
    instance = GraphQL.newGraphQL(schema)
        .queryExecutionStrategy(new AsyncExecutionStrategy())
        .build();
  }
  
  public <T> T executeGraphQlQuery(String name, List<String> outputFields, Map<String,Object> parameters, Class<T> returnType){

    var result = instance.execute(
        ExecutionInput.newExecutionInput()
            .query(
                String.format("query { %s %s }",
                    name,
                    toGraphNotation(outputFields,customOutputTypes.get(getClassName(returnType))))
            )
            .graphQLContext(parameters)
            .build()
    );
    
    if (!result.getErrors().isEmpty()){
        throw new UnexpectedException(result.getErrors());
    }
    
    if (result.isDataPresent()){
      return objectMapper.convertValue(
          result.<Map<String,Object>>getData().get(name),
          returnType
      );
    }
    
    throw new EmptyResultSetException();
  }
  
  private String createAndRegisterGraphQlObject(Class<?> clazz){
    var className = getClassName(clazz);
    if (!customOutputTypes.containsKey(className)) {
      
      // Define the GRAPHQL FIELDS of this object
      List<GraphQLFieldDefinition> newFields = new ArrayList<>();
      Arrays.stream(getDeclaredFields(clazz))
          .forEach(field -> {
            if (!field.getType().getPackageName().startsWith(Schema.class.getPackageName()) && !Modifier.isFinal(field.getModifiers())){
              var graphQlType = getGraphQlType(field.getGenericType());
              newFields.add(GraphQLFieldDefinition.newFieldDefinition()
                  .name(field.getName())
                  .type((GraphQLOutputType) graphQlType)
                  .build());
            }
          });
  
      // Create the GRAPHQL OBJECT with the defined fields
      var outputType = GraphQLObjectType.newObject()
          .name(className)
          .fields(newFields)
          .build();

      customOutputTypes.putIfAbsent(
          className,
          outputType
      );
      
    }
    return className;
  }
  
  private GraphQLType getGraphQlType(Type type){
    var typeName = (type instanceof ParameterizedType parameterizedType ?
        parameterizedType.getRawType() : type).getTypeName();

    return switch (typeName){
      case "java.lang.String", "java.time.Instant" -> GraphQLString;
      case "java.lang.Boolean" -> GraphQLBoolean;
      case "float","java.lang.Float" -> GraphQLFloat;
      case "long","java.lang.Long" -> ExtendedScalars.GraphQLLong;
      case "int", "java.lang.Integer" -> GraphQLInt;
      case "java.util.Map" -> ExtendedScalars.Json;
      case "java.util.List" -> {
        assert type instanceof ParameterizedType;
        yield GraphQLList.list(getGraphQlType(
          ((ParameterizedType) type).getActualTypeArguments()[0]
        ));
      }
      default -> {
        assert type instanceof Class;
        yield  GraphQLTypeReference.typeRef(
            createAndRegisterGraphQlObject((Class<?>) type)
        );
      }
    };
  }
  
  private String getClassName(Class<?> clazz){
    return (clazz.isArray() ? clazz.getComponentType() : clazz).getSimpleName();
  }

  private GraphQLOutputType getOutputTypeReference(Class<?> clazz){
    var typeReference = GraphQLTypeReference.typeRef(getClassName(clazz));
    return clazz.isArray() ? GraphQLList.list(typeReference) : typeReference;
  }
  
  private Field[] getDeclaredFields(Class<?> clazz){
    return (clazz.isArray() ? clazz.getComponentType() : clazz).getDeclaredFields();
  }
}
