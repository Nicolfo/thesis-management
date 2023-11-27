import { useState } from "react";
import { useEffect } from "react";
import API from "../API/Api";
import {Button, Table} from "react-bootstrap";
import dayjs from 'dayjs';
import {useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";


function BrowseApplicationsContent(props) {

    const navigate = useNavigate();

    if(!props.user || props.user.role !== "TEACHER") {
        navigate("/notAuthorized");
    }

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
            <div className="bordered-box">
                <h1>My application proposals</h1>
                <hr className="separator" />
                <Table striped hover className="mb-4">
                    <thead>
                    <tr>
                        <th className="col-4">Title</th>
                        <th className="d-none d-md-table-cell col-2">Apply date</th>
                        <th className="d-none d-md-table-cell col-2">Student</th>
                        <th className="d-none d-md-table-cell col-2">Average grades</th>
                        <th className="col-1">Status</th>
                        <th className="col-1">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {applications.map((application) => (
                        <ApplicationRow key={application.id} application={application} user={props.user} />
                    ))}
                    </tbody>
                </Table>
            </div>
        </>
    );
}

function ApplicationRow(props) {
    const navigate = useNavigate();
    const handleViewInfo = (id) => {
        navigate("/teacher/application/view?applicationId=" + props.application.id);
    };

    return (
        <tr>
            <td><strong>{props.application.proposalTitle}</strong></td>
            <td className="d-none d-md-table-cell">{dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss')}</td>
            <td className="d-none d-md-table-cell">{props.application.studentName} {props.application.studentSurname}</td>
            <td className="d-none d-md-table-cell">{props.application.studentAverageGrades}</td>
            <td>{props.application.status}</td>
            <td>
                <Button classname="d-flex align-items-center" onClick={() => handleViewInfo(props.application.id)} style={{ display: 'flex', alignItems: 'center' }}>
                    <span className="d-none d-md-table-cell">View info </span>
                    <FontAwesomeIcon icon={"chevron-right"} />
                </Button>
            </td>
        </tr>
    );
}

export default BrowseApplicationsContent;
