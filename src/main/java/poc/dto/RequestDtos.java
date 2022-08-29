package poc.dto;

import java.util.List;

public class RequestDtos {

  public record ItemByAdeoKey (String catalogId, String adeoProductId, List<String> getFromResponse) {}
  public record ItemByGtins (String catalogId, List<String> gtins, List<String> getFromResponse) {}

}
