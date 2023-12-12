import API from "../API/Api";
import {Button, FormGroup, FormLabel, Card, Row} from "react-bootstrap";
import {useNavigate, useParams} from 'react-router-dom';
import {useContext, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {AuthContext} from "react-oauth2-code-pkce";
import Container from "react-bootstrap/Container";
import toast, {Toaster} from 'react-hot-toast';

function RenderProposal(props) {

    const {proposalId} = useParams();

    const navigate = useNavigate();

    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role === "TEACHER")
        navigate("/notAuthorized");

    const [cv, setCv] = useState();
    const [applied, setApplied] = useState(false);
    const [disabledButtons, setDisabledButtons] =useState(false);

    const goBack= () => {
        navigate('/search-for-proposal')
        setApplied(false);
    };

    return (<div>
            <Container className="d-flex mt-5 justify-content-center">
                <Card>
                    <Card.Header className="text-start pe-3">Apply</Card.Header>
                    <Card.Body className="text-center pt-5 mx-5">
                        <FormGroup>
                            {applied ? <FormLabel><Row>Application successfully executed </Row> </FormLabel>
                                : <><FormLabel><Row>Insert your CV to apply</Row></FormLabel>
                            <Row><input type="file" disabled={disabledButtons} onChange={(event) => setCv(event.target.files[0])}/></Row></>}
                            <div><Button disabled={disabledButtons} className="me-3 mt-4" onClick={goBack}>
                                <FontAwesomeIcon icon={"chevron-left"}/> Go Back
                            </Button>
                                {!applied && <><Button className="mt-4" disabled={disabledButtons} onClick={() => {
                                    setDisabledButtons(true);
                                    toast.promise(
                                        (async () => {
                                            if (cv) {
                                                const id = await API.uploadFile(cv);
                                                await API.insertApplication(id, proposalId, props.user.token);
                                            } else {
                                                await API.insertApplication(null, proposalId, props.user.token);
                                            }
                                            setApplied(true);
                                            return "Application successfully executed";
                                        })(),
                                        {
                                            loading: 'Applying...',
                                            success: () => {
                                                setDisabledButtons(false);
                                                return <strong>Application successfully executed</strong>
                                            },
                                            error: (error) => {
                                                setApplied(false);
                                                setDisabledButtons(false);
                                                if (error && error.detail) {
                                                    return <strong>{error.detail}</strong>;
                                                } else {
                                                    return <strong>An error occurred while processing the application.</strong>;
                                                }
                                            },
                                        }
                                    );
                                }}>
                                    <FontAwesomeIcon icon="fa-file"/> Apply
                                </Button>
                                </>}</div>
                        </FormGroup>
                    </Card.Body>
                </Card>
                <Toaster
                    position="top-right"
                    containerClassName="mt-5"
                    reverseOrder={false}
                />
            </Container>


        </div>


    );

}


export default RenderProposal