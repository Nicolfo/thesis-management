"use strict";

const SERVER_URL = "http://localhost:8080/API/";

/**
 * A utility function for parsing the HTTP response.
 */
function getJson(httpResponsePromise) {
    // server API always return JSON, in case of error the format is the following { error: <message> }
    return new Promise((resolve, reject) => {
        httpResponsePromise
            .then((response) => {
                if (response.ok) {

                    // the server always returns a JSON, even empty {}. Never null or non json, otherwise the method will fail
                    response.json()
                        .then(json => resolve(json))
                        .catch(err => reject({ error: "Cannot parse server response" }))

                } else {
                    // analyzing the cause of error
                    response.json()
                        .then(obj =>
                            reject(obj)
                        ) // error msg in the response body
                        .catch(err => reject({ error: "Cannot parse server response" })) // something else
                }
            })
            .catch(err => {
                    reject({ error: "Cannot communicate" });
                    console.log(httpResponsePromise);
                }
            ) // connection error
    });
}

//Login: this function wants username and password inside a "credentials" object
const login = async (credentials) => {
    return getJson(fetch(SERVER_URL + 'login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        })
    )
};

const getAllProposals = async (jwt) => {
    return getJson(fetch(SERVER_URL+"proposal/getAll",{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getProposalsByProf = async (jwt) => {
    return getJson(fetch(SERVER_URL+"proposal/getByProf",{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

// Get all applications
const getAllApplications = async (jwt) => {
    return getJson(fetch(SERVER_URL+"application/getByProf",{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getTitleByApplicationId = async (jwt, applicationId) => {
    return getJson(fetch(SERVER_URL+`/application/getTitleByApplicationId/${applicationId}`,{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getStudentInfo = async (jwt, id) => {
    return getJson(fetch(SERVER_URL+`application/getNameById/${id}`,{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}



const API = { login, getAllApplications, getTitleByApplicationId, getStudentInfo, getAllProposals, getProposalsByProf };
export default API;