package org.example.firsthomework.service.global;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;

import java.util.List;

public interface TeacherService {
    TeacherDto.Response insert(TeacherDto.Request dto) throws DataAccessObjectException, InsertionException;
    int update(TeacherDto.Update dto) throws DataAccessObjectException, EntityNotFoundException;
    boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException;
    boolean containsById(long id) throws DataAccessObjectException;
    List<TeacherDto.Response> findAll() throws DataAccessObjectException;
    TeacherDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException;
    long addDisciplineToTeacher(long disciplineId, long teacherId) throws DataAccessObjectException, EntityNotFoundException;
    boolean deleteDisciplineFromTeacher(long teacherId) throws DataAccessObjectException, EntityNotFoundException;
}
