package org.example.firsthomework.mapper.global;

import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.entity.Teacher;

import java.util.List;

public interface TeacherMapper {
    Teacher map(TeacherDto.Request entityDto);

    TeacherDto.Response map(Teacher entity);

    TeacherDto.ShortResponse shortMap(Teacher entity);

    Teacher map(TeacherDto.Update entityDto);

    List<TeacherDto.Response> map(List<Teacher> entities);
}
