package com.manage.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.project.model.Field;
import com.manage.project.repo.FieldRepository;

@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    public Field getFieldById(Long id) {  
        return fieldRepository.findById(id).orElse(null);
    }

    public void saveField(Field field) {
        fieldRepository.save(field);
    }

    public void deleteField(Long id) {  // Thay đổi kiểu dữ liệu ở đây
        fieldRepository.deleteById(id);
    }
}
