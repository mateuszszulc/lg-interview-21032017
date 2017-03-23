package com.wojciechkocik.usage.service;

import com.wojciechkocik.usage.dto.CourseUsageCreate;
import com.wojciechkocik.usage.entity.CourseUsage;
import com.wojciechkocik.usage.repository.CourseUsageRepository;
import org.jfairy.Fairy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;

/**
 * @author Wojciech Kocik
 * @since 22.03.2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseUsageServiceTest {

    private Fairy fairy = Fairy.create();

    @Autowired
    private CourseUsageService courseUsageService;

    @Autowired
    private CourseUsageRepository courseUsageRepository;

    @Test
    public void createNewCourseUsage_dtoIsProperMappedToDatabase() {

        //Arrange
        String courseId = fairy.textProducer().randomString(10);
        String userId = fairy.textProducer().randomString(10);
        long timeSpent = 5000;
        ZonedDateTime started = ZonedDateTime.now();

        CourseUsageCreate courseUsageCreate = new CourseUsageCreate();
        courseUsageCreate.setCourseId(courseId);
        courseUsageCreate.setUserId(userId);
        courseUsageCreate.setTimeSpent(timeSpent);
        courseUsageCreate.setStarted(started);

        CourseUsage courseUsageExpected = new CourseUsage();
        courseUsageExpected.setTimeSpent(timeSpent);
        courseUsageExpected.setUserId(userId);
        courseUsageExpected.setCourseId(courseId);
        courseUsageExpected.setStarted(started);

        //Act
        CourseUsage courseUsageActual = courseUsageService.createNewCourseUsage(courseUsageCreate);
        courseUsageExpected.setId(courseUsageActual.getId());

        //Arrange
        Assert.assertEquals(courseUsageExpected, courseUsageActual);
    }

    @Test(expected = ConstraintViolationException.class)
    public void createNewCourseUsage_whenArgFieldsAreNull_throwsConstraintViolationException() {
        //Arrange
        CourseUsageCreate courseUsageCreate = new CourseUsageCreate();

        //Act
        CourseUsage courseUsageActual = courseUsageService.createNewCourseUsage(courseUsageCreate);
    }

    @Test
    public void findDailyUsageForCourse__whenCourseSessionCrossedMidnight_thenRestTimePassedToNextDay(){
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void findDailyUsageForCourse_returnsProperDateFormat(){
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void findDailyUsageForCourse_whenDaysWithoutActivity_notPresentInResponse() {
        //Arrange

        //Act

        //Assert
    }

    @After
    public void tearDown() {
        courseUsageRepository.deleteAll();
    }

}
