package org.example.firsthomework.service;

import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.global.GroupMapper;
import org.example.firsthomework.mapper.global.TeacherMapper;
import org.example.firsthomework.service.global.GroupService;
import org.example.firsthomework.service.global.TeacherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupServiceImpl implements GroupService {
    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final GroupMapper groupMapper;

    private static GroupService instance;

    public static GroupService getInstance(GroupDao groupDao, StudentDao studentDao, GroupMapper groupMapper) {
        if (instance == null) instance = new GroupServiceImpl(groupDao, studentDao, groupMapper);
        return instance;
    }

    private GroupServiceImpl(GroupDao groupDao, StudentDao studentDao, GroupMapper groupMapper) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.groupMapper = groupMapper;
    }

    @Override
    public GroupDto.Response insert(GroupDto.Request dto) throws DataAccessObjectException, InsertionException {
        long groupId = groupDao.insert(groupMapper.map(dto));
        Optional<Group> optional = groupDao.findById(groupId);
        return groupMapper.map(optional.orElseThrow(InsertionException::new));
    }

    @Override
    public int update(GroupDto.Update dto) throws DataAccessObjectException, EntityNotFoundException {
        if (dto == null || dto.getId() == 0 || !containsById(dto.getId())) throw new EntityNotFoundException(new IllegalArgumentException());
        return groupDao.update(groupMapper.map(dto));
    }

    @Override
    public boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Group> optional = groupDao.findById(id);
        return groupDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public boolean containsById(long id) throws DataAccessObjectException {
        return groupDao.containsById(id);
    }

    @Override
    public List<GroupDto.Response> findAll() throws DataAccessObjectException {
        List<Group> groups = groupDao.findAll();
        for (Group group : groups) {
            if (group.getStudents().isEmpty()) {
                group.setStudents(studentDao.findAllByGroupId(group.getId()));
            }
        }
        return groupMapper.map(groups);
    }

    @Override
    public GroupDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Group> optional = groupDao.findById(id);
        Group group = optional.orElseThrow(EntityNotFoundException::new);
        if (group.getStudents() == null || group.getStudents().isEmpty()) group.setStudents(studentDao.findAllByGroupId(group.getId()));
        return groupMapper.map(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<Integer> addStudentToGroup(List<Long> studentIds, long groupId) throws DataAccessObjectException, EntityNotFoundException {
        List<Integer> countsRowsUpdated = new ArrayList<>();
        for (long studentId : studentIds) {
            if (!studentDao.containsById(studentId) || !containsById(groupId)) throw new EntityNotFoundException();
            Student student = studentDao.findById(studentId).orElseThrow(EntityNotFoundException::new);
            student.setGroup(groupDao.findById(groupId).orElseThrow(EntityNotFoundException::new));
            countsRowsUpdated.add(studentDao.update(student));
        }
        return countsRowsUpdated;
    }

    @Override
    public List<Integer> deleteStudentFromGroup(List<Long> studentIds) throws DataAccessObjectException, EntityNotFoundException {
        List<Integer> countsRowsUpdated = new ArrayList<>();
        for (long studentId : studentIds) {
            if (!studentDao.containsById(studentId)) throw new EntityNotFoundException();
            Student student = studentDao.findById(studentId).orElseThrow(EntityNotFoundException::new);
            student.setGroup(null);
            countsRowsUpdated.add(studentDao.update(student));
        }
        return countsRowsUpdated;
    }
}
