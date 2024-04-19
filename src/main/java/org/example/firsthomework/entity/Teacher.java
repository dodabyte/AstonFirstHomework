package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

/**
 * Group entity <p>
 * Relation: <p>
 * One-to-One: Teacher <-> Discipline
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher implements Entity {
    private long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private Discipline discipline;
}
