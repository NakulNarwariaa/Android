# Abstract 
---
A complete Android application that explores Networking and user interfaces, and has following features : 
*	Android application for students to register for courses offered in SDSU. 
*	Login functionality and Dashboard for user which lets user see their registered courses and schedule. 
*	Filter functionality for course search for efficient results.
* Saved user credentials for better user experience.


# App Description
---
This application is a lighter version of SDSU's webportal( course related functionality).
The app will allow users to register for classes. A student needs to enter their personal information such as
first name, last name, SDSU red id, email address and a password. The user can
register for up to three courses and add themselves to waitlists for courses that are full. The
student can also drop classes that they are registered for or drop themselves from waitlists.
The app will allow students to select from a list of courses filtered via major (or subject like
Computer Science, Physics, etc), level of the course (lower division - 100 & 200 level courses,
upper division courses 300, 400, 500 level courses, and graduate courses (500 level and higher),
and time of day (classes starting after a given time and/or ending before a given time).
The app should display the courses the student is enrolled in and the courses that they are
waitlisted for. The app should store the students personal data on the device so that they do
not have to enter the data each time they use the app.


# Server Interaction Overview
---
A brief overview of the commands to the server are given below which are used in this application.
* subjectlist Returns a list of majors or subjects. Includes the name of the subject, the college
it is in allowing the classes to be grouped by college.
* classidslist Returns a list of courses based on subject(s), level, and time. Returns just the
ids of the courses.
* classdetails Given a course id returns information about the course: title, instructor, meeting
time and place, etc.
* addstudent Given the personal information about the student added the student to the
server so they can add classes.
* registerclass Given a course id, student’s red id and password registers the student in the
class.
* waitlistclass If a class is full given a course id, student’s red id and password adds the
student to the wait list of a class. If a student drops the course the server does not enroll a
student from the waitlist in the course.
* unregisterclass Drops a student from the course.
* unwaitlistclass Removes the student from a course waitlist.
* resetstudent Drops the student from all courses and removes them from all waitlists.
