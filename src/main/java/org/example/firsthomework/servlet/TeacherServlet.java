package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.TeacherDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.exception.JsonException;
import org.example.firsthomework.mapper.TeacherMapperImpl;
import org.example.firsthomework.service.TeacherServiceImpl;
import org.example.firsthomework.service.global.TeacherService;
import org.example.firsthomework.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/teacher/*")
public class TeacherServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TeacherService teacherService = TeacherServiceImpl.getInstance(TeacherDao.getInstance(),
            TeacherDisciplineDao.getInstance(), TeacherMapperImpl.getInstance());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && pathPart[1].equals("all")) {
                List<TeacherDto.Response> disciplineResponseDtoList = teacherService.findAll();
                responseBody = objectMapper.writeValueAsString(disciplineResponseDtoList);
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                TeacherDto.Response disciplineResponseDto = teacherService.findById(id);
                responseBody = objectMapper.writeValueAsString(disciplineResponseDto);
            }
            else {
                throw new IllegalArgumentException("Teacher Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Teacher Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Teacher Not Found");
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
            Optional<TeacherDto.Request> teacherRequest = Optional.ofNullable(objectMapper.readValue(requestBody, TeacherDto.Request.class));
            TeacherDto.Request teacherRequestDto = teacherRequest.orElseThrow(EntityNotFoundException::new);
            String responseBody = objectMapper.writeValueAsString(teacherService.insert(teacherRequestDto));
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            response.sendError(406, "Teacher Json Invalid");
        }
        catch (EntityNotFoundException e) {
            response.sendError(404, "Teacher Not Found");
        }
        catch (InsertionException e) {
            response.sendError(400, "Teacher could not be inserted");
        }
        catch (Exception e) {
            response.sendError(400, "Bad request");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                String requestBody = JsonUtil.getJson(request);
                Optional<TeacherDto.Update> teacherUpdate = Optional.ofNullable(objectMapper.readValue(requestBody, TeacherDto.Update.class));
                TeacherDto.Update teacherUpdateDto = teacherUpdate.orElseThrow(EntityNotFoundException::new);
                long id = Long.parseLong(pathPart[1]);
                teacherUpdateDto.setId(id);
                responseBody = objectMapper.writeValueAsString(teacherService.update(teacherUpdateDto));
            }
            else {
                throw new IllegalArgumentException("Teacher Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Teacher Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Teacher Not Found");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Bad Request");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            long id;
            if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                id = Long.parseLong(pathPart[1]);
                responseBody = objectMapper.writeValueAsString(teacherService.delete(id));
            }
            else {
                throw new IllegalArgumentException("Teacher Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Teacher Not Found");
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
