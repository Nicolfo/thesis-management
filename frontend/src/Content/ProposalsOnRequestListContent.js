import React, {useContext, useEffect, useState} from "react";
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
    DropdownButton, Modal,
    Row,
    useAccordionButton
} from "react-bootstrap";
import dayjs from "dayjs";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export default function ProposalsOnRequestListContent({user}) {
    const navigate = useNavigate();
    const {token} = useContext(AuthContext);

    const [proposals, setProposals] = useState([]);
    const [error, setError] = useState("");
    const [showWarning, setShowWarning] = useState(false);
    const [accept, setAccept] = useState(false);
    const [reject, setReject] = useState(false);
    const [updateId, setUpdateId] = useState();


    const getProposals = async () => {
        if(user && user.token)
            try {
                const proposals = await API.getAllProposalsOnRequest(user.token);
                setProposals(proposals);
            } catch (error) {
                handleError(error);
            }
    };

    useEffect(() => {
        if (!token)
            navigate("/notAuthorized");
        if (user && user.role !== "SECRETARY")
            navigate("/notAuthorized");
        if (!user)
            return;

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

            { showWarning ?
                <Warn user={user} accept={accept} setShowWarning={setShowWarning} setAccept={setAccept} updateId={updateId} getProposals={getProposals}></Warn>
                :
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
                                                              proposal={proposal}
                                                              setAccept={setAccept}
                                                              setReject={setReject}
                                                              setShowWarning={setShowWarning}
                                                              setUpdateId={setUpdateId}/>
                                })}
                            </Accordion>
                        </Card.Body> :
                        <Card.Body style={{"textAlign": "center"}} className="mt-4">
                            <strong>No proposals on request</strong>
                        </Card.Body>
                    }
                </Card>
            }

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
        <Button style={{marginLeft: "1rem"}} onClick={decoratedOnClick}>
            <div className="d-flex align-items-center">
                <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"}/>
                <span className="d-none d-md-table-cell ms-2"> Info </span>
            </div>
        </Button>
    );
}

function ProposalAccordion({proposal, setAccept, setReject, setShowWarning, setUpdateId}) {

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center ">
                    <Col className="d-flex justify-content-start"><strong>{proposal.title}</strong></Col>
                    <Col className="d-flex justify-content-end">
                        <Button variant="success" onClick={() => {setAccept(true); setShowWarning(true); setUpdateId(proposal.id)}}><FontAwesomeIcon icon="fa-solid fa-check" /> Accept </Button>
                        <Button variant="danger" style={{marginLeft: "1rem"}} onClick={() => {setReject(true); setShowWarning(true); setUpdateId(proposal.id)}}><FontAwesomeIcon icon="fa-solid fa-xmark" /> Reject </Button>
                        <CustomToggle eventKey={proposal.id}/>
                    </Col>
                </Row>

            </Card.Header>
            <Accordion.Collapse eventKey={proposal.id} flush>
                <Card.Body>
                    <Row>
                        {console.log(proposal)}
                        <Col><b>Supervisor</b><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}</Col>
                        {proposal.coSupervisors && proposal.coSupervisors.length > 0 &&
                            <Col
                                md="9"><b>Co-Supervisors</b><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}
                            </Col>
                        }
                    </Row>
                    <Row>
                        <Col><b>Student</b><br/>{proposal.student.surname + " " + proposal.student.name}</Col>
                        {proposal.approvalDate && <Col><b>Approval date</b><br/>{dayjs(proposal.approvalDate).format("DD/MM/YYYY")}</Col>}
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

function Warn(props) {
    return (
        <div
            className="modal show d-flex align-items-center justify-content-center vh-100"
        >
            <Modal.Dialog>
                <Modal.Header >
                    <Modal.Title> Warning!</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    { props.accept? <p>Do you want to accept this student request?</p>:<p>Do you want to reject this student request?</p>}

                </Modal.Body>
                { props.accept ?

                    <Modal.Footer>
                        <Button variant="primary" onClick={()=>{props.setAccept(false); props.setShowWarning(false)}}>Undo</Button>
                        <Button variant="success" onClick={()=> { API.secretaryAccept(props.user.token, props.updateId).then(()=> {props.getProposals().then(() => {props.setAccept(false); props.setShowWarning(false); })})}}>Accept</Button>
                    </Modal.Footer>
                    :
                    <Modal.Footer>
                        <Button variant="primary" onClick={()=>props.setShowWarning(false)}>Undo</Button>
                        <Button variant="danger" onClick={()=> { API.secretaryReject(props.user.token, props.updateId).then(()=> {props.getProposals().then(() => {props.setShowWarning(false)})})}}>Reject</Button>
                    </Modal.Footer>

                }
            </Modal.Dialog>
        </div>
    );
}
