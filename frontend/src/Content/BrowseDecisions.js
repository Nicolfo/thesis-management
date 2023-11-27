import {useEffect, useState} from "react";
import {Badge, Card, Col, Row, Table} from "react-bootstrap";
import API from "../API/Api";


function BrowseDecisions(props) {

    const [applications, setApplications] = useState([]);

    useEffect(() => {
        const getApplicationsByStudent = async () => {
            try {
                const applications = await API.getApplicationsByStudent(props.user.token);
                setApplications(applications);

            } catch (err) {
                console.error("Error fetching applications:", err);
            }
        };

        getApplicationsByStudent();
    }, []);


    return (
        <Card style={{"marginTop": "1rem", "marginBottom": "2rem", "marginRight": "1rem"}}>
            <Card.Header as="h3" style={{"textAlign": "center"}}>
                Your applications
            </Card.Header>
            { applications.length > 0 ?
                <Table>
                    <tbody>
                    { applications.map((application) => <TableRow key={application.id} application={application} /> )}
                    </tbody>
                </Table>
                :
                <Card.Body as="h5" style={{"textAlign": "center"}}>
                    You have no applications yet
                </Card.Body>
            }
        </Card>
    );
}


function TableRow(props) {
    const { application } = props;

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
                <Row>
                    <Col lg={5}>
                        <b> {application.proposalTitle} </b>
                    </Col>
                    <Col lg={5}>
                        Supervised by: <em> {application.supervisorSurname} {application.supervisorName} </em>
                    </Col>
                    <Col lg={1}>
                        {statusBadge()}
                    </Col>
                    <Row style={{"visibility": "hidden"}}> - </Row>
                </Row>
            </td>
        </tr>
    );
}


export default BrowseDecisions;