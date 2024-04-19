package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

import java.util.Date;

/**
 * SemesterPerformance entity <p>
 * Relation: <p>
 * Many-to-One: SemesterPerformance -> Student <p>
 * Many-to-One: SemesterPerformance -> Discipline
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterPerformance implements Entity {
    private long id;
    private Student student;
    private Discipline discipline;
    private int mark;
}
