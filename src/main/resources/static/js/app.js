// ====================================================
// FESTPASS - Frontend Application Logic
// ====================================================

const API = {
    dashboard: '/api/dashboard/stats',
    events: '/api/events',
    attendees: '/api/attendees',
    tickets: '/api/tickets',
    checkIn: '/api/check-in',
    reports: '/api/reports'
};

let cachedEvents = [];
let cachedAttendees = [];
let cachedTickets = [];

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initForms();
    loadDashboard();
});

// ====================================================
// NAVIGATION & TABS
// ====================================================
function initNavigation() {
    const navLinks = document.querySelectorAll('#sidebarNav .nav-link');
    const sections = {
        dashboard: document.getElementById('dashboardSection'),
        events: document.getElementById('eventsSection'),
        attendees: document.getElementById('attendeesSection'),
        tickets: document.getElementById('ticketsSection'),
        checkin: document.getElementById('checkinSection'),
        reports: document.getElementById('reportsSection')
    };

    const sectionTitles = {
        dashboard: 'Dashboard Overview',
        events: 'College Fest Events',
        attendees: 'Registered Attendees',
        tickets: 'Digital Ticket Management',
        checkin: 'QR Entrance Check-in',
        reports: 'Attendance & Revenue Reports'
    };

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetSection = link.getAttribute('data-section');
            switchSection(targetSection);
        });
    });

    document.getElementById('btnQuickIssueTicket')?.addEventListener('click', () => {
        openIssueTicketModal();
    });

    document.getElementById('btnQuickCheckIn')?.addEventListener('click', () => {
        switchSection('checkin');
        document.getElementById('checkInQrInput')?.focus();
    });

    function switchSection(targetSection) {
        navLinks.forEach(l => l.classList.remove('active'));
        const activeLink = document.querySelector(`#sidebarNav .nav-link[data-section="${targetSection}"]`);
        if (activeLink) activeLink.classList.add('active');

        Object.keys(sections).forEach(key => {
            if (sections[key]) {
                if (key === targetSection) {
                    sections[key].classList.remove('d-none');
                } else {
                    sections[key].classList.add('d-none');
                }
            }
        });

        const titleElem = document.getElementById('currentSectionTitle');
        if (titleElem && sectionTitles[targetSection]) {
            titleElem.textContent = sectionTitles[targetSection];
        }

        // Trigger section data load
        if (targetSection === 'dashboard') loadDashboard();
        if (targetSection === 'events') loadEvents();
        if (targetSection === 'attendees') loadAttendees();
        if (targetSection === 'tickets') loadTickets();
        if (targetSection === 'checkin') setupCheckInPage();
        if (targetSection === 'reports') loadReports();
    }
}

// ====================================================
// TOAST NOTIFICATIONS
// ====================================================
function showToast(message, type = 'info') {
    const toastElem = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');
    if (!toastElem || !toastBody) return;

    toastElem.className = 'toast align-items-center text-white border-0';
    if (type === 'success') toastElem.classList.add('bg-success');
    else if (type === 'danger' || type === 'error') toastElem.classList.add('bg-danger');
    else if (type === 'warning') toastElem.classList.add('bg-warning', 'text-dark');
    else toastElem.classList.add('bg-primary');

    toastBody.textContent = message;
    const toast = new bootstrap.Toast(toastElem, { delay: 4000 });
    toast.show();
}

