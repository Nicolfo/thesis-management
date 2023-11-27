import { useState } from "react";
import { useEffect, useContext } from "react";
import API from "../API/Api";
import {Accordion, Button, useAccordionButton, Card, Row, Col, AccordionContext, DropdownButton, Dropdown} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useNavigate } from "react-router-dom";
import dayjs from "dayjs";

export default function BrowseProposalsContent(props) {

    const navigate = useNavigate();

    if(!props.user || props.user.role !== "TEACHER") {
        navigate("/notAuthorized");
    }

    const getProposalList = async () => {
        const list = await API.getProposalsByProf(props.user.token);
        setProposalList(list);
    }

    const [proposalList, setProposalList] = useState([]);
    
    useEffect(() => {
        if(props.user)  {
            getProposalList();
        }
    }, [props.user]);

    return (
        <>
            <h4>My thesis proposals</h4>
            <Accordion defaultActiveKey="0">
                { proposalList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal => <ProposalAccordion key={proposal.id} proposal={proposal} />) }
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

    // const isCurrentEventKey = activeEventKey === eventKey;
  
    return (
      <Button
        onClick={decoratedOnClick}
      >
        {/*<FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"} />*/}
          <div className="d-flex align-items-center">
              <FontAwesomeIcon icon="fa-solid fa-magnifying-glass" />
              <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
              <span className="d-none d-md-table-cell"> Info </span>
          </div>

      </Button>
    );
  }

function ProposalAccordion({ proposal }) {
    const navigate = useNavigate();

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
                            <Dropdown.Item as="button" style={{color: "orange"}} onClick={() => navigate(`/updateProposal/${proposal.id}`)}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-pencil" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Update </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "green"}} onClick={() => navigate(`/copyProposal/${proposal.id}`)}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-copy" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Copy </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "#5c0000"}}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-box-archive" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Archive </span>
                                </div>
                            </Dropdown.Item>
                            <Dropdown.Item as="button" style={{color: "red"}}>
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