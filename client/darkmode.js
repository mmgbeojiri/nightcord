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