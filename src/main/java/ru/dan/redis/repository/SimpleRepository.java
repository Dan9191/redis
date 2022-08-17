package ru.dan.redis.repository;

import ru.dan.redis.serialize.TechDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для реализации методов работы с БД.
 */
public interface SimpleRepository {

    /**
     * Конвертирует значение по справочнику.
     *
     * @param table      Таблица справочника для конвертации.
     * @param fromColumn Из какого столбца конвертировать.
     * @param toColumn   В какой столбец конвертировать.
     * @param value      Значение для конвертации.
     * @param date       Дата конвертации.
     * @return Сконвертированное значение.
     */
    String convertByDictionary(String table, String fromColumn, String toColumn, String value, LocalDate date);

    /**
     * Метод возвращает список из дат tech_date_from и tech_date_to, удовлетворяющее условию where.
     *
     * @param table  Название таблицы.
     * @param params Мапа с условиями для where(все условия выполняются через И).
     * @return Список дат в формате String.
     */
    List<TechDate> datesListByDictionaryRecord(String table, Map<String, Object> params);
}
