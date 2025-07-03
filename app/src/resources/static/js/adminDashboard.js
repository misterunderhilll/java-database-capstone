// adminDashboard.js - Admin Dashboard for Managing Doctors

import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Handle "Add Doctor" button click
document.getElementById('addDocBtn')?.addEventListener('click', () => {
  openModal('addDoctor');
});

// Load doctor cards on page load
document.addEventListener('DOMContentLoaded', () => {
  loadDoctorCards();
});

// Load and display all doctor cards
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error('Failed to load doctors:', error);
  }
}

// Set up filter listeners
document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);

// Filter doctors by search and dropdowns
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || "";
  const time = document.getElementById("filterTime")?.value || "";
  const specialty = document.getElementById("filterSpecialty")?.value || "";

  try {
    const result = await filterDoctors(name, time, specialty);
    if (result.doctors.length > 0) {
      renderDoctorCards(result.doctors);
    } else {
      showNoDoctorsMessage("No doctors found with the given filters.");
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert("An error occurred while filtering doctors.");
  }
}

// Render a list of doctor cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Display a fallback message when no doctors are found
function showNoDoctorsMessage(message) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = `<p style="text-align:center; padding:20px;">${message}</p>`;
}

// Add a new doctor from modal form
window.adminAddDoctor = async function () {
  const name = document.getElementById("docName")?.value.trim();
  const email = document.getElementById("docEmail")?.value.trim();
  const phone = document.getElementById("docPhone")?.value.trim();
  const password = document.getElementById("docPassword")?.value.trim();
  const specialty = document.getElementById("docSpecialty")?.value.trim();

  const availableTimes = Array.from(
    document.querySelectorAll('input[name="availability"]:checked')
  ).map(input => input.value);

  const token = localStorage.getItem('token');

  if (!token) {
    alert("Authentication token missing. Please log in again.");
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      document.getElementById("addDoctorModal")?.classList.remove("show"); // Or closeModal() if available
      loadDoctorCards(); // Refresh list
    } else {
      alert("Failed to add doctor: " + result.message);
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert("An error occurred while adding the doctor.");
  }
};
