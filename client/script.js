messageInput = document.getElementById("messageInput");
  editing = false;
  editing_id = "";
  link = "https://humble-space-disco-pjj7vvrw9j5r26v5r-8500.app.github.dev";

  lukasMode = false;

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
   
  messageInput.addEventListener("keydown", function(event) { // Checks if user presses enter in the type box
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
    htmlCode = ""
  async function getData() { // Function to get messages 
    jsonData = [];
    lastHTML = htmlCode
    htmlCode = ""
    jsonContainer = document.getElementById("jsonContainer");
    try {
      fetch(link + "/")
      .then(response => response.json())
      .then(data => {
        data.forEach(element => {
          htmlCode = `
          <div class="message" id="${element.Id}">
            <div class="messageHeader">
              <h1>${element.Name}</h1>
              <div class="buttonContainer">
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
            </div>` +htmlCode;
          jsonData.push(element);
        });
      }).finally(() => {
        console.log("Fetch Completed.");
        


    if (lastHTML != htmlCode) { // Only update the HTML if it has changed
      console.log("Html is DIFFERENT. changing.");
      jsonContainer.innerHTML = htmlCode;

      console.log(typeof lastHTML, typeof htmlCode);
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
      "Timestamp": new Date().toISOString()
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
  }  catch (error) {
    console.error("Error: ", error); // Handles network errors or the error thrown above
  }

    getData();
    return;
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
    }  catch (error) {
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
    message_text= message.querySelector("p").innerText;

    document.getElementById("nameInput").value = message_name;
    document.getElementById("messageInput").value = message_text;
    editing_id = id;
    toggleEditing(true); // its not a toggle if you specifiy the boolean!
  }
  
  getData(); // Initial Fetch Of Messages
  setInterval(getData, 1000); // Auto Fetch New Messages Every Second

  /*if anyone wants to copy the message, this get data function removes their selection every second and is annoying.*/