import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {Alert, Button, Card, Container, Row, Col} from "react-bootstrap";

const SERVER_URL = "http://localhost:8080";

function ApplicationViewLayout(props) {
    const applicationId = new URLSearchParams(useLocation().search).get("applicationId");
    const [applicationData, setApplicationData] = useState(null);
    const [studentGradesData, setStudentGradesData] = useState(null);
    const [showSuccess, setShowSuccess] = useState(false);
    const [showError, setShowError] = useState(false);

    useEffect(() => {

        fetchApplicationData();
    }, [props.user]);

    const fetchApplicationData = () => {
        if(props.user && props.user.token)
        return fetch(`${SERVER_URL}/API/application/getApplicationById/` + applicationId,{
            method: 'GET',
            headers:{
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${props.user.token}`,
            }
        })
            .then(response => response.json())
            .then(data => {
                setApplicationData(data);

                fetchStudentGradesData(data.student.id);
            })
            .catch(error => console.error('Error:', error));
    }

    const fetchStudentGradesData = (studentId) => {
        if(props.user && props.user.token)
        fetch(`${SERVER_URL}/API/career/getByStudent/` + studentId,{
            method: 'GET',
            headers:{
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${props.user.token}`,
            }
        })
            .then(response => response.json())
            .then(data => {
                setStudentGradesData(data);

            })
            .catch(error => console.error('Error:', error));
    }

    const acceptApplication = () => {
        if(props.user && props.user.token)
        fetch(`${SERVER_URL}/API/application/acceptApplicationById/` + applicationId,{
            method: 'GET',
            headers:{
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${props.user.token}`,
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    setShowSuccess(true);
                    fetchApplicationData();
                } else {
                    setShowError(true);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                setShowError(true);
            });
    }

    const changeApplicationState = (newState) => {
        if(props.user && props.user.token)
            fetch(`${SERVER_URL}/API/application/changeApplicationStateById/` + applicationId + '/' + newState,{
                method: 'GET',
                headers:{
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${props.user.token}`,
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data) {
                        setShowSuccess(true);
                        fetchApplicationData();
                    } else {
                        setShowError(true);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    setShowError(true);
                });
    }

    const rejectApplication = () => {
        if(props.user && props.user.token)
        fetch(`${SERVER_URL}/API/application/rejectApplicationById/` + applicationId,{
            method: 'GET',
            headers:{
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${props.user.token}`,
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    setShowSuccess(true);
                    fetchApplicationData();
                } else {
                    setShowError(true);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                setShowError(true);
            });
    }

    if (!applicationData || !studentGradesData) {
        return <div>Loading...</div>;
    }

    return (
        <Container>
            <Row className="justify-content-md-center">
                <Col md="auto">
                    <h1>Information about Application</h1>

                    {showSuccess && <Alert variant="success">The application was successfully updated!</Alert>}
                    {showError && <Alert variant="danger">There was an error updating the application.</Alert>}

                    <Card style={{marginBottom: '2rem'}}>
                            <Card.Header as="h5">Application Information</Card.Header>
                            <Card.Body>
                                <Card.Text>
                                    <strong>Attachment:</strong> {applicationData.attachmentId ?
                                    <a href={`${SERVER_URL}/API/getFile/${applicationData.attachmentId}`}
                                       download>Download</a>
                                    : 'No attachment'} <br/>
                                    <strong>Apply Date:</strong> {new Date(applicationData.applyDate).toLocaleString('it-IT')} <br/>
                                    <strong>State:</strong> {applicationData.status}
                                </Card.Text>
                            </Card.Body>
                        </Card>

                    <Row>
                        <Col md={6}>
                            <Card style={{marginBottom: '2rem'}}>
                                <Card.Header as="h5">Proposal Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        <div><strong>Title:</strong> {applicationData.proposal.title} </div>
                                        <div><strong>Supervisor:</strong> {applicationData.proposal.supervisor.name + " " + applicationData.proposal.supervisor.surname}</div>
                                        <div><strong>Co-Supervisors:</strong> {applicationData.proposal.coSupervisors.map(supervisor => supervisor.name + " " + supervisor.surname).join(', ')}</div>
                                        <div><strong>Keywords:</strong> {applicationData.proposal.keywords} </div>
                                        <div><strong>Type:</strong> {applicationData.proposal.type}</div>
                                        <div><strong>Groups:</strong> {applicationData.proposal.groups.map(group => group.name).join(', ')}</div>
                                        <div><strong>Description:</strong> {applicationData.proposal.description}</div>
                                        <div><strong>Required Knowledge:</strong> {applicationData.proposal.requiredKnowledge} </div>
                                        {applicationData.proposal.notes && <><div><strong>Notes:</strong> ${applicationData.proposal.notes}</div></> }
                                        <div><strong>Expiration:</strong> {new Date(applicationData.proposal.expiration).toLocaleString('it-IT')}</div>
                                        <div><strong>Level:</strong> {applicationData.proposal.level} </div>
                                        <div><strong>CdS:</strong> {applicationData.proposal.cdS}</div>
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={6}>
                            <Card style={{marginBottom: '2rem'}}>
                            <Card.Header as="h5">Student Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        <div><strong>Surname:</strong> {applicationData.student.surname} </div>
                                        <div><strong>Name:</strong> {applicationData.student.name} </div>
                                        <div><strong>Gender:</strong> {applicationData.student.gender} </div>
                                        <div><strong>Nationality:</strong> {applicationData.student.nationality} </div>
                                        <div><strong>Email:</strong> {applicationData.student.email} </div>
                                        <div><strong>Degree:</strong> {applicationData.student.degree.codDegree} </div>
                                        <div><strong>Enrollment Year:</strong> {applicationData.student.enrollmentYear} </div>
                                        <div><strong>Grades:</strong>
                                        {studentGradesData.length > 0 ? (

                                            <ul>
                                                {studentGradesData.map((grade, index) => (
                                                    <li key={index}>{grade.titleCourse}: {grade.grade}</li>
                                                ))}
                                            </ul>
                                        ) : (
                                            "No grades available."
                                        )}</div>
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>


                        {applicationData.status==="PENDING" ? (
                            <>
                                <Button variant="success" onClick={() => acceptApplication()}>Accept</Button>
                                <Button variant="inline" color={"error"} onClick={() => rejectApplication()}>Reject</Button>
                            </>
                        ) : (
                            <>
                                <button type="button" className="btn btn-outline-success" onClick={() => changeApplicationState("ACCEPTED")}>Update State to Accept</button>
                                <button type="button" className="btn btn-outline-info" onClick={() => changeApplicationState("PENDING")}>Update State to Pending</button>
                                <button type="button" className="btn btn-outline-danger" onClick={() => changeApplicationState("REJECTED")}>Update State to Reject</button>
                            </>
                        )}
                    </Col>
                </Row>
        </Container>
    );
}

export default ApplicationViewLayout;