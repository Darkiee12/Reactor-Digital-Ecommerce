package com.ecommerce.nashtech.shared.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.ecommerce.nashtech.shared.json.JSON;
import com.ecommerce.nashtech.shared.types.Result;

import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public abstract class ReflectionPatcher<T,DTO> {
    Set<String> protectedFields;

    protected ReflectionPatcher(Set<String> protectedFields) {
        this.protectedFields = protectedFields;
    }

    public Result<T, Exception> patch(T instance, DTO updates) {
        return Result.wrap(() -> {
            T updatedInstance = JSON.deepClone(instance);
            Map<String, Object> updatesMap = JSON.mapify(updates);
            updatesMap.entrySet().stream().filter(entry -> !protectedFields.contains(entry.getKey())).forEach(entry -> {
                try {
                    Field field = instance.getClass().getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(updatedInstance, entry.getValue());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return updatedInstance;
        });
    }

}
