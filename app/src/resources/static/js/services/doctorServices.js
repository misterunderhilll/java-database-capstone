// doctorServices.js - Doctor-related API interactions

import { API_BASE_URL } from "../config/config.js";

// Base endpoint for all doctor-related actions
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Get all doctors from the backend
 * @returns {Promise<Array>} - Array of doctor objects or empty array on error
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error fetching doctors:', error);
    return [];
  }
}

/**
 * Delete a doctor by ID using an admin token
 * @param {string} id - Doctor's unique identifier
 * @param {string} token - Admin authentication token
 * @returns {Promise<Object>} - { success: boolean, message: string }
 */
export async function deleteDoctor(id, token) {
  try {
    const url = `${DOCTOR_API}/${id}/${token}`;
    const response = await fetch(url, {
      method: 'DELETE',
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || 'Unknown response from server',
    };
  } catch (error) {
    console.error('Error deleting doctor:', error);
    return {
      success: false,
      message: 'Failed to delete doctor. Please try again.',
    };
  }
}

/**
 * Save (add) a new doctor using POST and admin token
 * @param {Object} doctor - Doctor data object
 * @param {string} token - Admin authentication token
 * @returns {Promise<Object>} - { success: boolean, message: string }
 */
export async function saveDoctor(doctor, token) {
  try {
    const url = `${DOCTOR_API}/save/${token}`;
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || 'Doctor saved successfully',
    };
  } catch (error) {
    console.error('Error saving doctor:', error);
    return {
      success: false,
      message: 'Failed to save doctor. Please try again.',
    };
  }
}

/**
 * Filter doctors based on name, time, and specialty
 * @param {string} name - Doctor's name (optional)
 * @param {string} time - Availability time (optional)
 * @param {string} specialty - Specialty (optional)
 * @returns {Promise<Object>} - { doctors: Array }
 */
export async function filterDoctors(name = '', time = '', specialty = '') {
  try {
    const encodedName = encodeURIComponent(name);
    const encodedTime = encodeURIComponent(time);
    const encodedSpecialty = encodeURIComponent(specialty);

    const url = `${DOCTOR_API}/filter/${encodedName}/${encodedTime}/${encodedSpecialty}`;

    const response = await fetch(url);

    if (response.ok) {
      const data = await response.json();
      return { doctors: data.doctors || [] };
    } else {
      console.error('Failed to filter doctors:', response.status);
      return { doctors: [] };
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('An error occurred while filtering doctors.');
    return { doctors: [] };
  }
}
