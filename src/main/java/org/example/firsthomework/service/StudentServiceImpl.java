package org.example.firsthomework.service;

import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.global.GroupMapper;
import org.example.firsthomework.mapper.global.StudentMapper;
import org.example.firsthomework.service.global.StudentService;

import java.util.List;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {
    private final StudentDao studentDao;
    private final GroupDao groupDao;
    private final StudentMapper studentMapper;

    private static StudentService instance;

    public static StudentService getInstance(StudentDao studentDao, GroupDao groupDao, StudentMapper studentMapper) {
        if (instance == null) instance = new StudentServiceImpl(studentDao, groupDao, studentMapper);
        return instance;
    }

    private StudentServiceImpl(StudentDao studentDao, GroupDao groupDao, StudentMapper studentMapper) {
        this.studentDao = studentDao;
        this.groupDao = groupDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public StudentDto.Response insert(StudentDto.Request dto) throws DataAccessObjectException, InsertionException {
        long studentId = studentDao.insert(studentMapper.map(dto));
        Optional<Student> optional = studentDao.findById(studentId);
        return studentMapper.map(optional.orElseThrow(InsertionException::new));
    }

    @Override
    public int update(StudentDto.Update dto) throws DataAccessObjectException, EntityNotFoundException {
        if (dto == null || dto.getId() == 0 || !containsById(dto.getId())) throw new EntityNotFoundException(new IllegalArgumentException());
        return studentDao.update(studentMapper.map(dto));
    }

    @Override
    public boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Student> optional = studentDao.findById(id);
        return studentDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public boolean containsById(long id) throws DataAccessObjectException {
        return studentDao.containsById(id);
    }

    @Override
    public List<StudentDto.Response> findAll() throws DataAccessObjectException {
        return studentMapper.map(studentDao.findAll());
    }

    @Override
    public StudentDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException {
        return studentMapper.map(studentDao.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public int addGroupToStudent(long studentId, long groupId) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(studentId) || !groupDao.containsById(groupId)) throw new EntityNotFoundException();
        Student student = studentDao.findById(studentId).orElseThrow(EntityNotFoundException::new);
        student.setGroup(groupDao.findById(groupId).orElseThrow(EntityNotFoundException::new));
        return studentDao.update(student);
    }

    @Override
    public int deleteGroupFromStudent(long studentId) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(studentId)) throw new EntityNotFoundException();
        Student student = studentDao.findById(studentId).orElseThrow(EntityNotFoundException::new);
        student.setGroup(null);
        return studentDao.update(student);
    }
}
