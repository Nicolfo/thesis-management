import dayjs from 'dayjs';
const URL = 'http://localhost:8080';



function getJson(httpResponsePromise) {
    // server API always return JSON, in case of error the format is the following { error: <message> }
    return new Promise((resolve, reject) => {
      httpResponsePromise
        .then((response) => {
          if (response.ok) {

           // the server always returns a JSON, even empty {}. Never null or non json, otherwise the method will fail
           response.json()
              .then( json => resolve(json) )
              .catch( err => reject({ error: "Cannot parse server response" }))

          } else {
            // analyzing the cause of error
            response.json()
              .then(obj =>
                reject(obj)
                ) // error msg in the response body
              .catch(err => reject({ error: "Cannot parse server response" })) // something else
          }
        })
        .catch(err =>
          reject({ errorFetch: "Cannot communicate"  })
        ) // connection error
    });
  }


export async function getAllProposal() {
    return getJson( fetch(URL + '/API/proposal/getAll'));
};

export async function getAllSupervisors() {
    return getJson( fetch(URL + '/API/teacher/getAll'));
};

export async function uploadFile(file){
    const formData = new FormData();
    formData.append("file", file, file.name);
    let respJson;
    let response;
    try { response = await fetch(URL +'/API/uploadFile', {
        method: "POST",body: formData,
    });  respJson = await response.json();
    } catch (e) { console.log(e); throw {status: 404, detail: "Cannot communicate with server"}
    }        return respJson
}

export async function insertApplication(cvId, proposalId) {
    fetch(URL + '/API/application/insert', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            attachmentID: cvId.id,
            applyDate:dayjs(),
            proposalID: proposalId,
        }),
    }
)};


export async function deleteProposal(proposalId) {

    return getJson( fetch(URL + '/API/proposal/delete', {
        method: 'DELETE',
        headers : {'Content-Type': 'application/json'},
        body: JSON.stringify({"proposalId": proposalId})
    }))
};


export async function archiveProposal(proposalId,jwt) {

    return getJson( fetch(URL + `/API/proposal/archive/${proposalId}`, {
        method: 'POST',
        headers : {'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`,},
        body: JSON.stringify({"proposalId": proposalId})
    }))
};