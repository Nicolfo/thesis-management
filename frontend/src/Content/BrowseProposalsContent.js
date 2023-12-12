import {useState} from "react";
import {useEffect, useContext} from "react";
import API from "../API/Api";
import {
    Accordion,
    Button,
    useAccordionButton,
    Card,
    Row,
    Col,
    AccordionContext,
    DropdownButton,
    Dropdown
} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate} from "react-router-dom";
import dayjs from "dayjs";
import {AuthContext} from "react-oauth2-code-pkce";
import Warn from "./Warn";

export default function BrowseProposalsContent(props) {

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role === "STUDENT")
        navigate("/notAuthorized");

    const getProposalList = async () => {
        const list = await API.getProposalsByProf(props.user.token);
        setProposalList(list);
    }

    const [proposalList, setProposalList] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [archive, setArchive] = useState(false);
    const [deletingID, setDeletingID] = useState();


    useEffect(() => {
        if (props.user && !deleting) {

            getProposalList();
        }
    }, [props.user, deleting, archive]);

    return (
        <>
            {deleting ? <Row>
                    <Col>
                        <Warn archivedProposal={false} setArchive={setArchive} archive={archive} user={props.user}
                              setDeleting={setDeleting} deletingID={deletingID} getProposalList={getProposalList}><h1>My
                            thesis proposals</h1></Warn>
                    </Col>
                </Row> :
                <>
                    <Card>
                        <Card.Header>
                            <h1 style={{"textAlign": "center", marginTop: "0.5rem", marginBottom: "0.5rem"}}>My thesis
                                proposals</h1>
                            <h2 style={{"textAlign": "center"}}>Active proposals</h2>
                        </Card.Header>
                        {proposalList.length > 0 ?
                            <Card.Body>
                                <Accordion defaultActiveKey="0">
                                    {proposalList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal => {
                                        return <ProposalAccordion setArchive={setArchive} user={props.user}
                                                                  key={proposal.id}
                                                                  proposal={proposal} setDeleting={setDeleting}
                                                                  setDeletingID={setDeletingID}/>
                                    })}
                                </Accordion>
                            </Card.Body> :
                            <Card.Body style={{"textAlign": "center"}} className="mt-4">
                                <strong>You have no proposals yet</strong>
                            </Card.Body>
                        }
                    </Card>
                </>
            }
        </>
    );
}

function CustomToggle({eventKey, callback}) {
    const {activeEventKey} = useContext(AccordionContext);

    const decoratedOnClick = useAccordionButton(
        eventKey,
        () => callback && callback(eventKey),
    );

    const isCurrentEventKey = activeEventKey === eventKey;

    return (
        <Button onClick={decoratedOnClick}>
            <div className="d-flex align-items-center">
                <FontAwesomeIcon icon={isCurrentEventKey ? "chevron-up" : "chevron-down"}/>
                <span className="d-none d-md-table-cell ms-2"> Info </span>
            </div>
        </Button>
    );
}

function ProposalAccordion({proposal, setDeleting, setDeletingID, user, setArchive}) {
    const navigate = useNavigate();

    function deleteProp(proposalId, archiving) {
        if (archiving) {
            setDeleting(true);
            setArchive(true);
            setDeletingID(proposalId);
        } else {
            setDeleting(true);
            setDeletingID(proposalId)
        }
    }

    return (
        <>
            <Card id={proposal.id} className="m-2">
                <Card.Header>
                    <Row className="p-2 align-items-center">
                        <Col><strong>{proposal.title}</strong></Col>
                        <Col className="d-flex justify-content-end">

                            <DropdownButton id="dropdown-item-button" title={
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon="fa-solid fa-list-ul"/>
                                    <span className="d-none d-md-table-cell ms-2"> Options </span>
                                </div>
                            }
                            >
                                <Dropdown.Item as="button" style={{color: "#FC7A08"}}
                                               onClick={() => navigate(`/updateProposal/${proposal.id}`)}>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon="fa-pencil"/>
                                        <span className="d-none d-md-table-cell ms-2"> Update </span>
                                    </div>
                                </Dropdown.Item>
                                <Dropdown.Item as="button" style={{color: "#FC7A08"}}
                                               onClick={() => navigate(`/copyProposal/${proposal.id}`)}>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon="fa-solid fa-copy"/>
                                        <span className="d-none d-md-table-cell ms-2"> Copy </span>
                                    </div>
                                </Dropdown.Item>
                                <Dropdown.Item as="button" style={{color: "#FC7A08"}} onClick={() => {
                                    deleteProp(proposal.id, true);
                                }}>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon="fa-solid fa-box-archive"/>
                                        <span className="d-none d-md-table-cell ms-2"> Archive </span>
                                    </div>
                                </Dropdown.Item>
                                <Dropdown.Item as="button" style={{color: "#FC7A08"}} onClick={() => {
                                    deleteProp(proposal.id, false);
                                }}>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon="fa-solid fa-trash-can"/>
                                        <span className="d-none d-md-table-cell ms-2"> Delete </span>
                                    </div>
                                </Dropdown.Item>
                            </DropdownButton>
                            <CustomToggle eventKey={proposal.id}/>
                        </Col>
                    </Row>
                </Card.Header>
                <Accordion.Collapse eventKey={proposal.id} flush>
                    <Card.Body>
                        <Row>
                            <Col><strong>CdS</strong><br/>{proposal.cds}</Col>
                            <Col><strong>Groups</strong><br/>{proposal.groups.map(g => g.name).join(", ")}</Col>
                            <Col><strong>Level</strong><br/>{proposal.level}</Col>
                            <Col><strong>Type</strong><br/>{proposal.type}</Col>
                        </Row>
                        <Row>
                            <Col><strong>Keywords</strong><br/>{proposal.keywords}</Col>
                            {proposal.requiredKnowledge.length > 0 &&
                                <Col><strong>Required Knowledge</strong><br/>{proposal.requiredKnowledge}</Col>
                            }
                            <Col><strong>Expiration</strong><br/>{dayjs(proposal.expiration).format("DD/MM/YYYY")}</Col>
                        </Row>
                        <Row className="pt-2">
                            <Col md="3">
                                <strong>Supervisor</strong><br/>{proposal.supervisor.surname + " " + proposal.supervisor.name}
                            </Col>
                            {proposal.coSupervisors && proposal.coSupervisors.length > 0 &&
                                <Col
                                    md="9"><strong>Co-Supervisors</strong><br/>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}
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
        </>
    )
}