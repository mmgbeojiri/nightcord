const messageInput = document.getElementById("messageInput");
let editing = false;
let editing_id = "";
let link = "https://humble-space-disco-pjj7vvrw9j5r26v5r-8500.app.github.dev";

const lukasMode = false;
let currentChannel = "Tweets";
const autoFetch = true; // If this value is false, the service doesnt auto fetch messages. useful for checking console on frontend only days when too many 404 errors.

let storedDarkMode = window.localStorage.getItem("darkMode");
let darkMode = storedDarkMode === "true";
setDarkMode(darkMode);

function setDarkMode(value) {
  darkMode = value;
  window.localStorage.setItem("darkMode", darkMode);
  console.log(value) 

  document.getElementById("darkModeButton").innerText = `Dark Mode: ${darkMode ? "On" : "Off"}`;
  if (value) {
    document.body.classList.add("dark");
  } else {
    document.body.classList.remove("dark");
  };
}

if (lukasMode) {
  document.getElementById("messageInput").placeholder = "Lukas Mode is on. No changes will be sent, edited, or deleted."
}

function toggleEditing(bool) { // Toggles The Editing State
  editing = bool;
  let editingNote = document.getElementById("editing");

  activeElement = document.getElementById(editing_id);
  if (editing) {
    editingNote.style.display = "block";
    activeElement.classList.add("active");
  } else {
    editingNote.style.display = "none";
    activeElement.classList.remove("active");
  }
}

messageInput.addEventListener("keydown", function (event) { // Checks if user presses enter in the type box
  // If the user presses the "Enter" key on the keyboard
  if (event.key === "Enter" && !event.shiftKey) { // allow for shift enter
    // Cancel the default action, if needed, especially for textareas
    event.preventDefault();
    // Trigger the form submission or button click
    sendData();

  }

  if (event.key === "Escape" && editing) { // if escape is pressed while editing, cancel the edit
    toggleEditing(false);
    messageInput.value = "";
  }
});


const myDropdown = document.getElementById('myDropdown');
const svgButton = document.getElementById('svgButton');
document.addEventListener('click', (event) => {
  // If the click happened OUTSIDE the element
  if (!myDropdown.contains(event.target) && !svgButton.contains(event.target)) {
  if (!myDropdown.classList.contains('hidden')) {
    myDropdown.classList.add('hidden');
    svgButton.classList.remove('active');
    console.log("clicking outside dropdown when active");
  }
}
});

const normalize = (str) => str.replace(/\s+/g, '')
let htmlCode = "";
let jsonData = [];

async function getData() { // Function to get messages 
  let lastJsonData = jsonData;
  jsonData = [];
  htmlCode = ""




  jsonContainer = document.getElementById("jsonContainer");

  try {
    await fetch(link + "/")
      .then(response => response.json())
      .then(data => {
        htmlCode = `<h1>${currentChannel}</h1>`;
        data.forEach(element => {
          if (element.Channel == currentChannel) {
            htmlCode = `
            <div class="message" id="message_${element.Id}">
              <div class="messageHeader">
                <h1>${element.Name}</h1>
                <div class="buttonContainer">
                <button onclick="copyData('${element.Message}')">Copy</button>
                <button onclick="populateData('${element.Id}')">Edit</button>
                <button onclick="deleteData('${element.Id}', '${element.Message}')">Delete</button>
                </div>
              </div>
              <div class="messageContent">
              <p>${element.Message}</p>
              `
              + (element.Edited == 1 ? "<p class='editDisclaimer'>(edited)</p>" : "") +
              `
              </div>
              <span>${new Date(element.Timestamp).toLocaleString()}</span>
              </div>` + htmlCode;
            jsonData.push(element);
          }
        });
      }).finally(() => {
        //console.log("Fetch Completed.");



        /*if (normalize(jsonContainer.innerHTML) != normalize(htmlCode)) { // Only update the HTML if it has changed
          //console.log("Html is DIFFERENT. changing.");
          jsonContainer.innerHTML = htmlCode;

          //console.log(typeof lastHTML, typeof htmlCode);
        }*/

        if (JSON.stringify(jsonData) != JSON.stringify(lastJsonData)) { // Only update the HTML if the data has changed
          console.log("Data is DIFFERENT. changing.");
          jsonContainer.innerHTML = htmlCode;
        }
       
      });
  } catch {
    response = "Server Returned 500."
    console.log("Server Error.")
    return
  }


}

let channelHTMLCode = "";
let channelJsonData = [];

async function getChannelData() {
  
  channelContainer = document.getElementById("buttonContainer");
  channelHTMLCode = ""
  let lastChannelJsonData = channelJsonData;
  channelJsonData = [];


  try {
    await fetch(link + "/channelNames")
      .then(response => response.json())
      .then(data => {
        data.forEach(element => {
          channelHTMLCode += `
            <div 
            id="channel_${element.Id}" 
            onclick="changeActiveChannel('${element.Name}');"
            class="${element.Name == currentChannel ? "activeButton" : ""}">
            <p>${element.Name}</p>
            <div class="channelEditButtons">
            <button onclick="event.stopPropagation(); channelEdit('${element.Name}', '${element.Id}');">Edit</button>
            <button onclick="event.stopPropagation(); channelDelete('${element.Name}', '${element.Id}');">X</button>
            </div>
            </div>
            `
            channelJsonData.push(element);
        });
      }).finally(() => {
        //console.log("Fetch Completed.");



        if (JSON.stringify(lastChannelJsonData) != JSON.stringify(channelJsonData)) { // Only update the HTML if it has changed
          //We use a normaizlization function to ignore whitespace
          console.log("json for channels is DIFFERENT. changing.");

          channelContainer.innerHTML = channelHTMLCode;
          

        } 
      });
  } catch {
    response = "Server Returned 500."
    console.log("Server Error.")
    return
  }
}

