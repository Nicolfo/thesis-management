import {useContext, useState} from "react";
import { useEffect } from "react";
import API from "../API/Api";
import {Badge, Button, Table} from "react-bootstrap";
import dayjs from 'dayjs';
import {useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {AuthContext} from "react-oauth2-code-pkce";


function BrowseApplicationsContent(props) {

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role==="STUDENT")
        navigate("/notAuthorized");


    const [applications, setApplications] = useState([]);

    useEffect(() => {
            if(!props.user || props.user.role !== "TEACHER")
                return;
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
                <h1 style={{"textAlign": "center"}}>My application proposals</h1>
                <hr className="separator" />
                <Table responsive striped hover className="mb-4">
                    <thead>
                        { applications.length > 0 ?
                            <tr>
                                <th className="col-4">Title</th>
                                <th className="d-none d-md-table-cell col-2">Apply date</th>
                                <th className="d-none d-md-table-cell col-2">Student</th>
                                <th className="d-none d-md-table-cell col-2">Average grades</th>
                                <th className="col-1">Status</th>
                                <th className="col-1">Action</th>
                            </tr>
                            :
                            <tr>
                                <th style={{"textAlign": "center"}}>
                                    You have no applications yet
                                </th>
                            </tr>
                        }
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

    const statusBadge = () => {
        if (props.application.status === "PENDING")
            return <Badge bg="primary" className="mt-2"> ⦿ PENDING </Badge>
        else if (props.application.status === "ACCEPTED")
            return <Badge bg="success" className="mt-2"> ✓ ACCEPTED </Badge>
        else if (props.application.status === "REJECTED")
            return <Badge bg="danger" className="mt-2"> ✕ REJECTED </Badge>
    }

    const handleViewInfo = (id) => {
        navigate("/teacher/application/view?applicationId=" + props.application.id);
    };

    return (
        <tr>
            <td><strong>{props.application.proposalTitle}</strong></td>
            <td className="d-none d-md-table-cell">{dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss')}</td>
            <td className="d-none d-md-table-cell">{props.application.studentName} {props.application.studentSurname}</td>
            <td className="d-none d-md-table-cell">{props.application.studentAverageGrades}</td>
            <td>{statusBadge()}</td>
            <td>
                <Button classname="d-flex align-items-center" onClick={() => handleViewInfo(props.application.id)} style={{ display: 'flex', alignItems: 'center' }}>
                    <span className="d-none d-md-table-cell">Info </span>
                    <FontAwesomeIcon className="ms-1" icon={"chevron-right"} />
                </Button>
            </td>
        </tr>
    );
}

export default BrowseApplicationsContent;
