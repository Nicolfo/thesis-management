# Thesis management

## Application setup and start
- Run **Docker Desktop**
- Start the local Postgres instance with the command: `docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -d postgres`
- Run the **SpringBoot Application**
- Navigate to `thesis-management/frontend` and run `npm i` and then `npm start`
- Connect to the application via browser on `localhost:3000`

## API Server

### Users
- POST `/API/register`
  - request body content: an object containing information about the user to register
- POST `/API/login`
  - request body content: an object containing the credentials to perform the login
  - response body content: an object containing the logged in user information and the user's access token

### Students
- GET `/API/student/getAverageMarks/<studentId>`
  - response body content: a double representing the student's average mark
- GET `/API/student/getNameById/<studentId>`
  - response body content: a string containing the name and surname of the student with the given id
  
### Teachers
- GET `/API/teacher/getAll`
  - response body content: an array containing all the teachers
- GET `/API/teacher/getByEmail/<email>`
  - response body content: an object containing information about the teacher with the given email
- GET `API/teacher/getById/<id>`
  - response body content: an object containing information about the teacher with the given id

### Proposals
- GET `/API/proposal/getAll`
  - response body content: an array containing all the proposals
- GET `/API/proposal/getByProf`
  - response body content: if logged in as a teacher, returns all proposals with the logged in teacher as supervisor
- POST `/API/proposal/insert`
  - request body content: an object containing information about the proposal to insert
- PUT `/API/proposal/update`
  - request body content: an object containing information about the proposal to update
- PUT `/API/proposal/search`
  - request body content: an object containing the filters to search proposals
  - response body content: an array containing the proposals filtered with the info in the request body
  
### Applications
- GET `/API/application/getByProf`
  - response body content: if logged in as a teacher, returns all applications for proposals with the logged in teacher as supervisor
- GET `/API/application/getByStudent`
  - response body content: if logged in as a student, returns all applications submitted by the logged in student
- POST `/API/insert`
  - request body content: an object containing information about the application to insert
- GET `/API/application/getApplicationById/<applicationId>`
  - response body content: an object containing information about the application with the given id
- GET `/API/application/acceptApplicationById/<applicationId>`
  - to accept the application with the given id, as a teacher
- GET `/API/application/rejectApplicationById/<applicationId>`
  - to reject the application with the given id, as a teacher 

### Groups
- GET `/API/group/getAll`
  - response body content: an array containing all the groups
  
### Degrees
- GET `/API/Degree/getAllNames`
  - response body content: a list of all the degrees' names
  
### Careers
- GET `/API/career/getByStudent/<studentId>`
  - response body content: a list containing information about all the exams passed by the student with the corresponding id
  
### Attachments
- POST `/API/uploadFile`
  - request body content: an object containing information about the file to upload
  - request part: file to upload
  - response body content: an object containing information about the uploaded file
- GET `/API/getFile/<id>`
  - response content: the file associated to the specified id

## Users Credentials

- Teacher:
  - Email: nicolfo2@hotmail.it
  - Password: provaa
- Student:
  - Email: nicolfo3@hotmail.it
  - Password: provaa
