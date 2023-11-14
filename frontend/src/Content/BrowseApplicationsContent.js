import { useState } from "react";
import { useEffect } from "react";
import API from "../API/Api";
import {Button, Table} from "react-bootstrap";
import dayjs from 'dayjs';


function BrowseApplicationsContent(props) {

    const [applications, setApplications] = useState([]);

    useEffect(() => {
        const getApplications = async () => {
            try {
                console.log("TOKEN", props.user.token);
                const applications = await API.getAllApplications(props.user.token);
                setApplications(applications);
            } catch (error) {
                console.error("Error fetching applications:", error);
            }
        };

        getApplications();
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

    const [title, setTitle] = useState('');
    const [studentFullName, setStudentFullName] = useState('');
    const [averageMarks, setAverageMarks] = useState(0);


    useEffect(() => {
        const getTitleByProposalId = async () => {
            //console.log("proposalId", props.application.proposalId);
            //console.log("token", props.user.token);
            const title = await API.getTitleByProposalId(props.user.token, props.application.proposalId);
            setTitle(title);
        }
        getTitleByProposalId();
    }, []);

    useEffect(() => {
        const getStudentFullName = async () => {
            //console.log("studentId", props.application.studentId);
            //console.log("token", props.user.token);
            const userInfo = await API.getStudentFullName(props.user.token, props.application.studentId);
            setStudentFullName(userInfo);
        }
        getStudentFullName();
    }, []);

    useEffect(() => {
        const getAverageMarks = async () => {
            const averageMarks = await API.getAverageMarks(props.user.token, props.application.studentId);
            setAverageMarks(averageMarks);
        }
        getAverageMarks();
    }, []);

    const handleAccept = (id) => {
    };

    const handleReject = (id) => {
    };

    const handleViewPDF = (pdfUrl) => {
    };

    return (
        <tr>
            <td>{ title }</td>
            <td>{ dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss') }</td>
            <td>{ studentFullName }</td>
            <td>{ averageMarks }</td>
            <td>
                <Button variant="success" onClick={() => handleAccept(props.application.id)}>Accept</Button>{' '}
                <Button variant="danger" onClick={() => handleReject(props.application.id)}>Reject</Button>{' '}
                <Button onClick={() => handleViewPDF(props.application.pdfUrl)}>View CV</Button>
            </td>
        </tr>
    )
}

export default BrowseApplicationsContent;
