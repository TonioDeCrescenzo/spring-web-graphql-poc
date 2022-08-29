package poc.graphql.util;

import graphql.com.google.common.collect.Streams;
import graphql.schema.*;
import org.apache.logging.log4j.util.Strings;
import poc.graphql.exception.QuerySelectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class QueryNotationMapper {

  private QueryNotationMapper(){}

  public static List<String> toDotNotation(DataFetchingFieldSelectionSet fields){
    return fields.getFields().stream()
        .filter(f -> f.getType() instanceof GraphQLScalarType)
        .map(f -> f.getQualifiedName().replace("/","."))
        .toList();
  }

  public static String toGraphNotation(List<String> selection, GraphQLObjectType rootType){
    List<String> errors = new ArrayList<>();

    if(Objects.isNull(selection) || selection.isEmpty()){
      selection = rootType.getChildren().stream()
          .map(GraphQLFieldDefinition.class::cast)
          .map(GraphQLFieldDefinition::getName)
          .toList();
    }

    var graphQlNotation = String.format("{ %s }",
        Streams.mapWithIndex(
            selection.stream(),
            (field,index) -> {
                  if(Objects.isNull(field) || field.isBlank()){
                    errors.add(
                        String.format("'getFromResponse' cannot contain 'null' or 'empty' strings ( n. %s ).",
                            index+1));
                    return Strings.EMPTY;
                  }
                  return validateAndCompleteQuery(rootType, field, errors);
                }
        ).collect(Collectors.joining(" ")));

    if (errors.isEmpty()){
      return graphQlNotation;
    }

    throw new QuerySelectionException(errors);
  }

  private static String validateAndCompleteQuery(GraphQLObjectType type, String field, List<String> errors){
    var splitDotNotation = field.split("\\.",2);
    var fieldType = type.getChildren().stream()
        .map(GraphQLFieldDefinition.class::cast)
        .filter(graphField -> graphField.getName().equals(splitDotNotation[0]))
        .map(GraphQLFieldDefinition::getType)
        .findFirst();

    if(fieldType.isEmpty()){
      // Custom exception because this field name doesn't exist
      errors.add(
          String.format("Field '%s' not found in '%s' object",
              splitDotNotation[0], type.getName()));
      return Strings.EMPTY;
    }

    // if it is a scalar type then return its name
    if (GraphQLTypeUtil.isScalar(fieldType.get())){
      return splitDotNotation[0];
    }

    String innerQuery;
    GraphQLObjectType objType = GraphQLTypeUtil.unwrapOneAs(fieldType.get());
    // otherwise is an object then if is specified a child validate and complete it
    if (splitDotNotation.length > 1){
      innerQuery = validateAndCompleteQuery(objType, splitDotNotation[1], errors);
    }else{
      // otherwise complete it with all the children
      innerQuery = objType.getChildren().stream()
          .map(GraphQLFieldDefinition.class::cast)
          .map(child -> validateAndCompleteQuery(objType, child.getName(), errors))
          .collect(Collectors.joining(" "));
    }

    return String.format("%s { %s }", splitDotNotation[0], innerQuery);
  }

}
