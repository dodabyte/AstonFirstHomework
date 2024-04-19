package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.dao.GroupDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.StudentDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.exception.JsonException;
import org.example.firsthomework.mapper.StudentMapperImpl;
import org.example.firsthomework.service.StudentServiceImpl;
import org.example.firsthomework.service.global.StudentService;
import org.example.firsthomework.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/student/*")
public class StudentServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StudentService studentService = StudentServiceImpl.getInstance(StudentDao.getInstance(),
            GroupDao.getInstance(), StudentMapperImpl.getInstance());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && pathPart[1].equals("all")) {
                List<StudentDto.Response> disciplineResponseDtoList = studentService.findAll();
                responseBody = objectMapper.writeValueAsString(disciplineResponseDtoList);
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                StudentDto.Response disciplineResponseDto = studentService.findById(id);
                responseBody = objectMapper.writeValueAsString(disciplineResponseDto);
            }
            else {
                throw new IllegalArgumentException("Student Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Student Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Student Not Found");
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
            Optional<StudentDto.Request> studentRequest = Optional.ofNullable(objectMapper.readValue(requestBody, StudentDto.Request.class));
            StudentDto.Request studentRequestDto = studentRequest.orElseThrow(EntityNotFoundException::new);
            String responseBody = objectMapper.writeValueAsString(studentService.insert(studentRequestDto));
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Student Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Student Not Found");
        }
        catch (InsertionException e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Student could not be inserted");
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
            if (pathPart.length > 3 && pathPart[2].equals("group")) {
                long groupId = Long.parseLong(pathPart[1]);
                long studentId = Long.parseLong(pathPart[3]);
                responseBody = objectMapper.writeValueAsString(studentService.addGroupToStudent(studentId, groupId));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                String requestBody = JsonUtil.getJson(request);
                Optional<StudentDto.Update> studentUpdate = Optional.ofNullable(objectMapper.readValue(requestBody, StudentDto.Update.class));
                StudentDto.Update studentUpdateDto = studentUpdate.orElseThrow(EntityNotFoundException::new);
                long id = Long.parseLong(pathPart[1]);
                studentUpdateDto.setId(id);
                responseBody = objectMapper.writeValueAsString(studentService.update(studentUpdateDto));
            }
            else {
                throw new IllegalArgumentException("Student Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Student Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Student Not Found");
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
            long id = Long.parseLong(pathPart[1]);
            if (pathPart.length > 2 && pathPart[2].equals("group")) {
                responseBody = objectMapper.writeValueAsString(studentService.deleteGroupFromStudent(id));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                responseBody = objectMapper.writeValueAsString(studentService.delete(id));
            }
            else {
                throw new IllegalArgumentException("Student Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Student Not Found");
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
