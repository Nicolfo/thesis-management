import Table from 'react-bootstrap/Table';
import {Button} from "react-bootstrap";
import {useNavigate} from 'react-router-dom';

function ProposalList(props){
    const navigate = useNavigate();


    const shortProposalSchema = ["Id", "Title", "Supervisor","Keywords", "Type", "Groups","Expiration"]


    let indexForShortProposal = [0,1,2,4,5,6,10]

    let shortProposal = props.listOfProposal.map((e,i) => {
        return e.filter((item, index) => indexForShortProposal.includes(index))


    })

    return (

        <div>
            <h2>List of Active Proposal</h2>


            {shortProposal.map((proposal, index)=>{
                return(
                    <Table >
                        <thead >
                            <tr>
                                {
                                    shortProposalSchema.map((e,i) => {
                                        return(
                                            <th key={e}>{e}</th>
                                        );

                                    })
                                }

                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                        {proposal.map((e,i) => {

                            if (Array.isArray(e)){
                                return (<td key={i}>{e.map((el,ind) => {
                                    return(

                                       el+ " "

                                    );
                                })}</td>)

                            }else{
                            return( <td key={i}>{e}</td>

                            );
                        }})}
                                <Button onClick={()=> {props.setProposalSelected(index); navigate('/see-proposal')}}>See </Button>
                            </tr>

                        </tbody>
                    </Table>

                );

            })}
        </div>
    );
}

export default ProposalList;