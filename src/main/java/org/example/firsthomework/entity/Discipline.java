package org.example.firsthomework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firsthomework.entity.global.Entity;

import java.util.List;

/**
 * Group entity <p>
 * Relation: <p>
 * One-to-One: Discipline <-> Teacher
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discipline implements Entity {
    private long id;
    private String name;
    private Teacher teacher;
}
