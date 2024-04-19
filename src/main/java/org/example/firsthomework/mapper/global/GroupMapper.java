package org.example.firsthomework.mapper.global;

import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.entity.Group;

import java.util.List;

public interface GroupMapper {
    Group map(GroupDto.Request entityDto);

    Group shortMap(GroupDto.ShortRequest entityDto);

    GroupDto.Response map(Group entity);

    GroupDto.ShortResponse shortMap(Group entity);

    Group map(GroupDto.Update entityDto);

    Group shortMap(GroupDto.ShortUpdate entityDto);

    List<GroupDto.Response> map(List<Group> entities);
}
