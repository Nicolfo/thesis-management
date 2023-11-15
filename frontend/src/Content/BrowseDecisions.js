import {useEffect, useState} from "react";
import {Badge, Card, Table} from "react-bootstrap";
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
        <Card style={{"marginTop": "1rem", "marginBottom": "2rem"}}>
            <Card.Header as="h3" style={{"textAlign": "center"}}>
                Your applications
            </Card.Header>
            <Table>
                <tbody>
                    { applications.map((application) => <TableRow key={application.id} application={application} /> )}
                </tbody>
            </Table>
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
                <div className="d-flex justify-content-between">
                    <p className="text-start col-md-5 col-3">
                        <b> {application.proposalTitle} </b>
                    </p>
                    <p className="col-md-3 col-3">
                        <em> {application.supervisorSurname} {application.supervisorName} </em>
                    </p>
                    <p className="text-end col-md-3 col-3" style={{"marginRight": "2rem"}}> {statusBadge()} </p>
                </div>
            </td>
        </tr>
    );
}


export default BrowseDecisions;