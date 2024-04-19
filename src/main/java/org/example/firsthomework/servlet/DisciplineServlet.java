package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.TeacherDao;
import org.example.firsthomework.dao.TeacherDisciplineDao;
import org.example.firsthomework.dto.DisciplineDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.exception.JsonException;
import org.example.firsthomework.mapper.DisciplineMapperImpl;
import org.example.firsthomework.mapper.TeacherMapperImpl;
import org.example.firsthomework.service.DisciplineServiceImpl;
import org.example.firsthomework.service.global.DisciplineService;
import org.example.firsthomework.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/discipline/*")
public class DisciplineServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final DisciplineService disciplineService = DisciplineServiceImpl.getInstance(DisciplineDao.getInstance(), TeacherDao.getInstance(),
            TeacherDisciplineDao.getInstance(), DisciplineMapperImpl.getInstance(), TeacherMapperImpl.getInstance());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && pathPart[1].equals("all")) {
                List<DisciplineDto.Response> disciplineResponseDtoList = disciplineService.findAll();
                responseBody = objectMapper.writeValueAsString(disciplineResponseDtoList);
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                DisciplineDto.Response disciplineResponseDto = disciplineService.findById(id);
                responseBody = objectMapper.writeValueAsString(disciplineResponseDto);
            }
            else {
                throw new IllegalArgumentException("Discipline Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Discipline Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Discipline Not Found");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Bad request");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        try {
            String requestBody = JsonUtil.getJson(request);
            Optional<DisciplineDto.Request> disciplineRequest = Optional.ofNullable(objectMapper.readValue(requestBody, DisciplineDto.Request.class));
            DisciplineDto.Request disciplineRequestDto = disciplineRequest.orElseThrow(EntityNotFoundException::new);
            String responseBody = objectMapper.writeValueAsString(disciplineService.insert(disciplineRequestDto));
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Discipline Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Discipline Not Found");
        }
        catch (InsertionException e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Discipline could not be inserted");
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
            if (pathPart.length > 3 && pathPart[2].equals("teacher")) {
                long disciplineId = Long.parseLong(pathPart[1]);
                long teacherId = Long.parseLong(pathPart[3]);
                responseBody = objectMapper.writeValueAsString(disciplineService.addDisciplineToTeacher(disciplineId, teacherId));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                String requestBody = JsonUtil.getJson(request);
                Optional<DisciplineDto.Update> disciplineUpdate = Optional.ofNullable(objectMapper.readValue(requestBody, DisciplineDto.Update.class));
                DisciplineDto.Update disciplineUpdateDto = disciplineUpdate.orElseThrow(EntityNotFoundException::new);
                long id = Long.parseLong(pathPart[1]);
                disciplineUpdateDto.setId(id);
                responseBody = objectMapper.writeValueAsString(disciplineService.update(disciplineUpdateDto));
            }
            else {
                throw new IllegalArgumentException("Discipline Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Discipline Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Discipline Not Found");
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
            long id;
            if (pathPart.length > 3 && pathPart[2].equals("teacher")) {
                id = Long.parseLong(pathPart[3]);
                responseBody = objectMapper.writeValueAsString(disciplineService.deleteDisciplineFromTeacher(id));
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                id = Long.parseLong(pathPart[1]);
                responseBody = objectMapper.writeValueAsString(disciplineService.delete(id));
            }
            else {
                throw new IllegalArgumentException("Discipline Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Discipline Not Found");
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