function changeActiveChannel(channelName) {
  currentChannel = channelName;
  document.querySelectorAll("#buttonContainer > div").forEach(element => { // each element represents a channel button
    if (element.querySelector("p").innerText.trim() == channelName) {
      element.classList.add("activeButton");
    } else {
      element.classList.remove("activeButton");
    }
  });
  getData();
};

async function sendData() { // Function to send and edit messages

  name = document.getElementById("nameInput").value;
  message = document.getElementById("messageInput").value;


  if (name == "" || normalize(name) == "" || message == "" || normalize(message) == "") {
    return;

  };

  messageInput.value = "";

  if (editing) { // PUT REQUEST
    data = {
      "Name": name,
      "Message": message,
      "Timestamp": new Date(document.getElementById(editing_id).querySelector("span").innerText).toISOString(),
      "Id": editing_id,
    }

    toggleEditing(false);

    try {
      const response = await fetch(link + "/put", {
        method: "PUT", // *MUST* be 'PUT' for a PUT request
        headers: {
          "Content-Type": "application/json", // Indicates the body format is JSON
        },
        body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
      });
    } catch (error) {
      console.error("Error:", error); // Handles network errors or the error thrown above
    }

    getData();
    return;
  }

  // POST REQUEST 
  data = {
    "Name": name,
    "Message": message,
    "Timestamp": new Date().toISOString(),
    "Channel": currentChannel
  }
  console.log("button pressed.");
  try {
    const response = await fetch(link + "/post", {
      method: "POST", // *MUST* be 'POST' for a POST request
      headers: {
        "Content-Type": "application/json", // Indicates the body format is JSON
      },
      body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
    });
  } catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

  getData();
  return;
}
async function channelCreate() { // Function to send and edit messages

  if (lukasMode) {
    return;
  }

  nameOfNewChannel = prompt("Enter Channel Name");

  if (nameOfNewChannel == "" || normalize(nameOfNewChannel) == "") {
    return;

  };

  // POST REQUEST 
  data = {
    "Name": nameOfNewChannel,
  }
  console.log("button pressed.");
  try {
    const response = await fetch(link + "/channels", {
      method: "POST", // *MUST* be 'POST' for a POST request
      headers: {
        "Content-Type": "application/json", // Indicates the body format is JSON
      },
      body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
    });
  } catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

  getChannelData();
  return;
}

async function channelEdit(oldName, id) { // Function to send and edit messages

  if (lukasMode) {
    return;
  }

  nameOfNewChannel = prompt(`Rename channel '${oldName}' to:`);

  if (nameOfNewChannel == null || normalize(nameOfNewChannel) == "") {
    return;

  };


  // POST REQUEST 
  data = {
    "Name": nameOfNewChannel,
    "Id": id,
    "OldName": oldName
  }
  console.log(data);
  try {
    const response = await fetch(link + "/channels", {
      method: "PUT", // *MUST* be 'POST' for a POST request
      headers: {
        "Content-Type": "application/json", // Indicates the body format is JSON
      },
      body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
    });
  } catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

  getChannelData();
  if (currentChannel == oldName) {
    changeActiveChannel(nameOfNewChannel); // if the user renames channel they are in.
  }
  return;
}

async function channelDelete(name, id) {
  if (lukasMode) {
    return;
  }

  data = {
    "Id": id,
    "Channel": name,
  }
  
  if (confirm(`Are you sure you want to delete this channel: "${name}"?`) == false) {
    return;
  }
  console.log(data);
  try {
    const response = await fetch(link + "/channels", {
      method: "DELETE", // *MUST* be 'DELETE' for a DELETE request
      headers: {
        "Content-Type": "application/json", // Indicates the body format is JSON
      },
      body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
    });
  } catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

  getChannelData();
  if (currentChannel == name) {
    changeActiveChannel("Tweets"); // if the user deletes the channel they are in, move them to the default channel.
  }
  return;
}

function copyData(text) {
  navigator.clipboard.writeText(text);
}

async function deleteData(id, message) { // Function to delete messages
  if (lukasMode) {
    return;
  }

  if (confirm(`Are you sure you want to delete this message: "${message}"?`) == false) {
    return;
  }

  data = {
    "Id": id,
  }
  // yes we have to put the singular value in a json file
  try {
    const response = await fetch(link + "/delete", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json", // Indicates the body format is JSON
      },
      body: JSON.stringify(data), // Converts the JavaScript object to a JSON string
    });
  } catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

  getData();

}

function populateData(id) { // Function to populate the input fields with the message data for editing
  if (lukasMode) {
    return;
  }
  message = document.getElementById(id);
  message_name = message.querySelector("h1").innerText;
  message_text = message.querySelector("p").innerText;

  document.getElementById("nameInput").value = message_name;
  document.getElementById("messageInput").value = message_text;
  editing_id = id;
  toggleEditing(true); // its not a toggle if you specifiy the boolean!
}

//getChannelData();
//getData(); // Initial Fetch Of Messages
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

const startLoop = async () => {
  while (autoFetch) {
    await getData();
    await getChannelData();
    await delay(1000); // Wait 1 second before the next iteration
  }
};

startLoop(); // Start the loop