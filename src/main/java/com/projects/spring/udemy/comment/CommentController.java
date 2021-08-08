package com.projects.spring.udemy.comment;

import com.projects.spring.udemy.course.Course;
import com.projects.spring.udemy.course.dto.CommentWithUserID;
import com.projects.spring.udemy.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.URI;
import java.util.Set;

@CrossOrigin
@RestController
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private CommentService service;
    private CommentRepository repository;
    private final String basePath = "/courses/{courseId}/comments";

    public CommentController(CommentService service, CommentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping(path = basePath)
    ResponseEntity<Comment> createComment(
            @PathVariable Integer courseId,
            @RequestBody CommentWithUserID comment
    ) {
        logger.info("New comment has been added");
        var result = service.createComment(courseId, comment);
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = basePath + "/{commentId}")
    ResponseEntity<?> editComment(@PathVariable Integer commentId, @RequestBody Comment source) {
        logger.warn("Comment has been edited");
        service.editComment(commentId, source);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = basePath + "/{commentId}")
    ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        logger.warn("Comment has been deleted");
        service.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
