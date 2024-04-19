package org.example.firsthomework.mapper;

import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.mapper.global.GroupMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupMapperImpl implements GroupMapper {
    private static GroupMapper instance;

    private GroupMapperImpl() {}

    public static synchronized GroupMapper getInstance() {
        if (instance == null) instance = new GroupMapperImpl();
        return instance;
    }

    @Override
    public Group map(GroupDto.Request entityDto) {
        if (entityDto == null) return null;
        return new Group(
                0,
                entityDto.getName(),
                entityDto.getCourse(),
                entityDto.getSemester(),
                null);
    }

    @Override
    public Group shortMap(GroupDto.ShortRequest entityDto) {
        if (entityDto == null) return null;
        return new Group(
                entityDto.getId(),
                null,
                0,
                0,
                null);
    }

    @Override
    public GroupDto.Response map(Group entity) {
        if (entity == null) return null;
        if (entity.getStudents() == null) entity.setStudents(new ArrayList<>());
        List<StudentDto.ShortResponse> students =
                entity.getStudents().stream()
                        .map(student -> new StudentDto.ShortResponse(
                        student.getId(),
                        student.getLastName(),
                        student.getFirstName(),
                        student.getPatronymic()
                )).collect(Collectors.toList());

        return new GroupDto.Response(
                entity.getId(),
                entity.getName(),
                entity.getCourse(),
                entity.getSemester(),
                students);
    }

    @Override
    public GroupDto.ShortResponse shortMap(Group entity) {
        if (entity == null) return null;
        return new GroupDto.ShortResponse(
                entity.getId(),
                entity.getName(),
                entity.getCourse(),
                entity.getSemester());
    }

    @Override
    public Group map(GroupDto.Update entityDto) {
        if (entityDto == null) return null;
        return new Group(
                entityDto.getId(),
                entityDto.getName(),
                entityDto.getCourse(),
                entityDto.getSemester(),
                null);
    }

    @Override
    public Group shortMap(GroupDto.ShortUpdate entity) {
        if (entity == null) return null;
        return new Group(
                entity.getId(),
                null,
                0,
                0,
                null);
    }

    @Override
    public List<GroupDto.Response> map(List<Group> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
