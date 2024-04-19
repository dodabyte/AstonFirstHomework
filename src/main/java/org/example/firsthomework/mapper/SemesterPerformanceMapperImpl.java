package org.example.firsthomework.mapper;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.SemesterPerformance;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.mapper.global.DisciplineMapper;
import org.example.firsthomework.mapper.global.GroupMapper;
import org.example.firsthomework.mapper.global.SemesterPerformanceMapper;
import org.example.firsthomework.mapper.global.StudentMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SemesterPerformanceMapperImpl implements SemesterPerformanceMapper {
    private final StudentMapper studentMapper = StudentMapperImpl.getInstance();
    private final DisciplineMapper disciplineMapper = DisciplineMapperImpl.getInstance();

    private static SemesterPerformanceMapper instance;

    private SemesterPerformanceMapperImpl() {}

    public static synchronized SemesterPerformanceMapper getInstance() {
        if (instance == null) instance = new SemesterPerformanceMapperImpl();
        return instance;
    }

    @Override
    public SemesterPerformance map(SemesterPerformanceDto.Request entityDto) {
        if (entityDto == null) return null;
        return new SemesterPerformance(
                0,
                studentMapper.shortMap(entityDto.getStudent()),
                disciplineMapper.shortMap(entityDto.getDiscipline()),
                entityDto.getMark());
    }

    @Override
    public SemesterPerformanceDto.Response map(SemesterPerformance entity) {
        if (entity == null) return null;
        return new SemesterPerformanceDto.Response(
                entity.getId(),
                studentMapper.map(entity.getStudent()),
                disciplineMapper.map(entity.getDiscipline()),
                entity.getMark());
    }

    @Override
    public SemesterPerformance map(SemesterPerformanceDto.Update entityDto) {
        if (entityDto == null) return null;
        return new SemesterPerformance(
                entityDto.getId(),
                studentMapper.shortMap(entityDto.getStudent()),
                disciplineMapper.shortMap(entityDto.getDiscipline()),
                entityDto.getMark());
    }

    @Override
    public List<SemesterPerformanceDto.Response> map(List<SemesterPerformance> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
