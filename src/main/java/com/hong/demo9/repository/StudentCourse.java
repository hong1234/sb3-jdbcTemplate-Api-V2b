package com.hong.demo9.repository;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentCourse {
    private UUID studentId;
    private UUID courseId;
}
