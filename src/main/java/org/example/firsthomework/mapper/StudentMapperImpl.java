package org.example.firsthomework.mapper;

import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.mapper.global.GroupMapper;
import org.example.firsthomework.mapper.global.StudentMapper;

import java.util.List;
import java.util.stream.Collectors;

public class StudentMapperImpl implements StudentMapper {
    private final GroupMapper groupMapper = GroupMapperImpl.getInstance();

    private static StudentMapper instance;

    private StudentMapperImpl() {}

    public static synchronized StudentMapper getInstance() {
        if (instance == null) instance = new StudentMapperImpl();
        return instance;
    }

    @Override
    public Student map(StudentDto.Request entityDto) {
        if (entityDto == null) return null;
        return new Student(
                0,
                entityDto.getLastName(),
                entityDto.getFirstName(),
                entityDto.getPatronymic(),
                groupMapper.shortMap(entityDto.getGroup()),
                null);
    }

    @Override
    public Student shortMap(StudentDto.ShortRequest entityDto) {
        if (entityDto == null) return null;
        return new Student(
                entityDto.getId(),
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public StudentDto.Response map(Student entity) {
        if (entity == null) return null;
        return new StudentDto.Response(
                entity.getId(),
                entity.getLastName(),
                entity.getFirstName(),
                entity.getPatronymic(),
                groupMapper.shortMap(entity.getGroup()));
    }

    @Override
    public Student map(StudentDto.Update entityDto) {
        if (entityDto == null) return null;
        return new Student(
                entityDto.getId(),
                entityDto.getLastName(),
                entityDto.getFirstName(),
                entityDto.getPatronymic(),
                groupMapper.shortMap(entityDto.getGroup()),
                null);
    }

    @Override
    public Student shortMap(StudentDto.ShortUpdate entityDto) {
        if (entityDto == null) return null;
        return new Student(
                entityDto.getId(),
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public List<StudentDto.Response> map(List<Student> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
