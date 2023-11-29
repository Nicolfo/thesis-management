import React, {useContext, useEffect, useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {Alert, Button, Card, Container, Row, Col, Badge} from "react-bootstrap";
import {AuthContext} from "react-oauth2-code-pkce";

const SERVER_URL = "http://localhost:8081";

function ApplicationViewLayout(props) {

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role==="STUDENT")
        navigate("/notAuthorized");

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

    const statusBadge = () => {
        if (applicationData.status === "PENDING")
            return <Badge bg="primary"> ⦿ PENDING </Badge>
        else if (applicationData.status === "ACCEPTED")
            return <Badge bg="success"> ✓ ACCEPTED </Badge>
        else if (applicationData.status === "REJECTED")
            return <Badge bg="danger"> ✕ REJECTED </Badge>
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
                                    : 'No attachment'}
                                </Card.Text>
                                <Card.Text>
                                    <strong>Apply Date:</strong> {new Date(applicationData.applyDate).toLocaleString('it-IT')}
                                </Card.Text>
                                <Card.Text>
                                    <strong>State:</strong> {statusBadge()}
                                </Card.Text>
                            </Card.Body>
                        </Card>

                    <Row>
                        <Col md={6}>
                            <Card style={{marginBottom: '2rem'}}>
                                <Card.Header as="h5">Proposal Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        <strong>Title:</strong> {applicationData.proposal.title}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Supervisor:</strong> {applicationData.proposal.supervisor.name + " " + applicationData.proposal.supervisor.surname}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Co-Supervisors:</strong> {applicationData.proposal.coSupervisors.map(supervisor => supervisor.name + " " + supervisor.surname).join(', ')}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Keywords:</strong> {applicationData.proposal.keywords}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Type:</strong> {applicationData.proposal.type}
                                    </Card.Text>
                                    <Card.Text>
                                            <strong>Groups:</strong> {applicationData.proposal.groups.map(group => group.name).join(', ')}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Description:</strong> {applicationData.proposal.description}
                                    </Card.Text>
                                    <Card.Text>
                                            <strong>Required Knowledge:</strong> {applicationData.proposal.requiredKnowledge}
                                    </Card.Text>
                                    <Card.Text>
                                        {applicationData.proposal.notes && <><div><strong>Notes:</strong> {applicationData.proposal.notes}</div></> }
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Expiration:</strong> {new Date(applicationData.proposal.expiration).toLocaleString('it-IT')}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Level:</strong> {applicationData.proposal.level}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>CdS:</strong> {applicationData.proposal.cdS}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={6}>
                            <Card style={{marginBottom: '2rem'}}>
                            <Card.Header as="h5">Student Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        <strong>Surname:</strong> {applicationData.student.surname}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Name:</strong> {applicationData.student.name}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Gender:</strong> {applicationData.student.gender}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Nationality:</strong> {applicationData.student.nationality}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Email:</strong> {applicationData.student.email}
                                    </Card.Text>
                                    <Card.Text>
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Enrollment Year:</strong> {applicationData.student.enrollmentYear}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Average grades:</strong> {applicationData.studentAverageGrades}
                                    </Card.Text>
                                    <Card.Text>
                                        <strong>Grades:</strong>
                                        {studentGradesData.length > 0 ? (

                                            <ul>
                                                {studentGradesData.map((grade, index) => (
                                                    <li key={index}>{grade.titleCourse}: {grade.grade}</li>
                                                ))}
                                            </ul>
                                        ) : (
                                            "No grades available."
                                        )}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>

                    <Row>
                    { applicationData.status==="PENDING" ? (
                        <Col md={9}>
                            <Button variant="outline-success" style={{marginBottom: "1rem"}} onClick={() => acceptApplication()}>Accept</Button>
                            {" "}
                            <Button variant="outline-dark" style={{marginBottom: "1rem"}} onClick={() => rejectApplication()}>Reject</Button>
                        </Col>
                    ) : applicationData.status==="ACCEPTED" ? (
                        <Col md={9}>
                            <Button variant="outline-info" style={{marginBottom: "1rem"}} onClick={() => changeApplicationState("PENDING")}>Update State to Pending</Button>
                            {" "}
                            <Button variant="outline-dark" style={{marginBottom: "1rem"}} onClick={() => changeApplicationState("REJECTED")}>Update State to Reject</Button>
                        </Col>
                    ) : (
                        <Col md={9}>
                            <Button variant="outline-success" style={{marginBottom: "1rem"}} onClick={() => changeApplicationState("ACCEPTED")}>Update State to Accept</Button>
                            {" "}
                            <Button variant="outline-info" style={{marginBottom: "1rem"}} onClick={() => changeApplicationState("PENDING")}>Update State to Pending</Button>
                        </Col>
                    )
                    }

                    <Col style={{textAlign: "end"}}>
                        <Button variant="outline-danger" style={{marginBottom: "1rem"}} onClick={() => navigate('/teacher/application/browse')}> Go back </Button>
                    </Col>
                    </Row>
                </Col>
            </Row>
        </Container>
    );
}

export default ApplicationViewLayout;