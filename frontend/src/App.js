import logo from './logo.svg';
import './App.css';
import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter as Router, useLocation} from "react-router-dom";
import SideBar from "./SideBar/SideBar";
import NavBar from "./NavBar/NavBar";

function Content(props) {







  const path = useLocation().pathname.toString();
  switch (path) {                                //add to this switch-case your content (defined in the Content folder)
    case "/":
    case "/path-one":

      return <></>

    default:
      return <h1>Path not found</h1>
  }
}

function App() {

  return (
      <div className="container-fluid" style={{height: '90vh'}}>
        <div className="row align-items-start">
          <Router>
            <NavBar>
            </NavBar>
            <SideBar>
            </SideBar>
            <div className="col-9">
              <Content>
              </Content>
            </div>
          </Router>
        </div>
      </div>
  );
}

export default App;
