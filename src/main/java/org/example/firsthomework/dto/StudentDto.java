package org.example.firsthomework.dto;

import lombok.*;
import org.example.firsthomework.dto.global.DataTransferObject;

public class StudentDto implements DataTransferObject {
    private interface Id { long getId(); }
    private interface LastName { String getLastName(); }
    private interface FirstName { String getFirstName(); }
    private interface Patronymic { String getPatronymic(); }
    private interface GroupRequest { GroupDto.ShortRequest getGroup(); }
    private interface GroupUpdate { GroupDto.ShortUpdate getGroup(); }
    private interface GroupResponse { GroupDto.ShortResponse getGroup(); }

    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class Request implements LastName, FirstName, Patronymic, GroupRequest {
        String lastName;
        String firstName;
        String patronymic;
        GroupDto.ShortRequest group;
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
    public static class Update implements Id, LastName, FirstName, Patronymic, GroupUpdate {
        @Setter long id;
        String lastName;
        String firstName;
        String patronymic;
        GroupDto.ShortUpdate group;
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public static class ShortUpdate implements Id {
        @Setter long id;
    }

    @Value
    public static class Response implements Id, LastName, FirstName, Patronymic, GroupResponse {
        long id;
        String lastName;
        String firstName;
        String patronymic;
        GroupDto.ShortResponse group;
    }

    @Value
    public static class ShortResponse implements Id, LastName, FirstName, Patronymic {
        long id;
        String lastName;
        String firstName;
        String patronymic;
    }
}
