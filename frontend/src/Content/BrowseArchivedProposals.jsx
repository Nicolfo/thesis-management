import {
    Card,
    Form,
    Button,
    Row,
    Col,
    Accordion,
    Offcanvas,
    Alert,
    DropdownButton, Dropdown, OverlayTrigger, Tooltip
} from "react-bootstrap";
import API from "../API/Api";
import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { MultiSelect } from "react-multi-select-component";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import dayjs from "dayjs";
import {AuthContext} from "react-oauth2-code-pkce";
import Warn from "./Warn";

function BrowseArchivedProposals({user, applicationDate, setArchivedView}) {

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
    const [deleting, setDeleting] = useState(false);
    const [deletingID, setDeletingID] = useState();

    function deleteProp(proposalId) {
        setDeleting(true);
        setDeletingID(proposalId)
    }

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

        if (!user) {
            return;
        }

        const getResources = async () => {
            try {
                const teachers = await API.getAllSupervisors(user.token);
                setTeachersList(teachers.map(t => {
                    return {label: `${t.surname} ${t.name}`, value: t.id};
                }));
                const groups = await API.getAllGroups(user.token);
                setGroupsList(groups.map(g => {
                    return {label: `${g.name}`, value: g.codGroup};
                }));
                const proposals = await API.getArchivedProposalsByProf(user.token);
                setProposalsList(proposals);
            } catch (error) {
                handleError(error);
            }
        };
        getResources();
    }, [user])

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
            const proposals = await API.searchArchivedProposals(user.token, requestBody);
            setProposalsList(proposals);
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
            const proposals = await API.searchArchivedProposals(user.token, requestBody);
            setProposalsList(proposals);
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
            {deleting ? <Row><Col></Col><Col><Warn doSearch={doSearch} archivedProposal={true} user={user}
                                                   setDeleting={setDeleting} deletingID={deletingID}></Warn></Col>
                    <Col></Col></Row> :
                <>
                    <Row style={{"textAlign": "end"}}>
                        <Col>
                            <Button onClick={() => setShowSearchBar(it => !it)}> <FontAwesomeIcon
                                icon={"magnifying-glass"}/> Show searching filters </Button>
                        </Col>
                    </Row>
                    <Offcanvas show={showSearchBar} onHide={() => setShowSearchBar(false)} placement="end"
                               scroll={true}>
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
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Title" value={title}
                                                      onChange={event => setTitle(event.target.value)}/>
                                        <label htmlFor="floatingTitle"> Title </label>
                                    </Form.Floating>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Floating>
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Keyword(s)" value={keywords}
                                                      onChange={event => setKeywords(event.target.value)}/>
                                        <label htmlFor="floatingKeyword(s)"> Keyword(s) </label>
                                    </Form.Floating>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Floating>
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Type" value={type}
                                                      onChange={event => setType(event.target.value)}/>
                                        <label htmlFor="floatingType(s)"> Type(s) </label>
                                    </Form.Floating>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Floating>
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Description" value={description}
                                                      onChange={event => setDescription(event.target.value)}/>
                                        <label htmlFor="floatingDescription"> Description </label>
                                    </Form.Floating>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Floating>
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Required Knowledge"
                                                      value={requiredKnowledge}
                                                      onChange={event => setRequiredKnowledge(event.target.value)}/>
                                        <label htmlFor="floatingKnowledge"> Required Knowledge </label>
                                    </Form.Floating>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Floating>
                                        <Form.Control style={{borderRadius: "25px"}} type="text" placeholder="Notes" value={notes}
                                                      onChange={event => setNotes(event.target.value)}/>
                                        <label htmlFor="floatingNotes"> Notes </label>
                                    </Form.Floating>
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

                    <Card className="mt-3" style={{marginBottom: "1rem"}}>
                        <Card.Header><h1 className="my-3" style={{"textAlign": "center"}}>Results</h1></Card.Header>
                        {proposalsList.length > 0 ? <Card.Body><ProposalsList setArchivedView={setArchivedView} deleteProp={deleteProp}
                                                  proposals={proposalsList} user={user}
                                                  applicationDate={applicationDate}/></Card.Body>
                        : <Card.Body style={{"textAlign": "center"}} className="mt-4">
                                <strong>You have no archived proposals yet</strong>
                        </Card.Body>
                        }
                    </Card>
                </>} </>
    );
}

function ProposalsList({proposals, user, deleteProp, setArchivedView}) {
    return (

        <Accordion defaultActiveKey="0">
            {proposals.map(proposal => <ProposalEntry setArchivedView={setArchivedView} deleteProp={deleteProp}
                                                      key={proposal.id} proposal={proposal} user={user}/>)}
        </Accordion>
    )
}

function ProposalEntry({proposal, deleteProp, setArchivedView}) {
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
                    <Col md={10} onClick={toggleAccordion} className="cursor-pointer"><strong>{proposal.title}</strong></Col>
                    <Col md={2} className="d-flex justify-content-end mt-md-0 mt-3">
                        <DropdownButton id="dropdown-item-button"
                                        title={
                                            <div className="d-flex align-items-center">
                                                <FontAwesomeIcon icon="fa-solid fa-list-ul"/>
                                                <span className="d-none d-md-table-cell ms-2"> Options </span>
                                            </div>
                                        }
                                        className="me-2"
                        >

                            <Dropdown.Item as="button" style={{color: "#FC7A08"}} onClick={() => {
                                setArchivedView(true);
                                navigate(`/copyProposal/${proposal.id}`)
                            }}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-copy" className="me-1"/>
                                    <span className="d-none d-md-table-cell"> Copy </span>
                                </div>
                            </Dropdown.Item>

                            <OverlayTrigger overlay={proposal.status === "ACCEPTED" ? <Tooltip id="tooltip-disabled">Unable to delete because this proposal has been accepted!</Tooltip> : <></>} placement="left">
                                <span className="d-inline-block">
                                    <Dropdown.Item as="button" disabled={proposal.status === "ACCEPTED"} style={{color: proposal.status === "ACCEPTED" ? "#FBA65C" : "#FC7A08"}} onClick={() => {deleteProp(proposal.id);}}>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon="fa-solid fa-trash-can" className="me-1"/>
                                            <span className="d-none d-md-table-cell"> Delete </span>
                                        </div>
                                    </Dropdown.Item>
                                </span>
                            </OverlayTrigger>
                        </DropdownButton>
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
                        <Col>
                            <FontAwesomeIcon icon="fa-solid fa-calendar-days"/> <em>Expiration date: </em>{dayjs(proposal.expiration).format("DD/MM/YYYY")}
                        </Col>

                    <Row className="mt-4 mb-2">
                        <Col>
                            {truncated ? (
                                <>
                                    {proposal.description.length > 200 ? <Card.Text onClick={showFullText} className="cursor-pointer">{proposal.description.slice(0, 200)}...<span style={{ color: '#FC7A08', cursor: 'pointer' }}>See more</span></Card.Text> : proposal.description}
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

export default BrowseArchivedProposals;