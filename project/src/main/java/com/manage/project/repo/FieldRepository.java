package com.manage.project.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manage.project.model.Field;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByStatus(Boolean status); // Trả về danh sách sân theo trạng thái
}
