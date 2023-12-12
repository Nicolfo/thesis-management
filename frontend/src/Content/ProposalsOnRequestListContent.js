import {useContext, useEffect, useState} from "react";
import API from "../API/Api";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";
import {
    Accordion,
    AccordionContext,
    Alert, Button,
    Card,
    Col,
    Dropdown,
    DropdownButton,
    Row,
    useAccordionButton
} from "react-bootstrap";
import dayjs from "dayjs";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function ProposalsOnRequestListContent({user}) {
    const navigate = useNavigate();
    const {token} = useContext(AuthContext);

    const [proposals, setProposals] = useState([]);
    const [error, setError] = useState("");


    useEffect(() => {
        if (!token)
            navigate("/notAuthorized");
        if (user && user.role !== "SECRETARY")
            navigate("/notAuthorized");
        if (!user)
            return;

        const getProposals = async () => {
            if(user && user.token)
                try {
                    const proposals = await API.getAllProposalsOnRequest(user.token);
                    setProposals(proposals);
                } catch (error) {
                    handleError(error);
                }
        };
        getProposals();

    }, [user]);

    const handleError = error => {
        if (error.error)
            setError(error.error);
        else if (error.message)
            setError(error.message);
        else if (typeof error === "string")
            setError(error);
        else
            setError("Error");
    }

    return (
        <>
            {error !== "" &&
                <Row>
                    <Col>
                        <Alert variant="danger" dismissible onClose={() => setError("")}>
                            {error}
                        </Alert>
                    </Col>
                </Row>
            }

            <Card>
                <Card.Header>
                    <h1 style={{"textAlign": "center", marginTop: "0.5rem", marginBottom: "0.5rem"}}>Thesis
                        proposals on request</h1>
                    <h2 style={{"textAlign": "center"}}>Pending proposals</h2>
                </Card.Header>
                {proposals.length > 0 ?
                    <Card.Body>
                        <Accordion defaultActiveKey="0">
                            {proposals.map(proposal => {
                                return <ProposalAccordion user={user}
                                                          key={proposal.id}
                                                          proposal={proposal}/>
                            })}
                        </Accordion>
                    </Card.Body> :
                    <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>No proposals on request</strong>
                    </Card.Body>
                }
            </Card>

        </>
    );
}

function CustomToggle({eventKey, callback}) {
    const {activeEventKey} = useContext(AccordionContext);

    const decoratedOnClick = useAccordionButton(
        eventKey,
        () => callback && callback(eventKey),
    );

    const isCurrentEventKey = activeEventKey === eventKey;

    return (
        <Button onClick={decoratedOnClick}>
            <div className="d-flex align-items-center">
                <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"}/>
                <span className="d-none d-md-table-cell ms-2"> Info </span>
            </div>
        </Button>
    );
}

function ProposalAccordion({proposal}) {

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center ">
                    <Col className="d-flex justify-content-start"><strong>{proposal.title}</strong></Col>
                    <Col className="d-flex justify-content-end"><CustomToggle eventKey={proposal.id}/></Col>
                </Row>

            </Card.Header>
            <Accordion.Collapse eventKey={proposal.id} flush>
                <Card.Body>
                    <Row>
                        <Col><strong>Supervisor</strong><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}</Col>
                        {proposal.coSupervisors && proposal.coSupervisors.length > 0 &&
                            <Col
                                md="9"><strong>Co-Supervisors</strong><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}
                            </Col>
                        }
                    </Row>
                    <Row>
                        <Col><strong>Student</strong><br/>{proposal.student.surname + " " + proposal.student.name}</Col>
                        {proposal.approvalDate && <Col><strong>Approval date</strong><br/>{dayjs(proposal.approvalDate).format("DD/MM/YYYY")}</Col>}
                    </Row>
                    <hr className="me-4"/>
                    <Row>
                        <Col>
                            {proposal.description}
                        </Col>
                    </Row>
                </Card.Body>
            </Accordion.Collapse>
        </Card>
    );
}

export default ProposalsOnRequestListContent;