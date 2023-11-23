import { useState } from "react";
import { useEffect, useContext } from "react";
import API from "../API/Api";
import {
    Accordion,
    Button,
    useAccordionButton,
    Card,
    Row,
    Col,
    AccordionContext,
    Modal,
    ModalBody, ModalTitle
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useNavigate } from "react-router-dom";
import {archiveProposal, deleteProposal} from "../API/Api-Search";
import dayjs from "dayjs";

export default function BrowseProposalsContent(props) {

    const [proposalList, setProposalList] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [deletingID, setDeletingID] = useState();




    useEffect(() => {
        const getProposalList = async () => {
            const list = await API.getProposalsByProf(props.user.token);
            setProposalList(list);
        }
        getProposalList();
    }, []);

    return (
        <>



            <h4>Your thesis proposals</h4>
            {deleting? <Row><Col></Col><Col><Warning user={props.user} setDeleting={setDeleting} deletingID={deletingID}> </Warning></Col> <Col></Col></Row>:
            <Accordion defaultActiveKey="0">
                { proposalList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal => <ProposalAccordion user={props.user} key={proposal.id} proposal={proposal} setDeleting={setDeleting} setDeletingID={setDeletingID} />) }
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
        <FontAwesomeIcon icon={isCurrentEventKey ? "fa-xmark" : "fa-plus"} />
      </Button>
    );
  }


function View() {
    return null;
}

function ProposalAccordion({ proposal, setDeleting, setDeletingID }) {
    const navigate = useNavigate();

    function deleteProp(proposalId){

        setDeleting(true);
        setDeletingID(proposalId)
    }

    return (
        <Card id={proposal.id} className="m-2">


            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><b>{proposal.title}</b></Col>
                    <Col className="d-flex justify-content-end">

                        <Button onClick={() => deleteProp(proposal.id)}><FontAwesomeIcon icon="fa-trash" /></Button>
                        <Button onClick={() => navigate(`/updateProposal/${proposal.id}`)}><FontAwesomeIcon icon="fa-pencil" /></Button>
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
                <Row className="pt-2">
                    <Col md="3"><b>Supervisor</b><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}</Col>
                    <Col md="6"><b>Co-Supervisors</b><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}</Col>
                    <Col md="3"><b>Expiration</b><br/>{dayjs(proposal.expiration).format("DD/MM/YYYY")}</Col>
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
                    <Button variant="danger" onClick={()=> { deleteProposal(props.deletingID); props.setDeleting(false); }}>Delete</Button>
                </Modal.Footer>
            </Modal.Dialog>
        </div>
    );
}