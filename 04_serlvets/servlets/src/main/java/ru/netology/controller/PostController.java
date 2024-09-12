package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import ru.netology.model.Post;
import ru.netology.service.PostService;
import ru.netology.service.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
@Controller
public class PostController  {
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;
  private Service bonusService = new Service() ;
  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    bonusService.sendJsonResponse(response,service.all());
  }

  public void getById(long id, HttpServletResponse response) throws IOException {
    bonusService.sendJsonResponse(response,service.getById(id));

  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    final var post = new Gson().fromJson(body, Post.class);
    bonusService.sendJsonResponse(response,service.save(post));
  }

  public void removeById(long id, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    if (service.removeById(id)) {
      response.getWriter().print("Element № " + id + " was deleted");
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
