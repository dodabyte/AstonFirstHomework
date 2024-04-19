package org.example.firsthomework.mapper;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.mapper.global.TeacherMapper;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherMapperImpl implements TeacherMapper {
    private static TeacherMapper instance;

    private TeacherMapperImpl() {}

    public static synchronized TeacherMapper getInstance() {
        if (instance == null) instance = new TeacherMapperImpl();
        return instance;
    }

    @Override
    public Teacher map(TeacherDto.Request entityDto) {
        if (entityDto == null) return null;
        return new Teacher(
                0,
                entityDto.getLastName(),
                entityDto.getFirstName(),
                entityDto.getPatronymic(),
                null);
    }

    @Override
    public TeacherDto.Response map(Teacher entity) {
        if (entity == null) return null;
        DisciplineDto.ShortResponse discipline = null;
        if (entity.getDiscipline() != null) {
            discipline = new DisciplineDto.ShortResponse(
                    entity.getDiscipline().getId(),
                    entity.getDiscipline().getName());
        }

        return new TeacherDto.Response(
                entity.getId(),
                entity.getLastName(),
                entity.getFirstName(),
                entity.getPatronymic(),
                discipline);
    }

    @Override
    public TeacherDto.ShortResponse shortMap(Teacher entity) {
        if (entity == null) return null;
        return new TeacherDto.ShortResponse(
                entity.getId(),
                entity.getLastName(),
                entity.getFirstName(),
                entity.getPatronymic());
    }

    @Override
    public Teacher map(TeacherDto.Update entityDto) {
        if (entityDto == null) return null;
        return new Teacher(
                entityDto.getId(),
                entityDto.getLastName(),
                entityDto.getFirstName(),
                entityDto.getPatronymic(),
                null);
    }

    @Override
    public List<TeacherDto.Response> map(List<Teacher> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
