package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.GroupDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.exception.JsonException;
import org.example.firsthomework.mapper.GroupMapperImpl;
import org.example.firsthomework.service.GroupServiceImpl;
import org.example.firsthomework.service.global.GroupService;
import org.example.firsthomework.util.JsonUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/group/*")
public class GroupServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final GroupService groupService = GroupServiceImpl.getInstance(GroupDao.getInstance(),
            StudentDao.getInstance(), GroupMapperImpl.getInstance());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && "all".equals(pathPart[1])) { // чтобы не было NPE
                List<GroupDto.Response> groupResponseDtoList = groupService.findAll();
                responseBody = objectMapper.writeValueAsString(groupResponseDtoList);
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                GroupDto.Response groupResponseDto = groupService.findById(id);
                responseBody = objectMapper.writeValueAsString(groupResponseDto);
            }
            else {
                throw new IllegalArgumentException("Group Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Group Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Group Not Found");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Bad Request");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        try {
            String requestBody = JsonUtil.getJson(request);
            Optional<GroupDto.Request> groupRequest = Optional.ofNullable(objectMapper.readValue(requestBody, GroupDto.Request.class));
            GroupDto.Request groupRequestDto = groupRequest.orElseThrow(EntityNotFoundException::new);
            String responseBody = objectMapper.writeValueAsString(groupService.insert(groupRequestDto));
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Group Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Group Not Found");
        }
        catch (InsertionException e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Group could not be inserted");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Bad request");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 3 && pathPart[2].equals("student")) {
                long id = Long.parseLong(pathPart[1]);
                List<Long> studentIds = Arrays.stream(pathPart[3].split("\\+")).
                        map(Long::parseLong).collect(Collectors.toList());
                responseBody = objectMapper.writeValueAsString(groupService.addStudentToGroup(studentIds, id).
                        stream().map(Object::toString).collect(Collectors.joining(",")));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                String requestBody = JsonUtil.getJson(request);
                Optional<GroupDto.Update> groupUpdate = Optional.ofNullable(objectMapper.readValue(requestBody, GroupDto.Update.class));
                GroupDto.Update groupUpdateDto = groupUpdate.orElseThrow(EntityNotFoundException::new);
                long id = Long.parseLong(pathPart[1]);
                groupUpdateDto.setId(id);
                responseBody = objectMapper.writeValueAsString(groupService.update(groupUpdateDto));
                response.getWriter().println(responseBody);
            }
            else {
                throw new IllegalArgumentException("Group Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Group Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Group Not Found");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Bad request");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 3 && pathPart[2].equals("student")) {
                List<Long> studentIds = Arrays.stream(pathPart[3].split("\\+")).
                        map(Long::parseLong).collect(Collectors.toList());
                responseBody = objectMapper.writeValueAsString(groupService.deleteStudentFromGroup(studentIds).
                        stream().map(Object::toString).collect(Collectors.joining(",")));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                responseBody = objectMapper.writeValueAsString(groupService.delete(id));
            }
            else {
                throw new IllegalArgumentException("Group Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Group Not Found");
        }
        catch (DataAccessObjectException e) {
            log.error(e.getMessage(), e);
            response.sendError(500, "Server Error");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Bad request");
        }
    }
}
