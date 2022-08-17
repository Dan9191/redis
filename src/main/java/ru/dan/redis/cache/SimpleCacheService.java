package ru.dan.redis.cache;

import ru.dan.redis.serialize.TechDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Интерефейс для реализации методов работы с Redis.
 */
public interface SimpleCacheService {

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
    String getCachingConvertByDictionary(String table, String fromColumn, String toColumn, String value, LocalDate date);

    /**
     * Получение списка дат всех подходящих строк таблицы.
     *
     * @param table  Название таблицы.
     * @param params Мапа параметров.
     * @return Список пар.
     */
    List<TechDate> getCachingDateList(String table, Map<String, Object> params);
}
