package com.lt.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Lhz
 */
@Component
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final Gson gson = new Gson();
    public static String toString(Object obj) {
        if (ObjectUtils.isEmpty(obj)) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        String res = gson.toJson(obj);
        System.out.println(res);

        return gson.toJson(obj);
    }

    public static <T> T toBean(String json, Class<T> tClass) {

            return gson.fromJson(json, tClass);

    }

    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (JsonProcessingException e) {
            logger.error("json解析出错");
            return null;
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (JsonProcessingException e) {
            logger.error("json解析出错");
            return null;
        }
    }

    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            logger.error("json解析出错");
            return null;
        }
    }
}
