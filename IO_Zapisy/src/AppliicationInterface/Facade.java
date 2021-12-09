package AppliicationInterface;

import java.util.LinkedList;
import java.util.List;

import CourseData.Classroom;
import CourseData.Course;
import CourseData.Enrollment;
import CourseData.GroupFactory;
import PersonData.PersonFactory;

public class Facade {
private List<Enrollment> enrollments;
private List<Classroom> classrooms;
private List<Course> courses;
private GroupFactory groupFactory;
private PersonFactory personFactory;

public boolean createCourse(String [] data) {
	Course course = new Course(data);
	this.courses.add(course);
	return true;
}

public Course findCourse(String name) {
	return null;
}

public boolean createGroup(String [] data) {
	return groupFactory.createGroup(data);
}

public List<Course> getCourses(){
	return this.courses;
}
public Facade() {
	this.enrollments = new LinkedList();
	this.classrooms = new LinkedList();
	this.courses = new LinkedList();
	this.groupFactory = new GroupFactory();
	this.personFactory = new PersonFactory();
	
}
}
