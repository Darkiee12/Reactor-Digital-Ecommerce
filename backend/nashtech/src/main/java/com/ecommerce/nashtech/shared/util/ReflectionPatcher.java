package com.ecommerce.nashtech.shared.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils; // Apache Commons Lang

import com.ecommerce.nashtech.shared.json.JSON;
import com.ecommerce.nashtech.shared.types.Result;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public abstract class ReflectionPatcher<T, DTO> {
    Set<String> protectedFields;
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected ReflectionPatcher(Set<String> protectedFields) {
        this.protectedFields = Collections.unmodifiableSet(new HashSet<>(protectedFields));
    }

    public Result<T, Exception> patch(T instance, DTO updates) {
        return Result.wrap(() -> {
            @SuppressWarnings("unchecked")
            T clone = (T) mapper.readValue(
                mapper.writeValueAsBytes(instance),
                instance.getClass()
            );
    
            Map<String, Object> updMap = JSON.mapify(updates);
    
            for (var e : updMap.entrySet()) {
                String name = e.getKey();
                Object val = e.getValue();
    
                if (protectedFields.contains(name) || val == null) continue;
                if (val instanceof String s && s.isBlank()) continue;
    
                Field f = FieldUtils.getField(clone.getClass(), name, true);
                if (f == null) continue;
    
                try {
                    f.set(clone, val);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot write field '" + name + "'", ex);
                }
            }
    
            return clone;
        });
    }
    
}
