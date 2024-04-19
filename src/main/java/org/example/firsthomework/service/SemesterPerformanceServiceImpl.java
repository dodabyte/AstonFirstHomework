package org.example.firsthomework.service;

import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.SemesterPerformanceDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.entity.Group;
import org.example.firsthomework.entity.SemesterPerformance;
import org.example.firsthomework.entity.Student;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.mapper.SemesterPerformanceMapperImpl;
import org.example.firsthomework.mapper.global.GroupMapper;
import org.example.firsthomework.mapper.global.SemesterPerformanceMapper;
import org.example.firsthomework.service.global.SemesterPerformanceService;

import java.util.List;
import java.util.Optional;

public class SemesterPerformanceServiceImpl implements SemesterPerformanceService {
    private final SemesterPerformanceDao semesterPerformanceDao;
    private final StudentDao studentDao;
    private final DisciplineDao disciplineDao;
    private final SemesterPerformanceMapper semesterPerformanceMapper;

    private static SemesterPerformanceService instance;

    public static SemesterPerformanceService getInstance(SemesterPerformanceDao semesterPerformanceDao, StudentDao studentDao,
                                                         DisciplineDao disciplineDao, SemesterPerformanceMapper semesterPerformanceMapper) {
        if (instance == null) instance = new SemesterPerformanceServiceImpl(semesterPerformanceDao, studentDao, disciplineDao,
                semesterPerformanceMapper);
        return instance;
    }

    private SemesterPerformanceServiceImpl(SemesterPerformanceDao semesterPerformanceDao, StudentDao studentDao,
                                          DisciplineDao disciplineDao, SemesterPerformanceMapper semesterPerformanceMapper) {
        this.semesterPerformanceDao = semesterPerformanceDao;
        this.studentDao = studentDao;
        this.disciplineDao = disciplineDao;
        this.semesterPerformanceMapper = semesterPerformanceMapper;
    }

    @Override
    public SemesterPerformanceDto.Response insert(SemesterPerformanceDto.Request dto) throws DataAccessObjectException, InsertionException {
        long semesterPerformanceId = semesterPerformanceDao.insert(semesterPerformanceMapper.map(dto));
        Optional<SemesterPerformance> optional = semesterPerformanceDao.findById(semesterPerformanceId);
        return semesterPerformanceMapper.map(optional.orElseThrow(InsertionException::new));
    }

    @Override
    public int update(SemesterPerformanceDto.Update dto) throws DataAccessObjectException, EntityNotFoundException {
        if (dto == null || dto.getId() == 0 || !containsById(dto.getId())) throw new EntityNotFoundException(new IllegalArgumentException());
        return semesterPerformanceDao.update(semesterPerformanceMapper.map(dto));
    }

    @Override
    public boolean delete(long id) throws DataAccessObjectException, EntityNotFoundException {
        if (!containsById(id)) throw new EntityNotFoundException();
        Optional<SemesterPerformance> optional = semesterPerformanceDao.findById(id);
        return semesterPerformanceDao.delete(optional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public boolean containsById(long id) throws DataAccessObjectException {
        return semesterPerformanceDao.containsById(id);
    }

    @Override
    public List<SemesterPerformanceDto.Response> findAll() throws DataAccessObjectException {
        return semesterPerformanceMapper.map(semesterPerformanceDao.findAll());
    }

    @Override
    public SemesterPerformanceDto.Response findById(long id) throws DataAccessObjectException, EntityNotFoundException {
        return semesterPerformanceMapper.map(semesterPerformanceDao.findById(id).orElseThrow(EntityNotFoundException::new));
    }
}
