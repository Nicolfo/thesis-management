import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Form, Nav } from 'react-bootstrap';
import { useState } from 'react';

function NavBar({ realDate, applicationDate, updateApplicationDate }) {

    const [showVirtualClock, setShowVirtualClock] = useState(false);

    return (
        <Navbar bg="primary" data-bs-theme="dark">
        <Container fluid>
          <Navbar.Brand><FontAwesomeIcon icon="fa-book"/>{" "}Thesis Manager</Navbar.Brand>
          <Nav className="justify-content-end">
            { showVirtualClock && 
            <Form.Control type="date" value={applicationDate.format("YYYY-MM-DD")} min={realDate.format("YYYY-MM-DD")} onChange={event => updateApplicationDate(event.target.value)}/>
            }
            <Button className="ms-2" onClick={() => setShowVirtualClock(val => !val)}><FontAwesomeIcon icon={ showVirtualClock ? "fa-xmark" : "fa-clock"} /></Button>
            <Button className="ms-2"><FontAwesomeIcon icon="fa-user"/></Button>
          </Nav>
        </Container>
      </Navbar>
    );
}

export default NavBar;