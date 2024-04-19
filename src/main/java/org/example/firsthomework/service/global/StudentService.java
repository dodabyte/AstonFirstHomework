package org.example.firsthomework.service.global;

import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;

import java.util.List;

public interface StudentService {
    StudentDto.Response insert(StudentDto.Request dto) throws DataAccessObjectException, InsertionException;
    int update(StudentDto.Update dto) throws DataAccessObjectException, EntityNotFoundException;
    boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException;
    boolean containsById(long id) throws DataAccessObjectException;
    List<StudentDto.Response> findAll() throws DataAccessObjectException;
    StudentDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException;
    int addGroupToStudent(long studentId, long groupId) throws DataAccessObjectException, EntityNotFoundException;
    int deleteGroupFromStudent(long studentId) throws DataAccessObjectException, EntityNotFoundException;
}
