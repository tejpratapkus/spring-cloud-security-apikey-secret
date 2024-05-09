package com.demo.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {

    ObjectMapper mapper = new ObjectMapper();

    public <T> T mapObject(Object obj, Class<T> contentClassType) {
        return mapper.convertValue(obj, contentClassType);
    }
}
