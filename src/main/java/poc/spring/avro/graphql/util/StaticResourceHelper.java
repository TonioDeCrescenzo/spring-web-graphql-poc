package poc.spring.avro.graphql.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class StaticResourceHelper {

  private StaticResourceHelper() {}

  private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

  private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public static <T> T readJsonResource(final String path, final Class<T> clazz) throws IOException {
    final String response = readResouceAsString(path);
    return objectMapper.readValue(response, clazz);
  }

  public static String readResouceAsString(final String path) {
    final Resource resource = resourceLoader.getResource("classpath:/" + path);
    return asString(resource);
  }

  public static String asString(Resource resource) {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
