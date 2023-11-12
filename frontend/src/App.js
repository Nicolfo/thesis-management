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
import ApplicationViewLayout from "./Content/ApplicationViewLayout";

function Content(props) {
  const [user, setUser] = useState(null);
  const path = useLocation().pathname.toString();

  switch (path) {
    case "/":
      return <Navigation user={user} realDate={props.realDate} applicationDate={props.applicationDate} updateApplicationDate={props.updateApplicationDate}/>
    case "/login":
      return <LoginLayout user={user} setUser={setUser}/>
    case "/application/view":
      return <ApplicationViewLayout user={user} realDate={props.realDate} applicationDate={props.applicationDate} updateApplicationDate={props.updateApplicationDate}/>
    default:
      return <h1>Path not found</h1>
  }
}

function App() {

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
      <div className="container-fluid" style={{height: '90vh', padding:'0rem'}}>
        <div className="row align-items-start">
          <Router>
            <div>
              <Content realDate={realDate} applicationDate={applicationDate} updateApplicationDate={updateApplicationDate}>
              </Content>
            </div>
          </Router>
        </div>
      </div>
  );
}

export default App;

library.add(fab, fas, far);
