package com.hong.demo9.repository;

import lombok.AllArgsConstructor;
// import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import java.sql.Types;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

import com.hong.demo9.exception.ResourceNotFoundException;
import com.hong.demo9.exception.ReadyExistException;


@AllArgsConstructor
@Repository
public class StudentRepository {

    // private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void findById(UUID studentId){
        String sql = "SELECT * FROM STUDENT WHERE STUDENT_ID = :studentId";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", studentId);

        if(!jdbcTemplate.queryForRowSet(sql, parameters).first())
            throw new ResourceNotFoundException("Student with Id="+studentId.toString()+" not found");
    }

    public void findAssign(UUID studentId, UUID courseId){
        String sql = "SELECT * FROM STUDENT_COURSE WHERE STUDENT_ID = :studentId AND COURSE_ID = :courseId";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", studentId)
                .addValue("courseId", courseId)
                ;
        if(jdbcTemplate.queryForRowSet(sql, parameters).first())
            throw new ReadyExistException("Assign studentId="+studentId.toString()+" courseId="+courseId.toString()+" ready exist.");
    }

    public Student getStudentById(UUID studentId) {
        String sqlQuery = """
                SELECT S.STUDENT_ID, S.NAME, S.AGE, C.COURSE_ID, C.DESCRIPTION
                FROM STUDENT S
                LEFT JOIN STUDENT_COURSE SC ON S.STUDENT_ID = SC.STUDENT_ID
                LEFT JOIN COURSE C ON SC.COURSE_ID = C.COURSE_ID
                WHERE S.STUDENT_ID = :studentId
           """;
        // return jdbcTemplate.query(sqlQuery, new Object[]{studentId}, new int[]{Types.INTEGER}, new ResultSetExtractor<Student>() {

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", studentId);

        return jdbcTemplate.query(sqlQuery, parameters, new ResultSetExtractor<Student>() {
            @Override
            public Student extractData(ResultSet rs) throws SQLException, DataAccessException {
                Student student = null;
                // Map<String, Course> courses = new HashMap<>();
                List<Course> courses = new ArrayList<>();

                while (rs.next()) {
                    if (student == null) {
                        student = new Student();
                        student.setStudentId(UUID.fromString(rs.getString("STUDENT_ID")));
                        student.setName(rs.getString("NAME"));
                        student.setAge(rs.getInt("AGE"));
                    }

                    if (rs.getString("COURSE_ID") != null){
                        Course course = new Course();
                        course.setCourseId(UUID.fromString(rs.getString("COURSE_ID")));
                        course.setDescription(rs.getString("DESCRIPTION"));
                        courses.add(course);
                        // courses.put(course.getUuid(), course);
                    }
                }
                student.setCourses(courses);

                return student;
            }

        });
    }

    public Iterable<Student> getAllStudents() {
        String sqlQuery = """
		    SELECT S.STUDENT_ID, S.NAME, S.AGE, C.COURSE_ID, C.DESCRIPTION
		    FROM STUDENT S
		    LEFT JOIN STUDENT_COURSE SC ON S.STUDENT_ID = SC.STUDENT_ID
		    LEFT JOIN COURSE C ON SC.COURSE_ID = C.COURSE_ID
		    ORDER BY S.STUDENT_ID ASC
		""";

        return jdbcTemplate.query(sqlQuery, new StudentsExtractor());

//        return jdbcTemplate.query(sqlQuery, new ResultSetExtractor<List<Student>>() {
//            @Override
//            public List<Student> extractData(ResultSet rs) throws SQLException, DataAccessException {
//                while(rs.next()) {
//                   // ...
//                }
//                return new ArrayList<>(studentMap.values());
//            }
//        });

    }

    public void assignCourse(UUID studentId, UUID courseId) {
        String sql = "INSERT INTO STUDENT_COURSE (STUDENT_ID, COURSE_ID) VALUES (:studentId, :courseId)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", studentId)
                .addValue("courseId", courseId)
                ;
        jdbcTemplate.update(sql, parameters);
    }

    public Student saveStudent(Student student) {
        String sql = "INSERT INTO STUDENT (student_id, name, age) VALUES (:studentId, :name, :age)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", student.getStudentId())
                .addValue("name", student.getName())
                .addValue("age", student.getAge())
                ;

        jdbcTemplate.update(sql, parameters);

        if (student.getCourses() != null) {
            for (Course course : student.getCourses()) {
                assignCourse(student.getStudentId(), course.getCourseId());
            }
        }

        // return student;
        return getStudentById(student.getStudentId());
    }

    public void deleteById(UUID studentId) {
        String sql = "DELETE FROM STUDENT WHERE STUDENT_ID = :studentId";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("studentId", studentId);
        jdbcTemplate.update(sql, parameters);
    }

}
