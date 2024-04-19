package org.example.firsthomework.dto;

import lombok.*;
import org.example.firsthomework.dto.global.DataTransferObject;

import java.util.List;

public enum GroupDto implements DataTransferObject {;
    private interface Id { long getId(); }
    private interface Name { String getName(); }
    private interface Course { int getCourse(); }
    private interface Semester { int getSemester(); }
    private interface StudentRequest { List<StudentDto.Request> getStudents(); }
    private interface StudentUpdate { List<StudentDto.Update> getStudents(); }
    private interface StudentResponse { List<StudentDto.ShortResponse> getStudents(); }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Request implements Name, Course, Semester {
        String name;
        int course;
        int semester;
    }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class ShortRequest implements Id {
        long id;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Update implements Id, Name, Course, Semester {
        @Setter long id;
        String name;
        int course;
        int semester;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class ShortUpdate implements Id {
        @Setter long id;
    }

    @Value
    public static class Response implements Id, Name, Course, Semester, StudentResponse {
        long id;
        String name;
        int course;
        int semester;
        List<StudentDto.ShortResponse> students;
    }

    @Value
    public static class ShortResponse implements Id, Name, Course, Semester {
        long id;
        String name;
        int course;
        int semester;
    }
}
