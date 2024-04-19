package org.example.firsthomework.mapper.global;

import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Student;

import java.util.List;

public interface StudentMapper {
    Student map(StudentDto.Request entityDto);

    Student shortMap(StudentDto.ShortRequest entityDto);

    StudentDto.Response map(Student entity);

    Student map(StudentDto.Update entityDto);

    Student shortMap(StudentDto.ShortUpdate entityDto);

    List<StudentDto.Response> map(List<Student> entities);
}
