import {useNavigate, useLocation} from 'react-router-dom';
import {Button, Nav, Navbar, Container, Col} from 'react-bootstrap';

function SideBar(props) {
    const path = useLocation().pathname;
    const navigate = useNavigate();

    const userIsTeacher = () => {
        return props.user && props.user.role === "TEACHER";
    }
    const userIsStudent = () => {
        return props.user && props.user.role === "STUDENT";
    }
    if (props.user)
        return (
            <Navbar bg="light" expand={false}>
                <Container fluid>
                    <Navbar.Toggle aria-controls="navbar-nav"/>
                    <Navbar.Collapse id="navbar-nav">
                        <Nav className="flex-column">
                            {userIsStudent() && <>
                                <Col xs={12}>
                                    <Button variant="light" block
                                            className={path === '/search-for-proposal' ? "active" : ""} onClick={() => {
                                        navigate('/search-for-proposal');
                                        props.searchForProposalClicked()
                                    }}>
                                        Search for proposal
                                    </Button>
                                </Col>
                                <Col xs={12}>
                                    <Button variant="light" block
                                            className={path === '/browseDecisions' ? "active" : ""} onClick={() => {
                                        navigate('/browseDecisions')
                                    }}>
                                        My applications decisions
                                    </Button>
                                </Col>
                            </>
                            }
                            {
                                userIsTeacher() && <>
                                    <Col xs={12}>
                                        <Button variant="light" block
                                                className={path === '/teacher/proposals' ? "active" : ""} onClick={() => {
                                            navigate('/teacher/proposals')
                                        }}>
                                            My thesis proposals
                                        </Button>
                                    </Col>
                                    <Col xs={12}>
                                        <Button variant="light" block className={path === '/insertProposal' ? "active" : ""}
                                                onClick={() => {
                                                    navigate('/insertProposal')
                                                }}>
                                            Insert Proposal
                                        </Button>
                                    </Col>
                                    <Col xs={12}>
                                        <Button variant="light" block
                                                className={path === '/teacher/application/browse' || path === '/teacher/application/view' ? "active" : ""}
                                                onClick={() => {
                                                    navigate('/teacher/application/browse')
                                                }}>
                                            My application proposals
                                        </Button>
                                    </Col>
                                </>
                            }
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        )
}


export default SideBar;