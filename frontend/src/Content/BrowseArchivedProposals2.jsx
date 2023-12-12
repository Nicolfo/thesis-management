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

function BrowseArchivedProposals2({ user, applicationDate, setArchivedView }) {

    const navigate = useNavigate();

    // const levelOptions = [
    //     "Any",
    //     "Bachelor's",
    //     "Master's"
    // ];
    const {token} = useContext(AuthContext);
    const [showSearchBar,setShowSearchBar] = useState(false);
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


    function deleteProp(proposalId){

            setDeleting(true);
            setDeletingID(proposalId)
        }


    const clearFields = ()=> {
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
        if( !token )
            navigate("/notAuthorized");

        if (!user) {
            return;
        }

        const getResources = async () => {
            try {
                const teachers = await API.getAllSupervisors(user.token);
                setTeachersList(teachers.map(t => { return { label: `${t.surname} ${t.name}`, value: t.id }; }));
                const groups = await API.getAllGroups(user.token);
                setGroupsList(groups.map(g => { return { label: `${g.name}`, value: g.codGroup }; }));
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
            { error !== "" &&
                <Row>
                    <Col>
                        <Alert variant="danger" dismissible onClose={() => setError("")} >
                            {error}
                        </Alert>
                    </Col>
                </Row>
            }
            {deleting? <Row><Col></Col><Col><Warn doSearch={doSearch}  archivedProposal={true} user={user} setDeleting={setDeleting} deletingID={deletingID} ></Warn></Col> <Col></Col></Row>:
                <>
                    <h2>Archive</h2>
            <Row style={{"textAlign": "end"}}>
                <Col>
                    <Button onClick={()=> setShowSearchBar(it=> !it )}> <FontAwesomeIcon icon={"magnifying-glass"} /> Show searching filters </Button>
                </Col>
            </Row>
            <Offcanvas show={showSearchBar} onHide={() => setShowSearchBar(false)} placement="end" scroll={true} >
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>
                        <Button variant="outline-primary" onClick={() => { doSearch(); setShowSearchBar(false); }}> <FontAwesomeIcon icon={"magnifying-glass"} /> SEARCH BY </Button>
                        {" "}
                        <Button variant="outline-danger" size="sm" onClick={clearFields}> <FontAwesomeIcon icon="fa-solid fa-arrow-rotate-left" /> Reset filters </Button>
                    </Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Title" value={title} onChange={event => setTitle(event.target.value)} />
                                <label htmlFor="floatingTitle" > Title </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Keyword(s)" value={keywords} onChange={event => setKeywords(event.target.value)} />
                                <label htmlFor="floatingKeyword(s)" > Keyword(s) </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Type" value={type} onChange={event => setType(event.target.value)} />
                                <label htmlFor="floatingType(s)" > Type(s) </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Description" value={description} onChange={event => setDescription(event.target.value)} />
                                <label htmlFor="floatingDescription" > Description </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Required Knowledge" value={requiredKnowledge} onChange={event => setRequiredKnowledge(event.target.value)} />
                                <label htmlFor="floatingKnowledge" > Required Knowledge </label>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Floating>
                                <Form.Control type="text" placeholder="Notes" value={notes} onChange={event => setNotes(event.target.value)} />
                                <label htmlFor="floatingNotes" > Notes </label>
                            </Form.Floating>
                        </Form.Group>
                        {/*<Form.Group className="mb-3">*/}
                        {/*    <Form.Label>CdS</Form.Label>*/}
                        {/*    <MultiSelect*/}
                        {/*        options={cdsList}*/}
                        {/*        value={selectedCds}*/}
                        {/*        onChange={setSelectedCds}*/}
                        {/*        labelledBy="Select CdS"*/}
                        {/*    />*/}
                        {/*</Form.Group>*/}

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
                        {/*<Form.Group className="mb-3">*/}
                        {/*    <Form.Label> Level </Form.Label>*/}
                        {/*    <Form.Select name="level" value={level} onChange={(e) => setLevel(e.target.value)}>*/}
                        {/*        { levelOptions.map(levelOption => <option value={levelOption}>{levelOption}</option>) }*/}
                        {/*    </Form.Select>*/}
                        {/*</Form.Group>*/}
                        {/*<Form.Group className="mb-3">*/}
                        {/*    <Form.Label>Min. Expiration Date</Form.Label>*/}
                        {/*    <Form.Control type="date" value={minExpiration} onChange={event => setMinExpiration(event.target.value)} />*/}
                        {/*</Form.Group>*/}
                        {/*<Form.Group className="mb-3">*/}
                        {/*    <Form.Label>Max. Expiration Date</Form.Label>*/}
                        {/*    <Form.Control type="date" value={maxExpiration} onChange={event => setMaxExpiration(event.target.value)} />*/}
                        {/*</Form.Group>*/}
                    </Form>
                </Offcanvas.Body>
            </Offcanvas>

            <Card className="mt-3">
                <Card.Header><b>Results</b></Card.Header>
                <Card.Body><ProposalsList setArchivedView={setArchivedView} deleteProp={deleteProp} proposals={proposalsList} user={user} applicationDate={applicationDate} /></Card.Body>
            </Card>
                </>} </>
);
}

function ProposalsList({ proposals, user, applicationDate, deleteProp, setArchivedView }) {
    return (

        <Accordion defaultActiveKey="0">
            { proposals.map(proposal => <ProposalEntry setArchivedView={setArchivedView} deleteProp={deleteProp} key={proposal.id} proposal={proposal} user={user} />) }
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
            <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"} />
        </Button>
    );
}

function ProposalEntry({ proposal, user, deleteProp, setArchivedView }) {
    const navigate = useNavigate();

    return (
        <Card id={proposal.id} className="m-2">
            <Card.Header>
                <Row className="p-2 align-items-center">
                    <Col><b>{proposal.title}</b></Col>
                    <Col className="d-flex justify-content-end">
                        <DropdownButton id="dropdown-item-button" title={
                            <div className="d-flex align-items-center">
                                <FontAwesomeIcon icon="fa-solid fa-list-ul" />
                                <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                <span className="d-none d-md-table-cell"> Options </span>
                            </div>
                        }
                        >

                            <Dropdown.Item as="button" style={{color: "#FC7A08"}} onClick={() => { setArchivedView(true);  navigate(`/copyProposal/${proposal.id}`)}}>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-copy" />
                                    <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                    <span className="d-none d-md-table-cell"> Copy </span>
                                </div>
                            </Dropdown.Item>
                            
                            <OverlayTrigger overlay={proposal.status === "ACCEPTED" ? <Tooltip id="tooltip-disabled">Unable to delete because this proposal has been accepted!</Tooltip> : <></>} placement="left">
                                <span className="d-inline-block">
                                    <Dropdown.Item as="button" disabled={proposal.status === "ACCEPTED"} style={{color: proposal.status === "ACCEPTED" ? "#FBA65C" : "#FC7A08"}} onClick={() => {deleteProp(proposal.id);}}>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon="fa-solid fa-trash-can" />
                                            <span className="d-none d-md-table-cell" style={{visibility: "hidden"}}> _ </span>
                                            <span className="d-none d-md-table-cell"> Delete </span>
                                        </div>
                                    </Dropdown.Item>
                                </span>
                            </OverlayTrigger>
                        </DropdownButton>
                        <CustomToggle eventKey={proposal.id} />
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

export default BrowseArchivedProposals2;