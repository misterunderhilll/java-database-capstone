// Import necessary functions
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientByToken } from "../services/patientServices.js";

// Export function for reuse
export function createDoctorCard(doctor) {
  // 1. Create main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // 2. Fetch current user role
  const role = localStorage.getItem("userRole");

  // 3. Create doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available Times: ${doctor.availableTimes?.join(", ") || "N/A"}`;

  // Append doctor info to infoDiv
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // 4. Create button container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // 5. Conditional buttons based on role

  // === Admin ===
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      const token = localStorage.getItem("token");
      try {
        const result = await deleteDoctor(doctor.id, token);
        if (result.success) {
          alert("Doctor deleted successfully.");
          card.remove(); // Remove from DOM
        } else {
          alert("Failed to delete doctor.");
        }
      } catch (err) {
        console.error(err);
        alert("An error occurred while deleting the doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // === Guest Patient ===
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // === Logged-in Patient ===
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      try {
        const patientData = await getPatientByToken(token);
        if (!patientData) {
          alert("Failed to retrieve patient information.");
          return;
        }

        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Booking error:", error);
        alert("An error occurred while processing your booking.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // 6. Assemble and return card
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
