package com.wojciechkocik.usage.service;

import com.wojciechkocik.usage.dto.DailyUsageResponse;
import com.wojciechkocik.usage.dto.PerCourseUsageForUser;
import com.wojciechkocik.usage.entity.CourseUsage;
import com.wojciechkocik.usage.repository.CourseUsageRepository;
import org.jfairy.Fairy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Wojciech Kocik
 * @since 23.03.2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserUsageServiceTest {

    @Autowired
    private CourseUsageRepository courseUsageRepository;

    @Autowired
    private UserUsageService userUsageService;

    @Autowired
    CommandLineRunner commandLineRunner;

    Fairy fairy = Fairy.create();

    @Before
    public void setUp() {
        courseUsageRepository.deleteAll();
    }

    @Test
    public void findPerCourseUsage_groupCourse_hasProperSpentTime() throws Exception {
        //Arrange
        int coursesQuantity = 10;
        String courseId = fairy.textProducer().randomString(50);
        String userId = fairy.textProducer().randomString(50);

        long timeSpentSumExpected = 0;

        for (int i = 0; i < coursesQuantity; i++) {
            int spentTime = new Random().nextInt(50000);
            timeSpentSumExpected += spentTime;
            CourseUsage courseUsage = new CourseUsage();
            courseUsage.setStarted(ZonedDateTime.now());
            courseUsage.setUserId(userId);
            courseUsage.setCourseId(courseId);
            courseUsage.setTimeSpent(spentTime);
            courseUsageRepository.save(courseUsage);
        }

        //Act
        List<PerCourseUsageForUser> perCourseUsage = userUsageService.findPerCourseUsage(userId);
        long timeSpentSumActual = perCourseUsage.get(0).getTime();

        //Assert
        Assert.assertEquals(timeSpentSumExpected, timeSpentSumActual);
    }

    @Test
    public void findPerCourseUsage_whenCoursesWithoutActivity_notPresentInResponse() {
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void findDailyUsagesForUser_groupDate_hasProperSpentTime(){
        //Arrange
        ZonedDateTime started = ZonedDateTime.parse("2017-03-24T11:56:26.595+01:00[Europe/Belgrade]");
        int entitiesWithSameDateForGroup = 5;
        String userId = fairy.textProducer().randomString(10);
        String courseId = fairy.textProducer().randomString(10);

        for (int i = 0; i < entitiesWithSameDateForGroup; i++) {
            CourseUsage courseUsage = new CourseUsage(
                    started,
                    new Random().nextInt(5000),
                    userId,
                    courseId
            );
            courseUsageRepository.save(courseUsage);
        }

        int sizeExpected = 1;

        //Act
        List<DailyUsageResponse> dailyUsageForCourse = userUsageService.findDailyUsagesForUser(userId);
        int sizeActual = dailyUsageForCourse.size();

        //Assert
        Assert.assertEquals(sizeExpected, sizeActual);
    }

    @Test
    public void findDailyUsagesForUser_whenCourseSessionCrossedMidnight_thenRestTimePassedToNextDay() {
        //Arrange
        int timeSpentMinutes = 5;

        String userId = fairy.textProducer().randomString(10);
        CourseUsage courseUsageWithTimeCrossedMidnight = new CourseUsage();
        ZonedDateTime startedOneMinuteBeforeMidnight = ZonedDateTime.parse("2017-03-23T23:59:00.000+01:00[Europe/Warsaw]");
        String firstSimpleDay = "2017-03-23";
        String secondSimpleDay = "2017-03-24";
        courseUsageWithTimeCrossedMidnight.setStarted(startedOneMinuteBeforeMidnight);
        courseUsageWithTimeCrossedMidnight.setCourseId(fairy.textProducer().randomString(10));
        courseUsageWithTimeCrossedMidnight.setUserId(userId);
        courseUsageWithTimeCrossedMidnight.setTimeSpent(Duration.ofMinutes(timeSpentMinutes).getSeconds()); //one minute in the next day

        courseUsageRepository.save(courseUsageWithTimeCrossedMidnight);

        int responseSizeExpected = 2;

        long firstDayMinutesExpected = 1;
        long secondDayMinutesExpected = timeSpentMinutes - firstDayMinutesExpected;

        //Act
        List<DailyUsageResponse> dailyUsagesForCourse = userUsageService.findDailyUsagesForUser(userId);
        int responseSizeActual = dailyUsagesForCourse.size();
        long firstDayMinutesActual = dailyUsagesForCourse.stream().filter(f->f.getDateTime().equals(firstSimpleDay))
                .collect(Collectors.toList()).get(0).getTime();
        long secondDayMinutesActual = dailyUsagesForCourse.stream().filter(f->f.getDateTime().equals(secondSimpleDay))
                .collect(Collectors.toList()).get(0).getTime();

        //convert to minutes
        firstDayMinutesActual = Duration.ofSeconds(firstDayMinutesActual).toMinutes();
        secondDayMinutesActual = Duration.ofSeconds(secondDayMinutesActual).toMinutes();

        //Assert
        Assert.assertEquals(responseSizeExpected, responseSizeActual);
        Assert.assertEquals(firstDayMinutesExpected, firstDayMinutesActual);
        Assert.assertEquals(secondDayMinutesExpected, secondDayMinutesActual);
    }

    @Test
    public void findDailyUsagesForUser_whenDaysWithoutActivity_notPresentInResponse() {
        //Arrange

        //Act

        //Assert
    }

    @After
    public void tearDown() {
        courseUsageRepository.deleteAll();
    }


}
