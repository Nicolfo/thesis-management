import dayjs from "dayjs";
 
const SERVER_URL = "http://localhost:8081/API/";

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
                        .catch(err => reject({error: "Cannot parse server response"}))

                } else {
                    // analyzing the cause of error
                    response.json()
                        .then(obj =>
                            reject(obj)
                        ) // error msg in the response body
                        .catch(err => reject({error: "Cannot parse server response"})) // something else
                }
            })
            .catch(err => {
                    reject({error: "Cannot communicate"});
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
    return getJson(fetch(SERVER_URL + 'proposal/getByProf', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllCds = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'Degree/getAllNames', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getByEmail = async (email, jwt) => {
    return getJson(fetch(`${SERVER_URL}teacher/getByEmail/${email}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const insertProposal = async (proposal, jwt) => {
    return fetch(SERVER_URL + 'proposal/insert/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(Object.assign({}, proposal)) //Aggiungere JSON.stringify per cosupervisors e groups se non funziona
    })
};

const updateProposal = async (proposal, jwt) => {
    return fetch(`${SERVER_URL}proposal/update/${proposal.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(Object.assign({}, proposal)) //Aggiungere JSON.stringify per cosupervisors e groups se non funziona
    })
};

const getApplicationsByProf = async (jwt) => {
    return getJson(fetch(SERVER_URL + "application/getByProf", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getApplicationsByStudent = async (jwt) => {
    return getJson(fetch(SERVER_URL + "application/getByStudent", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getProposalOnRequestByStudent = async (jwt) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/getByStudent", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getApplicationsByProposalId = async (jwt, proposalId) => {
    try {
        const response = await getJson(fetch(SERVER_URL + `application/getApplicationsByProposalId/${proposalId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`,
            },
        }));
        return response;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

const getAllProposals = async (jwt) => {
    return getJson(fetch(SERVER_URL + "proposal/getAll", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllProposalsOnRequest = async (jwt) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/getAllPending", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllTeachers = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'teacher/getAll', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const getAllGroups = async (jwt) => {
    return getJson(fetch(SERVER_URL + "group/getAll", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const searchProposals = async (jwt, body) => {
    return getJson(fetch(SERVER_URL + "proposal/search", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

async function archiveProposal(proposalId, jwt) {


    return new Promise((resolve, reject) => {
        fetch(SERVER_URL + `proposal/archive/${proposalId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`,
            },
            body: JSON.stringify({"proposalId": proposalId})
        })
            .then(response => {
                if (!response.ok) {
                    // Handle non-successful responses
                    reject(`Error: ${response.status} - ${response.statusText}`);
                } else {
                    // Resolve if the status is OK
                    resolve();
                }
            })
            .catch(error => {
                // Handle network errors or other exceptions

                reject(`Error: ${error.message}`);
            });
    });
};

async function deleteProposal(proposalId, jwt) {
    return new Promise((resolve, reject) => {
        fetch(SERVER_URL + 'proposal/delete/' + proposalId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    // Handle non-successful responses
                    reject(`Error: ${response.status} - ${response.statusText}`);
                } else {
                    // Resolve if the status is OK
                    resolve();
                }
            })
            .catch(error => {
                // Handle network errors or other exceptions

                reject(`Error: ${error.message}`);
            });
    });
};

async function getAllSupervisors() {
    return getJson(fetch(SERVER_URL + 'teacher/getAll'));
};

async function uploadFile(file) {
    const formData = new FormData();
    formData.append("file", file, file.name);
    let respJson;
    let response;
    try {
        response = await fetch(SERVER_URL + 'uploadFile', {
            method: "POST", body: formData,
        });
        respJson = await response.json();
    } catch (e) {
        console.log(e);
        throw {status: 404, detail: "Cannot communicate with server"}
    }
    return respJson
}

async function insertApplication(cvId, proposalId, jwt) {
    return new Promise((resolve, reject) => {
        fetch(SERVER_URL + 'application/insert/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                },
                body: JSON.stringify({
                    attachmentId: cvId ? cvId.id : null,
                    applyDate: dayjs(),
                    proposalId: proposalId,
                }),
            }
        ).then(async (response) => {
            // Check if the response status is OK (200)
            if (response.ok) {
                resolve(); // Assuming the response is in JSON format
            } else {
                reject(await response.json());
            }
        }).catch(err => {
                reject({error: "Cannot communicate"});
            }
        ) // connection error
    });
};


const getArchivedProposalsByProf = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'proposal/getArchived',{
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const searchArchivedProposals = async (jwt, body) => {
    return getJson(fetch(SERVER_URL + "proposal/searchArchived", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const secretaryAccept = async(jwt, id) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/updateStatus/secretaryAccepted/" + id , {
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
}

const setVirtualClock=async(newOffset) =>{
    fetch(SERVER_URL + 'virtualTimer/changeOffset/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                offset:newOffset
            }),
        }
    )
};

 const startRequest = async (request,jwt) => {
    return getJson(fetch(SERVER_URL + 'proposalOnRequest/create/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(Object.assign({}, request))
    }))
};

const secretaryReject = async(jwt, id) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/updateStatus/secretaryRejected/" + id , {
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
};

const teacherAccept = async (jwt, id) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/updateStatus/teacherAccepted/" + id , {
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
};

const teacherRequestChange = async (jwt, id, description) => {
    const requestChangeDTO = { requestedChange: description };
    return getJson(fetch(SERVER_URL + "proposalOnRequest/updateStatus/teacherRequestChange/" + id , {
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        },
        body: JSON.stringify(Object.assign({}, requestChangeDTO))
    }));
};

const teacherReject = async (jwt, id) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/updateStatus/teacherRejected/" + id , {
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
};

const getAcceptedProposalOnRequestsByTeacher = async (jwt) => {
    return getJson(fetch(SERVER_URL + "proposalOnRequest/getByTeacherAccepted", {
        method: 'GET',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,
        }
    }));
};

const getAllNotificationsOfCurrentUser = async (jwt) => {
    return getJson(fetch(SERVER_URL + 'notification/getAllNotificationsOfCurrentUser/', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    }))
};

const getSingleNotification = async (jwt, id) => {
    return getJson(fetch(SERVER_URL + `notification/getSingleNotifications/${id}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    }))
};

const markNotificationAsRead = async (jwt, id) => {
    return getJson(fetch(SERVER_URL + `notification/markNotificationAsRead/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    }))
};

const getUnreadNotificationsCount = async (jwt) => {
    return getJson(fetch(SERVER_URL + `notification/getUnreadNotificationsCount/`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    }))
};

const getApplicationById = async (jwt, id) => {
    return getJson(fetch(SERVER_URL + `application/getApplicationById/${id}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    }))
};

const API = { markNotificationAsRead, getUnreadNotificationsCount, getAllNotificationsOfCurrentUser, getSingleNotification, getProposalOnRequestByStudent, getAcceptedProposalOnRequestsByTeacher, teacherReject, teacherRequestChange, teacherAccept, insertApplication, uploadFile, getAllSupervisors, deleteProposal, archiveProposal, searchProposals, getAllGroups, getApplicationsByStudent,getApplicationsByProf, getApplicationsByProposalId, login, getAllProposals, getAllProposalsOnRequest, getAllTeachers, getAllCds, getByEmail, getProposalsByProf, insertProposal, updateProposal, getArchivedProposalsByProf, searchArchivedProposals, secretaryAccept, secretaryReject, startRequest , setVirtualClock, getApplicationById};

export default API;
