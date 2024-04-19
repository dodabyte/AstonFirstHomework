package org.example.firsthomework.service.global;

import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;

import java.util.List;

public interface GroupService {
    GroupDto.Response insert(GroupDto.Request dto) throws DataAccessObjectException, InsertionException;
    int update(GroupDto.Update dto) throws DataAccessObjectException, EntityNotFoundException;
    boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException;
    boolean containsById(long id) throws DataAccessObjectException;
    List<GroupDto.Response> findAll() throws DataAccessObjectException;
    GroupDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException;
    List<Integer> addStudentToGroup(List<Long> studentIds, long groupId) throws DataAccessObjectException, EntityNotFoundException;
    List<Integer> deleteStudentFromGroup(List<Long> studentIds) throws DataAccessObjectException, EntityNotFoundException;
}
