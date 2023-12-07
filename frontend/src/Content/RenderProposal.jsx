import API from "../API/Api";
import {Button, FormGroup, FormLabel, Alert, Card} from "react-bootstrap";
import {useNavigate, useParams} from 'react-router-dom';
import {useContext, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {AuthContext} from "react-oauth2-code-pkce";
import Container from "react-bootstrap/Container";


function RenderProposal(props) {

    const {proposalId} = useParams();

    const navigate = useNavigate();

    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role === "TEACHER")
        navigate("/notAuthorized");

    const [cv, setCv] = useState();

    return (<div>
            <Container className="d-flex mt-5 justify-content-center">
                <Card>
                    <Card.Header className="text-start pe-3">Apply</Card.Header>
                    <Card.Body className="text-center pt-5 mx-5">
                        <FormGroup>
                            <FormLabel>Insert your CV to apply</FormLabel>
                            <div></div>
                            <input type="file" onChange={(event) => setCv(event.target.files[0])}/>
                            <div className="mt-4"><Button className="me-3" onClick={() => navigate('/search-for-proposal')}>
                                <FontAwesomeIcon icon={"chevron-left"}/> Go Back
                            </Button>
                                <Button onClick={() => {
                                    if (cv != undefined) {

                                        API.uploadFile(cv).then((id) => {
                                            API.insertApplication(id, proposalId, props.user.token)
                                        })
                                        navigate('/search-for-proposal')
                                    } else {
                                        API.insertApplication(null, proposalId, props.user.token)
                                    }
                                }}><FontAwesomeIcon icon="fa-file"/> Apply
                                </Button>
                            </div>
                        </FormGroup>
                    </Card.Body>
                </Card>
            </Container>


        </div>


    );

}


export default RenderProposal