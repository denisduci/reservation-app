package com.ikub.reservationapp.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.touk.throwing.ThrowingSupplier;

@UtilityClass
public final class JsonUtils {

    private static final ObjectMapper objectMapper; // Thread-safe

    static {
        objectMapper = new ObjectMapper(new JsonFactory());
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> String toJsonString(T object) {
        return doInTryCatch(ThrowingSupplier.unchecked(() -> objectMapper.writeValueAsString(object)));
    }

    public static <T> T toObject(String jsonString, @NonNull Class<T> objectType) {
        if (jsonString == null) {
            return null; // valid json for a null input
        }
        if (StringUtils.isBlank(jsonString)) {
            throw new IllegalArgumentException("Json is empty or blank: cannot create an object");
        }

        return doInTryCatch(
                ThrowingSupplier.unchecked(() -> objectMapper.readValue(jsonString, objectType)));
    }

    public static <T> List<T> toList(String jsonArray, @NonNull Class<T> objectType) {
        JsonNode jsonNode = readNode(jsonArray);
        if (jsonNode == null || !jsonNode.isArray()) {
            throw new IllegalArgumentException("Given json is not an array");
        } else {
            ArrayList<T> list = new ArrayList();
            jsonNode.forEach(
                    (node) -> {
                        list.add(toObject(node.toString(), objectType));
                    });
            return list;
        }
    }

    private static JsonNode readNode(String jsonArray) {
        return doInTryCatch(ThrowingSupplier.unchecked(() -> objectMapper.readTree(jsonArray)));
    }

    private static <T> T doInTryCatch(Supplier<T> supplier) {
        T result = null;
        try {
            result = supplier.get();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
        return result;
    }
}