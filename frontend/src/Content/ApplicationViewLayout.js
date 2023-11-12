import NavBar from "../NavBar/NavBar";

import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {Alert, Button, Card} from "react-bootstrap";

const SERVER_URL = "http://localhost:8080";

function ApplicationViewLayout(props) {
    const applicationId = new URLSearchParams(useLocation().search).get("applicationId");
    const [applicationData, setApplicationData] = useState(null);
    const [showSuccess, setShowSuccess] = useState(false);
    const [showError, setShowError] = useState(false);

    useEffect(() => {
        fetchApplicationData();
    }, []);

    const fetchApplicationData = () => {
        fetch(`${SERVER_URL}/API/application/getApplicationById/` + applicationId)
            .then(response => response.json())
            .then(data => {
                setApplicationData(data);
                console.log(data);
            })
            .catch(error => console.error('Error:', error));
    }

    const acceptApplication = () => {
        fetch(`${SERVER_URL}/API/application/acceptApplicationById/` + applicationId)
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

    if (!applicationData) {
        return <div>Loading...</div>;
    }

    return (
        <>
            <div style={{display: 'flex', flexDirection: 'column'}}>
                <NavBar user={props.user} realDate={props.realDate} applicationDate={props.applicationDate}
                        updateApplicationDate={props.updateApplicationDate}>
                </NavBar>

                <div style={{display: 'flex', justifyContent: 'center'}}>
                    <div style={{width: '70rem', marginBottom: '2rem'}}>
                        <h1>Information about Application</h1>

                        {showSuccess && <Alert variant="success">The application was successfully accepted!</Alert>}
                        {showError && <Alert variant="danger">There was an error accepting the application.</Alert>}

                        <Card style={{marginBottom: '2rem'}}>
                            <Card.Header as="h5">Application Information</Card.Header>
                            <Card.Body>
                                <Card.Text>
                                    Attachment: {applicationData.attachment} <br/>
                                    Apply Date: {new Date(applicationData.applyDate).toLocaleString('it-IT')} <br/>
                                    Accepted: {applicationData.accepted ? 'Yes' : 'No'}
                                </Card.Text>
                            </Card.Body>
                        </Card>

                        <div style={{display: 'flex', flexDirection: 'row'}}>
                            <Card style={{width: '50%', marginRight: '1rem'}}>
                                <Card.Header as="h5">Proposal Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        Title: {applicationData.proposal.title} <br/>
                                        Supervisor: {applicationData.proposal.supervisor.name} <br/>
                                        Co-Supervisors: {applicationData.proposal.coSupervisors.join(', ')} <br/>
                                        Keywords: {applicationData.proposal.keywords} <br/>
                                        Type: {applicationData.proposal.type} <br/>
                                        Groups: {applicationData.proposal.groups.join(', ')} <br/>
                                        Description: {applicationData.proposal.description} <br/>
                                        Required Knowledge: {applicationData.proposal.requiredKnowledge} <br/>
                                        Notes: {applicationData.proposal.notes} <br/>
                                        Expiration: {new Date(applicationData.proposal.expiration).toLocaleString('it-IT')}
                                        <br/>
                                        Level: {applicationData.proposal.level} <br/>
                                        CdS: {applicationData.proposal.cdS}
                                    </Card.Text>
                                </Card.Body>
                            </Card>

                            <Card style={{width: '50%'}}>
                                <Card.Header as="h5">Student Information</Card.Header>
                                <Card.Body>
                                    <Card.Text>
                                        Surname: {applicationData.student.surname} <br/>
                                        Name: {applicationData.student.name} <br/>
                                        Gender: {applicationData.student.gender} <br/>
                                        Nationality: {applicationData.student.nationality} <br/>
                                        Email: {applicationData.student.email} <br/>
                                        Degree: {applicationData.student.degree.codDegree} <br/>
                                        Enrollment Year: {applicationData.student.enrollmentYear} <br/>
                                        Grades: {applicationData.student.grades.join(', ')}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </div>


                        <Button variant="success" onClick={acceptApplication}>Accept</Button>
                    </div>
                </div>
            </div>
        </>
    );
}

export default ApplicationViewLayout;