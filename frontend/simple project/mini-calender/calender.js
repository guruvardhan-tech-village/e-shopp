const monthNameEl = document.getElementById("month-name");
const dayNameEl = document.getElementById("day-name");
const dayNumEl = document.getElementById("day-number");
const yearEl = document.getElementById("year");
const daysContainer = document.getElementById("days-container");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");

let currentDate = new Date();
let selectedDate = new Date();

function updateSelectedDate(date) {
    selectedDate = new Date(date);
    dayNameEl.textContent = selectedDate.toLocaleString('default', { weekday: 'long' });
    dayNumEl.textContent = String(selectedDate.getDate()).padStart(2, '0');
    yearEl.textContent = selectedDate.getFullYear();
}

function renderCalendar() {
    const month = currentDate.getMonth();
    const year = currentDate.getFullYear();
    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();

    monthNameEl.textContent = currentDate.toLocaleString('default', { month: 'long', year: 'numeric' });
    daysContainer.innerHTML = '';

    for (let i = 0; i < firstDay; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('day-cell', 'empty-cell');
        daysContainer.appendChild(emptyCell);
    }

    for (let i = 1; i <= lastDate; i++) {
        const dayCell = document.createElement('div');
        dayCell.classList.add('day-cell');
        dayCell.textContent = i;

        const cellDate = new Date(year, month, i);
        if (cellDate.toDateString() === selectedDate.toDateString()) {
            dayCell.classList.add('selected');
        } else if (cellDate.toDateString() === new Date().toDateString()) {
            dayCell.classList.add('today');
        }

        dayCell.addEventListener('click', () => {
            updateSelectedDate(cellDate);
            renderCalendar();
        });

        daysContainer.appendChild(dayCell);
    }
}

prevBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

nextBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

updateSelectedDate(currentDate);
renderCalendar();
