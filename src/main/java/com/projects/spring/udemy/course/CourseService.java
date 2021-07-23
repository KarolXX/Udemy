package com.projects.spring.udemy.course;

import com.projects.spring.udemy.comment.Comment;
import com.projects.spring.udemy.course.dto.CourseWithUserIDs;
import com.projects.spring.udemy.relationship.CourseRating;
import com.projects.spring.udemy.relationship.CourseRatingKey;
import com.projects.spring.udemy.relationship.CourseRatingRepository;
import com.projects.spring.udemy.user.User;
import com.projects.spring.udemy.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private CourseRepository repository;
    private UserRepository userRepository;
    private CourseRatingRepository ratingRepository;

    public CourseService(CourseRepository repository, UserRepository userRepository, CourseRatingRepository ratingRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    public CourseWithUserIDs getCourse(Integer id) {
        Course target = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No course with given id"));
        List<Integer> userIDs = new ArrayList<>(
                target.getRatings()
                    .stream().map(rating -> rating.getId().getUserId())
                    .collect(Collectors.toList())
        );
        return new CourseWithUserIDs(target, userIDs);
    }

    public Comment createComment(Integer userId, Integer courseId, Comment comment) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("You can not add comment to non-existing course"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        // create new set of comments ( just adding a new comment to existing ones )
        Set<Comment> updatedComments = new HashSet<>(course.getComments());
        updatedComments.add(comment);
        // keeping both sides of the association between course and comments in-sync
        comment.setCourse(course);
        course.setComments(updatedComments);
        // keeping both sides of the association between user and comments in-sync
        comment.setUser(user);
        user.setComments(updatedComments);
        // saving changes
        var updatedCourse = repository.save(course);
        return updatedCourse.getComments()
                .stream()
                .filter(com -> com.getText().equals(comment.getText()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Logic error"));
    }

//    @Transactional
//    // FIXME
//    //  Do I need @Transactional here?
//    //  None of the modified fields are directly entered into the DB
//    //  ( I mean all of them are association fields )
//    public CourseRating buyCourse(CourseRatingKey key) {
//        CourseRating relationship = new CourseRating(key);
//        // FIXME: Why am I getting error ? ( uncomment and check )
////         var target = ratingRepository.save(relationship);
////         return target;
//
//        // FIXME another solution
//        User user = userRepository.findById(key.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("No user with given id"));
//        Course course = repository.findById(key.getCourseId())
//                .orElseThrow(() -> new IllegalArgumentException("No course with given id"));
//        course.setRatings(new HashSet<>(Set.of(relationship)));
//        user.setRatings(new HashSet<>(Set.of(relationship)));
//
//        relationship.setCourse(course);
//        relationship.setUser(user);
//
//        return ratingRepository.save(relationship);
//    }
}
