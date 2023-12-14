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
import toast, {Toaster} from 'react-hot-toast';


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

    const accepted = (token, updateId, getProposals, setAccept, setShowWarning) => {
        toast.promise(
            (async () => {
                await API.secretaryAccept(token, updateId);
                await getProposals();
                setAccept(false);
                setShowWarning(false);
                return "Thesis request successfully forwarded to the supervisor";
            })(),
            {
                loading: 'Loading...',
                success: () => {
                    return <strong>Thesis request successfully forwarded to the supervisor</strong>
                },
                error: (error) => {
                    if (error && error.detail) {
                        return <strong>{error.detail}</strong>;
                    } else {
                        return <strong>An error occurred while forwarding the request</strong>;
                    }
                }
            });

        // API.secretaryAccept(props.user.token, props.updateId)
        //     .then(()=> {
        //         props.getProposals()
        //             .then(() => {
        //                 props.setAccept(false);
        //                 props.setShowWarning(false);
        //             })
        //     })
    }

    const rejected = (token, updateId, getProposals, setShowWarning) => {
        toast.promise(
            (async () => {
                await API.secretaryReject(token, updateId);
                await getProposals();
                setShowWarning(false);
                return "Thesis request successfully rejected";
            })(),
            {
                loading: 'Loading...',
                success: () => {
                    return <strong>Thesis request successfully rejected</strong>
                },
                error: (error) => {
                    if (error && error.detail) {
                        return <strong>{error.detail}</strong>;
                    } else {
                        return <strong>An error occurred while rejecting the request</strong>;
                    }
                }
            });

        // API.secretaryReject(props.user.token, props.updateId)
        //     .then(()=> {
        //         props.getProposals()
        //             .then(() => {
        //                 props.setShowWarning(false)}
        //         )})
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
                <Warn user={user} accept={accept} setShowWarning={setShowWarning} setAccept={setAccept} updateId={updateId} getProposals={getProposals} accepted={accepted} rejected={rejected}></Warn>
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
                                                              setUpdateId={setUpdateId}
                                                              />
                                })}
                            </Accordion>
                        </Card.Body> :
                        <Card.Body style={{"textAlign": "center"}} className="mt-4">
                            <strong>No proposals on request</strong>
                        </Card.Body>
                    }
                </Card>
            }

            <Toaster
                position="top-right"
                containerClassName="mt-5"
                reverseOrder={false}
            />
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
                    { props.accept?
                        <p>Do you want to accept this student request?</p>
                        :
                        <p>Do you want to reject this student request?</p>
                    }
                </Modal.Body>

                { props.accept ?
                    <Modal.Footer>
                        <Button variant="primary" onClick={()=> {props.setAccept(false); props.setShowWarning(false)}}>Undo</Button>
                        <Button variant="success" onClick={() => {props.accepted(props.user.token, props.updateId, props.getProposals, props.setAccept, props.setShowWarning)}}>Accept</Button>
                    </Modal.Footer>
                    :
                    <Modal.Footer>
                        <Button variant="primary" onClick={() => props.setShowWarning(false)}>Undo</Button>
                        <Button variant="danger" onClick={() => {props.rejected(props.user.token, props.updateId, props.getProposals, props.setShowWarning)}}>Reject</Button>
                    </Modal.Footer>
                }
            </Modal.Dialog>
        </div>
    );
}
