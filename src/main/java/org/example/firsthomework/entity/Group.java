package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

import java.util.List;

/**
 * Group entity
 * <p>
 * Relation:
 * One-to-Many: Group -> Students
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group implements Entity {
    private long id;
    private String name;
    private int course;
    private int semester;
    private List<Student> students;
}
