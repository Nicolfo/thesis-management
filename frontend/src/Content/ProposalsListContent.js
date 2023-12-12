import {
    Card,
    Form,
    Button,
    Row,
    Col,
    Accordion,
    AccordionContext,
    Offcanvas,
    useAccordionButton,
    Alert
} from "react-bootstrap";
import API from "../API/Api";
import {useState, useEffect, useContext} from "react";
import {useNavigate} from "react-router-dom";
import {MultiSelect} from "react-multi-select-component";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import dayjs from "dayjs";
import {AuthContext} from "react-oauth2-code-pkce";
import {forEach} from "react-bootstrap/ElementChildren";

function ProposalsListContent({user, applicationDate}) {

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    const [showSearchBar, setShowSearchBar] = useState(false);
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
    const [proposalsList, setProposalsList] = useState([]);
    const [error, setError] = useState("");


    const clearFields = () => {
        setTitle("");
        setNotes("");
        setRequiredKnowledge("");
        setDescription("");
        setType("");
        setKeywords("");
        setSelectedGroupIds([]);
        setSelectedSupervisorIds([]);
        setSelectedCoSupervisorIds([]);
    }

    useEffect(() => {
        if (!token)
            navigate("/notAuthorized");
        if (user && user.role !== "STUDENT")
            navigate("/notAuthorized");
        if (!user)
            return;

        const getResources = async () => {
            try {
                const teachers = await API.getAllSupervisors(user.token);
                setTeachersList(teachers.map(t => ({
                    label: `${t.surname} ${t.name}`,
                    value: t.id
                })));

                const groups = await API.getAllGroups(user.token);
                setGroupsList(groups.map(g => ({
                    label: `${g.name}`,
                    value: g.codGroup
                })));

                const proposals = await API.searchProposals(user.token, {});
                const updatedProposalsList = await Promise.all(proposals.map(async (proposal) => {
                    const application = await API.getApplicationsByProposalId(user.token, proposal.id);
                    const hasApplication = application.length > 0 ? 1 : 0;
                    return {
                        ...proposal,
                        hasApplication: hasApplication,
                    };
                }));

                setProposalsList(updatedProposalsList);
            } catch (error) {
                handleError(error);
            }
        };

        getResources();
    }, [user]);


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
            notes: notes || null
        };

        // Remove properties with null values
        Object.keys(requestBody).forEach((key) => requestBody[key] === null && delete requestBody[key]);

        try {
            const proposals = await API.searchProposals(user.token, requestBody);
            const updatedProposalsList = await Promise.all(proposals.map(async (proposal) => {
                const application = await API.getApplicationsByProposalId(user.token, proposal.id);
                const hasApplication = application.length > 0 ? 1 : 0;
                return {
                    ...proposal,
                    hasApplication: hasApplication,
                };
            }));

            setProposalsList(updatedProposalsList);
        } catch (error) {
            handleError(error);
        }

    }

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
            <Row style={{"textAlign": "end"}}>
                <Col>
                    <Button onClick={() => setShowSearchBar(it => !it)}> <FontAwesomeIcon
                        icon={"magnifying-glass"}/> Show searching filters </Button>
                </Col>
            </Row>
            <Offcanvas show={showSearchBar} onHide={() => setShowSearchBar(false)} placement="end" scroll={true}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>
                        <Button variant="outline-primary"  className="me-2" onClick={() => {
                            doSearch();
                            setShowSearchBar(false);
                        }}> <FontAwesomeIcon icon={"magnifying-glass"}/> SEARCH BY </Button>
                        <Button variant="outline-danger" onClick={clearFields}> <FontAwesomeIcon
                            icon="fa-solid fa-arrow-rotate-left"/> Reset filters </Button>
                    </Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Title" value={title} onChange={event => setTitle(event.target.value)} />
                                <label htmlFor="floatingTitle" > Title </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Keyword(s)" value={keywords} onChange={event => setKeywords(event.target.value)} />
                                <label htmlFor="floatingKeyword(s)" > Keyword(s) </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Type" value={type} onChange={event => setType(event.target.value)} />
                                <label htmlFor="floatingType(s)" > Type(s) </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Description" value={description} onChange={event => setDescription(event.target.value)} />
                                <label htmlFor="floatingDescription" > Description </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Required Knowledge" value={requiredKnowledge} onChange={event => setRequiredKnowledge(event.target.value)} />
                                <label htmlFor="floatingKnowledge" > Required Knowledge </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Notes" value={notes} onChange={event => setNotes(event.target.value)} />
                                <label htmlFor="floatingNotes" > Notes </label>
                            </Form.Floating>
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
                            <Form.Label>Group(s)</Form.Label>
                            <MultiSelect
                                options={groupsList}
                                value={selectedGroupIds}
                                onChange={setSelectedGroupIds}
                                labelledBy="Select Groups"
                            />
                        </Form.Group>
                    </Form>
                </Offcanvas.Body>
            </Offcanvas>

            <Card className="mt-3">
                <Card.Header><h1 className="my-3" style={{"textAlign": "center"}}>Results</h1></Card.Header>
                <Card.Body><ProposalsList proposals={proposalsList} user={user}
                                          applicationDate={applicationDate}/></Card.Body>
            </Card>
        </>
    );
}

