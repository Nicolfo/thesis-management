import {useContext, useEffect, useState} from "react";
import {useParams, useNavigate } from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";
import API from "../API/Api";
import {
    Card,
    Row,
    Col,
    Button
} from "react-bootstrap";
import dayjs from "dayjs";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function ProposalViewLayout(props) {

    const [proposal, setProposal] = useState(null);

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role !== "STUDENT" && props.user.role !== "TEACHER")
        navigate("/notAuthorized");

    const {proposalId} = useParams();

    const getProposalById = async () => {
        if (props.user && props.user.token) {
            try {
                const proposal = await API.getProposalById(props.user.token, proposalId);
                setProposal(proposal);
            } catch (error) {
                console.error("Error fetching proposals:", error);
            }
        }
    }

    useEffect(() => {
        if (!props.user || (props.user.role !== "TEACHER" && props.user.role !== "STUDENT") )
            return;
        getProposalById();
    }, [props.user]);

    const userIsTeacher = () => {
        return props.user && props.user.role === "TEACHER";
    }
    const userIsStudent = () => {
        return props.user && props.user.role === "STUDENT";
    }

    const handleGoBack = () => {
        navigate(-1);
    };

    return (
        <>
            {proposal &&
                <>
                    <Card className="m-lg-5 m-md-3">
                        <Card.Header>
                            {proposal.type &&
                                <Col className="d-flex justify-content-end p-2" style={{fontSize: '1.2rem'}}>
                                    <strong>{proposal.type}</strong>
                                </Col>
                            }
                        </Card.Header>
                        <Card.Body className="p-5 m-lg-3">
                            <Row>
                                <Row className="mb-2" style={{fontSize: '1.3rem'}}>
                                    <strong>{proposal.title}</strong>
                                </Row>

                                <hr/>

                                <Row>
                                    <Col>
                                        <Row>
                                            <Col md={3}>
                                                <FontAwesomeIcon icon="fa-solid fa-calendar-days"/> <em>Expiration date: </em>{dayjs(proposal.expiration).format("DD/MM/YYYY")}
                                            </Col>
                                            {userIsStudent() &&
                                                <Col md={3}>
                                                    <FontAwesomeIcon icon="fa-solid fa-user" className="me-2"/>
                                                    <em>Supervisor: </em>{proposal.supervisor.surname} {proposal.supervisor.name}
                                                </Col>
                                            }
                                            <Col md={(userIsStudent() && 6) || (userIsTeacher() && 9)}>
                                                <FontAwesomeIcon icon="fa-solid fa-users" className="me-2"/>
                                                <em>Co-supervisors:</em> {proposal.coSupervisors.length > 0 && <>{proposal.coSupervisors.map(coSupervisor => coSupervisor.surname + " " + coSupervisor.name).join(", ")}</>}
                                            </Col>

                                        </Row>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="4" className="pt-4 order-md-2 order-sm-1">
                                        <Card className="p-4">
                                            {userIsTeacher() && proposal.level &&
                                                <Col>
                                                    <strong>Level: </strong>{proposal.level}
                                                </Col>
                                            }

                                            {userIsTeacher() && proposal.cds &&
                                                <Col>
                                                    <strong>CdS: </strong> {proposal.cds}
                                                </Col>
                                            }

                                            {proposal.groups && proposal.groups.length > 0 &&
                                                <Col>
                                                    <strong>Groups: </strong>{proposal.groups.map(g => g.name).join(", ")}
                                                </Col>
                                            }

                                            {proposal.requiredKnowledge.length > 0 &&
                                                <Col className="mt-2">
                                                    <FontAwesomeIcon icon="fa-solid fa-book" className="me-2"/>
                                                    <strong>Required knowledge: </strong>{proposal.requiredKnowledge}
                                                </Col>
                                            }

                                            {proposal.notes &&
                                                <Col className="mt-2">
                                                    <strong>Notes: </strong><i>{proposal.notes}</i>
                                                </Col>
                                            }

                                            {proposal.keywords &&
                                                <Col className="mt-2">
                                                    <FontAwesomeIcon icon="fa-solid fa-tag" className="me-2"/>
                                                    <strong>Keywords: </strong>{proposal.keywords}
                                                </Col>
                                            }
                                        </Card>
                                    </Col>

                                    <Col md="8" className="pe-lg-3 pt-5 order-md-1 order-sm-2" style={{fontSize: '1.1rem'}}>
                                        {proposal.description}
                                    </Col>
                                </Row>
                            </Row>
                        </Card.Body>
                    </Card>
                    <Col style={{textAlign: "end"}}>
                        <Button className="me-lg-5 mb-5 mt-3 mt-lg-0" onClick={handleGoBack}>
                            <FontAwesomeIcon icon={"chevron-left"}/> Go Back
                        </Button>
                    </Col>
                </>
            }
        </>
    );
}


export default ProposalViewLayout;