import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {Button, Col, Form, Nav} from 'react-bootstrap';
import {useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";

function NavBar(props) {

    const [showVirtualClock, setShowVirtualClock] = useState(false);

    const path = useLocation().pathname;
    const navigate = useNavigate();

    const handleClick = (e) => {
        e.preventDefault();
        if (props.user === null)
            navigate("/login");
        else {
            props.setUser(null);
            localStorage.removeItem("email");
            localStorage.removeItem("token");
            localStorage.removeItem("role");
            navigate("/login");
        }
    }

    const userIsTeacher = () => {
        return props.user && props.user.role === "TEACHER";
    }
    const userIsStudent = () => {
        return props.user && props.user.role === "STUDENT";
    }

    return (
        (userIsStudent() || userIsTeacher()) &&
        <>
            <Navbar className="bg-color ps-3" expand="md" data-bs-theme="dark">
                <Container fluid>
                    <Navbar.Brand href="/">
                        <FontAwesomeIcon icon="fa-book"/> Thesis Manager
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="navbarResponsive"/>
                    <Navbar.Collapse id="navbarResponsive">
                        <Nav className="m-auto">
                            {userIsStudent() && (
                                <>
                                    <Button
                                        variant={path === '/search-for-proposal' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/search-for-proposal' ? {backgroundColor: "#3B85B7", color: "white"} : {}}
                                        onClick={() => {
                                            navigate('/search-for-proposal');
                                            props.searchForProposalClicked();
                                        }}
                                    >
                                        Search for proposal
                                    </Button>
                                    <Button
                                        variant={path === '/browseDecisions' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/browseDecisions' ? {backgroundColor: "#3B85B7", color: "white"} : {}}
                                        onClick={() => {
                                            navigate('/browseDecisions');
                                        }}
                                    >
                                        My applications decisions
                                    </Button>
                                </>
                            )}
                            {userIsTeacher() && (
                                <>
                                    <Button
                                        variant={path === '/teacher/proposals' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/proposals' ? {backgroundColor: "#3B85B7", color: "white"} : {}}
                                        onClick={() => {
                                            navigate('/teacher/proposals');
                                        }}
                                    >
                                        My thesis proposals
                                    </Button>
                                    <Button
                                        variant={path === '/insertProposal' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/insertProposal' ? {backgroundColor: "#3B85B7", color: "white"} : {}}
                                        onClick={() => {
                                            navigate('/insertProposal');
                                        }}
                                    >
                                        Insert proposal
                                    </Button>
                                    <Button
                                        variant={path === '/teacher/application/browse' || path === '/teacher/application/view' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/application/browse' || path === '/teacher/application/view' ? {backgroundColor: "#3B85B7", color: "white"} : {}}
                                        onClick={() => {
                                            navigate('/teacher/application/browse');
                                        }}
                                    >
                                        My application proposals
                                    </Button>
                                </>
                            )}
                        </Nav>
                        <Nav>
                            <div className="d-flex justify-content-center border-b">
                                {showVirtualClock && (
                                    <Col xs="auto" className="me-lg-2">
                                        <Form.Control
                                            className="dateForm no-border-sm"
                                            type="date"
                                            value={props.applicationDate.format('YYYY-MM-DD')}
                                            min={props.realDate.format('YYYY-MM-DD')}
                                            onChange={(event) => props.updateApplicationDate(event.target.value)}
                                        />
                                    </Col>
                                )}
                                <Button className="no-border-sm" onClick={() => setShowVirtualClock((val) => !val)}>
                                    <FontAwesomeIcon icon={showVirtualClock ? 'fa-xmark' : 'fa-clock'} />
                                </Button>
                            </div>

                            <Button className="ms-lg-3 me-lg-3 ms-md-3 me-md-3 border-b" onClick={handleClick}>
                                {props.user !== null ? 'Logout' : 'Login'}
                            </Button>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
}

export default NavBar;
