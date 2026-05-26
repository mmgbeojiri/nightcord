const messageInput = document.getElementById("messageInput");
let editing = false;
let editing_id = "";
let link = "https://humble-space-disco-pjj7vvrw9j5r26v5r-8500.app.github.dev";
const nameInput = document.getElementById("nameInput")
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