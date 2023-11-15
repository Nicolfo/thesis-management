import { useState } from "react";
import { useEffect, useContext } from "react";
import API from "../API/Api";
import { Accordion, Button, useAccordionButton, Card, Row, Col, AccordionContext } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export default function BrowseProposalsContent(props) {

    const [proposalList, setProposalList] = useState([]);
    
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
            <Accordion defaultActiveKey="0">
                { proposalList.map(proposal => <ProposalAccordion proposal={proposal} />) }
            </Accordion>
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

function ProposalAccordion({ proposal }) {
    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><b>{proposal.title}</b></Col>
                    <Col className="d-flex justify-content-end">
                        <Button><FontAwesomeIcon icon="fa-file" /></Button>
                        <Button><FontAwesomeIcon icon="fa-pencil" /></Button>
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
                    <Col md="9"><b>Co-Supervisors</b><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}</Col>
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