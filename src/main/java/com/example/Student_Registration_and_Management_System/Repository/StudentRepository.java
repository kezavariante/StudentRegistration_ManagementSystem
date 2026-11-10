package com.example.Student_Registration_and_Management_System.Repository;

import com.example.Student_Registration_and_Management_System.Model.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private final DataSource dataSource;

    public StudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // INSERT student
    public void save(Student student) {

        String sql = "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";

        String fullName = student.getFirstName() + " " + student.getLastName();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, fullName);
            statement.setInt(2, student.getAge());
            statement.setString(3, student.getEmail());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    student.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not save student", e);
        }
    }

    // SELECT all students
    public List<Student> findAll() {

        String sql = "SELECT * FROM students";

        List<Student> students = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not retrieve students", e);
        }

        return students;
    }

    // SELECT student by ID (Bonus)
    public Optional<Student> findById(int id) {

        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapStudent(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not find student", e);
        }

        return Optional.empty();
    }

    // Convert database record into Student object
    private Student mapStudent(ResultSet resultSet) throws SQLException {

        Student student = new Student();

        student.setId(resultSet.getInt("id"));

        String fullName = resultSet.getString("full_name").trim();
        int spaceIndex = fullName.indexOf(' ');

        if (spaceIndex == -1) {
            student.setFirstName(fullName);
            student.setLastName("");
        } else {
            student.setFirstName(fullName.substring(0, spaceIndex));
            student.setLastName(fullName.substring(spaceIndex + 1));
        }

        student.setAge(resultSet.getInt("age"));
        student.setEmail(resultSet.getString("email"));

        return student;
    }
}
