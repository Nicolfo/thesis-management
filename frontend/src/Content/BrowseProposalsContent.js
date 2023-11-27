import { useState } from "react";
import { useEffect, useContext } from "react";
import API from "../API/Api";
import {archiveProposal} from "../API/Api-Search";


import {Accordion, Button, useAccordionButton, Card, Row, Col, AccordionContext, DropdownButton,Modal, Dropdown,ModalBody, ModalTitle} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useNavigate } from "react-router-dom";
import {archiveProposal, deleteProposal} from "../API/Api-Search";
import dayjs from "dayjs";
import {AuthContext} from "react-oauth2-code-pkce";

export default function BrowseProposalsContent(props) {

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role==="STUDENT")
        navigate("/notAuthorized");

    const getProposalList = async () => {
        const list = await API.getProposalsByProf(props.user.token);
        setProposalList(list);
    }

    const [proposalList, setProposalList] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [deletingID, setDeletingID] = useState();




    useEffect(() => {
        if(props.user)  {
            getProposalList();
        }
    }, [props.user, deleting]);

    return (
        <>

            <h4>Your thesis proposals</h4>
            {deleting? <Row><Col></Col><Col><Warning user={props.user} setDeleting={setDeleting} deletingID={deletingID}> </Warning></Col> <Col></Col></Row>:
            <Accordion defaultActiveKey="0">
                { proposalList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal =>{ console.log(proposal);return <ProposalAccordion user={props.user} key={proposal.id} proposal={proposal} setDeleting={setDeleting} setDeletingID={setDeletingID}  />}) }
            </Accordion>}
        </>
    );

}

function CustomToggle({ children, eventKey, callback }) {
    const { activeEventKey } = useContext(AccordionContext);

    const decoratedOnClick = useAccordionButton(
        eventKey,
        () => callback && callback(eventKey),
    );

    const isCurrentEventKey = activeEventKey === eventKey;
  
    return (
      <Button
        onClick={decoratedOnClick}
      >

      <div className="d-flex align-items-center">
          <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"} />
          <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
          <span className="d-none d-md-table-cell"> Info </span>
      </div>

      </Button>
    );
  }


function View() {
    return null;
}

function ProposalAccordion({ proposal, setDeleting, setDeletingID }) {
    const navigate = useNavigate();

    function deleteProp(proposalId){
        console.log(proposalId)
        setDeleting(true);
        setDeletingID(proposalId)
    }

    return (
        <Card id={proposal.id} className="m-2">


            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><strong>{proposal.title}</strong></Col>
                    <Col className="d-flex justify-content-end">

                        <DropdownButton id="dropdown-item-button" title={
                            <div className="d-flex align-items-center">
                                <FontAwesomeIcon icon="fa-solid fa-list-ul" />
                                <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                <span className="d-none d-md-table-cell"> Options </span>
                            </div>
                            }
                        >
                            <Dropdown.Item as="button" style={{color: "#0B67A5"}} onClick={() => navigate(`/updateProposal/${proposal.id}`)}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-pencil" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Update </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "#0B67A5"}} onClick={() => navigate(`/copyProposal/${proposal.id}`)}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-copy" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Copy </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "#0B67A5"}}onClick={() => {archiveProposal(proposal.id, props.user.token)}} >
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-box-archive" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Archive </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "#0B67A5"}} onClick={() => {deleteProp(proposal.id);}}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-trash-can" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Delete </span>
                                </div>
                            </Dropdown.Item>
                        </DropdownButton>
                        <CustomToggle eventKey={proposal.id} />
                    </Col>
                </Row>
            </Card.Header>
            <Accordion.Collapse eventKey={proposal.id} flush>
            <Card.Body>
                <Row>
                    <Col><b>CdS</b><br/>{proposal.cdS}</Col>
                    <Col><b>Groups</b><br/>{proposal.groups.map(g => g.name).join(", ")}</Col>
                    <Col><b>Level</b><br/>{proposal.level}</Col>
                    <Col><b>Type</b><br/>{proposal.type}</Col>
                </Row>
                <Row>
                    <Col><b>Keywords</b><br/>{proposal.keywords}</Col>
                    { proposal.requiredKnowledge.length > 0 &&
                        <Col><b>Required Knowledge</b><br/>{proposal.requiredKnowledge}</Col>
                    }
                    <Col><b>Expiration</b><br/>{dayjs(proposal.expiration).format("DD/MM/YYYY")}</Col>
                </Row>
                <Row className="pt-2">
                    <Col md="3"><b>Supervisor</b><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}</Col>
                    { proposal.coSupervisors.length > 0 &&
                        <Col md="9"><b>Co-Supervisors</b><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}</Col>
                    }
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
    )
}


function Warning(props) {
    return (
        <div
            className="modal show"
            style={{ display: 'block', position: 'initial' }}
        >
            <Modal.Dialog>
                <Modal.Header >
                    <Modal.Title> Warning!</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <p>Do you want to delete this proposal?</p>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="primary" onClick={()=>props.setDeleting(false)}>Undo</Button>
                    <Button variant="danger" onClick={()=> { deleteProposal(props.deletingID,props.user.token); props.setDeleting(false); }}>Delete</Button>
                </Modal.Footer>
            </Modal.Dialog>
        </div>
    );
}