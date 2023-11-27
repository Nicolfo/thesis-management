import {library} from '@fortawesome/fontawesome-svg-core';
import {fab} from '@fortawesome/free-brands-svg-icons';
import {fas} from '@fortawesome/free-solid-svg-icons';
import {far} from '@fortawesome/free-regular-svg-icons';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Navigate, BrowserRouter, Outlet, Route, BrowserRouter as Router, Routes, useLocation} from "react-router-dom";
import {getAllProposal, getAllSupervisors} from "./API/Api-Search";
import ProposalList from "./Content/ProposalList";
import RenderProposal from "./Content/RenderProposal";
import NavBar from "./NavBar/NavBar";
import Navigation from "./Navigation/Navigation";
import {LoginLayout} from "./LoginLayout/LoginLayout";
import { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import ApplicationViewLayout from "./Content/ApplicationViewLayout";
import BrowseApplicationsContent from "./Content/BrowseApplicationsContent";
import BrowseDecisions from "./Content/BrowseDecisions";
import BrowseProposalsContent from './Content/BrowseProposalsContent';
import InsertUpdateProposal from "./Content/InsertUpdateProposal";
import ProposalsListContent from './Content/ProposalsListContent';
import { AuthContext, AuthProvider } from 'react-oauth2-code-pkce';
import { jwtDecode } from 'jwt-decode';
import NotAuthorizedLayout from "./Content/NotAuthorizedLayout";
import NotFound from "./Content/NotFound";
import API from "./API/API2";
const authConfig = {
  clientId: 'oidc-client',
  authorizationEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/auth',
  logoutEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/logout',
  tokenEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/token',
  redirectUri: 'http://localhost:3000/',
  scope: 'profile openid',
  // Example to redirect back to original path after login has completed
  // preLogin: () => localStorage.setItem('preLoginPath', window.location.pathname),
  // postLogin: () => window.location.replace(localStorage.getItem('preLoginPath') || ''),
  decodeToken: true,
  autoLogin: false,
}


function App() {

    const [user, setUser] = useState(null);


    const [clickOnProposal, setClickOnProposal] = useState(0);


//MOCK DATA
    const listOfFilters = ["Professor", "Type"];
    const correspondingFields = [2, 5]
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

    const updateApplicationDate = dateStr => {
        let date = dayjs(dateStr);
        // If the user didn't provide a valid date, default to the current one
        let newOffset;
        if (!date.isValid() || date.diff(dayjs(), "day") === 0) {
            newOffset = 0;
        } else {
            newOffset = date.diff(realDate, "day") + 1;
        }
        setOffsetDate(newOffset);
        setApplicationDate(realDate.add(newOffset, "day"));
    };

    useEffect(() => {
        setRealDate(dayjs());
        setApplicationDate(realDate.add(offsetDate, "day"));
    }, []);


    // function selectFilter(el1, el2, filterType) {
    //
    //     switch (filterType) {
    //         // by professor
    //         case 0:
    //
    //
    //             return (el1.supervisor.name == el2)
    //             break;
    //         case 1:
    //
    //             return (el1.level == el2)
    //
    //             // code block
    //             break;
    //         default:
    //         // code block
    //     }
    //
    //
    // }

    const searchForProposalClicked = () => {
        setClickOnProposal((clickOnProposal) => clickOnProposal + 1);
    }
    useEffect(() => {
            if (user !== null) {
                localStorage.setItem("email", user.email);
                localStorage.setItem("token", user.token);
                localStorage.setItem("role", user.role);
            } else {
                const email = localStorage.getItem("email");
                const token = localStorage.getItem("token");
                const role = localStorage.getItem("role");

                if (email !== null && token !== null && role !== null) {
                    setUser({email: email, token: token, role: role});
                }

            }


        }


        , [user]);

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

            getAllSupervisors()
                .then((list) => {
                    setListOfSupervisors(list);
                })
        }


    }, [user]);

    return (
        <AuthProvider authConfig={authConfig}>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={
                    <>
                        <div className="container-fluid" style={{height: '90vh', padding: '0rem'}}>
                            <div className="row align-items-start">
                                <NavBar user={user} setUser={setUser} realDate={realDate}
                                        applicationDate={applicationDate} updateApplicationDate={updateApplicationDate}
                                        searchForProposalClicked={searchForProposalClicked}/>
                                <div className="ps-5 pe-5 pt-3">
                                    <Outlet/>
                                </div>
                            </div>
                        </div>
                    </>
                }>
                    <Route index element={(user && user.role === "TEACHER" && <Navigate to={"/teacher/proposals"}/>)
                                          || (user && user.role === "STUDENT" && <Navigate to={"/search-for-proposal"}/>)
                                          || (!user && <h1>Welcome to Thesis Manager!</h1> ) }/>
                    <Route path="/login"
                           element={(user && user.role === "TEACHER" && <Navigate to={"/teacher/proposals"}/>)
                               || (user && user.role === "STUDENT" && <Navigate to={"/search-for-proposal"}/>)
                               || (!user && <LoginLayout user={user} setUser={setUser}/>)}/>
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
                           element={<InsertUpdateProposal user={user}/>}/>
                    <Route path="/teacher/proposals"
                           element={<BrowseProposalsContent user={user} applicationDate={applicationDate}/>}/>
                    <Route path="/notAuthorized"
                           element={<NotAuthorizedLayout user={user}/>}/>
                    <Route path="*"
                           element={<NotFound user={user}/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
        </AuthProvider>
    );

}
export default App;

library.add(fab, fas, far);
