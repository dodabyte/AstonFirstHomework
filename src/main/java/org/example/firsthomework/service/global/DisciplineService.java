package org.example.firsthomework.service.global;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;

import java.util.List;

public interface DisciplineService {
    DisciplineDto.Response insert(DisciplineDto.Request dto) throws DataAccessObjectException, InsertionException;
    int update(DisciplineDto.Update dto) throws DataAccessObjectException, EntityNotFoundException;
    boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException;
    boolean containsById(long id) throws DataAccessObjectException;
    List<DisciplineDto.Response> findAll() throws DataAccessObjectException;
    DisciplineDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException;
    int addDisciplineToTeacher(long disciplineId, long teacherId) throws DataAccessObjectException, EntityNotFoundException;
    boolean deleteDisciplineFromTeacher(long teacherId) throws DataAccessObjectException, EntityNotFoundException;
}
