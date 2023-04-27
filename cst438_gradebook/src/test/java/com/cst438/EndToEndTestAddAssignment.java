package com.cst438;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;

//import io.github.bonigarcia.wdm.WebDriverManager;






@SpringBootTest
public class EndToEndTestAddAssignment {
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/betsyruiz/Desktop/chromedriver_mac64/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;
	

	@Test
	public void addCourseTest() throws Exception {

//create course to add assignment to	
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);

//add an assignment for course 99999
		Assignment a = new Assignment();
		a.setCourse(c);
		// set assignment due date to 24 hours ago
		a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		a.setName(TEST_ASSIGNMENT_NAME);



		courseRepository.save(c);
		a = assignmentRepository.save(a);

		AssignmentGrade ag = null;

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='assignmentName']/div"));
			boolean found = false;
			for (WebElement we : elements) {
				System.out.println(we.getText()); // for debug
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("descendant::input")).click();
					break;
				}
			}
			
			assertTrue( found, "TEST ASSIGNMENT was not added.");

			driver.findElement(By.xpath("//button[@id='Submit']")).click();
			Thread.sleep(SLEEP_DURATION);

		} catch (Exception ex) {
			throw ex;
		} finally {

			ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
			if (ag!=null) assignnmentGradeRepository.delete(ag);
			
			assignmentRepository.delete(a);
			courseRepository.delete(c);

			driver.quit();
		}

	}

}
