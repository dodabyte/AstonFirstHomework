package org.example.firsthomework.dto;

import lombok.*;
import org.example.firsthomework.dto.global.DataTransferObject;

public enum TeacherDto implements DataTransferObject {;
    private interface Id { long getId(); }
    private interface LastName { String getLastName(); }
    private interface FirstName { String getFirstName(); }
    private interface Patronymic { String getPatronymic(); }
    private interface DisciplineResponse { DisciplineDto.ShortResponse getDiscipline(); }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Request implements LastName, FirstName, Patronymic {
        String lastName;
        String firstName;
        String patronymic;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Update implements Id, LastName, FirstName, Patronymic {
        @Setter long id;
        String lastName;
        String firstName;
        String patronymic;
    }

    @Value
    public static class Response implements Id, LastName, FirstName, Patronymic, TeacherDto.DisciplineResponse  {
        long id;
        String lastName;
        String firstName;
        String patronymic;
        DisciplineDto.ShortResponse discipline;
    }

    @Value
    public static class ShortResponse implements Id, LastName, FirstName, Patronymic  {
        long id;
        String lastName;
        String firstName;
        String patronymic;
    }
}
