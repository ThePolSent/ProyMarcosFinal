
const htmlElement = document.documentElement;

const toggleBtn = document.getElementById("toggleDarkMode");

const applySavedTheme = () => {
  const savedTheme = localStorage.getItem("theme");
  if (savedTheme) {
    htmlElement.setAttribute('data-bs-theme', savedTheme);
    if (savedTheme === 'dark') {
      toggleBtn.innerHTML = '<i class="bi bi-sun"></i>';
    } else {
      toggleBtn.innerHTML = '<i class="bi bi-moon"></i>';
    }
  }
};

applySavedTheme();

toggleBtn.addEventListener("click", () => {
  const currentTheme = htmlElement.getAttribute('data-bs-theme');
  const newTheme = currentTheme === 'dark' ? 'light' : 'dark';


  htmlElement.setAttribute('data-bs-theme', newTheme);
  localStorage.setItem('theme', newTheme);

  if (newTheme === 'dark') {
    toggleBtn.innerHTML = '<i class="bi bi-sun"></i>';
  } else {
    toggleBtn.innerHTML = '<i class="bi bi-moon"></i>';
  }
});