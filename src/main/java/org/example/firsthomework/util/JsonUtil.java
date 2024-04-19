package org.example.firsthomework.util;

import org.example.firsthomework.exception.JsonException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonUtil {
    public static void setJsonOption(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    public static String getJson(HttpServletRequest request) throws JsonException {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader postData = request.getReader();
            String string;
            while ((string = postData.readLine()) != null) {
                builder.append(string);
            }
            return builder.toString();
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
