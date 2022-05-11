package com.cst438;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cst438.controller.StudentController;



@ContextConfiguration(classes = {StudentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class StudentTest {
	
	static final String URl = "http://localhoast:8080"; 
    public final static int TEST_STUDENT_ID = 1;
    public final static String TEST_STUDENT_EMAIL = "test@csumb.edu";
    public final static String TEST_STUDENT_STATUS = "test status";
    public final static String TEST_STUDENT_NAME = "test";
    public final static int TEST_STUDENT_STATUS_CODE = 0;

    @MockBean
    StudentRepository studentRepository;

    @Autowired
    private MockMvc mvc;//replaces real spring server with moc

    //no main method
    @Test 
    public void addStudent() throws Exception { //no input or return data 
        MockHttpServletResponse response; //response object

        Student student = new Student();
        student.setStudent_id(TEST_STUDENT_ID);
        student.setName(TEST_STUDENT_NAME);
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setStatus(TEST_STUDENT_STATUS);
        student.setStatusCode(TEST_STUDENT_STATUS_CODE);

        //defines what moc repo will do 
        given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
        given(studentRepository.save(any(Student.class))).willReturn(student);

        //creation of dto for email
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;
        
        //http post request with body of studentDTO as json
        response = mvc.perform( //rest call to moc server (not real server)
                MockMvcRequestBuilders
                    .post("/student")
                    .content(asJsonString(studentDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //verify status is OK
        assertEquals(200, response.getStatus());
        
        //verify that returned data has non zero primary key &convert string to java class
        StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
        assertNotEquals(0, result.student_id);

        //verify that repo save method was called
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    public void placeStudentHold() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setStudent_id(TEST_STUDENT_ID);
        student.setName(TEST_STUDENT_NAME);
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setStatus(TEST_STUDENT_STATUS);
        student.setStatusCode(TEST_STUDENT_STATUS_CODE);

        given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
        given(studentRepository.save(any(Student.class))).willReturn(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/student/place-hold")
                    .content(asJsonString(studentDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
        assertNotEquals(0, result.status_code);

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    public void releaseStudentHold() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setStudent_id(TEST_STUDENT_ID);
        student.setName(TEST_STUDENT_NAME);
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setStatus(TEST_STUDENT_STATUS);
        student.setStatusCode(TEST_STUDENT_STATUS_CODE);

        given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
        given(studentRepository.save(any(Student.class))).willReturn(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/student/release-hold")
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
        assertNotEquals(1, result.status_code);

        verify(studentRepository).save(any(Student.class));
    }

    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}