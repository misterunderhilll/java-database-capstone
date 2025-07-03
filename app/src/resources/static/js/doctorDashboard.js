// doctorDashboard.js – Managing Appointments

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Global variables
const tableBody = document.getElementById('patientTableBody');
let selectedDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
const token = localStorage.getItem('token');
let patientName = null;

// Search bar filtering
document.getElementById('searchBar')?.addEventListener('input', (event) => {
  const value = event.target.value.trim();
  patientName = value !== '' ? value : "null";
  loadAppointments();
});

// "Today's Appointments" button
document.getElementById('todayButton')?.addEventListener('click', () => {
  selectedDate = new Date().toISOString().split('T')[0];
  document.getElementById('datePicker').value = selectedDate;
  loadAppointments();
});

// Date picker change handler
document.getElementById('datePicker')?.addEventListener('change', (event) => {
  selectedDate = event.target.value;
  loadAppointments();
});

// Load and render appointments
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    tableBody.innerHTML = '';

    if (!appointments || appointments.length === 0) {
      showTableMessage('No Appointments found for today.');
      return;
    }

    appointments.forEach(appointment => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error('Error loading appointments:', error);
    showTableMessage('Error loading appointments. Try again later.');
  }
}

// Display a message row inside the table
function showTableMessage(message) {
  tableBody.innerHTML = `
    <tr>
      <td colspan="4" style="text-align: center; padding: 1rem;">${message}</td>
    </tr>
  `;
}

// Initial render
document.addEventListener('DOMContentLoaded', () => {
  if (typeof renderContent === 'function') {
    renderContent(); // Optional layout rendering
  }

  document.getElementById('datePicker').value = selectedDate;
  loadAppointments();
});
