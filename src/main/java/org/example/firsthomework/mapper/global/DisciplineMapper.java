package org.example.firsthomework.mapper.global;

import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.entity.Discipline;

import java.util.List;

public interface DisciplineMapper {
    Discipline map(DisciplineDto.Request entityDto);

    public Discipline shortMap(DisciplineDto.ShortRequest entityDto);

    DisciplineDto.Response map(Discipline entity);

    Discipline map(DisciplineDto.Update entityDto);

    Discipline shortMap(DisciplineDto.ShortUpdate entityDto);

    List<DisciplineDto.Response> map(List<Discipline> entities);
}
