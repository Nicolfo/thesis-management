import { library } from '@fortawesome/fontawesome-svg-core';
import { fab } from '@fortawesome/free-brands-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { far } from '@fortawesome/free-regular-svg-icons';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {BrowserRouter as Router, useLocation} from "react-router-dom";
import Navigation from "./Navigation/Navigation";
import {LoginLayout} from "./LoginLayout/LoginLayout";
import { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import BrowseApplicationsContent from "./Content/BrowseApplicationsContent";
import SideBar from "./SideBar/SideBar";
import BrowseDecisions from "./Content/BrowseDecisions";

function Content(props) {

  const path = useLocation().pathname.toString();
  switch (path) {                                //add to this switch-case your content (defined in the Content folder)
    case "/":
      return <b>Home page</b>
    /*case "/search-for-proposal":
      return <ProposalList clickOnProposal={props.clickOnProposal} filterProposals={props.filterProposals} listOfProposal={props.listOfProposal} setProposalSelected={props.setProposalSelected}></ProposalList>
    case "/see-proposal":
      return <RenderProposal listOfProposal={props.listOfProposal} proposalSelected={props.proposalSelected}></RenderProposal>

    case "/teacher/proposal/browse":
      return <BrowseProposalsContent user={props.user}/>*/
    case "/teacher/application/browse":
      return <BrowseApplicationsContent user={props.user}/>
    case "/login":
      return <LoginLayout user={props.user} setUser={props.setUser} />
    case "/browseDecisions":
      return <BrowseDecisions user={props.user} />

    default:
      return <h1>Path not found</h1>
  }
}

function App() {

  const [user, setUser] = useState(null);

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
  },[]);

  return (
      <div className="container-fluid" style={{height: '90vh', padding: '0rem'}}>
        <div className="row align-items-start">
          <Router>
            <Navigation user={user} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} />
            <div className="row g-0">
              {/*<SideBar searchForProposalClicked={searchForProposalClicked}>*/}
                <SideBar>
              </SideBar>
              <div className="col-10 p-2">
                {/*<Content clickOnProposal={clickOnProposal} filterProposals={filterProposals} listOfProposal={filteredProposals} setProposalSelected={setProposalSelected} proposalSelected={proposalSelected} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} user={user} setUser={setUser}/>*/}
                <Content realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} user={user} setUser={setUser}/>
              </div>
            </div>
          </Router>
        </div>
      </div>
  );
}

export default App;

library.add(fab, fas, far);
