import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {Button, Col, Form, Nav, Row} from 'react-bootstrap';
import {useState, useContext, useEffect} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {AuthContext} from 'react-oauth2-code-pkce';
import logoPolitecnico from './logoPolitecnico.png';
import API from '../API/Api';


function NavBar(props) {
    const {tokenData, token, login, logOut, loginInProgress, error} = useContext(AuthContext);

    const [showVirtualClock, setShowVirtualClock] = useState(false);
    const [date,setDate]=useState(props.applicationDate.format('YYYY-MM-DD'));
    const [unreadNotifications, setUnreadNotifications] = useState(0);

    const getNotificationsIntervalMs = 30000;

    const path = useLocation().pathname;
    const navigate = useNavigate();
    const handleClick = (e) => {
        e.preventDefault();

        if (props.user === undefined || props.user === null) {
            login();
        } else {

            logOut();
        }
    }

    /**
     * Every 30 seconds, fetch the notification list
     * for the current user, and keep track of the number of unread ones
     */
    useEffect(() => {
        const getNotifications = async () => {
            try {
                const unread = await API.getUnreadNotificationsCount(token);
                setUnreadNotifications(unread);
            } catch (e) {
                console.log(e);
            }
            setTimeout(getNotifications, getNotificationsIntervalMs);
        }
        getNotifications();
    }, []);

    /**
     * Every time tokenData (the decoded token for the logged-in user on the browser) changes, we update the
     * corresponding user state so that it is available for all the components.
     * The user state is an object containing the user's email, name, surname, role and token.
     */
    useEffect(() => {
        if (!props.user && tokenData) {
            let role;
            const roles = tokenData.realm_access.roles;

            switch (true) {
                case roles.includes("STUDENT"):
                    role = "STUDENT";
                    break;
                case roles.includes("TEACHER"):
                    role = "TEACHER";
                    break;
                case roles.includes("SECRETARY"):
                    role = "SECRETARY";
                    break;
                // Add more cases if needed
                default:
                    // Handle the case when no role is found
                    break;

            }
            props.setUser({
                email: tokenData.preferred_username,
                role: role,
                name: tokenData.firstName,
                surname: tokenData.lastName,
                token: token
            });
        }
    }, [tokenData]);

    const userIsTeacher = () => {
        return props.user && props.user.role === "TEACHER";
    }
    const userIsStudent = () => {
        return props.user && props.user.role === "STUDENT";
    }

    return (

        <>
            <Navbar className={props.user ? 'bg-color ps-3' : 'bg-color-start ps-3'} expand="lg" data-bs-theme="dark">
                <Container fluid>
                    <Navbar.Brand href="/">
                        {props.user ? <><FontAwesomeIcon icon="fa-book"/> Thesis Manager</> : <><img
                            src={logoPolitecnico} alt="Politecnico's logo" style={{height: "5rem"}}/></>}
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="navbarResponsive"/>
                    <Navbar.Collapse id="navbarResponsive">
                        <Nav className="m-auto">
                            {userIsStudent() && (
                                <>
                                    <Button
                                        variant={path === '/search-for-proposal' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/search-for-proposal' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/search-for-proposal');
                                            props.searchForProposalClicked();
                                        }}
                                    >
                                        Search for proposal
                                    </Button>
                                    <Button
                                        variant={path === '/browseDecisions' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/browseDecisions' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/browseDecisions');
                                        }}
                                    >
                                        My applications decisions
                                    </Button>
                                    <Button
                                        variant={path === '/startRequest' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/startRequest' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/startRequest');
                                        }}
                                    >
                                        Start thesis request
                                    </Button>
                                </>
                            )}
                            {userIsTeacher() && (
                                <>
                                    <Button
                                        variant={path === '/teacher/proposals' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/proposals' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            props.setArchivedView(false);
                                            navigate('/teacher/proposals');
                                        }}
                                    >
                                        My thesis proposals
                                    </Button>
                                    <Button
                                        variant={path === '/insertProposal' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/insertProposal' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/insertProposal');
                                        }}
                                    >
                                        Insert proposal
                                    </Button>
                                    <Button
                                        variant={path === '/teacher/application/browse' || path === '/teacher/application/view' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/application/browse' || path === '/teacher/application/view' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/teacher/application/browse');
                                        }}
                                    >
                                        My application proposals
                                    </Button>

                                    <Button
                                        variant={path === '/teacher/proposals/archived' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/proposals/archived' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/teacher/proposals/archived');
                                        }}
                                    >
                                        My archive
                                    </Button>

                                    <Button
                                        variant={path === '/teacher/proposalOnRequest/browse' ? "border-b no-border-lg margin-end-lg" : "primary border-b no-border-lg margin-end-lg"}
                                        style={path === '/teacher/proposalOnRequest/browse' ? {
                                            backgroundColor: "#FEA65A",
                                            color: "white"
                                        } : {}}
                                        onClick={() => {
                                            navigate('/teacher/proposalOnRequest/browse');
                                        }}
                                    >
                                        My thesis requests
                                    </Button>
                                </>
                            )}
                        </Nav>
                        <Nav>
                            {/*{props.user && <div className="d-flex justify-content-center border-b">
                                <Button className="ms-lg-3 me-lg-3 ms-md-3 me-md-3 border-b"
                                        onClick={() => console.log("hello")}>
                                    <FontAwesomeIcon icon="fa-bell"/>
                                </Button>
                                    </div>} */}

                            <div className="d-flex justify-content-center border-b">
                                {props.user && 
                                <Button className="no-border-sm me-3"
                                        onClick={() => navigate("/notifications")}>
                                    <FontAwesomeIcon icon="fa-bell"/>
                                    {path !== "/notifications" && unreadNotifications > 0 && <span className="badge text-bg-light text-danger ms-2 ">
                                        {unreadNotifications}
                                    </span>}
                                </Button>}
                                {showVirtualClock && (
  
                                    <Col xs="auto" className="me-lg-2">
                                        <Row>
                                            <Col><Form.Control
                                                className={props.user ? "dateForm no-border-sm" : "dateForm-start no-border-sm"}
                                                type="date"
                                                value={date}
                                                min={props.realDate.format('YYYY-MM-DD')}
                                                onChange={(event) => setDate(event.target.value)}
                                            />
                                            </Col>
                                            <Col>
                                                <Button
                                                        onClick={(event) => props.updateApplicationDate(date)}>Set</Button>
                                            </Col></Row>
                                    </Col>
                                )}
                                <Button className={props.user ? "no-border-sm" : "no-border-sm btn-primary-start"}
                                        onClick={() => setShowVirtualClock((val) => !val)}>
                                    <FontAwesomeIcon icon={showVirtualClock ? 'fa-xmark' : 'fa-clock'}/>
                                </Button>
                            </div>

                            <Button
                                className={props.user ? "ms-lg-3 me-lg-3 ms-md-3 me-md-3 border-b" : "btn-primary-start ms-lg-3 me-lg-3 ms-md-3 me-md-3 border-b"}
                                onClick={handleClick}>
                                {props.user !== null ? <><FontAwesomeIcon icon="fa-solid fa-user"/> Logout</> : <>
                                    <FontAwesomeIcon icon="fa-solid fa-user"/> Login</>}
                            </Button>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
}

export default NavBar;
