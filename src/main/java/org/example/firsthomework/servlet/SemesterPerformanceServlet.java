package org.example.firsthomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.firsthomework.dao.DisciplineDao;
import org.example.firsthomework.dao.SemesterPerformanceDao;
import org.example.firsthomework.dao.StudentDao;
import org.example.firsthomework.dto.SemesterPerformanceDto;
import org.example.firsthomework.exception.DataAccessObjectException;
import org.example.firsthomework.exception.EntityNotFoundException;
import org.example.firsthomework.exception.InsertionException;
import org.example.firsthomework.exception.JsonException;
import org.example.firsthomework.mapper.SemesterPerformanceMapperImpl;
import org.example.firsthomework.service.SemesterPerformanceServiceImpl;
import org.example.firsthomework.service.global.SemesterPerformanceService;
import org.example.firsthomework.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/semester_performance/*")
public class SemesterPerformanceServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final SemesterPerformanceService semesterPerformanceService = SemesterPerformanceServiceImpl.getInstance(
            SemesterPerformanceDao.getInstance(), StudentDao.getInstance(), DisciplineDao.getInstance(),
            SemesterPerformanceMapperImpl.getInstance());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.setJsonOption(response);

        String responseBody;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 1 && pathPart[1].equals("all")) {
                List<SemesterPerformanceDto.Response> semesterPerformanceResponseDtoList = semesterPerformanceService.findAll();
                responseBody = objectMapper.writeValueAsString(semesterPerformanceResponseDtoList);
            }
            else if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                SemesterPerformanceDto.Response semesterPerformanceResponseDto = semesterPerformanceService.findById(id);
                responseBody = objectMapper.writeValueAsString(semesterPerformanceResponseDto);
            }
            else {
                throw new IllegalArgumentException("Semester Performance Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Semester Performance Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Semester Performance Not Found");
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
            Optional<SemesterPerformanceDto.Request> semesterPerformanceRequest =
                    Optional.ofNullable(objectMapper.readValue(requestBody, SemesterPerformanceDto.Request.class));
            SemesterPerformanceDto.Request semesterPerformanceRequestDto = semesterPerformanceRequest.orElseThrow(EntityNotFoundException::new);
            String responseBody = objectMapper.writeValueAsString(semesterPerformanceService.insert(semesterPerformanceRequestDto));
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Semester Performance Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Semester Performance Not Found");
        }
        catch (InsertionException e) {
            log.error(e.getMessage(), e);
            response.sendError(400, "Semester Performance could not be inserted");
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
            if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                String requestBody = JsonUtil.getJson(request);
                Optional<SemesterPerformanceDto.Update> semesterPerformanceUpdate =
                        Optional.ofNullable(objectMapper.readValue(requestBody, SemesterPerformanceDto.Update.class));
                SemesterPerformanceDto.Update semesterPerformanceUpdateDto = semesterPerformanceUpdate.orElseThrow(EntityNotFoundException::new);
                long id = Long.parseLong(pathPart[1]);
                semesterPerformanceUpdateDto.setId(id);
                responseBody = objectMapper.writeValueAsString(semesterPerformanceService.update(semesterPerformanceUpdateDto));
            }
            else {
                throw new IllegalArgumentException("Semester Performance Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (JsonException e) {
            log.error(e.getMessage(), e);
            response.sendError(406, "Semester Performance Json Invalid");
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Semester Performance Not Found");
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
            if (pathPart.length > 1 && pathPart[1].matches("\\d+")) {
                long id = Long.parseLong(pathPart[1]);
                responseBody = objectMapper.writeValueAsString(semesterPerformanceService.delete(id));
            }
            else {
                throw new IllegalArgumentException("Semester Performance Id or command Invalid");
            }
            response.getWriter().println(responseBody);
        }
        catch (EntityNotFoundException e) {
            log.error(e.getMessage(), e);
            response.sendError(404, "Semester Performance Not Found");
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
