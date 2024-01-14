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

    const doSearchReset = async () => {
        const requestBody = {
            title: null,
            supervisorIdList: null,
            coSupervisorIdList: null,
            keywords: null,
            type: null,
            codGroupList: null,
            description: null,
            requiredKnowledge: null,
            notes: null
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
                        <Button variant="outline-primary" onClick={() => {
                            doSearch();
                            setShowSearchBar(false);
                        }}> <FontAwesomeIcon icon={"magnifying-glass"}/> SEARCH BY </Button>
                        {" "}
                        <Button variant="outline-danger" onClick={() => {clearFields(); doSearchReset();}}>
                            <FontAwesomeIcon icon="fa-solid fa-arrow-rotate-left"/> Reset filters
                        </Button>
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
                { proposalsList.length > 0 ? <Card.Body><ProposalsList proposals={proposalsList} user={user}
                                          applicationDate={applicationDate}/></Card.Body>
                    : <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>You have no proposals yet</strong>
                    </Card.Body>}
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

function ProposalEntry({proposal}) {
    const navigate = useNavigate();

    const [isAccordionOpen, setIsAccordionOpen] = useState(false);

    function toggleAccordion() {
        setIsAccordionOpen(!isAccordionOpen);
    }

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col md={10} onClick={toggleAccordion}><strong>{proposal.title}</strong></Col>
                    <Col md={2} className="d-flex justify-content-end mt-md-0 mt-3">
                        {proposal.hasApplication ? <Button disabled={true}>
                            <><FontAwesomeIcon icon="fa-solid fa-check" className="me-2"/>Applied</>
                        </Button> : <Button onClick={() => navigate(`/proposal/apply/${proposal.id}`)}>
                            <><FontAwesomeIcon icon="fa-file" className="me-2"/>Apply</>
                        </Button>
                        }

                        <Button onClick={toggleAccordion}>
                            <div className="d-flex align-items-center">
                                <FontAwesomeIcon icon={isAccordionOpen ? "chevron-up" : "chevron-down"}/>
                            </div>
                        </Button>
                    </Col>
                </Row>

            </Card.Header>
            <Accordion.Collapse eventKey={proposal.id} flush in={isAccordionOpen}>
                <Card.Body>
                    <Row>
                        <Col md="2" style={{marginTop: "0.5rem"}}>
                            <strong>Supervisor</strong><br/>{proposal.supervisor.surname} {proposal.supervisor.name}
                        </Col>
                        {proposal.coSupervisors.length > 0 ?
                            <Col md="4" style={{marginTop: "0.5rem"}}>
                                <strong>Co-Supervisors</strong><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}
                            </Col>
                            :
                            <Col md="4"></Col>
                        }
                        {proposal.groups && proposal.groups.length > 0 &&
                            <Col md="6" style={{marginTop: "0.5rem"}}>
                                <strong>Groups</strong><br/>{proposal.groups.map(g => g.name).join(", ")}
                            </Col>
                        }
                    </Row>
                    <Row>
                        {proposal.type &&
                            <Col md="6" style={{marginTop: "0.5rem"}}>
                                <strong>Type</strong><br/>{proposal.type}
                            </Col>
                        }
                        <Col md="6" style={{marginTop: "0.5rem"}}>
                            <strong>Expiration</strong><br/>{dayjs(proposal.expiration).format("DD/MM/YYYY")}
                        </Col>
                    </Row>
                    <Row>
                        {proposal.keywords &&
                            <Col md="6" style={{marginTop: "0.5rem"}}>
                                <strong>Keywords</strong><br/>{proposal.keywords}
                            </Col>
                        }
                        {proposal.requiredKnowledge.length > 0 &&
                            <Col md="6" style={{marginTop: "0.5rem"}}>
                                <strong>Required Knowledge</strong><br/>{proposal.requiredKnowledge}
                            </Col>
                        }
                    </Row>
                    <hr className="me-4"/>
                    <Row style={{marginBottom: "0.5rem"}}>
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