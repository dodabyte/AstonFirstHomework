package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

import java.util.Date;
import java.util.List;

/**
 * Student entity <p>
 * Relation: <p>
 * Many-to-One: Student -> Groups <p>
 * One-to-Many: Student -> SemesterPerformance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Entity {
    private long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private Group group;
    private List<SemesterPerformance> semesterPerformance;
}
