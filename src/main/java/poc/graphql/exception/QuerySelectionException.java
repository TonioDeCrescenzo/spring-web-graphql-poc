package poc.graphql.exception;

import java.util.List;

public class QuerySelectionException extends RuntimeException{

  private final List<String> errors;

  public QuerySelectionException(List<String> errors){
    super();
    this.errors = errors;
  }

  public List<String> getErrors(){
    return errors;
  }
}
