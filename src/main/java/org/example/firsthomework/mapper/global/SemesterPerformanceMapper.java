package org.example.firsthomework.mapper.global;

import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.entity.SemesterPerformance;

import java.util.List;

public interface SemesterPerformanceMapper {
    SemesterPerformance map(SemesterPerformanceDto.Request entityDto);

    SemesterPerformanceDto.Response map(SemesterPerformance entity);

    SemesterPerformance map(SemesterPerformanceDto.Update entityDto);

    List<SemesterPerformanceDto.Response> map(List<SemesterPerformance> entities);
}
