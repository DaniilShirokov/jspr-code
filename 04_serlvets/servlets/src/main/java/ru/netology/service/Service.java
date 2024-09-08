package ru.netology.service;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.netology.controller.PostController.APPLICATION_JSON;

public class Service {


    public void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        if(data == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        response.getWriter().print(gson.toJson(data));
    }

}
