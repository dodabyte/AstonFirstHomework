package org.example.firsthomework.service;

import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.entity.Teacher;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.global.TeacherMapper;
import org.example.firsthomework.service.global.TeacherService;

import java.util.List;
import java.util.Optional;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherDisciplineDao teacherDisciplineDao;
    private final TeacherDao teacherDao;
    private final TeacherMapper teacherMapper;

    private static TeacherService instance;

    public static TeacherService getInstance(TeacherDao teacherDao, TeacherDisciplineDao teacherDisciplineDao,
                                             TeacherMapper teacherMapper) {
        if (instance == null) instance = new TeacherServiceImpl(teacherDao, teacherDisciplineDao, teacherMapper);
        return instance;
    }

    private TeacherServiceImpl(TeacherDao teacherDao, TeacherDisciplineDao teacherDisciplineDao,
                              TeacherMapper teacherMapper) {
        this.teacherDao = teacherDao;
        this.teacherDisciplineDao = teacherDisciplineDao;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public TeacherDto.Response insert(TeacherDto.Request dto) throws DataAccessObjectException, InsertionException {
        long disciplineId = teacherDao.insert(teacherMapper.map(dto));
        Optional<Teacher> optional = teacherDao.findById(disciplineId);
        return teacherMapper.map(optional.orElseThrow(InsertionException::new));
    }

    @Override
    public int update(TeacherDto.Update dto) throws DataAccessObjectException, EntityNotFoundException {
        if (dto == null || dto.getId() == 0 || !containsById(dto.getId())) throw new EntityNotFoundException();
        return teacherDao.update(teacherMapper.map(dto));
    }

    @Override
    public boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Teacher> optional = teacherDao.findById(id);
        return teacherDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public boolean containsById(long id) throws DataAccessObjectException {
        return teacherDao.containsById(id);
    }

    @Override
    public List<TeacherDto.Response> findAll() throws DataAccessObjectException {
        List<Teacher> teachers = teacherDao.findAll();
        for (Teacher teacher : teachers) {
            if (teacher.getDiscipline() == null) {
                teacher.setDiscipline(teacherDisciplineDao.findDisciplineByTeacherId(teacher.getId()));
            }
        }
        return teacherMapper.map(teachers);
    }

    @Override
    public TeacherDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Teacher> optional = teacherDao.findById(id);
        Teacher teacher = optional.orElseThrow(EntityNotFoundException::new);
        if (teacher.getDiscipline() == null) teacher.setDiscipline(teacherDisciplineDao.findDisciplineByTeacherId(teacher.getId()));
        return teacherMapper.map(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public long addDisciplineToTeacher(long disciplineId, long teacherId) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(disciplineId) || !teacherDao.containsById(teacherId)) throw new EntityNotFoundException();
        TeacherDiscipline teacherDiscipline = new TeacherDiscipline(
                                                0,
                                                teacherId,
                                                disciplineId);
        return teacherDisciplineDao.insert(teacherDiscipline);
    }

    @Override
    public boolean deleteDisciplineFromTeacher(long teacherId) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(teacherId)) throw new EntityNotFoundException();
        Optional<TeacherDiscipline> optional = teacherDisciplineDao.findByTeacherId(teacherId);
        return teacherDisciplineDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }
}
