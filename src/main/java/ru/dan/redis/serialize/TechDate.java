package ru.dan.redis.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TechDate {
    private String dateFrom;
    private String dateTo;
}