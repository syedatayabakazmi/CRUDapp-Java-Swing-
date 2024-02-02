import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class crud {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/studentdb?useSSL=false";

    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    public static void main(String[] args) {
        JFrame frame = new JFrame("STUDENT DETAILS");
        frame.setVisible(true);
        frame.setLayout(new FlowLayout());
        frame.setSize(350, 400);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);

        JLabel l1 = new JLabel("STUDENT NAME");
        JLabel l2 = new JLabel("STUDENT AGE");
        JLabel l3 = new JLabel("STUDENT DEPARTMENT");
        JLabel l4 = new JLabel("CGPA");
        JLabel l5 = new JLabel("STUDENT ID");

        JTextField search = new JTextField();
        search.setColumns(20);
        JTextField stid = new JTextField();
        stid.setColumns(15);
        JTextField name = new JTextField();
        name.setColumns(15);
        JTextField age = new JTextField();
        age.setColumns(15);
        JTextField dep = new JTextField();
        dep.setColumns(15);
        JTextField cgpa = new JTextField();
        cgpa.setColumns(23);

        JButton add = new JButton("ADD");
        add.setBackground(Color.red);
        JButton update = new JButton("UPDATE");
        update.setBackground(Color.red);
        JButton delete = new JButton("DELETE");
        delete.setBackground(Color.red);
        JButton srch = new JButton("SEARCH");
        srch.setBackground(Color.red);

        frame.add(search);
        frame.add(srch);

        frame.add(l5);
        frame.add(stid);
        
        frame.add(l1);
        frame.add(name);

        frame.add(l2);
        frame.add(age);

        frame.add(l3);
        frame.add(dep);

        frame.add(l4);
        frame.add(cgpa);

        frame.add(add);
        frame.add(update);
        frame.add(delete);

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            createTableIfNotExists(connection);

            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                    	 int studentid = Integer.parseInt(stid.getText());
                        String studentName = name.getText();
                        int studentAge = Integer.parseInt(age.getText());
                        String studentDepartment = dep.getText();
                        double studentCGPA = Double.parseDouble(cgpa.getText());

                        insertStudent(connection, studentid, studentName, studentAge, studentDepartment, studentCGPA);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            srch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String searchText = search.getText();
                        Student student = searchStudent(connection, searchText);

                        if (student != null) {
                        	 stid.setText(String.valueOf(student.getStid()));
                            name.setText(student.getName());
                            age.setText(String.valueOf(student.getAge()));
                            dep.setText(student.getDepartment());
                            cgpa.setText(String.valueOf(student.getCGPA()));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            update.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                    	int studentid = Integer.parseInt(stid.getText());
                        String studentName = name.getText();
                        int studentAge = Integer.parseInt(age.getText());
                        String studentDepartment = dep.getText();
                        double studentCGPA = Double.parseDouble(cgpa.getText());

                        updateStudent(connection,studentid, studentName, studentAge, studentDepartment, studentCGPA);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                    	int studentid = Integer.parseInt(stid.getText());
                        deleteStudent(connection, studentid);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (stid INT PRIMARY KEY, name VARCHAR(255), age INT, department VARCHAR(255), cgpa DOUBLE)";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.executeUpdate();
        }
    }



    private static Student searchStudent(Connection connection, String searchText) throws SQLException {
        String searchSQL = "SELECT * FROM students WHERE stid LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(searchSQL)) {
            statement.setString(1, searchText);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Student(
                    		resultSet.getInt("stid"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("department"),
                            resultSet.getDouble("cgpa")
                    );
                }
            }
        }
        return null;
    }
    private static void insertStudent(Connection connection, int stid, String name, int age, String department, double cgpa) throws SQLException {
        String insertSQL = "INSERT INTO students (stid, name, age, department, cgpa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setInt(1, stid);
            statement.setString(2, name);
            statement.setInt(3, age);
            statement.setString(4, department);
            statement.setDouble(5, cgpa);
            statement.executeUpdate();
        }
    }

    private static void updateStudent(Connection connection, int stid, String name, int age, String department, double cgpa) throws SQLException {
        String updateSQL = "UPDATE students SET name=?, age=?, department=?, cgpa=? WHERE stid=?";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, department);
            statement.setDouble(4, cgpa);
            statement.setInt(5, stid);
            statement.executeUpdate();
        }
    }

    private static void deleteStudent(Connection connection, int stid) throws SQLException {
        String deleteSQL = "DELETE FROM students WHERE stid=?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setInt(1, stid);
            statement.executeUpdate();
        }
    }

    private static class Student {
    	private int stid;
        private String name;
        private int age;
        private String department;
        private double cgpa;

        public Student(int stid,String name, int age, String department, double cgpa) {
            this.stid=stid;
        	this.name = name;
            this.age = age;
            this.department = department;
            this.cgpa = cgpa;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getDepartment() {
            return department;
        }

        public double getCGPA() {
            return cgpa;
        }

		public int getStid() {
			return stid;
		}

		public void setStid(int stid) {
			this.stid = stid;
		}
    }
}
