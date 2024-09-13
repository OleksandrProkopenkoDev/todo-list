package ua.spro.todolist.specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;
import ua.spro.todolist.model.entity.Task;

public class TaskSpecification {

  public static Specification<Task> filterByParams(Map<String, String> params) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction();

      // Filter by exact dueDate if exists
      if (params.containsKey("dueDate")) {
        LocalDateTime dueDate = LocalDateTime.parse(params.get("dueDate"));
        predicate = cb.and(predicate, cb.equal(root.get("dueDate"), dueDate));
      }

      // Filter by dueDate before a certain date
      if (params.containsKey("dueDateBefore")) {
        LocalDateTime dueDateBefore = LocalDateTime.parse(params.get("dueDateBefore"));
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dueDate"), dueDateBefore));
      }

      // Filter by dueDate between two dates
      if (params.containsKey("dueDateFrom") && params.containsKey("dueDateTo")) {
        LocalDateTime dueDateFrom = LocalDateTime.parse(params.get("dueDateFrom"));
        LocalDateTime dueDateTo = LocalDateTime.parse(params.get("dueDateTo"));
        predicate = cb.and(predicate, cb.between(root.get("dueDate"), dueDateFrom, dueDateTo));
      }

      // Filter by completed status if exists
      if (params.containsKey("completed")) {
        Boolean completed = Boolean.valueOf(params.get("completed"));
        predicate = cb.and(predicate, cb.equal(root.get("completed"), completed));
      }

      return predicate;
    };
  }
}
