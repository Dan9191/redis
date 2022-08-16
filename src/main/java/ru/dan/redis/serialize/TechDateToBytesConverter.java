package ru.dan.redis.serialize;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@WritingConverter
public class TechDateToBytesConverter implements Converter<TechDate, byte[]> {

    private final Jackson2JsonRedisSerializer<TechDate> serializer;

    public TechDateToBytesConverter() {

        serializer = new Jackson2JsonRedisSerializer<TechDate>(TechDate.class);
        serializer.setObjectMapper(new ObjectMapper());
    }

    @Override
    public byte[] convert(TechDate value) {
        return serializer.serialize(value);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }
}