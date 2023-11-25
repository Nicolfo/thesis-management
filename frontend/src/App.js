import { library } from '@fortawesome/fontawesome-svg-core';
import { fab } from '@fortawesome/free-brands-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { far } from '@fortawesome/free-regular-svg-icons';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import { getAllSupervisors } from "./API/Api-Search";
import RenderProposal from "./Content/RenderProposal";
import Navigation from "./Navigation/Navigation";
import { LoginLayout } from "./LoginLayout/LoginLayout";
import { useEffect, useState, useContext } from 'react';
import dayjs from 'dayjs';
import ApplicationViewLayout from "./Content/ApplicationViewLayout";
import BrowseApplicationsContent from "./Content/BrowseApplicationsContent";
import SideBar from "./SideBar/SideBar";
import BrowseDecisions from "./Content/BrowseDecisions";
import BrowseProposalsContent from './Content/BrowseProposalsContent';
import InsertUpdateProposal from "./Content/InsertUpdateProposal";
import API from "./API/API2";
import ProposalsListContent from './Content/ProposalsListContent';
import { AuthContext, AuthProvider } from 'react-oauth2-code-pkce';
import { jwtDecode } from 'jwt-decode';

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
      date = dayjs();
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

  return (
    <AuthProvider authConfig={authConfig}>
      <BrowserRouter>
        <Routes>
          <Route element={
            <>
              <div className="container-fluid" style={{ height: '90vh', padding: '0rem' }}>
                <div className="row align-items-start">
                  <Navigation user={user} setUser={setUser} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} />
                  <div className="row g-0">
                    <SideBar user={user}  />
                    <div className="col-10 p-2">
                      <Outlet />
                    </div>
                  </div>
                </div>
              </div>
            </>
          }>
            
            <Route index element={<h1>Welcome to Thesis Manager!</h1>} />
            <Route path="/search-for-proposal"
              element={<ProposalsListContent user={user} applicationDate={applicationDate} />} />
            <Route path="/teacher/application/browse"
              element={<BrowseApplicationsContent user={user} />} />
            <Route path="/login"
              element={<LoginLayout user={user} setUser={setUser} />} />
            <Route path="/browseDecisions"
              element={<BrowseDecisions user={user} />} />
            <Route path="/teacher/application/view"
              element={<ApplicationViewLayout user={user} realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate} />} />
            <Route path="/insertProposal"
              element={<InsertUpdateProposal user={user} />} />
            <Route path="/updateProposal/:proposalID"
              element={<InsertUpdateProposal user={user} />} />
            <Route path="/teacher/proposals"
              element={<BrowseProposalsContent user={user} applicationDate={applicationDate} />} />
            <Route path="/proposal/apply/:proposalId"
              element={<RenderProposal user={user} />} />
            <Route path="*"
              element={<h1>Path not found</h1>} />
            
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );

}
export default App;

library.add(fab, fas, far);
