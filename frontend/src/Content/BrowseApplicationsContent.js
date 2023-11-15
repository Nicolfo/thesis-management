import { useState } from "react";
import { useEffect } from "react";
import API from "../API/Api";
import {Button, Table} from "react-bootstrap";
import dayjs from 'dayjs';
import {useNavigate} from "react-router-dom";


function BrowseApplicationsContent(props) {

    const [applications, setApplications] = useState([]);

    useEffect(() => {
        const getApplicationsByProf = async () => {
            if(props.user && props.user.token)
            try {
                const applications = await API.getApplicationsByProf(props.user.token);
                setApplications(applications);
            } catch (error) {
                console.error("Error fetching applications:", error);
            }
        };

        getApplicationsByProf();
    }, [props.user]);

    return (
        <>
        <div className="bordered-box mt-5 pe-3 ms-5 me-5">
            <h1>Applications</h1>
            <hr className="separator" />
                <Table striped hover className="mb-4">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Apply date</th>
                        <th>Student</th>
                        <th>Average grades</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    { applications.map(application => <ApplicationRow key={application.id} application={application} user={props.user}/>) }
                    </tbody>
                </Table>
        </div>
        </>
    );
}


function ApplicationRow(props) {
    const navigate=useNavigate()
    const handleViewInfo = (id) => {
        navigate("/application/view?applicationId="+props.application.id);
    };

    return (
        <tr>
            <td>{ props.application.proposalTitle }</td>
            <td>{ dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss') }</td>
            <td>{ props.application.studentName} {props.application.studentSurname}</td>
            <td>{ props.application.studentAverageGrades }</td>
            <td>{ props.application.status}</td>
            <td>
                <Button onClick={() => handleViewInfo(props.application.id)}>View info</Button>
            </td>
        </tr>
    )
}

export default BrowseApplicationsContent;
