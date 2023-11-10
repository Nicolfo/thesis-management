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


const getAllTeacherGroups = async () => {
    return getJson(fetch(SERVER_URL + ''))
}

const insertProposal = async (proposal) => {
    return getJson(fetch(SERVER_URL + '', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(Object.assign({}, proposal))
    }))
};



// Get all proposals
// TODO: this is a placeholder until the back-end endpoint is available
// For now I always return the same hard-coded values
const getAllProposals = async () => {
    return [
        {
            "id": 2,
            "title": "A Mobile App for Remote Telepathology",
            "supervisor": {
                "id": 3,
                "surname": "Marchetto",
                "name": "Guido",
                "email": "g.marchetto@polito.iot",
                "group": {
                    "codGroup": "CHENERGY",
                    "name": "Chemistry and Energy Technologies"
                },
                "department": {
                    "codDepartment": "DET",
                    "name": "Electronics and Telecommunications"
                }
            },
            "coSupervisors": [
                {
                    "id": 1,
                    "surname": "Rossi",
                    "name": "Mario",
                    "email": "m.rossi@polito.it",
                    "group": {
                        "codGroup": "CMPCS",
                        "name": "Condensed Matter Physics and Complex Systems"
                    },
                    "department": {
                        "codDepartment": "DAD",
                        "name": "Architecture and Design"
                    }
                },
                {
                    "id": 3,
                    "surname": "Bianchi",
                    "name": "Luca",
                    "email": "l.bianchi@polito.it",
                    "group": {
                        "codGroup": "CREST",
                        "name": "Catalytic Reaction Engineering for Sustainable Technologies"
                    },
                    "department": {
                        "codDepartment": "CCE",
                        "name": "Control and Computer Engineering"
                    }
                }
            ],
            "keywords": "Mobile Applications, Artifical Intelligence, Human-Computer Interfaces, Machine Learning, Software Testing",
            "type": "External Company Project",
            "groups": [
                {
                    "codGroup": "GRAINS",
                    "name": "Generative Relational Analysis and INtegrations Systems"
                }
            ],
            "description": "Many End-to-End (E2E) testing tools allow developers to create repeatable test scripts that mimic a human user's interaction with the application and evaluate its response. Various paradigms of testing tools have been defined that are differentiated based upon the way the widgets are located on the GUI (e.g., text, images, layout properties). This thesis falls within the visual GUI testing paradigm scope, where screen snapshots used as visual locators:\n- identify the widgets, and\n- replicate an existing test suite across multiple devices or versions of the same app.\n\nThis technique leverages image recognition algorithms.\n\nIn particular, the thesis focuses on testing mobile applications where the same widgets have to be identified across multiple devices, with different screen sizes and characteristics. In this context, marginal variations in the graphical rendering can invalidate widgets' recognition if the image recognition algorithm is not sufficiently robust. This thesis aims to extend an existing prototype for visual GUI testing by leveraging deep learning. In particular, the following activities are envisioned:\n- Training a detector to identify widgets and their classes with high recall.\n- Using the features extracted by the neural network to match the same widgets when the app is rendered across multiple devices.\n- Investigating the possibility of integrating text extraction with image analysis.\n\nThe solution will be characterized and possibly optimized in terms of accuracy and execution speed. The developed techniques will be included in an open-source library.\nThe student should possess or should be willing to acquire these skills:\n- programming skills (Python, deep learning frameworks);\n- experience in training deep neural networks;\n- fundamentals of mobile development (Android GUI, the Android Studio development environment).",
            "requiredKnowledge": "programming skills (Python, deep learning frameworks), experience in training deep neural networks, fundamentals of mobile development (Android GUI, the Android Studio development environment)",
            "notes": "See also: http://grains.polito.it/work.php",
            "expiration": "2024-07-19T22:00:00.000+00:00",
            "level": "Master's",
            "cdS": "Electrical Engineering"
        },
        {
            "id": 3,
            "title": "3D Reconstruction and VR visualization of nuclear scattering events",
            "supervisor": {
                "id": 1,
                "surname": "Lamberti",
                "name": "Fabrizio",
                "email": "f.lamberti@polito.iot",
                "group": {
                    "codGroup": "CHENERGY",
                    "name": "Chemistry and Energy Technologies"
                },
                "department": {
                    "codDepartment": "DET",
                    "name": "Electronics and Telecommunications"
                }
            },
            "coSupervisors": [
                {
                    "id": 1,
                    "surname": "Rossi",
                    "name": "Mario",
                    "email": "m.rossi@polito.it",
                    "group": {
                        "codGroup": "CMPCS",
                        "name": "Condensed Matter Physics and Complex Systems"
                    },
                    "department": {
                        "codDepartment": "DAD",
                        "name": "Architecture and Design"
                    }
                },
                {
                    "id": 3,
                    "surname": "Bianchi",
                    "name": "Luca",
                    "email": "l.bianchi@polito.it",
                    "group": {
                        "codGroup": "CREST",
                        "name": "Catalytic Reaction Engineering for Sustainable Technologies"
                    },
                    "department": {
                        "codDepartment": "CCE",
                        "name": "Control and Computer Engineering"
                    }
                }
            ],
            "keywords": "Mobile Applications, Artifical Intelligence, Human-Computer Interfaces, Machine Learning, Software Testing",
            "type": "External Company Project",
            "groups": [
                {
                    "codGroup": "GRAINS",
                    "name": "Generative Relational Analysis and INtegrations Systems"
                }
            ],
            "description": "Many End-to-End (E2E) testing tools allow developers to create repeatable test scripts that mimic a human user's interaction with the application and evaluate its response. Various paradigms of testing tools have been defined that are differentiated based upon the way the widgets are located on the GUI (e.g., text, images, layout properties). This thesis falls within the visual GUI testing paradigm scope, where screen snapshots used as visual locators:\n- identify the widgets, and\n- replicate an existing test suite across multiple devices or versions of the same app.\n\nThis technique leverages image recognition algorithms.\n\nIn particular, the thesis focuses on testing mobile applications where the same widgets have to be identified across multiple devices, with different screen sizes and characteristics. In this context, marginal variations in the graphical rendering can invalidate widgets' recognition if the image recognition algorithm is not sufficiently robust. This thesis aims to extend an existing prototype for visual GUI testing by leveraging deep learning. In particular, the following activities are envisioned:\n- Training a detector to identify widgets and their classes with high recall.\n- Using the features extracted by the neural network to match the same widgets when the app is rendered across multiple devices.\n- Investigating the possibility of integrating text extraction with image analysis.\n\nThe solution will be characterized and possibly optimized in terms of accuracy and execution speed. The developed techniques will be included in an open-source library.\nThe student should possess or should be willing to acquire these skills:\n- programming skills (Python, deep learning frameworks);\n- experience in training deep neural networks;\n- fundamentals of mobile development (Android GUI, the Android Studio development environment).",
            "requiredKnowledge": "programming skills (Python, deep learning frameworks), experience in training deep neural networks, fundamentals of mobile development (Android GUI, the Android Studio development environment)",
            "notes": "See also: http://grains.polito.it/work.php",
            "expiration": "2024-07-19T22:00:00.000+00:00",
            "level": "Bachelor's",
            "cdS": "Computer Engineering"
        },
        {
            "id": 4,
            "title": "Computer vision techniques for mobile testing",
            "supervisor": {
                "id": 2,
                "surname": "Verdi",
                "name": "Giuseppe",
                "email": "g.verdi@polito.iot",
                "group": {
                    "codGroup": "CHENERGY",
                    "name": "Chemistry and Energy Technologies"
                },
                "department": {
                    "codDepartment": "DET",
                    "name": "Electronics and Telecommunications"
                }
            },
            "coSupervisors": [
                {
                    "id": 1,
                    "surname": "Rossi",
                    "name": "Mario",
                    "email": "m.rossi@polito.it",
                    "group": {
                        "codGroup": "CMPCS",
                        "name": "Condensed Matter Physics and Complex Systems"
                    },
                    "department": {
                        "codDepartment": "DAD",
                        "name": "Architecture and Design"
                    }
                },
                {
                    "id": 3,
                    "surname": "Bianchi",
                    "name": "Luca",
                    "email": "l.bianchi@polito.it",
                    "group": {
                        "codGroup": "CREST",
                        "name": "Catalytic Reaction Engineering for Sustainable Technologies"
                    },
                    "department": {
                        "codDepartment": "CCE",
                        "name": "Control and Computer Engineering"
                    }
                }
            ],
            "keywords": "Mobile Applications, Artifical Intelligence, Human-Computer Interfaces, Machine Learning, Software Testing",
            "type": "External Company Project",
            "groups": [
                {
                    "codGroup": "GRAINS",
                    "name": "Generative Relational Analysis and INtegrations Systems"
                }
            ],
            "description": "Many End-to-End (E2E) testing tools allow developers to create repeatable test scripts that mimic a human user's interaction with the application and evaluate its response. Various paradigms of testing tools have been defined that are differentiated based upon the way the widgets are located on the GUI (e.g., text, images, layout properties). This thesis falls within the visual GUI testing paradigm scope, where screen snapshots used as visual locators:\n- identify the widgets, and\n- replicate an existing test suite across multiple devices or versions of the same app.\n\nThis technique leverages image recognition algorithms.\n\nIn particular, the thesis focuses on testing mobile applications where the same widgets have to be identified across multiple devices, with different screen sizes and characteristics. In this context, marginal variations in the graphical rendering can invalidate widgets' recognition if the image recognition algorithm is not sufficiently robust. This thesis aims to extend an existing prototype for visual GUI testing by leveraging deep learning. In particular, the following activities are envisioned:\n- Training a detector to identify widgets and their classes with high recall.\n- Using the features extracted by the neural network to match the same widgets when the app is rendered across multiple devices.\n- Investigating the possibility of integrating text extraction with image analysis.\n\nThe solution will be characterized and possibly optimized in terms of accuracy and execution speed. The developed techniques will be included in an open-source library.\nThe student should possess or should be willing to acquire these skills:\n- programming skills (Python, deep learning frameworks);\n- experience in training deep neural networks;\n- fundamentals of mobile development (Android GUI, the Android Studio development environment).",
            "requiredKnowledge": "programming skills (Python, deep learning frameworks), experience in training deep neural networks, fundamentals of mobile development (Android GUI, the Android Studio development environment)",
            "notes": "See also: http://grains.polito.it/work.php",
            "expiration": "2024-07-19T22:00:00.000+00:00",
            "level": "Master's",
            "cdS": "Computer Engineering"
        },
    ];
}

const API = { login, getAllProposals, getAllTeacherGroups, insertProposal };
export default API;