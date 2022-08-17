package ru.dan.redis.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.dan.redis.serialize.TechDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация методов работы с БД.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleRepositoryImpl implements SimpleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final static String SIMPLE_SELECT_QUERY = "select %s from %s where %s = ? limit 1";

    private final static String DATES_SELECT_QUERY = "select to_char(tech_date_from, 'yyyy-MM-dd') as tech_date_from, "
            + "to_char(tech_date_to, 'yyyy-MM-dd') as tech_date_to from %s where %s";

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
    @Override
    public String convertByDictionary(String table,
                                      String fromColumn,
                                      String toColumn,
                                      String value,
                                      LocalDate date) {
        String query = String.format(SIMPLE_SELECT_QUERY, toColumn, table, fromColumn);
        try {
            String result = jdbcTemplate.queryForObject(
                    query,
                    new Object[]{value},
                    (rs, rowNum) -> rs.getString(toColumn)
            );
            log.trace("Query result: {}", result);
            return result;
        } catch (Exception e) {
            String message = String.format("Error executing query '%s'", query);
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Метод возвращает список из дат tech_date_from и tech_date_to, удовлетворяющее условию where.
     *
     * @param table  Название таблицы.
     * @param params Мапа с условиями для where(все условия выполняются через И).
     * @return Список дат в формате String.
     */
    @Override
    public List<TechDate> datesListByDictionaryRecord(String table, Map<String, Object> params) {
        if (params.isEmpty()) {
            throw new RuntimeException(
                    String.format("Переданы пустые параметры фильтрации для справочника '%s'", table)
            );
        }
        List<Object> values = new ArrayList<>();
        String whereConditions =
                params.keySet().stream()
                        .map(key -> {
                            values.add(params.get(key));
                            return key + " = ?";
                        })
                        .collect(Collectors.joining(" and "));
        String query = String.format(DATES_SELECT_QUERY, table, whereConditions);
        return new ArrayList<>(jdbcTemplate.query(
                query,
                values.toArray(),
                (rs, rowNum) -> new TechDate(rs.getString("tech_date_from"), rs.getString("tech_date_to"))));
    }
}
