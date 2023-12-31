import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useNavigate } from "react-router-dom";
import API from "../API/Api";
import { Card, Container, Row, Col, Button, Modal } from "react-bootstrap";
import toast, {Toaster} from 'react-hot-toast';


function TeacherApproveStartRequestContent({ user }) {
    const navigate = useNavigate();
    const { token } = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (user && user.role !== "TEACHER")
        navigate("/notAuthorized");

    const [requestList, setRequestList] = useState([]);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [operation, setOperation] = useState("");
    const [showModal, setShowModal] = useState(false);


    const onConfirm = async () => {
        switch (operation) {
            case "accept":
                toast.promise(
                    (async () => {
                        await API.teacherAccept(token, selectedRequest.id);
                        // Refresh list
                        const list = await API.getAcceptedProposalOnRequestsByTeacher(token);
                        setRequestList(list);
                        return "Thesis request successfully accepted";
                    })(),
                    {
                        loading: 'Sending...',
                        success: () => {
                            return <strong>Thesis request successfully accepted</strong>
                        },
                        error: (error) => {
                            if (error && error.detail) {
                                return <strong>{error.detail}</strong>;
                            } else {
                                return <strong>An error occurred while accepting the request</strong>;
                            }
                        }
                    });
                break;
            case "reject":
                toast.promise(
                    (async () => {
                        await API.teacherReject(token, selectedRequest.id);
                        // Refresh list
                        const list = await API.getAcceptedProposalOnRequestsByTeacher(token);
                        setRequestList(list);
                        return "Thesis request successfully rejected";
                    })(),
                    {
                        loading: 'Sending...',
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
                break;
            case "requestChange":
                toast.promise(
                    (async () => {
                        await API.teacherRequestChange(token, selectedRequest.id);
                        // Refresh list
                        const list = await API.getAcceptedProposalOnRequestsByTeacher(token);
                        setRequestList(list);
                        return "Thesis change successfully requested";
                    })(),
                    {
                        loading: 'Sending...',
                        success: () => {
                            return <strong>Thesis change successfully requested</strong>
                        },
                        error: (error) => {
                            if (error && error.detail) {
                                return <strong>{error.detail}</strong>;
                            } else {
                                return <strong>An error occurred while requesting the change</strong>;
                            }
                        }
                    });
                break;
        }

    }

    // Not authorized if not teacher
    useEffect(() => {
        if (!token || !user)
            navigate("/notAuthorized");
        if (user && user.role !== "TEACHER")
            navigate("/notAuthorized");
    }, []);

    // Get list of available requests
    // The thesis start requests that have been accepted by a secretary, for the given teacher
    useEffect(() => {
        const getResources = async () => {
            const list = await API.getAcceptedProposalOnRequestsByTeacher(token);
            setRequestList(list);
        };
        getResources();
    }, []);

    return (
        <>
            <Card>
                <Card.Header>
                    <h1 className="my-3" style={{"textAlign": "center"}}>Student thesis requests</h1>
                </Card.Header>
                { requestList.length > 0 ?
                <Card.Body>
                { requestList.map(r => <RequestEntry key={r.id} request={r} setSelectedRequest={setSelectedRequest} setOperation={setOperation} setShowModal={setShowModal} />) }
                { selectedRequest && <OperationModal show={showModal} setShow={setShowModal} selectedRequest={selectedRequest} setSelectedRequest={setSelectedRequest} operation={operation} onConfirm={onConfirm} /> }
                </Card.Body>
                : <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>You have no student thesis requests yet</strong>
                    </Card.Body>}
            </Card>

            <Toaster
                position="top-right"
                containerClassName="mt-5"
                reverseOrder={false}
            />
        </>
    );
}

function RequestEntry({ request, setSelectedRequest, setOperation, setShowModal }) {

    const startAccept = () => {
        setSelectedRequest(request);
        setOperation("accept");
        setShowModal(true);
    }

    const startRequestChange = () => {
        setSelectedRequest(request);
        setOperation("requestChange");
        setShowModal(true);
    }

    const startReject = () => {
        setSelectedRequest(request);
        setOperation("reject");
        setShowModal(true);
    }

    return (
        <Card className="mb-3">
            <Card.Header className="text-start pe-3">{request.title}</Card.Header>
            <Container className="p-3">
                <Row>
                    <Col xs={4}><strong>Student </strong><br/>{request.student.name} {request.student.surname}</Col>
                    <Col xs={8}><strong>Co-Supervisors </strong><br/>{request.coSupervisors.length > 0 ? request.coSupervisors.map(t => `${t.name} ${t.surname}`).join(", ") : "None"}</Col>
                </Row>
                <Row>
                    <Col><strong>Description</strong><br/>{request.description}</Col>
                </Row>
                <Row className="mt-2">
                    <Col>
                        <Button variant="success me-2" onClick={startAccept}>Accept</Button>
                        <Button variant="info me-2" onClick={startRequestChange}>Request Change</Button>
                        <Button variant="danger" onClick={startReject}>Reject</Button>
                    </Col>
                </Row>
            </Container>
        </Card>
    );
}

function OperationModal({ show, setShow, selectedRequest, setSelectedRequest, operation, onConfirm }) {

    return (
        <Modal show={show} onHide={() => { setSelectedRequest(null); setShow(false); }} backdrop="static" keyboard={false}>
            <Modal.Header closeButton>
                <Modal.Title>
                    {operation === "accept" && "Accept Request"}
                    {operation === "reject" && "Reject Request"}
                    {operation === "requestChange" && "Change Request"}
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                Are you sure you want to 
                {operation === "accept" && " accept "}
                {operation === "reject" && " reject "}
                {operation === "requestChange" && " request a change for "} the request '{selectedRequest.title}'?
            </Modal.Body>
            <Modal.Footer>
                <Button className="btn-dark" onClick={() => { onConfirm(); setShow(false); }}>Yes</Button>
                <Button className="btn-danger" onClick={() => { setSelectedRequest(null); setShow(false); }}>Cancel</Button>
            </Modal.Footer>
        </Modal>
    );

}

export default TeacherApproveStartRequestContent;