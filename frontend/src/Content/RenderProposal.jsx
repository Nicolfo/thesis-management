import {insertApplication, uploadFile} from "../API/Api-Search";
import {map} from "react-bootstrap/ElementChildren";
import {Form, Button, Col, FormGroup, FormLabel, Row, Alert} from "react-bootstrap";
import {useNavigate, useParams} from 'react-router-dom';
import {useState} from "react";


function RenderProposal(props){

    const { proposalId } = useParams();

    const navigate = useNavigate();
    const [cv, setCv] = useState();
    const [cvSelected, setCvSelected] = useState(true);

    return( <div>
            <FormGroup>
                <FormLabel>Insert your cv to apply</FormLabel>
                <div></div>
                <input type="file"  onChange={(event) => setCv(  event.target.files[0] )} />

            </FormGroup>
            {cvSelected ? <p></p> : <Alert  variant={"danger"}>
                Select your cv file before proceede
            </Alert>}
            <Button onClick={()=> {
                if(cv != undefined){
                    console.log(proposalId);
                    setCvSelected(true);
                uploadFile(cv).then((id) => {
                    insertApplication(id, proposalId)
                })
                navigate('/search-for-proposal')}
                else{
                    setCvSelected(false)
                }
            }}>Apply</Button>
            <Button onClick={()=> navigate('/search-for-proposal')}>Go Back</Button>
        </div>




    );

}




export default RenderProposal