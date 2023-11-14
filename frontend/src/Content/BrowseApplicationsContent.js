import { useState } from "react";
import { useEffect } from "react";
import API from "../API/Api";
import {Button, Table} from "react-bootstrap";
import dayjs from 'dayjs';


function BrowseApplicationsContent(props) {

    const [applications, setApplications] = useState([]);

    useEffect(() => {
        const getApplicationsByProf = async () => {
            try {
                const applications = await API.getApplicationsByProf(props.user.token);
                setApplications(applications);
            } catch (error) {
                console.error("Error fetching applications:", error);
            }
        };

        getApplicationsByProf();
    }, [props.user.token]);

    return (
        <>
            <h1>Applications</h1>
            <Table striped hover>
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Apply date</th>
                    <th>Student</th>
                    <th>Average grades</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                { applications.map(application => <ApplicationRow application={application} user={props.user}/>) }
                </tbody>
            </Table>
        </>
    );
}


function ApplicationRow(props) {

    const handleAccept = (id) => {
    };

    const handleReject = (id) => {
    };

    const handleViewPDF = (pdfUrl) => {
    };

    return (
        <tr>
            <td>{ props.application.title }</td>
            <td>{ dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss') }</td>
            <td>{ props.application.studentName} {props.application.studentSurname}</td>
            <td>{ props.application.studentAverageGrades }</td>
            <td>
                <Button variant="success" onClick={() => handleAccept(props.application.id)}>Accept</Button>{' '}
                <Button variant="danger" onClick={() => handleReject(props.application.id)}>Reject</Button>{' '}
                <Button onClick={() => handleViewPDF(props.application.pdfUrl)}>View CV</Button>
            </td>
        </tr>
    )
}

export default BrowseApplicationsContent;
