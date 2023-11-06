import {map} from "react-bootstrap/ElementChildren";
import {Button} from "react-bootstrap";
import {useNavigate} from 'react-router-dom';


function RenderProposal(props){
    let proposal = props.listOfProposal[props.proposalSelected];
    const navigate = useNavigate();

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
            <Button onClick={()=> navigate('/search-for-proposal')}>Apply</Button>
            <Button onClick={()=> navigate('/search-for-proposal')}>Go Back</Button>
        </div>




    );

}

export default RenderProposal