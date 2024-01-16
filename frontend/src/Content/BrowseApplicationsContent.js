import {useContext, useState} from "react";
import {useEffect} from "react";
import API from "../API/Api";
import {Row, Col, Badge, Button, Table, Card, Accordion} from "react-bootstrap";
import dayjs from 'dayjs';
import {useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {AuthContext} from "react-oauth2-code-pkce";

function BrowseApplicationsContent(props) {

    const [accordionStates, setAccordionStates] = useState({});

    const navigate = useNavigate();

    function toggleAccordion(applicationId) {
        setAccordionStates((prevStates) => ({
            ...prevStates,
            [applicationId]: !prevStates[applicationId],
        }));
    }

    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role === "STUDENT")
        navigate("/notAuthorized");

    const [applications, setApplications] = useState([]);

    useEffect(() => {
        if (!props.user || props.user.role !== "TEACHER")
            return;
        const getApplicationsByProf = async () => {
            if (props.user && props.user.token)
                try {
                    const applications = await API.getApplicationsByProf(props.user.token);
                    setApplications(applications);
                } catch (error) {
                    console.error("Error fetching applications:", error);
                }
        };

        getApplicationsByProf();
    }, [props.user]);

    function groupBy(array, key) {
        return array.reduce((result, currentItem) => {
            (result[currentItem[key]] = result[currentItem[key]] || []).push(currentItem);
            return result;
        }, {});
    }

    let groupedApplications = null;
    if (applications && applications.length !== 0) {
        groupedApplications = groupBy(applications, 'proposalTitle');
    }

    return (
        <>
            <Card>
                <Card.Header className="mb-2">
                    <h1 className="my-3" style={{"textAlign": "center"}}>My application proposals</h1>
                </Card.Header>

                {!applications.length > 0 && <Card.Body style={{"textAlign": "center"}} className="mt-4">
                    <strong>You have no application proposals yet</strong>
                </Card.Body>}
                {
                    groupedApplications && Object.values(groupedApplications).map((e) => {
                        return <>
                            <Card.Body key={e[0].id}>
                                <Accordion defaultActiveKey="0">
                                    <Card className="mx-md-5">
                                        <Card.Header onClick={() => toggleAccordion(e[0].id)}>
                                            <Row className="p-2 align-items-center ">
                                                <Col className="d-flex justify-content-start">
                                                    <strong>{e[0].proposalTitle}</strong>
                                                </Col>
                                                <Col className="d-flex justify-content-end">
                                                    <Button>
                                                        <div className="d-flex align-items-center">
                                                            <FontAwesomeIcon icon={accordionStates[e[0].id] ? "chevron-up" : "chevron-down"}/>
                                                        </div>
                                                    </Button>
                                                </Col>
                                            </Row>
                                        </Card.Header>
                                        <Accordion.Collapse eventKey={e[0].id} flush in={accordionStates[e[0].id]}>
                                            <Card.Body>
                                                <ApplicationsTable setUpdateBeforeApplicationId={props.setUpdateBeforeApplicationId} applications={e}/>

                                            </Card.Body>
                                        </Accordion.Collapse>
                                    </Card>
                                </Accordion>
                            </Card.Body>
                        </>
                    })
                }
            </Card>
        </>
    );
}

function ApplicationsTable({applications, user}) {

    return (
        <Table responsive striped hover className="mb-4">
            <thead>
            {applications.length > 0 ?
                <tr>
                    <th className="d-none d-md-table-cell col">Apply date</th>
                    <th className="col">Student</th>
                    <th className="d-none d-md-table-cell col">Average grades</th>
                    <th className="d-none d-md-table-cell col">Status</th>
                    <th className="col">Action</th>
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
                <ApplicationRow key={application.id} application={application} user={user} setUpdateBeforeApplicationId={setUpdateBeforeApplicationId}/>
            ))}
            </tbody>
        </Table>
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

    const handleViewInfo = () => {
        props.setUpdateBeforeApplicationId(props.application.id);
        navigate("/teacher/application/view?applicationId=" + props.application.id);
    };

    return (
        <tr>
            <td className="d-none d-md-table-cell">{dayjs(props.application.applyDate).format('MMMM DD, YYYY HH:mm:ss')}</td>
            <td>{props.application.studentName} {props.application.studentSurname}</td>
            <td className="d-none d-md-table-cell">{props.application.studentAverageGrades}</td>
            <td className="d-none d-md-table-cell">{statusBadge()}</td>
            <td>
                <Button className="d-flex align-items-center" onClick={() => handleViewInfo(props.application.id)}
                        style={{display: 'flex', alignItems: 'center'}}>
                    <span className="d-none d-md-table-cell">Info </span>
                    <FontAwesomeIcon className="ms-1" icon={"chevron-right"}/>
                </Button>
            </td>
        </tr>
    );
}


export default BrowseApplicationsContent;
