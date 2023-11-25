import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Form, Nav } from 'react-bootstrap';
import { useState, useContext, useEffect } from 'react';
import {useNavigate} from "react-router-dom";
import { AuthContext } from 'react-oauth2-code-pkce';

function NavBar(props) {

    const { tokenData, token, login, logOut, idToken, error } = useContext(AuthContext);

    const [showVirtualClock, setShowVirtualClock] = useState(false);

    const navigate = useNavigate();
    const handleClick= (e)=> {
        e.preventDefault();
        if (props.user === null) {
            login();
        } else {
            props.setUser(null);
            logOut();
        }
    }

    /**
     * Every time tokenData (the decoded token for the logged-in user on the browser) changes, we update the
     * corresponding user state so that it is available for all the components.
     * The user state is an object containing the user's email, name, surname, role and token.
     */
    useEffect(() => {
      if (!props.user && tokenData) {
        props.setUser({ email: tokenData.preferred_username, role: tokenData.role, name: tokenData.firstName, surname: tokenData.lastName, token: token });
      } else {
        props.setUser(null);
      }
    }, [tokenData]);

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