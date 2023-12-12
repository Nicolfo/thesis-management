import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {AuthProvider} from "react-oauth2-code-pkce";
const authConfig = {
    clientId: 'oidc-client',
    authorizationEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/auth',
    logoutEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/logout',
    tokenEndpoint: 'http://localhost:8080/realms/oidcrealm/protocol/openid-connect/token',
    redirectUri: 'http://localhost:3000/', //change this to localhost:3000 if in developent/ localhost:8081 in deploy
    scope: 'profile openid',
    // Example to redirect back to original path after login has completed
    // preLogin: () => localStorage.setItem('preLoginPath', window.location.pathname),
    // postLogin: () => window.location.replace(localStorage.getItem('preLoginPath') || ''),
    decodeToken: true,
    autoLogin: false,
    onRefreshTokenExpire: (event) => window.confirm('Session expired. Refresh page to continue using the site?') && event.login(),
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
      <AuthProvider authConfig={authConfig}>
         <App />
      </AuthProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
