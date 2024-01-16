import {library} from '@fortawesome/fontawesome-svg-core';
import {fab} from '@fortawesome/free-brands-svg-icons';
import {fas} from '@fortawesome/free-solid-svg-icons';
import {far} from '@fortawesome/free-regular-svg-icons';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Navigate, BrowserRouter, Outlet, Route, Routes} from "react-router-dom";

import RenderProposal from "./Content/RenderProposal";
import NavBar from "./NavBar/NavBar";

import { useEffect, useState} from 'react';
import dayjs from 'dayjs';
import ApplicationViewLayout from "./Content/ApplicationViewLayout";
import BrowseApplicationsContent from "./Content/BrowseApplicationsContent";
import BrowseDecisions from "./Content/BrowseDecisions";
import BrowseProposalsContent from './Content/BrowseProposalsContent';
import InsertUpdateProposal from "./Content/InsertUpdateProposal";
import ProposalsListContent from './Content/ProposalsListContent';

import NotAuthorizedLayout from "./Content/NotAuthorizedLayout";
import NotFound from "./Content/NotFound";
import API from "./API/Api";
import BrowseArchivedProposals from "./Content/BrowseArchivedProposals";
import StartRequest from "./Content/StartRequest";
import ProposalsOnRequestListContent from "./Content/ProposalsOnRequestListContent";
import TeacherApproveStartRequestContent from './Content/TeacherApproveStartRequest';
import UserListNotificationContent from './Content/UserListNotificationContent';
import ProposalViewLayout from "./Content/ProposalViewLayout";

function App() {

    const [user, setUser] = useState(null);
    const [archivedView, setArchivedView] = useState(false);


    const [clickOnProposal, setClickOnProposal] = useState(0);

    const [listOfProposal, setListOfProposal] = useState([])

    const [listOfSupervisors, setListOfSupervisors] = useState([])
    const [filteredProposals, setFilteredProposals] = useState([])

    /*We use 3 states to manage the current date and the virtual clock:
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
    const [sent, setSent] = useState(false);

    const updateApplicationDate = dateStr => {
        let date = dayjs(dateStr);
        // If the user didn't provide a valid date, default to the current one
        let newOffset;
        if (!date.isValid() || date.diff(dayjs(), "day") === 0) {
            newOffset = 0;
        } else {
            newOffset = date.diff(realDate, "day") + 1;
        }

        API.setVirtualClock(newOffset);

        setOffsetDate(newOffset);
        setApplicationDate(realDate.add(newOffset, "day"));
    };

    useEffect(() => {
        setRealDate(dayjs());
        setApplicationDate(realDate.add(offsetDate, "day"));
    }, []);


    const searchForProposalClicked = () => {
        setClickOnProposal((clickOnProposal) => clickOnProposal + 1);
    }

    useEffect(() => {
        // call the api to retrieve the list of active proposal
        // api called every time the user click on the button to search for proposal.
        // retrieve all the proposals. for the filters, they are applicated on the font-end (we wanna evitate to do a lot of queries so a lot of api calls)
        // cause we already have all the active proposals (more time to do api than local computation)
        // we can do that because we can assume that the insert of a new proposal is a lot less of the number of search for a proposal
        if (user !== null) {
            API.getAllProposals(user.token)
                .then((list) => {
                    setListOfProposal(Array.from(Object.values(list)));
                    setFilteredProposals(Array.from(Object.values(list)))
                })

            API.getAllSupervisors()
                .then((list) => {
                    setListOfSupervisors(list);
                })
        }


    }, [user]);

    return (

        <BrowserRouter>
            <Routes>
                <Route path="/" element={
                    <>
                        <div className={!user ? "container-fluid background-with-image" : ""} style={{height: '100vh', width:'100vw', padding: '0rem'}}>
                            <div className="row align-items-start">
                                <NavBar user={user} setUser={setUser} realDate={realDate} sent={sent} setSent={setSent}
                                        applicationDate={applicationDate} updateApplicationDate={updateApplicationDate}
                                        searchForProposalClicked={searchForProposalClicked} setArchivedView={setArchivedView}/>
                                <div className="ps-5 pe-5 pt-3">
                                    <Outlet/>
                                </div>
                            </div>
                        </div>
                    </>
                }>
                    <Route index element={(user && user.role === "TEACHER" && <Navigate to={"/teacher/proposals"}/>)
                                          || (user && user.role === "STUDENT" && <Navigate to={"/search-for-proposal"}/>)
                                          || (user && user.role === "SECRETARY" && <Navigate to={"/proposalOnRequest/browse"}/>)
                                          || (!user && <h1 style={{textAlign: "center"}}>Welcome to thesis management!</h1> ) }/>
                    <Route path="/search-for-proposal"
                           element={<ProposalsListContent user={user} applicationDate={applicationDate}/>}/>
                    <Route path="/browseDecisions"
                           element={<BrowseDecisions user={user}/>}/>
                    <Route path="/proposal/apply/:proposalId"
                           element={<RenderProposal user={user}/>}/>

                    <Route path="/teacher/application/browse"
                           element={<BrowseApplicationsContent user={user}/>}/>
                    <Route path="/teacher/application/view"
                           element={<ApplicationViewLayout user={user} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate}/>}/>

                    <Route path="/insertProposal"
                           element={<InsertUpdateProposal user={user}/>}/>
                    <Route path="/updateProposal/:editProposalID"
                           element={<InsertUpdateProposal user={user}/>}/>
                    <Route path="/copyProposal/:copyProposalID"
                           element={<InsertUpdateProposal user={user} archivedView={archivedView}/>}/>

                    <Route path="/teacher/proposals"
                           element={<BrowseProposalsContent user={user} applicationDate={applicationDate}/>}/>
                    <Route path="/proposal/view/:proposalId"
                           element={<ProposalViewLayout user={user}/>}/>

                    <Route path="/teacher/proposals/archived"
                           element={<BrowseArchivedProposals setArchivedView={setArchivedView} user={user} applicationDate={applicationDate}/>}/>
                    <Route path="/startRequest"
                           element={<StartRequest user={user} sent={sent} setSent={setSent} />}/>
                    <Route path="/startRequestFromApplication/:applicationId"
                           element={<StartRequest user={user} sent={sent} setSent={setSent} />}/>
                    <Route path="/proposalOnRequest/browse"
                           element={<ProposalsOnRequestListContent user={user}/>}/>
                    <Route path="/teacher/proposalOnRequest/browse"
                           element={<TeacherApproveStartRequestContent user={user}/>}/>
                    <Route path="/notifications"
                           element={<UserListNotificationContent user={user}/>}/>
                    <Route path="/notAuthorized"
                           element={<NotAuthorizedLayout user={user}/>}/>
                    <Route path="*"
                           element={<NotFound user={user}/>}/>
                </Route>
            </Routes>
        </BrowserRouter>

    );

}
export default App;

library.add(fab, fas, far);
