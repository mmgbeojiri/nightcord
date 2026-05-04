messageInput = document.getElementById("messageInput");
editing = false;
editing_id = "";
link = "https://humble-space-disco-pjj7vvrw9j5r26v5r-8500.app.github.dev";
lukasMode = false;
currentChannel = "Tweets";

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
let htmlCode = "";

async function getData() { // Function to get messages 
  jsonData = [];
  let lastHTML = htmlCode; // Store the last HTML code to compare for changes
  htmlCode = ""




  jsonContainer = document.getElementById("jsonContainer");

  try {
    fetch(link + "/")
      .then(response => response.json())
      .then(data => {
        data.forEach(element => {
          if (element.Channel == currentChannel) {
            htmlCode = `
            <div class="message" id="${element.Id}">
              <div class="messageHeader">
                <h1>${element.Name}</h1>
                <div class="buttonContainer">
                <button onclick="copyData('${element.Message}')">copy</button>
                <button onclick="populateData('${element.Id}')">edit</button>
                <button onclick="deleteData('${element.Id}')">delete</button>
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



        if (lastHTML != htmlCode) { // Only update the HTML if it has changed
          //console.log("Html is DIFFERENT. changing.");
          jsonContainer.innerHTML = htmlCode;

          //console.log(typeof lastHTML, typeof htmlCode);
        }
      });
  } catch {
    response = "Server Returned 500."
    console.log("Server Error.")
    return
  }


}
let channelHTMLCode = "";
async function getChannelData() {

  let lastChannelHTML = channelHTMLCode; // Store the last HTML code to compare for changes
  channelHTMLCode = ""

  channelContainer = document.getElementById("buttonContainer");

  try {
    fetch(link + "/channelNames")
      .then(response => response.json())
      .then(data => {
        data.forEach(element => {
          channelHTMLCode += `
            <div 
            id="channel_${element.Id}" 
            onclick="currentChannel = '${element.Name}'; getData();"
            class=${element.Name == currentChannel ? "activeButton" : ""} >
            <p>${element.Name}<p>
            <div class="channelEditButtons">
            <button>Edit</button>
            <button>X</button>
            </div>
            </div>
            `

        });
      }).finally(() => {
        //console.log("Fetch Completed.");



        if (lastChannelHTML != channelHTMLCode) { // Only update the HTML if it has changed
          //console.log("Html for channels is DIFFERENT. changing.");
          channelContainer.innerHTML = channelHTMLCode;

        }
      });
  } catch {
    response = "Server Returned 500."
    console.log("Server Error.")
    return
  }
}
async function sendData() { // Function to send and edit messages

  name = document.getElementById("nameInput").value;
  message = document.getElementById("messageInput").value;


  if (name == "" || message == "") {
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

  if (nameOfNewChannel == "") {
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

  if (nameOfNewChannel == "") {
    return;

  };

  // POST REQUEST 
  data = {
    "Name": nameOfNewChannel,
    "Id": id,
    "oldName": oldName
  }
  console.log("button pressed.");
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
  return;
}

function copyData(text) {
  navigator.clipboard.writeText(text);
}

async function deleteData(id) { // Function to delete messages
  if (lukasMode) {
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
  while (true) {
    getData();
    getChannelData();
    await delay(1000); // Wait 1 second before the next iteration
  }
};

startLoop(); // Start the loop