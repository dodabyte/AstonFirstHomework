package org.example.firsthomework.dto;

import lombok.*;
import org.example.firsthomework.dto.global.DataTransferObject;

public enum SemesterPerformanceDto implements DataTransferObject {;
    private interface Id { long getId(); }
    private interface StudentRequest { StudentDto.ShortRequest getStudent(); }
    private interface StudentUpdate { StudentDto.ShortUpdate getStudent(); }
    private interface StudentResponse { StudentDto.Response getStudent(); }
    private interface DisciplineRequest { DisciplineDto.ShortRequest getDiscipline(); }
    private interface DisciplineUpdate { DisciplineDto.ShortUpdate getDiscipline(); }
    private interface DisciplineResponse { DisciplineDto.Response getDiscipline(); }
    private interface Mark { int getMark(); }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Request implements StudentRequest, DisciplineRequest, Mark {
        StudentDto.ShortRequest student;
        DisciplineDto.ShortRequest discipline;
        int mark;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Update implements Id, StudentUpdate, DisciplineUpdate, Mark {
        @Setter long id;
        StudentDto.ShortUpdate student;
        DisciplineDto.ShortUpdate discipline;
        int mark;
    }

    @Value
    public static class Response implements Id, StudentResponse, DisciplineResponse, Mark {
        @Setter long id;
        StudentDto.Response student;
        DisciplineDto.Response discipline;
        int mark;
    }
}
