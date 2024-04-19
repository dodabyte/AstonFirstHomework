package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDiscipline implements Entity {
    private long id;
    private long teacherId;
    private long disciplineId;
}
