package ru.dan.redis.serialize;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JacksonSerializeService {

    public void serialize() {
        TechDateToBytesConverter serialize = new TechDateToBytesConverter();
        BytesToTechDateConverter deserialize = new BytesToTechDateConverter();
        TechDate date1 = new TechDate("param1", "value1");
        TechDate date2 = new TechDate("param2", "value2");
        TechDate date3 = new TechDate("param3", "value3");
        TechDate date4 = new TechDate("param4", "value4");
        List<TechDate> dates = Arrays.asList(date1, date2, date3, date4);
        List<byte[]> listBytes = dates.stream()
                .map(serialize::convert)
                .collect(Collectors.toList());
        List<TechDate> deserializeDates = listBytes.stream()
                .map(deserialize::convert)
                .collect(Collectors.toList());

        System.out.println(dates);
        System.out.println("=========");
        System.out.println(listBytes);
        System.out.println("=========");
        System.out.println(deserializeDates);
    }
}
