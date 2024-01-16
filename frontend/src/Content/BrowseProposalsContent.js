import {useState} from "react";
import {useEffect, useContext} from "react";
import API from "../API/Api";
import {
    Accordion,
    Button,
    Card,
    Row,
    Col,
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
        const coSupervisorList = await API.getProposalsByCoSupervisor(props.user.token);
        setProposalCosupervisorList(coSupervisorList);
    }

    const [proposalList, setProposalList] = useState([]);
    const [proposalCosupervisorList, setProposalCosupervisorList] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [archive, setArchive] = useState(false);
    const [deletingID, setDeletingID] = useState();

    useEffect(() => {
        if (props.user && !deleting)
        if (props.user && !deleting) {
            getProposalList();
        }
    }, [props.user, deleting, archive]);

    return (
        <>
            {deleting ?
                <Row>
                    <Col>
                        <Warn archivedProposal={false} setArchive={setArchive} archive={archive} user={props.user}
                              setDeleting={setDeleting} deletingID={deletingID} getProposalList={getProposalList}><h1>My
                            thesis proposals</h1></Warn>
                    </Col>
                </Row>
                :
                <>
                    <Card>
                        <Card.Header>
                            <h1 style={{"textAlign": "center", marginTop: "0.5rem", marginBottom: "0.5rem"}}>Active proposals</h1>
                            <h2 style={{"textAlign": "center"}}>as <b>Supervisor</b></h2>
                        </Card.Header>
                        {proposalList.length > 0 ?
                            <Card.Body>
                                <Accordion defaultActiveKey="0">
                                    {proposalList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal => {
                                        return <ProposalAccordion setArchive={setArchive} user={props.user}
                                                                  key={proposal.id}
                                                                  proposal={proposal} setDeleting={setDeleting}
                                                                  setDeletingID={setDeletingID}
                                                                  coSupervisor={false}/>
                                    })}
                                </Accordion>
                            </Card.Body>
                            :
                            <Card.Body style={{"textAlign": "center"}} className="mt-4">
                                <strong>You have no proposals yet</strong>
                            </Card.Body>
                        }
                    </Card>

                    <Card style={{marginTop: "4rem", marginBottom: "2rem"}}>
                        <Card.Header>
                            <h1 style={{"textAlign": "center", marginTop: "0.5rem", marginBottom: "0.5rem"}}>Active proposals</h1>
                            <h2 style={{"textAlign": "center"}}>as <i>Co-Supervisor</i></h2>
                        </Card.Header>
                        {proposalCosupervisorList.length > 0 ?
                            <Card.Body>
                                <Accordion defaultActiveKey="0">
                                    {proposalCosupervisorList.filter(proposal => dayjs(proposal.expiration).isAfter(props.applicationDate)).map(proposal => {
                                        return <ProposalAccordion user={props.user}
                                                                  key={proposal.id}
                                                                  proposal={proposal}
                                                                  coSupervisor={true}/>
                                    })}
                                </Accordion>
                            </Card.Body>
                            :
                            <Card.Body style={{"textAlign": "center", marginBottom: "1rem"}} className="mt-4">
                                <strong>You are not a co-supervisor of any proposal yet</strong>
                            </Card.Body>
                        }
                    </Card>
                </>
            }
        </>
    );
}

function ProposalAccordion({proposal, setDeleting, setDeletingID, user, setArchive, coSupervisor}) {
    const [isAccordionOpen, setIsAccordionOpen] = useState(false);

    const navigate = useNavigate();

    function toggleAccordion() {
        setIsAccordionOpen(!isAccordionOpen);
    }

    function deleteProp(proposalId, archiving) {
        if (archiving) {
            setDeleting(true);
            setArchive(true);
            setDeletingID(proposalId);
        } else {
            setDeleting(true);
            setDeletingID(proposalId);
        }
    }

    const [truncated, setTruncated] = useState(true);

    const showFullText = () => {
        setTruncated(false);
    };

    return (
        <>
            <Card id={proposal.id} className="m-2">
                <Card.Header>
                    <Row className="p-2 align-items-center">
                        <Col md={10} onClick={toggleAccordion} className="cursor-pointer"><strong>{proposal.title}</strong></Col>
                        <Col md={2} className="d-flex justify-content-end mt-md-0 mt-3">
                            { !coSupervisor &&
                                <DropdownButton id="dropdown-item-button"
                                                title={
                                                    <div className="d-flex align-items-center">
                                                        <FontAwesomeIcon icon="fa-solid fa-list-ul"/>
                                                        <span className="d-none d-md-table-cell ms-2"> Options </span>
                                                    </div>
                                                }
                                                className="me-2"
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
        </>
    )
}