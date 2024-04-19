package org.example.firsthomework.service.global;

import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;

import java.util.List;

public interface SemesterPerformanceService {
    SemesterPerformanceDto.Response insert(SemesterPerformanceDto.Request dto) throws DataAccessObjectException, InsertionException;
    int update(SemesterPerformanceDto.Update dto) throws DataAccessObjectException, EntityNotFoundException;
    boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException;
    boolean containsById(long id) throws DataAccessObjectException;
    List<SemesterPerformanceDto.Response> findAll() throws DataAccessObjectException;
    SemesterPerformanceDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException;
//    void addStudentToSemesterPerformance(long studentId, long groupId) throws DataAccessObjectException, EntityNotFoundException;
//    void deleteStudentFromSemesterPerformance(long studentId) throws DataAccessObjectException, EntityNotFoundException;
}
