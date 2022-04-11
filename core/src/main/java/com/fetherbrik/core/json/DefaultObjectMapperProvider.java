package com.fetherbrik.core.json;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import javax.inject.Provider;

/**
 * Provides a JsonMapper that can very nearly parse JSON5 full spec.
 * See https://stackoverflow.com/a/68312228/1867101
 * <p>
 * There is a static 'mapper' method for use in non-injected contexts, such as 'parseJson/toJson' methods in POJOs.
 */
public class DefaultObjectMapperProvider implements Provider<ObjectMapper> {

  private static final ObjectMapper objectMapper = initMapper();

  public static ObjectMapper mapper() {
    return objectMapper;
  }

  private static ObjectMapper initMapper() {
    return JsonMapper.builder().enable(
        JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES,
        JsonReadFeature.ALLOW_TRAILING_COMMA,
        JsonReadFeature.ALLOW_SINGLE_QUOTES,
        JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
        JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS,
        JsonReadFeature.ALLOW_JAVA_COMMENTS,
        JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS
    ).build();
  }

  @Override public ObjectMapper get() {
    return objectMapper;
  }
}
