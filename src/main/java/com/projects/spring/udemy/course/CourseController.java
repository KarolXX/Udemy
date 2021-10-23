package com.projects.spring.udemy.course;

import com.projects.spring.udemy.ConfigurationProperties;
import com.projects.spring.udemy.course.dto.CourseInMenu;
import com.projects.spring.udemy.course.dto.SingleCourseModel;
import com.projects.spring.udemy.course.dto.FileModel;
import com.projects.spring.udemy.file.AppFileService;
import com.projects.spring.udemy.relationship.CourseRating;
import com.projects.spring.udemy.relationship.CourseRatingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/courses")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private CourseRepository repository;
    private CourseService service;
    private AppFileService appFileService;
    private ConfigurationProperties configuration;

    public CourseController(
            CourseRepository repository,
            CourseService service,
            AppFileService appFileService,
            ConfigurationProperties configuration
    ) {
        this.repository = repository;
        this.service = service;
        this.appFileService = appFileService;
        this.configuration = configuration;
    }

    @GetMapping
    ResponseEntity<List<CourseInMenu>> getAllCourses() {
        logger.warn("Exposing all the courses!");
        var result = repository.getCourseMenu();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{courseId}", params = "user")
    ResponseEntity<SingleCourseModel> getCourseById(@PathVariable Integer courseId, @RequestParam("user") Integer userId) {
        logger.warn("Exposing course");
        var result = service.getCourse(courseId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{id}/participants/other-courses", params = "user")
    ResponseEntity<List<CourseInMenu>> getOtherParticipantsCourses(@PathVariable("id") Integer targetCourseId, @RequestParam("user") Integer userId) {
        logger.warn("Exposing users courses");
        var result = service.getOtherParticipantsCourses(targetCourseId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(params = "category")
    ResponseEntity<List<CourseInMenu>> getCoursesByCategoryId(
            @RequestParam("category") Integer categoryId
    ) {
        logger.warn("Exposing course");
        var result = repository.getCourseMenuByCategoryId(categoryId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<Course> createCourse(@RequestBody Course source) {
        logger.info("New course has been created");
        var result = repository.save(source);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(source);
    }

    @PostMapping("/course-purchase")
    ResponseEntity<?> buyCourse(@RequestBody CourseRatingKey key) {
        logger.warn("Client bought course");
        return service.buyCourse(key);
    }

    @PutMapping("/course-rating")
    ResponseEntity<?> rateCourse(@RequestBody CourseRating source) {
        logger.info("Client rated course");
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Access-Control-Allow-Origin", "*");
        double result = service.rateCourse(source);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{courseId}")
    ResponseEntity<?> deleteCourse(@PathVariable Integer courseId) {
        logger.warn("Course has been deleted");
        repository.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}/img")
    ResponseEntity<?> deleteCourseImage(@PathVariable Integer id) {
        logger.warn("Course image has been deleted!");
        service.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadFile(@PathVariable Integer id, FileModel fileModel) {
        logger.info("Uploading course image/video");
        return appFileService.saveFile(id, fileModel, "course");
    }

    @GetMapping(value = "/{id}/img")
    ResponseEntity<?> getCourseImage(@PathVariable Integer id) {
        logger.warn("Exposing course image");
        return appFileService.getFile(id, "course", false);
    }

    @GetMapping(value = "/{id}/video")
    ResponseEntity<?> getCourseVideo(@PathVariable Integer id) {
        logger.warn("Exposing course video");
        return appFileService.getFile(id, "course", true);
    }
}
