import { library } from '@fortawesome/fontawesome-svg-core';
import { fab } from '@fortawesome/free-brands-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { far } from '@fortawesome/free-regular-svg-icons';
import './App.css';

import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter as Router, useLocation} from "react-router-dom";
import SideBar from "./SideBar/SideBar";
import NavBar from "./NavBar/NavBar";
import {useEffect, useState} from "react";
import {getAllProposal, getAllSupervisors} from "./API/Api-Search";
import ProposalList from "./Content/ProposalList";
import RenderProposal from "./Content/RenderProposal";
import Navigation from "./Navigation/Navigation";
import { LoginLayout } from "./LoginLayout/LoginLayout";
import dayjs from 'dayjs';
import BrowseProposalsContent from "./Content/BrowseProposalsContent";

function Content(props) {


  const path = useLocation().pathname.toString();
  switch (path) {                                //add to this switch-case your content (defined in the Content folder)
    case "/":
      return <b>Home page</b>
    case "/search-for-proposal":
      return <ProposalList clickOnProposal={props.clickOnProposal} filterProposals={props.filterProposals} listOfProposal={props.listOfProposal} setProposalSelected={props.setProposalSelected}></ProposalList>
    case "/see-proposal":
      return <RenderProposal listOfProposal={props.listOfProposal} proposalSelected={props.proposalSelected}></RenderProposal>
    case "/login":
      return <LoginLayout user={props.user} setUser={props.setUser} />
    case "/teacher/proposal/browse":
      return <BrowseProposalsContent user={props.user}/>

    default:
      return <h1>Path not found</h1>
  }
}

function App() {


  const [clickOnProposal, setClickOnProposal] = useState(0);


//MOCK DATA
  let pro1 = [1, "Ai nel tempo", "Lia Morra", ["Antonio", "Gianni"], ["Ai", "Time"], "Master", ["C1", "C2"], "Nel tempo bla bla", "ai2", "No notes", "25/9/2024"]
  let pro2 = [2, "L'inter la storia", "Simone Inzaghi", ["Adriano", "Milito"], ["Inter", "Milano"], "Bachelor", "F1", "La storia dell'inter nel 2010", "scienze motorie 1", "No notes", "25/9/2023"]
  let pro3 = [3, "Explainable ai", "Lia Morra", ["Antonio"], "Ai", "Master", ["C1", "C2"], "The challenge of the new era", "ai2", "No notes", "25/9/2024"]
  let proposalList = [pro1, pro2, pro3];
  let professorsList = ["Andrea", "Lia Morra", "Simone Inzaghi", "Marco", "Giardini", "Feroce"]
  const listOfFilters = ["Professor", "Type"];
  const correspondingFields = [2,5]
  const [listOfProposal, setListOfProposal] = useState(proposalList)

  const [listOfSupervisors, setListOfSupervisors] = useState(professorsList)
  const [filteredProposals, setFilteredProposals] = useState(listOfProposal)
    /*
  We use 3 states to manage the current date and the virtual clock:
    - realDate: this represents the real current date, according to the system. It is refreshed at every render
    - offsetDate: this represents the offset that has been set by the user, in terms of additional days starting from realDate.
    - applicationDate: this represents the date that is considered by the application.
    For example, if the realDate is 5/11/2023 and the offsetDate is 10, the applicationDate is then 15/11/2023.
    Whenever considering any date-related logic for the front end, ***YOU SHOULD ALWAYS USE applicationDate***

    TL;DR: use applicationDate to get the date that has been set by the user, currently (by default the real one).
  */

  const [realDate, setRealDate] = useState(dayjs());
  const [offsetDate, setOffsetDate] = useState(0);
  const [applicationDate, setApplicationDate] = useState(dayjs());

  const [user, setUser] = useState(null);

  const updateApplicationDate = dateStr => {
    let date = dayjs(dateStr);
    // If the user didn't provide a valid date, default to the current one
    if (!date.isValid())
      date = dayjs();
    const newOffset = date.diff(realDate, "day");
    setOffsetDate(newOffset);
    setApplicationDate(realDate.add(newOffset, "day"));
  };

  useEffect(() => {
    setRealDate(dayjs());
    setApplicationDate(realDate.add(offsetDate, "day"));
  }, []);

function selectFilter(el1, el2, filterType){

  switch(filterType) {
    // by professor
    case 0:
      console.log(el1.supervisor.name)
      console.log(el1.supervisor.name == el2)

      return (el1.supervisor.name == el2)
      break;
    case 1:

      return (el1.level == el2)

      // code block
      break;
    default:
      // code block
  }


}


function filterProposals(filters){
  let listOfFilters = ["supervisor", "level"];
  let index = 0;


  setFilteredProposals(listOfProposal)
  let xfilteredProposals = [...listOfProposal]

  let noFilterSelected = true;


  for(const filterx  of filters){

    if(filterx === "All"){
    }else {

      noFilterSelected = false
       xfilteredProposals = xfilteredProposals.filter((proposal , i) => {
        if (Array.isArray(proposal[correspondingFields[index]])){

          return (proposal[correspondingFields[index]].includes(filterx))

        }else{
          return(selectFilter(proposal, filterx, index))}
      })

    }
    index++
    }
  setFilteredProposals([...xfilteredProposals])
    if(noFilterSelected){
      setFilteredProposals(listOfProposal)
    }
  }





  const [proposalSelected, setProposalSelected] = useState(0);
  const searchForProposalClicked = () => {
    setClickOnProposal((clickOnProposal) => clickOnProposal + 1);
  }

  useEffect( ()=>{
    // call the api to retrieve the list of active proposal
    // api called every time the user click on the button to search for proposal.
    // retrieve all the proposals. for the filters, they are applicated on the font-end (we wanna evitate to do a lot of queries so a lot of api calls)
    // cause we already have all the active proposals (more time to do api than local computation)
    // we can do that because we can assume that the insert of a new proposal is a lot less of the number of search for a proposal


    getAllProposal()
          .then((list)=> { setListOfProposal(Array.from(Object.values(list))); setFilteredProposals(Array.from(Object.values(list)))})
 /*
    getAllSupervisors
          .then((list) => {setListOfSupervisors(list);})
          */
    //
    console.log("api called")
    console.log(clickOnProposal)



  }, [clickOnProposal]);

  return (
      <div className="container-fluid" style={{height: '90vh', padding: '0rem'}}>
        <div className="row align-items-start">
          <Router>
            <Navigation user={user} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} />
            <div className="row g-0">
            <SideBar searchForProposalClicked={searchForProposalClicked}>
            </SideBar>
             <div className="col-10 p-2">
              <Content clickOnProposal={clickOnProposal} filterProposals={filterProposals} listOfProposal={filteredProposals} setProposalSelected={setProposalSelected} proposalSelected={proposalSelected} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} user={user} setUser={setUser}/>
            </div>
          </div>
        </Router>
      </div>
    </div>
  );
}

export default App;

library.add(fab, fas, far);
