import { Card, Form, Button, Row, Col, Accordion, AccordionContext, useAccordionButton } from "react-bootstrap";
import { getAllSupervisors } from "../API/Api-Search";
import API from "../API/API2";
import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { MultiSelect } from "react-multi-select-component";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

function ProposalsListContent({ user }) {

    const levelOptions = [
        "Any",
        "Bachelor's",
        "Master's"
    ];

    const navigate = useNavigate();

    const [teachersList, setTeachersList] = useState([]);
    const [selectedSupervisorIds, setSelectedSupervisorIds] = useState([]);
    const [selectedCoSupervisorIds, setSelectedCoSupervisorIds] = useState([]);
    const [groupsList, setGroupsList] = useState([]);
    const [selectedGroupIds, setSelectedGroupIds] = useState([]);
    const [title, setTitle] = useState("");
    const [keywords, setKeywords] = useState("");
    const [type, setType] = useState("");
    const [description, setDescription] = useState("");
    const [requiredKnowledge, setRequiredKnowledge] = useState("");
    const [notes, setNotes] = useState("");
    const [minExpiration, setMinExpiration] = useState("");
    const [maxExpiration, setMaxExpiration] = useState("");
    const [level, setLevel] = useState("Any");
    const [cds, setCds] = useState("");

    const [proposalsList, setProposalsList] = useState([]);

    useEffect(() => {
        if (!user) {
            navigate("/login");
        }
        const getResources = async () => {
            const teachers = await getAllSupervisors(user.token);
            setTeachersList(teachers.map(t => { return { label: `${t.surname} ${t.name}`, value: t.id }; }));
            const groups = await API.getAllGroups(user.token);
            setGroupsList(groups.map(g => { return { label: `${g.name}`, value: g.codGroup }; }));
            const proposals = await API.getAllProposals(user.token);
            setProposalsList(proposals);
        };
        getResources();
    }, [])

    const doSearch = async () => {
        const requestBody = {
            title: title || null,
            supervisorIdList: selectedSupervisorIds.length > 0 ? selectedSupervisorIds.map(e => e.value) : null,
            coSupervisorIdList: selectedCoSupervisorIds.length > 0 ? selectedCoSupervisorIds.map(e => e.value) : null,
            keywords: keywords || null,
            type: type || null,
            codGroupList: selectedGroupIds.length > 0 ? selectedGroupIds.map(e => e.value) : null,
            description: description || null,
            requiredKnowledge: requiredKnowledge || null,
            notes: notes || null,
            minExpiration: minExpiration || null,
            maxExpiration: maxExpiration || null,
            level: level === "Any" ? null : level,
            CdS: cds || null,
        };


        
        // Remove properties with null values
        Object.keys(requestBody).forEach((key) => requestBody[key] === null && delete requestBody[key]);
        
        const proposals = await API.searchProposals(user.token, requestBody);
        setProposalsList(proposals);
    }

    return (
        <>
        <Card>
            <Card.Title>Search Proposal</Card.Title>
            <Card.Body>
                <Form>
                <Form.Group className="mb-3">
                    <Form.Label>Title</Form.Label>
                    <Form.Control type="text" placeholder="Title" value={title} onChange={event => setTitle(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Supervisor(s)</Form.Label>
                    <MultiSelect
                        options={teachersList}
                        value={selectedSupervisorIds}
                        onChange={setSelectedSupervisorIds}
                        labelledBy="Select Supervisors"
                    />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Co-Supervisor(s)</Form.Label>
                    <MultiSelect
                        options={teachersList}
                        value={selectedCoSupervisorIds}
                        onChange={setSelectedCoSupervisorIds}
                        labelledBy="Select Co-Supervisors"
                    />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Keywords</Form.Label>
                    <Form.Control type="text" placeholder="Keywords" value={keywords} onChange={event => setKeywords(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Type</Form.Label>
                    <Form.Control type="text" placeholder="Type" value={type} onChange={event => setType(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Group(s)</Form.Label>
                    <MultiSelect
                        options={groupsList}
                        value={selectedGroupIds}
                        onChange={setSelectedGroupIds}
                        labelledBy="Select Groups"
                    />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Description</Form.Label>
                    <Form.Control type="text" placeholder="Description" value={description} onChange={event => setDescription(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Required Knowledge</Form.Label>
                    <Form.Control type="text" placeholder="Required Knowledge" value={requiredKnowledge} onChange={event => setRequiredKnowledge(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Notes</Form.Label>
                    <Form.Control type="text" placeholder="Notes" value={notes} onChange={event => setNotes(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Level</Form.Label>
                    <Form.Select name="level" value={level} onChange={(e) => setLevel(e.target.value)}>
                        { levelOptions.map(levelOption => <option value={levelOption}>{levelOption}</option>) }
                    </Form.Select>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>CdS</Form.Label>
                    <Form.Control type="text" placeholder="CdS" value={cds} onChange={event => setCds(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Min. Expiration Date</Form.Label>
                    <Form.Control type="date" value={minExpiration} onChange={event => setMinExpiration(event.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Max. Expiration Date</Form.Label>
                    <Form.Control type="date" value={maxExpiration} onChange={event => setMaxExpiration(event.target.value)} />
                </Form.Group>
                </Form>
                <Button onClick={doSearch}>Search</Button>
            </Card.Body>
        </Card>
        <ProposalsList proposals={proposalsList} user={user} />
        </>
    );
}

function ProposalsList({ proposals, user }) {
    return (
        <Accordion defaultActiveKey="0">
            { proposals.map(proposal => <ProposalEntry key={proposal.id} proposal={proposal} user={user} />) }
        </Accordion>
    )
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

function ProposalEntry({ proposal, user }) {
    const navigate = useNavigate();

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><b>{proposal.title}</b></Col>
                    <Col className="d-flex justify-content-end">
                        <Button onClick={() => navigate(`/proposal/apply/${proposal.id}`)}><FontAwesomeIcon icon="fa-file" /></Button>
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
                    <Col><b>Required Knowledge</b><br/>{proposal.requiredKnowledge}</Col>
                    <Col><b>Expiration</b><br/>{proposal.expiration}</Col>
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

export default ProposalsListContent;