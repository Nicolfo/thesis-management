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
                const applications = await API.getApplicationsByStudent(props.user.token);
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
                <Card.Body style={{"textAlign": "center"}}>
                    <strong>You have no applications yet</strong>
                </Card.Body>
            }
        </Card>
    );
}


function TableRow(props) {
    const {application} = props;

    const statusBadge = () => {
        if (application.status === "PENDING")
            return <Badge bg="primary"> ⦿ PENDING </Badge>
        else if (application.status === "ACCEPTED")
            return <Badge bg="success"> ✓ ACCEPTED </Badge>
        else if (application.status === "REJECTED")
            return <Badge bg="danger"> ✕ REJECTED </Badge>
    }


    return (
        <tr>
            <td>
                <Row className="my-3">
                    <Col lg={5}>
                        <strong> {application.proposalTitle} </strong>
                    </Col>
                    <Col lg={5}>
                        Supervised by: <em> {application.supervisorSurname} {application.supervisorName} </em>
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