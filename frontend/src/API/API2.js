

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
    );
};

const getProposalsByProf = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'proposal/getByProf',{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllTeachers = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'teacher/getAll',{
        method: 'GET',
            headers:{
            'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllCds = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'Degree/getAllNames',{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getByEmail = async (email,jwt) => {
    return getJson(fetch(`${SERVER_URL}teacher/getByEmail/${email}`,{
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const insertProposal = async (proposal,jwt) => {
    return fetch(SERVER_URL + 'proposal/insert', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(Object.assign({}, proposal)) //Aggiungere JSON.stringify per cosupervisors e groups se non funziona
    })
};

const updateProposal = async (proposal,jwt) => {
    return fetch(`${SERVER_URL}proposal/update/${proposal.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(Object.assign({}, proposal)) //Aggiungere JSON.stringify per cosupervisors e groups se non funziona
    })
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

const getAllGroups = async (jwt) => {
    return getJson(fetch(SERVER_URL + "group/getAll" , {
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const searchProposals = async(jwt, body) => {
    return getJson(fetch(SERVER_URL + "proposal/search" , {
        method: 'POST',
        body: JSON.stringify(body),
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}



const API = { login, getAllProposals, getAllTeachers, getAllCds, getByEmail, getProposalsByProf, insertProposal, updateProposal, getAllGroups, searchProposals };
export default API;