// ====================================================
// MODULE 1: DASHBOARD
// ====================================================
async function loadDashboard() {
    try {
        const res = await fetch(API.dashboard);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        const data = json.data;

        // Metric Cards
        document.getElementById('dashTotalEvents').textContent = data.totalEvents;
        document.getElementById('dashTicketsIssued').textContent = data.totalTicketsIssued;
        document.getElementById('dashCheckedIn').textContent = data.totalCheckedIn;
        document.getElementById('dashAvailableSeats').textContent = data.totalAvailableSeats;
        document.getElementById('dashActiveEventsBadge').textContent = `Active Events: ${data.activeEvents}`;

        // Event Attendance Table
        const tbody = document.getElementById('dashEventTableBody');
        tbody.innerHTML = '';
        if (!data.events || data.events.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No events created yet.</td></tr>`;
        } else {
            data.events.forEach(ev => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>
                        <strong class="text-dark">${escapeHtml(ev.eventName)}</strong>
                        <div class="small text-muted">${escapeHtml(ev.venue || '')}</div>
                    </td>
                    <td><span class="badge bg-light text-dark border px-2 py-1">${ev.capacity}</span></td>
                    <td><strong>${ev.ticketsIssued}</strong></td>
                    <td class="text-success fw-bold">${ev.checkedIn}</td>
                    <td><span class="badge bg-info-subtle text-info border">${ev.remainingCapacity}</span></td>
                    <td style="min-width: 200px;">
                        <div class="d-flex justify-content-between small mb-1">
                            <span>${ev.checkedIn} / ${ev.capacity}</span>
                            <span class="fw-bold">${ev.attendancePercentage}%</span>
                        </div>
                        <div class="progress progress-capacity">
                            <div class="progress-bar bg-primary" role="progressbar" style="width: ${Math.min(ev.attendancePercentage, 100)}%" aria-valuenow="${ev.attendancePercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }

        // Recent Check-ins
        const checkinTbody = document.getElementById('dashRecentCheckInsBody');
        checkinTbody.innerHTML = '';
        if (!data.recentCheckIns || data.recentCheckIns.length === 0) {
            checkinTbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No check-ins recorded yet.</td></tr>`;
        } else {
            data.recentCheckIns.forEach(ci => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><span class="font-monospace fw-bold text-primary">${escapeHtml(ci.ticketNumber)}</span></td>
                    <td>${escapeHtml(ci.attendeeName)}</td>
                    <td>${escapeHtml(ci.eventName)}</td>
                    <td><i class="bi bi-clock me-1 text-muted"></i>${ci.checkInTime}</td>
                    <td><span class="badge badge-status-used border">USED</span></td>
                `;
                checkinTbody.appendChild(tr);
            });
        }

    } catch (err) {
        console.error('Error loading dashboard:', err);
    }
}

// ====================================================
// MODULE 2: FEST EVENTS
// ====================================================
async function loadEvents() {
    try {
        const res = await fetch(API.events);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        cachedEvents = json.data;
        const tbody = document.getElementById('eventsTableBody');
        tbody.innerHTML = '';

        if (cachedEvents.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No events found. Click "+ Create Event" to add one.</td></tr>`;
            return;
        }

        cachedEvents.forEach(ev => {
            const tr = document.createElement('tr');
            let statusBadge = 'badge-status-active';
            if (ev.status === 'UPCOMING') statusBadge = 'badge-status-upcoming';
            else if (ev.status === 'COMPLETED') statusBadge = 'badge-status-completed';
            else if (ev.status === 'CANCELLED') statusBadge = 'badge-status-cancelled';

            tr.innerHTML = `
                <td>
                    <strong class="text-dark">${escapeHtml(ev.eventName)}</strong>
                    <div class="small text-muted text-truncate" style="max-width: 250px;">${escapeHtml(ev.description || 'No description')}</div>
                </td>
                <td><i class="bi bi-calendar-event me-1 text-muted"></i>${ev.eventDate}</td>
                <td><i class="bi bi-geo-alt me-1 text-muted"></i>${escapeHtml(ev.venue)}</td>
                <td><span class="badge bg-light text-dark border">${ev.capacity}</span></td>
                <td><strong>${ev.ticketsIssued}</strong></td>
                <td>₹${ev.ticketPrice}</td>
                <td><span class="badge ${statusBadge} border px-2 py-1">${ev.status}</span></td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteEvent(${ev.id}, '${escapeHtml(ev.eventName)}')">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        console.error('Error loading events:', err);
        showToast('Failed to load events: ' + err.message, 'danger');
    }
}

async function deleteEvent(id, name) {
    if (!confirm(`Are you sure you want to delete event "${name}"?`)) return;
    try {
        const res = await fetch(`${API.events}/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        showToast('Event deleted successfully', 'success');
        loadEvents();
        loadDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

// ====================================================
// MODULE 3: ATTENDEES
// ====================================================
async function loadAttendees() {
    try {
        const res = await fetch(API.attendees);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        cachedAttendees = json.data;
        const tbody = document.getElementById('attendeesTableBody');
        tbody.innerHTML = '';

        if (cachedAttendees.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No registered attendees. Click "+ Add Attendee" to register one.</td></tr>`;
            return;
        }

        cachedAttendees.forEach(att => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="fw-bold text-dark">${escapeHtml(att.name)}</div>
                </td>
                <td><a href="mailto:${escapeHtml(att.email)}" class="text-decoration-none">${escapeHtml(att.email)}</a></td>
                <td>${escapeHtml(att.phone)}</td>
                <td>${escapeHtml(att.collegeName || 'N/A')}</td>
                <td><span class="badge bg-light text-dark border font-monospace">${escapeHtml(att.studentId)}</span></td>
                <td><span class="badge bg-primary-subtle text-primary border">${att.totalTickets || 0} Ticket(s)</span></td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        console.error('Error loading attendees:', err);
        showToast('Failed to load attendees: ' + err.message, 'danger');
    }
}

// ====================================================
// MODULE 4: TICKETS
// ====================================================
async function loadTickets() {
    try {
        const res = await fetch(API.tickets);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        cachedTickets = json.data;
        const tbody = document.getElementById('ticketsTableBody');
        tbody.innerHTML = '';

        if (cachedTickets.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No tickets issued yet. Click "+ Issue New Ticket" to create one.</td></tr>`;
            return;
        }

        cachedTickets.forEach(t => {
            const tr = document.createElement('tr');
            let statusBadge = 'badge-status-active';
            if (t.status === 'USED') statusBadge = 'badge-status-used';
            else if (t.status === 'CANCELLED') statusBadge = 'badge-status-cancelled';

            const issuedDate = t.issuedAt ? new Date(t.issuedAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A';

            tr.innerHTML = `
                <td>
                    <span class="font-monospace fw-bold text-primary">${escapeHtml(t.ticketNumber)}</span>
                    <div class="small text-muted font-monospace" style="font-size: 0.72rem;">${escapeHtml(t.qrCode)}</div>
                </td>
                <td>
                    <strong>${escapeHtml(t.attendeeName)}</strong>
                    <div class="small text-muted">${escapeHtml(t.collegeName || '')}</div>
                </td>
                <td>
                    <strong>${escapeHtml(t.eventName)}</strong>
                    <div class="small text-muted">${escapeHtml(t.venue || '')}</div>
                </td>
                <td>₹${t.ticketPrice}</td>
                <td><span class="badge ${statusBadge} border px-2 py-1">${t.status}</span></td>
                <td><small class="text-muted">${issuedDate}</small></td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewDigitalTicket(${t.id})">
                        <i class="bi bi-eye me-1"></i> View Ticket
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        console.error('Error loading tickets:', err);
        showToast('Failed to load tickets: ' + err.message, 'danger');
    }
}

// Open Issue Ticket Modal & Populate Dropdowns
async function openIssueTicketModal() {
    try {
        const [eventsRes, attendeesRes] = await Promise.all([
            fetch(API.events).then(r => r.json()),
            fetch(API.attendees).then(r => r.json())
        ]);

        const eventSelect = document.getElementById('ticketSelectEvent');
        const attendeeSelect = document.getElementById('ticketSelectAttendee');

        eventSelect.innerHTML = '<option value="">-- Choose an Event --</option>';
        attendeeSelect.innerHTML = '<option value="">-- Choose Attendee --</option>';

        if (eventsRes.success && eventsRes.data) {
            cachedEvents = eventsRes.data;
            eventsRes.data.forEach(e => {
                const opt = document.createElement('option');
                opt.value = e.id;
                opt.textContent = `${e.eventName} (Seats left: ${e.remainingCapacity}) - ₹${e.ticketPrice}`;
                opt.disabled = (e.remainingCapacity <= 0 || e.status === 'CANCELLED' || e.status === 'COMPLETED');
                eventSelect.appendChild(opt);
            });
        }

        if (attendeesRes.success && attendeesRes.data) {
            cachedAttendees = attendeesRes.data;
            attendeesRes.data.forEach(a => {
                const opt = document.createElement('option');
                opt.value = a.id;
                opt.textContent = `${a.name} (${a.studentId}) - ${a.collegeName || 'Student'}`;
                attendeeSelect.appendChild(opt);
            });
        }

        eventSelect.onchange = () => {
            const selectedId = parseInt(eventSelect.value);
            const found = cachedEvents.find(e => e.id === selectedId);
            const remBadge = document.getElementById('ticketEventRemainingBadge');
            const priceBadge = document.getElementById('ticketEventPriceBadge');

            if (found) {
                remBadge.textContent = `Remaining Capacity: ${found.remainingCapacity} / ${found.capacity}`;
                priceBadge.textContent = `Price: ₹${found.ticketPrice}`;
                if (found.remainingCapacity <= 0) {
                    remBadge.className = 'text-danger fw-bold';
                    remBadge.textContent = 'SOLD OUT (Capacity reached)';
                } else {
                    remBadge.className = 'text-success fw-bold';
                }
            } else {
                remBadge.textContent = 'Remaining Capacity: -';
                priceBadge.textContent = 'Price: -';
            }
        };

        const modal = new bootstrap.Modal(document.getElementById('issueTicketModal'));
        modal.show();

    } catch (err) {
        showToast('Error preparing ticket issuance: ' + err.message, 'danger');
    }
}

// ====================================================
// MODULE 5: DIGITAL TICKET MODAL VIEW
// ====================================================
async function viewDigitalTicket(ticketId) {
    try {
        const res = await fetch(`${API.tickets}/${ticketId}`);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        const t = json.data;

        document.getElementById('modalTicketEventName').textContent = t.eventName;
        document.getElementById('modalTicketVenueDate').textContent = `${t.eventDate || ''} • ${t.venue || ''}`;
        document.getElementById('modalTicketAttendeeName').textContent = t.attendeeName;
        document.getElementById('modalTicketCollege').textContent = t.collegeName || 'College Fest Attendee';
        document.getElementById('modalTicketStudentId').textContent = `Student ID: ${t.studentId || 'N/A'}`;
        document.getElementById('modalTicketNumber').textContent = t.ticketNumber;
        document.getElementById('modalTicketPrice').textContent = `₹${t.ticketPrice}`;
        document.getElementById('modalTicketQrToken').textContent = t.qrCode;

        const statusElem = document.getElementById('modalTicketStatus');
        statusElem.textContent = t.status;
        if (t.status === 'ACTIVE') {
            statusElem.className = 'badge bg-success-subtle text-success border';
        } else if (t.status === 'USED') {
            statusElem.className = 'badge bg-secondary-subtle text-secondary border';
        } else {
            statusElem.className = 'badge bg-danger-subtle text-danger border';
        }

        // QR Code Image
        const qrImg = document.getElementById('modalTicketQrImage');
        if (t.qrCodeBase64) {
            qrImg.src = t.qrCodeBase64;
        } else {
            qrImg.src = `${API.tickets}/${t.id}/qr-image?width=260&height=260`;
        }

        const modal = new bootstrap.Modal(document.getElementById('digitalTicketModal'));
        modal.show();

    } catch (err) {
        showToast('Error loading ticket: ' + err.message, 'danger');
    }
}

// ====================================================
// MODULE 6: QR CHECK-IN
// ====================================================
async function setupCheckInPage() {
    try {
        // Fetch tickets to show sample tokens for 1-click evaluation
        const res = await fetch(API.tickets);
        const json = await res.json();
        if (json.success && json.data) {
            cachedTickets = json.data;
            const container = document.getElementById('quickTestTokensContainer');
            container.innerHTML = '';

            const activeTicket = cachedTickets.find(t => t.status === 'ACTIVE');
            const usedTicket = cachedTickets.find(t => t.status === 'USED');

            if (activeTicket) {
                const btnActive = document.createElement('button');
                btnActive.className = 'btn btn-sm btn-outline-success';
                btnActive.innerHTML = `<i class="bi bi-check-circle me-1"></i> Test Active: ${activeTicket.attendeeName.split(' ')[0]} (${activeTicket.ticketNumber})`;
                btnActive.onclick = () => setQuickQr(activeTicket.qrCode);
                container.appendChild(btnActive);
            }

            if (usedTicket) {
                const btnUsed = document.createElement('button');
                btnUsed.className = 'btn btn-sm btn-outline-warning';
                btnUsed.innerHTML = `<i class="bi bi-exclamation-triangle me-1"></i> Test Used: ${usedTicket.attendeeName.split(' ')[0]} (${usedTicket.ticketNumber})`;
                btnUsed.onclick = () => setQuickQr(usedTicket.qrCode);
                container.appendChild(btnUsed);
            }

            const btnInvalid = document.createElement('button');
            btnInvalid.className = 'btn btn-sm btn-outline-danger';
            btnInvalid.innerHTML = `<i class="bi bi-x-circle me-1"></i> Test Invalid Code`;
            btnInvalid.onclick = () => setQuickQr('INVALID-QR-TOKEN-999');
            container.appendChild(btnInvalid);
        }
    } catch (e) {
        console.error('Error setting up checkin helper:', e);
    }
}

function setQuickQr(token) {
    const input = document.getElementById('checkInQrInput');
    if (input) {
        input.value = token;
        document.getElementById('btnSubmitCheckIn')?.click();
    }
}

async function processCheckIn(qrCode) {
    const feedback = document.getElementById('checkInFeedback');
    feedback.className = 'd-none';

    try {
        const res = await fetch(API.checkIn, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ qrCode: qrCode })
        });

        const json = await res.json();

        if (res.ok && json.success) {
            // SUCCESS
            const data = json.data || json;
            feedback.className = 'checkin-feedback checkin-feedback-success';
            feedback.innerHTML = `
                <div class="display-6 text-success mb-2"><i class="bi bi-check-circle-fill"></i></div>
                <h4 class="fw-bold mb-1">✓ CHECK-IN SUCCESSFUL</h4>
                <div class="fs-5 fw-semibold text-dark mt-2">${escapeHtml(data.attendeeName || '')}</div>
                <div class="text-muted">${escapeHtml(data.eventName || '')}</div>
                <div class="font-monospace text-primary fw-bold mt-2">Ticket: ${escapeHtml(data.ticketNumber || '')}</div>
                <div class="small text-muted mt-1"><i class="bi bi-clock me-1"></i> Time: ${data.checkInTime || 'Just now'}</div>
            `;
            showToast('Check-in confirmed!', 'success');
            setupCheckInPage();
            loadDashboard();
        } else {
            // ERROR: Determine if Already Used or Invalid
            const msg = json.message || 'Check-in failed';
            if (msg.toLowerCase().includes('already') || msg.toLowerCase().includes('used')) {
                feedback.className = 'checkin-feedback checkin-feedback-warning';
                feedback.innerHTML = `
                    <div class="display-6 text-warning mb-2"><i class="bi bi-exclamation-triangle-fill"></i></div>
                    <h4 class="fw-bold mb-1">⚠ TICKET ALREADY USED</h4>
                    <p class="mb-0 mt-2">${escapeHtml(msg)}</p>
                    <small class="text-muted d-block mt-2">This ticket was checked in earlier and cannot be reused.</small>
                `;
                showToast(msg, 'warning');
            } else {
                feedback.className = 'checkin-feedback checkin-feedback-danger';
                feedback.innerHTML = `
                    <div class="display-6 text-danger mb-2"><i class="bi bi-x-circle-fill"></i></div>
                    <h4 class="fw-bold mb-1">✕ INVALID QR CODE</h4>
                    <p class="mb-0 mt-2">${escapeHtml(msg)}</p>
                    <small class="text-muted d-block mt-2">No matching valid ticket found in the system.</small>
                `;
                showToast(msg, 'danger');
            }
        }
    } catch (err) {
        feedback.className = 'checkin-feedback checkin-feedback-danger';
        feedback.innerHTML = `
            <div class="display-6 text-danger mb-2"><i class="bi bi-x-circle-fill"></i></div>
            <h4 class="fw-bold mb-1">✕ SYSTEM ERROR</h4>
            <p class="mb-0 mt-2">${escapeHtml(err.message)}</p>
        `;
        showToast('Check-in error: ' + err.message, 'danger');
    }
}

// ====================================================
// MODULE 7: REPORTS
// ====================================================
async function loadReports() {
    try {
        const res = await fetch(API.reports);
        const json = await res.json();
        if (!json.success) throw new Error(json.message);

        const data = json.data;

        // Metric Cards
        document.getElementById('repTotalTickets').textContent = data.totalTicketsSold;
        document.getElementById('repTotalCheckedIn').textContent = data.totalCheckedIn;
        document.getElementById('repAvgPrice').textContent = `₹${data.averageTicketPrice}`;
        document.getElementById('repTotalRevenue').textContent = `₹${data.totalRevenue.toLocaleString('en-IN')}`;

        // Table
        const tbody = document.getElementById('reportsTableBody');
        tbody.innerHTML = '';

        if (!data.eventReports || data.eventReports.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No event reports available.</td></tr>`;
            return;
        }

        data.eventReports.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong class="text-dark">${escapeHtml(r.eventName)}</strong></td>
                <td><span class="badge bg-light text-dark border">${r.capacity}</span></td>
                <td><strong>${r.ticketsSold}</strong></td>
                <td class="text-success fw-bold">${r.checkedIn}</td>
                <td><span class="badge bg-info-subtle text-info border">${r.remaining}</span></td>
                <td>
                    <span class="fw-bold">${r.attendancePercentage}%</span>
                    <div class="progress progress-capacity mt-1">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: ${Math.min(r.attendancePercentage, 100)}%"></div>
                    </div>
                </td>
                <td>₹${r.ticketPrice}</td>
                <td><strong class="text-success">₹${r.revenue.toLocaleString('en-IN')}</strong></td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        console.error('Error loading reports:', err);
        showToast('Failed to load reports: ' + err.message, 'danger');
    }
}

// ====================================================
// FORMS INITIALIZATION
// ====================================================
function initForms() {
    // 1. Create Event Form
    const createEventForm = document.getElementById('createEventForm');
    createEventForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            eventName: document.getElementById('eventInputName').value,
            description: document.getElementById('eventInputDesc').value,
            eventDate: document.getElementById('eventInputDate').value,
            status: document.getElementById('eventInputStatus').value,
            venue: document.getElementById('eventInputVenue').value,
            capacity: parseInt(document.getElementById('eventInputCapacity').value),
            ticketPrice: parseFloat(document.getElementById('eventInputPrice').value)
        };

        try {
            const res = await fetch(API.events, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const json = await res.json();
            if (!json.success) throw new Error(json.message);

            showToast('Event created successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('createEventModal'))?.hide();
            createEventForm.reset();
            loadEvents();
            loadDashboard();
        } catch (err) {
            showToast(err.message, 'danger');
        }
    });

    // 2. Add Attendee Form
    const addAttendeeForm = document.getElementById('addAttendeeForm');
    addAttendeeForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            name: document.getElementById('attendeeInputName').value,
            email: document.getElementById('attendeeInputEmail').value,
            phone: document.getElementById('attendeeInputPhone').value,
            collegeName: document.getElementById('attendeeInputCollege').value,
            studentId: document.getElementById('attendeeInputStudentId').value
        };

        try {
            const res = await fetch(API.attendees, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const json = await res.json();
            if (!json.success) throw new Error(json.message);

            showToast('Attendee registered successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addAttendeeModal'))?.hide();
            addAttendeeForm.reset();
            loadAttendees();
        } catch (err) {
            showToast(err.message, 'danger');
        }
    });

    // 3. Issue Ticket Form
    const issueTicketForm = document.getElementById('issueTicketForm');
    issueTicketForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            eventId: parseInt(document.getElementById('ticketSelectEvent').value),
            attendeeId: parseInt(document.getElementById('ticketSelectAttendee').value)
        };

        try {
            const res = await fetch(API.tickets, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const json = await res.json();
            if (!json.success) throw new Error(json.message);

            showToast('Ticket issued successfully with QR code!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('issueTicketModal'))?.hide();
            issueTicketForm.reset();

            // Refresh data
            loadTickets();
            loadDashboard();

            // Immediately view the newly issued ticket
            if (json.data && json.data.id) {
                viewDigitalTicket(json.data.id);
            }
        } catch (err) {
            showToast(err.message, 'danger');
        }
    });

    // 4. Check-in Form
    const checkInForm = document.getElementById('checkInForm');
    checkInForm?.addEventListener('submit', (e) => {
        e.preventDefault();
        const qrInput = document.getElementById('checkInQrInput');
        const token = qrInput?.value.trim();
        if (token) {
            processCheckIn(token);
        }
    });
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
