import {insertApplication, uploadFile} from "../API/Api-Search";
import {map} from "react-bootstrap/ElementChildren";
import {Form, Button, Col, FormGroup, FormLabel, Row, Alert} from "react-bootstrap";
import {useNavigate} from 'react-router-dom';
import {useState} from "react";


function RenderProposal(props){
    let proposal = props.listOfProposal[props.proposalSelected];
    const navigate = useNavigate();
    const [cv, setCv] = useState();
    const [cvSelected, setCvSelected] = useState(true);

    let proposalKeys = Object.keys(proposal)
    let proposalValues = Object.values(proposal)



    const proposalPart = ["id", "title", "supervisor", "coSupervisors", "keywords", "type", "groups", "description", "requiredKnowledge", "notes", "expiration"]





    return( <div>

            <h2>Selected proposal: {proposal.id}</h2>
            {proposalValues.map((el, indx) =>{

                if(indx == 2){ //supervisor
                    return(
                        <div>
                            <h5>{proposalKeys[indx]} : </h5>
                            <p>{el.id}- {el.name} {el.surname}</p>
                        </div>)

                }else if(indx==3 || indx == 6){ //co supervisor
                    return(
                        <div>
                            <h5>{proposalKeys[indx]} : </h5>
                            {el.map(e => {return(<p>e</p>)})}

                        </div>)

                } else if(indx == 12){

                } else if(indx == 10){
                    return(
                    <div>
                        <h5>{proposalKeys[indx]} : </h5>

                        <p>{el.slice(0,10)}</p>

                    </div>)

                }

                else{
                return(
                    <div>
                        <h5>{proposalKeys[indx]} : </h5>

                         <p>{el}</p>

                    </div>
                );


                }})
            }
            <FormGroup>
                <FormLabel>Insert your cv to apply</FormLabel>
                <div></div>
                <input type="file"  onChange={(event) => setCv(  event.target.files[0] )} />

            </FormGroup>
            <p> </p>
            {cvSelected ? <p></p> : <Alert  variant={"danger"}>
                Select your cv file before proceede
            </Alert>}
            <p> </p>
            <Button onClick={()=> {
                if(cv != undefined){
                    setCvSelected(true);
                uploadFile(cv).then((id) => {
                    insertApplication(id, proposal.id)
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