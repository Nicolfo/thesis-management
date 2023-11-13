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

