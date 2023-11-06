import logo from './logo.svg';
import './App.css';
import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter as Router, useLocation} from "react-router-dom";
import SideBar from "./SideBar/SideBar";
import NavBar from "./NavBar/NavBar";
import {useEffect, useState} from "react";
import {getAllProposal, getAllSupervisors} from "./API/Api";
import ProposalList from "./Content/ProposalList";
import RenderProposal from "./Content/RenderProposal";

function Content(props) {


  const path = useLocation().pathname.toString();
  switch (path) {                                //add to this switch-case your content (defined in the Content folder)
    case "/":
    case "/search-for-proposal":
      return <ProposalList listOfProposal={props.listOfProposal} setProposalSelected={props.setProposalSelected}></ProposalList>
    case "/see-proposal":
      return <RenderProposal listOfProposal={props.listOfProposal} proposalSelected={props.proposalSelected}></RenderProposal>

    default:
      return <h1>Path not found</h1>
  }
}

function App() {

  const [clickOnProposal, setClickOnProposal] = useState(0);

//MOCK DATA
  let pro1 = [1, "Ai nel tempo", "Lia morra", ["Antonio", "Gianni"], ["Ai", "Time"], "Master", ["C1", "C2"], "Nel tempo bla bla", "ai2", "No notes", "25/9/2024"]
  let pro2 = [2, "L'inter la storia", "Simone Inzaghi", ["Adriano", "Milito"], ["Inter", "Milano"], "Bachelor", "F1", "La storia dell'inter nel 2010", "scienze motorie 1", "No notes", "25/9/2023"]
  let pro3 = [3, "Explainable ai", "Lia morra", ["Antonio"], "Ai", "Master", ["C1", "C2"], "The challenge of the new era", "ai2", "No notes", "25/9/2024"]
  let proposalList = [pro1, pro2, pro3];
  let professorsList = ["Lia Morra", "Simone Inzaghi", "Marco", "Giardini", "Feroce"]




  const [listOfProposal, setListOfProposal] = useState(proposalList)
  const [listOfSupervisors, setListOfSupervisors] = useState(professorsList)

  const [proposalSelected, setProposalSelected] = useState(0);
  const searchForProposalClicked = () => {
    setClickOnProposal((clickOnProposal) => clickOnProposal + 1);
  }

  useEffect( ()=>{
    // call the api to retrieve the list of active proposal
    // api called every time the user click on the button to search for proposal.
    // retrieve all the proposals. for the filters, they are applicated on the fontend (we wanna evitate to do a lot of queries so a lot of api calls)
    // cause we already have all the acrtive proposals (more time to do api than local computation)
    // we can do that because we can assume that the insert of a new proposal is a lot less of the number of serch for a proposal

    /*
    getAllProposal()
          .then((list)=> { setListOfProposal(list);})

    getAllSupervisors
          .then((list) => {setListOfSupervisors(list);})
          */

      console.log("api called")
    console.log(clickOnProposal)



  }, [clickOnProposal]);

  return (
      <div className="container-fluid" style={{height: '90vh'}}>
        <div className="row align-items-start">
          <Router>
            <NavBar>
            </NavBar>
            <SideBar searchForProposalClicked={searchForProposalClicked}>
            </SideBar>
            <div className="col-9">
              <Content listOfProposal={listOfProposal} setProposalSelected={setProposalSelected} proposalSelected={proposalSelected}>
              </Content>
            </div>
          </Router>
        </div>
      </div>
  );
}

export default App;
