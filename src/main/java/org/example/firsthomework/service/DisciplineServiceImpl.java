package org.example.firsthomework.service;

import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.entity.Discipline;
import org.example.firsthomework.entity.TeacherDiscipline;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.global.DisciplineMapper;
import org.example.firsthomework.mapper.global.TeacherMapper;
import org.example.firsthomework.service.global.DisciplineService;

import java.util.List;
import java.util.Optional;

public class DisciplineServiceImpl implements DisciplineService {
    private final TeacherDisciplineDao teacherDisciplineDao;
    private final TeacherDao teacherDao;
    private final DisciplineDao disciplineDao;
    private final DisciplineMapper disciplineMapper;
    private final TeacherMapper teacherMapper;

    private static DisciplineService instance;

    public static DisciplineService getInstance(DisciplineDao disciplineDao, TeacherDao teacherDao,
                                                TeacherDisciplineDao teacherDisciplineDao, DisciplineMapper disciplineMapper,
                                                TeacherMapper teacherMapper) {
        if (instance == null) instance = new DisciplineServiceImpl(disciplineDao, teacherDao, teacherDisciplineDao,
                disciplineMapper, teacherMapper);
        return instance;
    }

    private DisciplineServiceImpl(DisciplineDao disciplineDao, TeacherDao teacherDao,
                                 TeacherDisciplineDao teacherDisciplineDao, DisciplineMapper disciplineMapper,
                                 TeacherMapper teacherMapper) {
        this.disciplineDao = disciplineDao;
        this.teacherDao = teacherDao;
        this.teacherDisciplineDao = teacherDisciplineDao;
        this.disciplineMapper = disciplineMapper;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public DisciplineDto.Response insert(DisciplineDto.Request dto) throws DataAccessObjectException, InsertionException {
        Discipline discipline = disciplineMapper.map(dto);

        long teacherId = 0;
        if (dto.getTeacher() != null) {
            teacherId = teacherDao.insert(teacherMapper.map(dto.getTeacher()));
            discipline.setTeacher(teacherDao.findById(teacherId).orElseThrow(InsertionException::new));
        }

        long disciplineId = disciplineDao.insert(discipline);
        try {
            if (teacherId > 0) addDisciplineToTeacher(disciplineId, teacherId);
        }
        catch (EntityNotFoundException e) {
            throw new InsertionException(e);
        }

        Optional<Discipline> optional = disciplineDao.findById(disciplineId);
        return disciplineMapper.map(optional.orElseThrow(InsertionException::new));
    }

    @Override
    public int update(DisciplineDto.Update dto) throws DataAccessObjectException, EntityNotFoundException {
        if (dto == null || dto.getId() == 0 || !containsById(dto.getId())) throw new EntityNotFoundException(new IllegalArgumentException());
        return disciplineDao.update(disciplineMapper.map(dto));
    }

    @Override
    public boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Discipline> optional = disciplineDao.findById(id);
        return disciplineDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public boolean containsById(long id) throws DataAccessObjectException {
        return disciplineDao.containsById(id);
    }

    @Override
    public List<DisciplineDto.Response> findAll() throws DataAccessObjectException {
        List<Discipline> disciplines = disciplineDao.findAll();
        return disciplineMapper.map(disciplines);
    }

    @Override
    public DisciplineDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<Discipline> optional = disciplineDao.findById(id);
        return disciplineMapper.map(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public int addDisciplineToTeacher(long disciplineId, long teacherId) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(disciplineId) || !teacherDao.containsById(teacherId)) throw new EntityNotFoundException();
        TeacherDiscipline teacherDiscipline = new TeacherDiscipline(
                0,
                teacherId,
                disciplineId);
        teacherDisciplineDao.insert(teacherDiscipline);
        Optional<Discipline> optional = disciplineDao.findById(disciplineId);
        Discipline discipline = optional.orElseThrow(EntityNotFoundException::new);
        discipline.setTeacher(teacherDisciplineDao.findTeacherByDisciplineId(disciplineId));
        return disciplineDao.update(discipline);
    }

    @Override
    public boolean deleteDisciplineFromTeacher(long teacherId) throws DataAccessObjectException, EntityNotFoundException {
        if (!teacherDisciplineDao.containsByTeacherId(teacherId)) throw new EntityNotFoundException();
        Discipline discipline = teacherDisciplineDao.findDisciplineByTeacherId(teacherId);
        if (discipline == null || !containsById(discipline.getId())) return false;
        discipline.setTeacher(null);
        disciplineDao.update(discipline);
        Optional<TeacherDiscipline> optional = teacherDisciplineDao.findByTeacherId(teacherId);
        return teacherDisciplineDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }
}
