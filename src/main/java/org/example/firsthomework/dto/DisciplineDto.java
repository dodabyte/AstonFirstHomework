package org.example.firsthomework.dto;

import lombok.*;
import org.example.firsthomework.dto.global.DataTransferObject;

public enum DisciplineDto implements DataTransferObject {; // ты сильно перемудрил с ДТО, это тут лишнее. И с интерфейсами тоже
    private interface Id { long getId(); }
    private interface Name { String getName(); }
    private interface TeacherRequest { TeacherDto.Request getTeacher(); }
    private interface TeacherUpdate { TeacherDto.Update getTeacher(); }
    private interface TeacherResponse { TeacherDto.ShortResponse getTeacher(); }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Request implements Name, TeacherRequest {
        String name;
        TeacherDto.Request teacher;
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
    public static class Update implements Id, Name, TeacherUpdate {
        @Setter long id;
        String name;
        TeacherDto.Update teacher;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class ShortUpdate implements Id {
        @Setter long id;
    }

    @Value
    public static class Response implements Id, Name, TeacherResponse {
        long id;
        String name;
        TeacherDto.ShortResponse teacher;
    }

    @Value
    public static class ShortResponse implements Id, Name {
        long id;
        String name;
    }
}
