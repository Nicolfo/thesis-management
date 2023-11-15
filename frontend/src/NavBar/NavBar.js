import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Form, Nav } from 'react-bootstrap';
import { useState } from 'react';
import {useNavigate} from "react-router-dom";

function NavBar(props) {

    const [showVirtualClock, setShowVirtualClock] = useState(false);

    const navigate = useNavigate();
    const handleClick=(e)=>{
        e.preventDefault();
        console.log(props.user)
        if(props.user===null)
            navigate("/login");
        else{

            props.setUser(null);
            navigate("/login");
        }
    }

    return (
        <Navbar className='bg-color ps-3' data-bs-theme="dark">
        <Container fluid>
          <Navbar.Brand><FontAwesomeIcon icon="fa-book"/>{" "}Thesis Manager</Navbar.Brand>
          <Nav className="justify-content-end">
            { showVirtualClock && 
            <Form.Control className="dateForm" type="date" value={props.applicationDate.format("YYYY-MM-DD")} min={props.realDate.format("YYYY-MM-DD")} onChange={event => props.updateApplicationDate(event.target.value)}/>
            }
            <Button className="ms-2" onClick={() => setShowVirtualClock(val => !val)}><FontAwesomeIcon icon={ showVirtualClock ? "fa-xmark" : "fa-clock"} /></Button>
              <Button className="ms-2 me-3" onClick={handleClick}>
                  {props.user!==null ? 'Logout' : 'Login'}
              </Button>
          </Nav>
        </Container>
      </Navbar>
    );
}

export default NavBar;