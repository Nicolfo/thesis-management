import {insertApplication, uploadFile} from "../API/Api-Search";
import {Button, FormGroup, FormLabel, Alert} from "react-bootstrap";
import {useNavigate, useParams} from 'react-router-dom';
import {useContext, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {AuthContext} from "react-oauth2-code-pkce";


function RenderProposal(props){

    const { proposalId } = useParams();

    const navigate = useNavigate();

    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role==="TEACHER")
        navigate("/notAuthorized");

    const [cv, setCv] = useState();
    const [cvSelected, setCvSelected] = useState(true);

    return( <div>
            <FormGroup>
                <FormLabel>Insert your cv to apply</FormLabel>
                <div></div>
                <input type="file"  onChange={(event) => setCv(  event.target.files[0] )} />

            </FormGroup>
            {cvSelected ? <p></p> : <Alert  variant={"danger"}>
                Select your cv file before proceed
            </Alert>}
            <Button onClick={()=> navigate('/search-for-proposal')}>
                <FontAwesomeIcon icon={"chevron-left"} /> Go Back
            </Button>
            <Button onClick={()=> {
                if(cv != undefined){

                    setCvSelected(true);
                uploadFile(cv).then((id) => {
                    insertApplication(id, proposalId, props.user.token)
                })
                navigate('/search-for-proposal')}
                else{
                    setCvSelected(false)
                }
            }}><FontAwesomeIcon icon="fa-file" /> Apply</Button>

        </div>




    );

}




export default RenderProposal