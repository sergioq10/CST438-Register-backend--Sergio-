package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;


@SpringBootTest
public class EndToEndAddStudent {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/yeyo/Desktop/chromedriver.exe";
	
	public static final String URL = "http://localhost:3000";
	
	public static final int TEST_STUDENT_ID = 4;
	
	public static final String TEST_STUDENT_NAME = "test_st_1";
	
	public static final String TEST_STUDENT_EMAIL = "testing@st.test";
	
	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	@Autowired
	StudentRepository studentRepository;
	
	@Test
	public void addStudentTest() throws Exception {
		
		//if student is already exists, then delete the student.
		
		Student x = null;
		
		do {
			x = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

	
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			// Locate and click "Add Student" button
			driver.findElement(By.xpath("//button[@id='main_add_student']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Enter student name, student email, and click add button
			driver.findElement(By.xpath("//input[@type='text' and @name='name']")).sendKeys(TEST_STUDENT_NAME);
			driver.findElement(By.xpath("//input[@type='text' and @name='email']")).sendKeys(TEST_STUDENT_EMAIL);
			driver.findElement(By.xpath("//button[@id='add_student']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			
			boolean found = false;
			Student student = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
	
			if((student.getEmail().equals(TEST_STUDENT_EMAIL))) {
				found = true;
			}
			assertTrue(found, "Student added but in the schedule");
			assertNotNull(student, "Student not found in database");
		} 
		catch (Exception ex) {
			throw ex;
		} 
		finally {
			
			Student stud = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			if(stud != null)
				studentRepository.delete(stud);
			
			driver.quit();
		}
	}
}