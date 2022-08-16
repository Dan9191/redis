package ru.dan.redis.serialize;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@ReadingConverter
public class BytesToTechDateConverter implements Converter<byte[], TechDate> {

    private final Jackson2JsonRedisSerializer<TechDate> serializer;

    public BytesToTechDateConverter() {

        serializer = new Jackson2JsonRedisSerializer<TechDate>(TechDate.class);
        serializer.setObjectMapper(new ObjectMapper());
    }

    @Override
    public TechDate convert(byte[] value) {
        return serializer.deserialize(value);
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