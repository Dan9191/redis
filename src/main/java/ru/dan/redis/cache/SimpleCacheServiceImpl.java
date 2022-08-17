package ru.dan.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import ru.dan.redis.repository.SimpleRepository;
import ru.dan.redis.serialize.BytesToTechDateConverter;
import ru.dan.redis.serialize.TechDate;
import ru.dan.redis.serialize.TechDateToBytesConverter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Реализации методов работы с Redis.
 */
@Slf4j
@Service
public class SimpleCacheServiceImpl implements SimpleCacheService {

    /**
     * Сервис работы с Redis.
     */
    private final RedisTemplate<String, byte[]> redisTemplate;

    /**
     * Сервис работы с БД.
     */
    private final SimpleRepository simpleRepository;

    /**
     * Оператор для операций над типами переменных String.
     */
    private final ValueOperations<String, byte[]> valueOperations;

    /**
     * Оператор для операций над типами переменных List.
     */
    private final ListOperations<String, byte[]> listOperations;

    /**
     * Время до удаления записи по ключу.
     */
    private final int deleteRecordTime;

    /**
     * Конвертор для сериализации списка дат в Redis.
     */
    private final TechDateToBytesConverter serialize;

    /**
     * Конвертор для десериализации списка дат в Redis.
     */
    private final BytesToTechDateConverter deserialize;

    public SimpleCacheServiceImpl(RedisTemplate<String, byte[]> redisTemplate,
                                  SimpleRepository simpleRepository,
                                  @Value("${spring.redis.record-delete-time}") int deleteRecordTime) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
        this.valueOperations = redisTemplate.opsForValue();
        this.simpleRepository = simpleRepository;
        this.deleteRecordTime = deleteRecordTime;
        this.serialize = new TechDateToBytesConverter();
        this.deserialize = new BytesToTechDateConverter();
    }

    /**
     * Конвертирует значение value по справочнику из fromColumn в toColumn.
     *
     * @param table      Таблица справочника.
     * @param fromColumn Столбец с исходным значением.
     * @param toColumn   Столбец с искомым значением.
     * @param value      Значение для конвертации.
     * @param date       Дата конвертации.
     * @return Сконвертированное значение.
     */
    @Override
    public String getCachingConvertByDictionary(String table, String fromColumn, String toColumn, String value, LocalDate date) {
        String result;
        String searchKey = generateKey("dict", table, fromColumn, toColumn, value);
        try {
            if (BooleanUtils.isTrue(redisTemplate.hasKey(searchKey))) {
                result = new String(Objects.requireNonNull(valueOperations.get(searchKey)));
                log.error("key {} exist, value {}", searchKey, result); //delete
            } else {
                result = simpleRepository.convertByDictionary(table, fromColumn, toColumn, value, date);
                valueOperations.set(searchKey, result.getBytes(StandardCharsets.UTF_8));
                redisTemplate.expire(searchKey, deleteRecordTime, TimeUnit.SECONDS);
                log.error("key {} not exist, value {}", searchKey, result); //delete
            }
            return result;
        } catch (Exception e) {
            log.error("Redis not found"); //delete
            log.warn("Redis cache is not available");
            result = simpleRepository.convertByDictionary(table, fromColumn, toColumn, value, date);
            log.trace("Query result: {}", result);
            return result;
        }
    }

    /**
     * Получение списка дат всех подходящих строк таблицы.
     *
     * @param table  Название таблицы.
     * @param params Мапа параметров.
     * @return Список пар.
     */
    @Override
    public List<TechDate> getCachingDateList(String table, Map<String, Object> params) {
        if (params.isEmpty()) {
            throw new RuntimeException(String.format("Переданы пустые параметры фильтрации для справочника '%s'", table));
        }
        List<TechDate> dateList;
        String searchKey = generateKey(table, params);
        try {
            if (BooleanUtils.isTrue(redisTemplate.hasKey(searchKey))) {
                dateList = Objects.requireNonNull(listOperations.range(new String(searchKey.getBytes(StandardCharsets.UTF_8)), 0, -1))
                        .stream()
                        .map(deserialize::convert)
                        .collect(Collectors.toList());
                log.error("key {} exist, value {}", searchKey, dateList); //delete
            } else {
                dateList = simpleRepository.datesListByDictionaryRecord(table, params);
                listOperations.rightPushAll(
                        searchKey,
                        dateList.stream()
                                .map(serialize::convert)
                                .collect(Collectors.toList())
                );
                redisTemplate.expire(searchKey, deleteRecordTime, TimeUnit.SECONDS);
                log.error("key {} not exist, value {}", searchKey, dateList); //delete
            }
            return dateList;
        } catch (Exception e) {
            log.error("Redis not found"); //delete
            log.warn("Redis cache is not available");
            dateList = simpleRepository.datesListByDictionaryRecord(table, params);
            return dateList;
        }
    }

    /**
     * Простая генерация ключа с разделителем "#", используемая для поиска в Redis.
     *
     * @param params Данные запроса к БД.
     * @return Сгенерированный ключ.
     */
    private String generateKey(String... params) {
        return String.join("#", params);
    }

    /**
     * Простая генерация ключа с разделителем "#", используемая для поиска в Redis.
     *
     * @param table  Название таблцы.
     * @param params Мапа полей и их значений.
     * @return Сгенерированный ключ.
     */
    private String generateKey(String table, Map<String, Object> params) {
        List<String> keys = params.keySet().stream().sorted().collect(Collectors.toList());
        return String.format("%s#", table)
                .concat(keys.stream()
                        .map(key -> String.format("%s#%s", key, params.get(key)))
                        .collect(Collectors.joining("#")));
    }
}
