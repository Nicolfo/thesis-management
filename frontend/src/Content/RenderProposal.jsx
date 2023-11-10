import {map} from "react-bootstrap/ElementChildren";
import {Form, Button, Col, FormGroup, FormLabel, Row} from "react-bootstrap";
import {useNavigate} from 'react-router-dom';
import {useState} from "react";


function RenderProposal(props){
    let proposal = props.listOfProposal[props.proposalSelected];
    const navigate = useNavigate();
    const [cv, setCv] = useState();

    const proposalPart = ["Id", "Title", "Supervisor", "CoSupervisors", "Keywords", "Type", "Groups", "Description", "RequiredKnowledge", "Notes", "Expiration"]

    return( <div>
            <h2>Selected proposal: {proposal[1]}</h2>
            {proposal.map((el, indx) =>{
                return(
                    <div>
                        <h5>{proposalPart[indx]} : </h5>

                        {!Array.isArray(el) ? <p>{el}</p>: <p>{el.map((e,ind) => {
                            return(
                            e + " "
                            );
                        }) }</p>}

                    </div>
                );


                })
            }
            <FormGroup>
                <FormLabel>Insert your cv to apply</FormLabel>
                <div></div>
                <input type="file" accept=".pdf" onChange={(event) => setCv({ selectedFile: event.target.files[0] })} />

            </FormGroup>
            <p> </p>
            <Button onClick={()=> navigate('/search-for-proposal')}>Apply</Button>
            <Button onClick={()=> navigate('/search-for-proposal')}>Go Back</Button>
        </div>




    );

}




export default RenderProposal