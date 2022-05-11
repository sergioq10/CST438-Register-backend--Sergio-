package com.cst438.service;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StudentService {

    private @Resource StudentRepository studentRepository;

    public Student create(Student student) throws ServiceException {
        return studentRepository.save(student);
    }

    public Student update(Student student) throws ServiceException {
        return studentRepository.save(student);
    }

}
//ignore