package poc.graphql.exception;

import graphql.GraphQLError;

import java.util.List;

public class UnexpectedException extends RuntimeException{

  private final List<GraphQLError> errors;

  public UnexpectedException(List<GraphQLError> errors){
    super();
    this.errors = errors;
  }

  public List<GraphQLError> getErrors(){
    return errors;
  }
}
