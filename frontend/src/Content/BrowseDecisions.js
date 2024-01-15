import {useContext, useEffect, useState} from "react";
import {Badge, Button, Card, Col, Row, Table} from "react-bootstrap";
import API from "../API/Api";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function BrowseDecisions(props) {
    const { token } = useContext(AuthContext);

    const [applications, setApplications] = useState([]);
    const [proposalOnRequest, setproposalOnRequest] = useState([]);


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
                let applicationsOnReq = await API.getProposalOnRequestByStudent(props.user.token);
                //applications = applications.concat(applicationsOnReq);
                setproposalOnRequest(applicationsOnReq);
                setApplications(applications);

            } catch (err) {
                console.error("Error fetching applications:", err);
            }
        };

        getApplicationsByStudent();
    }, [props.user]);


    return (
        <>
            <Card style={{"marginTop": "1rem", "marginBottom": "2rem", "marginRight": "1rem"}}>
                <Card.Header as="h1" style={{"textAlign": "center"}} className="py-3">
                    Your applications
                </Card.Header>

                {applications.length > 0 ?

                    <Table>
                        <tbody>
                        {applications.map((application) => <TableRow key={application.id} application={application} proposalOnRequest={proposalOnRequest} navigate={navigate}/>)}
                        </tbody>
                    </Table>

                    :
                    <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>You have no applications yet</strong>
                    </Card.Body>
                }
            </Card>

            <Card style={{ "marginTop": "1rem", "marginBottom": "2rem", "marginRight": "1rem" }}>
                <Card.Header as="h1" style={{ "textAlign": "center" }} className="py-3">
                    Your proposals on request
                </Card.Header>

                {proposalOnRequest.length > 0 ?

                    <Table>
                        <tbody>
                        {proposalOnRequest.map((application) => <TableRow key={application.id} application={application} proposalOnRequest={proposalOnRequest} navigate={navigate}/>)}
                        </tbody>
                    </Table>

                    :
                    <Card.Body style={{ "textAlign": "center" }} className="mt-4">
                        <strong>You have no proposals on request yet</strong>
                    </Card.Body>
                }
            </Card>
        </>);
}


function TableRow(props) {
    const { application, proposalOnRequest, navigate } = props;

    const statusBadge = () => {
        if (application.status === "PENDING" || application.status === "SECRETARY_ACCEPTED")
            return <Badge bg="primary"> ⦿ PENDING </Badge>
        else if (application.status === "ACCEPTED" || application.status === "TEACHER_ACCEPTED" ) {
            return (
                <>
                    <Badge bg="success"> ✓ ACCEPTED </Badge>
                    { proposalOnRequest.length === 0 &&
                        <Button size="sm" style={{marginLeft: "2rem", borderRadius: 10}} onClick={() => navigate(`/startRequestFromApplication/${application.id}`)}>
                            <FontAwesomeIcon icon="fa-solid fa-chevron-right" /> Start request
                        </Button>
                    }
                </>
            )
        }
        else if (application.status === "REJECTED" || application.status === "TEACHER_REJECTED" || application.status === "SECRETARY_REJECTED")
            return <Badge bg="danger"> ✕ REJECTED </Badge>
        else if (application.status === "CANCELLED" || application.status === "TEACHER_CANCELLED")
            return <Badge bg="dark"> <FontAwesomeIcon icon="fa-solid fa-ban" /> CANCELLED </Badge>
        else if (application.status === "TEACHER_REVIEW") {
            return (
                <>
                    <Badge bg="warning"> ✎ TO CHANGE </Badge>
                    <Button size="sm" style={{marginLeft: "2rem", borderRadius: 10}} onClick={() => navigate(`/changeRequest/${application.id}`)}>
                        <FontAwesomeIcon icon="fa-solid fa-chevron-right" /> Change request
                    </Button>
                </>
            )
        }
    }


    return (
        <tr>
            <td>
                <Row className="my-3 d-flex align-items-center">
                    <Col lg={5}>
                        <strong> {application.proposalTitle || application.title} </strong>
                    </Col>
                    <Col lg={4}>
                        Supervised by: <em> {application.supervisorSurname || application.supervisor.surname} {application.supervisorName || application.supervisor.name} </em>
                    </Col>
                    <Col lg={3}>
                        {statusBadge()}
                    </Col>
                </Row>
            </td>
        </tr>
    );
}


export default BrowseDecisions;