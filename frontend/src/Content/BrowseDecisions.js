import {useContext, useEffect, useState} from "react";
import {Badge, Card, Col, Row, Table} from "react-bootstrap";
import API from "../API/Api";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";

function BrowseDecisions(props) {
    const {token} = useContext(AuthContext);

    const [applications, setApplications] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        if (!token)
            navigate("/notAuthorized");
        if (props.user && props.user.role === "TEACHER")
            navigate("/notAuthorized");
        const getApplicationsByStudent = async () => {
            if (!props.user)
                return;

            try {
                let applications = await API.getApplicationsByStudent(props.user.token);
                //let applicationsOnReq = await API.getProposalOnRequestByStudent(props.user.token);
                //applications = applications.concat(applicationsOnReq);
                setApplications(applications);

            } catch (err) {
                console.error("Error fetching applications:", err);
            }
        };

        getApplicationsByStudent();
    }, [props.user]);


    return (
        <Card style={{"marginTop": "1rem", "marginBottom": "2rem", "marginRight": "1rem"}}>
            <Card.Header as="h1" style={{"textAlign": "center"}} className="py-3">
                Your applications
            </Card.Header>
            {applications.length > 0 ?
                <Table>
                    <tbody>
                    {applications.map((application) => <TableRow key={application.id} application={application}/>)}
                    </tbody>
                </Table>
                :
                <Card.Body style={{"textAlign": "center"}} className="mt-4">
                    <strong>You have no applications yet</strong>
                </Card.Body>
            }
        </Card>
    );
}


function TableRow(props) {
    const {application} = props;

    const statusBadge = () => {
        if (application.status === "PENDING" || application.status === "SECRETARY_ACCEPTED")
            return <Badge bg="primary"> ⦿ PENDING </Badge>
        else if (application.status === "ACCEPTED" || application.status === "TEACHER_ACCEPTED" )
            return <Badge bg="success"> ✓ ACCEPTED </Badge>
        else if (application.status === "REJECTED" || application.status === "TEACHER_ACCEPTED" || application.status === "SECRETARY_REJECTED")
            return <Badge bg="danger"> ✕ REJECTED </Badge>
    }


    return (
        <tr>
            <td>
                <Row className="my-3">
                    <Col lg={5}>
                        <strong> {application.proposalTitle || application.title} </strong>
                    </Col>
                    <Col lg={5}>
                        Supervised by: <em> {application.supervisorSurname || application.supervisor.surname} {application.supervisorName || application.supervisor.name} </em>
                    </Col>
                    <Col lg={1}>
                        {statusBadge()}
                    </Col>
                </Row>
            </td>
        </tr>
    );
}


export default BrowseDecisions;