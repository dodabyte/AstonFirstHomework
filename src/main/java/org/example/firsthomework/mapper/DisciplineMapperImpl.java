package org.example.firsthomework.mapper;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.mapper.global.DisciplineMapper;
import org.example.firsthomework.mapper.global.TeacherMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DisciplineMapperImpl implements DisciplineMapper {
    private final TeacherMapper teacherMapper = TeacherMapperImpl.getInstance();
    private static DisciplineMapper instance;

    private DisciplineMapperImpl() {}

    public static synchronized DisciplineMapper getInstance() {
        if (instance == null) instance = new DisciplineMapperImpl();
        return instance;
    }

    @Override
    public Discipline map(DisciplineDto.Request entityDto) {
        if (entityDto == null) return null;
        return new Discipline(
                0,
                entityDto.getName(),
                teacherMapper.map(entityDto.getTeacher()));
    }

    @Override
    public Discipline shortMap(DisciplineDto.ShortRequest entityDto) {
        if (entityDto == null) return null;
        return new Discipline(
                entityDto.getId(),
                null,
                null);
    }

    @Override
    public DisciplineDto.Response map(Discipline entity) {
        if (entity == null) return null;
        return new DisciplineDto.Response(
                entity.getId(),
                entity.getName(),
                teacherMapper.shortMap(entity.getTeacher()));
    }

    @Override
    public Discipline map(DisciplineDto.Update entityDto) {
        if (entityDto == null) return null;
        return new Discipline(
                entityDto.getId(),
                entityDto.getName(),
                teacherMapper.map(entityDto.getTeacher()));
    }

    @Override
    public Discipline shortMap(DisciplineDto.ShortUpdate entityDto) {
        if (entityDto == null) return null;
        return new Discipline(
                entityDto.getId(),
                null,
                null);
    }

    @Override
    public List<DisciplineDto.Response> map(List<Discipline> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
