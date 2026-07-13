const cities = [
  {name: "Delhi", code: "DEL", country: "India"},
  {name: "Bengaluru", code: "BLR", country: "India"},
  {name: "Mumbai", code: "BOM", country: "India"},
  {name: "Hyderabad", code: "HYD", country: "India"}
];

const fromInput = document.querySelector("input[name='from']");
const suggestionList = document.querySelector(".sg-list-a9f");
const dateTrigger = document.querySelector("#departure-date");
const calendar = document.querySelector("#calendar");
const monthHeading = document.querySelector(".calendar-month");
const calendarGrid = document.querySelector(".calendar-grid");
const monthNames = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];
const monthShortNames = [
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];
const weekdayShortNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
let displayedMonth = new Date(Date.UTC(2026, 5, 1));

function showSuggestions(query) {
  const matches = cities.filter(city => city.name.toLowerCase().includes(query.toLowerCase()));
  suggestionList.innerHTML = "";

  matches.forEach(city => {
    const item = document.createElement("li");
    item.className = "sg-item sg-item-" + city.code.toLowerCase();
    item.setAttribute("role", "option");
    item.innerHTML = `<span>${city.name}</span><small>${city.code} &middot; ${city.country}</small>`;
    item.addEventListener("click", () => {
      fromInput.value = city.name;
      fromInput.dataset.airport = city.code;
      suggestionList.hidden = true;
    });
    suggestionList.appendChild(item);
  });

  suggestionList.hidden = matches.length === 0;
}

fromInput.addEventListener("input", event => {
  delete fromInput.dataset.airport;
  showSuggestions(event.target.value);
});

fromInput.addEventListener("focus", () => showSuggestions(fromInput.value));

document.addEventListener("click", event => {
  if (!event.target.closest(".autosuggest")) {
    suggestionList.hidden = true;
  }
});

function dayAriaLabel(year, month, day) {
  const date = new Date(Date.UTC(year, month, day));
  return `${weekdayShortNames[date.getUTCDay()]} ${monthShortNames[month]} ${day} ${year}`;
}

function renderCalendar() {
  const year = displayedMonth.getUTCFullYear();
  const month = displayedMonth.getUTCMonth();
  const firstWeekday = new Date(Date.UTC(year, month, 1)).getUTCDay();
  const daysInMonth = new Date(Date.UTC(year, month + 1, 0)).getUTCDate();

  monthHeading.textContent = `${monthNames[month]} ${year}`;
  calendarGrid.innerHTML = "";

  for (let spacer = 0; spacer < firstWeekday; spacer += 1) {
    const blank = document.createElement("span");
    blank.className = "calendar-spacer";
    blank.setAttribute("aria-hidden", "true");
    calendarGrid.appendChild(blank);
  }

  for (let day = 1; day <= daysInMonth; day += 1) {
    const dateButton = document.createElement("button");
    const ariaLabel = dayAriaLabel(year, month, day);
    dateButton.type = "button";
    dateButton.className = "DayPicker-Day DayPicker-Day-" + String(day).padStart(2, "0");
    dateButton.setAttribute("aria-label", ariaLabel);
    dateButton.textContent = day;
    dateButton.addEventListener("click", () => {
      dateTrigger.textContent = ariaLabel;
      dateTrigger.dataset.selectedDate = ariaLabel;
      dateTrigger.setAttribute("aria-expanded", "false");
      calendar.hidden = true;
    });
    calendarGrid.appendChild(dateButton);
  }
}

dateTrigger.addEventListener("click", () => {
  calendar.hidden = !calendar.hidden;
  dateTrigger.setAttribute("aria-expanded", String(!calendar.hidden));
  renderCalendar();
});

document.querySelector(".DayPicker-NavButton--next").addEventListener("click", () => {
  displayedMonth = new Date(Date.UTC(
    displayedMonth.getUTCFullYear(),
    displayedMonth.getUTCMonth() + 1,
    1
  ));
  renderCalendar();
});

document.querySelector("#search-flights").addEventListener("click", () => {
  const status = document.querySelector("#search-status");
  if (!fromInput.dataset.airport) {
    status.textContent = "Select a city from the suggestions.";
    return;
  }
  if (!dateTrigger.dataset.selectedDate) {
    status.textContent = "Choose a departure date.";
    return;
  }

  status.textContent = "";
  document.querySelector("#results").hidden = false;
  document.querySelector("#results").scrollIntoView({behavior: "instant", block: "start"});
});

function showResultType(type) {
  const flightsSelected = type === "flights";
  document.querySelector("#flight-results").hidden = !flightsSelected;
  document.querySelector("#bus-results").hidden = flightsSelected;
  document.querySelector("#flights-tab").setAttribute("aria-selected", String(flightsSelected));
  document.querySelector("#buses-tab").setAttribute("aria-selected", String(!flightsSelected));
}

document.querySelector("#flights-tab").addEventListener("click", () => showResultType("flights"));
document.querySelector("#buses-tab").addEventListener("click", () => showResultType("buses"));

document.querySelectorAll(".book-action").forEach(button => {
  button.addEventListener("click", event => {
    const card = event.target.closest("[class*='flight-card']");
    const number = card.querySelector(".num").textContent;
    document.querySelector("#booking-status").textContent = `Flight ${number} selected.`;
  });
});

document.querySelectorAll(".seat-action").forEach(button => {
  button.addEventListener("click", event => {
    const card = event.target.closest("[class*='bus-card']");
    const operator = card.querySelector(".operator").textContent;
    const seatMap = document.querySelector("#seat-map");
    seatMap.querySelector(".eyebrow").textContent = operator;
    seatMap.hidden = false;
    document.querySelector("#booking-status").textContent = `${operator} seat map opened.`;
    seatMap.scrollIntoView({behavior: "instant", block: "center"});
  });
});

document.querySelectorAll(".seat.available").forEach(seat => {
  seat.addEventListener("click", event => {
    document.querySelectorAll(".seat.selected").forEach(selected => selected.classList.remove("selected"));
    event.currentTarget.classList.add("selected");
    document.querySelector("#seat-status").textContent = `Seat ${event.currentTarget.dataset.seat} selected.`;
  });
});

document.querySelector("#account-form").addEventListener("submit", event => {
  event.preventDefault();
  const email = document.querySelector("#account-email");
  const password = document.querySelector("#account-password");
  const status = document.querySelector("#account-status");

  if (!email.value || !password.value) {
    status.textContent = "Email and password are required.";
    return;
  }

  password.value = "";
  status.textContent = `Secure sign-in request prepared for ${email.value}.`;
});
