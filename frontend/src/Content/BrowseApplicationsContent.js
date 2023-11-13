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
    const [userInfo, setUserInfo] = useState('');


    useEffect(() => {
        const getTitleByApplicationId = async () => {
            const title = await API.getTitleByApplicationId(props.user.token, props.application.id);
            setTitle(title);
        }
        getTitleByApplicationId();
    }, []);

    useEffect(() => {
        const getStudentInfo = async () => {
            const userInfo = await API.getStudentInfo(props.user.token, props.application.student_id);
            setUserInfo(userInfo);
        }
        getStudentInfo();
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
            <td>{ userInfo }</td>
            <td>MEDIA VOTI</td>
            <td>
                <Button variant="success" onClick={() => handleAccept(props.application.id)}>Accept</Button>{' '}
                <Button variant="danger" onClick={() => handleReject(props.application.id)}>Reject</Button>{' '}
                <Button onClick={() => handleViewPDF(props.application.pdfUrl)}>View CV</Button>
            </td>
        </tr>
    )
}

export default BrowseApplicationsContent;
