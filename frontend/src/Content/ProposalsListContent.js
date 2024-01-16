import {
    Card,
    Form,
    Button,
    Row,
    Col,
    Accordion,
    Offcanvas,
    Alert
} from "react-bootstrap";
import API from "../API/Api";
import {useState, useEffect, useContext} from "react";
import {useNavigate} from "react-router-dom";
import {MultiSelect} from "react-multi-select-component";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import dayjs from "dayjs";
import {AuthContext} from "react-oauth2-code-pkce";

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
    const [disableButtons, setDisableButtons] = useState(false);



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
                    if(hasApplication) {
                        setDisableButtons(true);
                    }
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
        setDisableButtons(false);
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
                if(hasApplication) {
                    setDisableButtons(true);
                }
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
                if(hasApplication) {
                    setDisableButtons(true);
                }
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
                                          applicationDate={applicationDate} disableButtons={disableButtons}/></Card.Body>
                    : <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>You have no proposals yet</strong>
                    </Card.Body>}
            </Card>
        </>
    );
}

function ProposalsList({proposals, user, applicationDate, disableButtons}) {
    return (
        <Accordion defaultActiveKey="0">
            {proposals.filter(proposal => dayjs(proposal.expiration).isAfter(applicationDate)).map(proposal =>
                <ProposalEntry key={proposal.id} proposal={proposal} user={user} disableButtons={disableButtons}/>)}
        </Accordion>
    )
}

function ProposalEntry({proposal, disableButtons}) {
    const navigate = useNavigate();

    const [isAccordionOpen, setIsAccordionOpen] = useState(false);

    function toggleAccordion() {
        setIsAccordionOpen(!isAccordionOpen);
    }

    const [truncated, setTruncated] = useState(true);

    const showFullText = () => {
        setTruncated(false);
    };

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col md={10} onClick={toggleAccordion}><strong>{proposal.title}</strong></Col>
                    <Col md={2} className="d-flex justify-content-end mt-md-0 mt-3">
                        {proposal.hasApplication ? <Button disabled={true}>
                            <><FontAwesomeIcon icon="fa-solid fa-check" className="me-2"/>Applied</>
                        </Button> : <Button disabled={disableButtons} onClick={() => navigate(`/proposal/apply/${proposal.id}`)}>
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
                        <Col md={4}>
                            <FontAwesomeIcon icon="fa-solid fa-calendar-days"/> <em>Expiration date: </em>{dayjs(proposal.expiration).format("DD/MM/YYYY")}
                        </Col>
                        <Col md={4}>
                            <FontAwesomeIcon icon="fa-solid fa-users"/> <em>Supervisor: </em>{proposal.supervisor.surname} {proposal.supervisor.name}
                        </Col>
                        <Col md={4}>
                            {proposal.type &&
                                <Col md={6}>
                                    <em>Type: </em>{proposal.type}
                                </Col>
                            }                        </Col>
                    </Row>
                    <Row className="mt-4 mb-2">
                        <Col>
                            {truncated ? (
                                <>
                                    {proposal.description.length > 200 ? <Card.Text onClick={showFullText}>{proposal.description.slice(0, 200)}...<span style={{ color: '#FC7A08', cursor: 'pointer' }}>See more</span></Card.Text> : proposal.description}
                                </>
                            ) : (
                                <>
                                    <Card.Text onClick={() => setTruncated(true)}>{proposal.description} <span style={{ color: '#FC7A08', cursor: 'pointer' }}>See less</span></Card.Text>
                                </>
                            )}
                        </Col>
                    </Row>
                    <div className="d-flex align-items-end">
                        <Button className="ms-auto my-2" onClick={() => navigate(`/proposal/view/${proposal.id}`)}>
                            Detailed Info <FontAwesomeIcon className="ms-1 pt-1" icon={"chevron-right"}/>
                        </Button>
                    </div>
                </Card.Body>
            </Accordion.Collapse>
        </Card>
    )
}

export default ProposalsListContent;