function ProposalsList({proposals, user, applicationDate}) {
    return (
        <Accordion defaultActiveKey="0">
            {proposals.filter(proposal => dayjs(proposal.expiration).isAfter(applicationDate)).map(proposal =>
                <ProposalEntry key={proposal.id} proposal={proposal} user={user}/>)}
        </Accordion>
    )
}

function CustomToggle({eventKey, callback}) {
    const {activeEventKey} = useContext(AccordionContext);

    const decoratedOnClick = useAccordionButton(
        eventKey,
        () => callback && callback(eventKey),
    );

    const isCurrentEventKey = activeEventKey === eventKey;

    return (
        <Button
            onClick={decoratedOnClick}
        >
            <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"}/>
        </Button>
    );
}

function ProposalEntry({proposal}) {
    const navigate = useNavigate();

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><b>{proposal.title}</b></Col>
                    <Col className="d-flex justify-content-end">
                        {proposal.hasApplication ? <Button disabled={true}>
                                <><FontAwesomeIcon icon="fa-solid fa-check" className="me-2"/>Applied</>
                            </Button> : <Button onClick={() => navigate(`/proposal/apply/${proposal.id}`)}>
                            <><FontAwesomeIcon icon="fa-file" className="me-2"/>Apply</>
                        </Button>
                        }
                        <CustomToggle eventKey={proposal.id}/>
                    </Col>
                </Row>

            </Card.Header>
            <Accordion.Collapse eventKey={proposal.id} flush>
                <Card.Body>
                    <Row>
                        <Col><b>CdS</b><br/>{proposal.cds}</Col>
                        <Col><b>Groups</b><br/>{proposal.groups.map(g => g.name).join(", ")}</Col>
                        <Col><b>Level</b><br/>{proposal.level}</Col>
                        <Col><b>Type</b><br/>{proposal.type}</Col>
                    </Row>
                    <Row>
                        <Col><b>Keywords</b><br/>{proposal.keywords}</Col>
                        {proposal.requiredKnowledge.length > 0 &&
                            <Col><b>Required Knowledge</b><br/>{proposal.requiredKnowledge}</Col>
                        }
                        <Col><b>Expiration</b><br/>{dayjs(proposal.expiration).format("DD/MM/YYYY")}</Col>
                    </Row>
                    <Row className="pt-2">
                        <Col
                            md="3"><b>Supervisor</b><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}
                        </Col>
                        {proposal.coSupervisors.length > 0 &&
                            <Col
                                md="9"><b>Co-Supervisors</b><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}
                            </Col>
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

export default ProposalsListContent;