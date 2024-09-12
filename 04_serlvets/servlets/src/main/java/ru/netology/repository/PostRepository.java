package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Stub
@Repository
public class PostRepository {
  private  Map<Long, Post> postMap = new ConcurrentHashMap<>();
  long count = 1;

  public List<Post> all() {
    return postMap.values().stream().collect(Collectors.toList());
  }

  public Post getById(long id) {
    return postMap.get(id);
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      var newID = count;
      count++;
      post.setId(newID);
      return postMap.put(newID, post);
    } else {
      return postMap.replace(post.getId(),post);
    }
  }

  public boolean removeById(long id) {
    var item = postMap.get(id);
    if (item != null) {
      postMap.remove(id);
      return true;
    }
    return false;
  }
}
