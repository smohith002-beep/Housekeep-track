// Bookings and Checkout Flow Script

let allBookings = [];

document.addEventListener('DOMContentLoaded', () => {
    loadBookings();
    loadReadyRoomsForBooking();

    const bookingForm = document.getElementById('newBookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', handleNewBookingSubmit);
    }
});

async function loadReadyRoomsForBooking() {
    const select = document.getElementById('bookingRoomSelect');
    if (!select) return;

    try {
        const res = await fetch('/api/rooms');
        if (!res.ok) return;
        const rooms = await res.json();

        select.innerHTML = '<option value="">-- Choose a Room --</option>';
        rooms.forEach(r => {
            const isReady = r.status === 'READY';
            const opt = document.createElement('option');
            opt.value = r.id;
            opt.textContent = `Room ${r.roomNumber} (${r.roomType}) - ${r.status}${!isReady ? ' ⚠️ [NOT READY]' : ' ✓'}`;
            if (!isReady) {
                opt.style.color = '#c22929';
            }
            select.appendChild(opt);
        });
    } catch (e) {
        console.error('Failed to load rooms for booking select', e);
    }
}

async function loadBookings() {
    const tbody = document.getElementById('bookings-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/bookings');
        if (!res.ok) throw new Error('Failed to load bookings');
        allBookings = await res.json();

        tbody.innerHTML = '';
        if (allBookings.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center" style="padding: 30px; color: #888;">No reservations recorded yet.</td></tr>';
            return;
        }

        allBookings.forEach(b => {
            const tr = document.createElement('tr');
            const isCheckedIn = b.bookingStatus === 'CHECKED_IN';
            const isCheckedOut = b.bookingStatus === 'CHECKED_OUT';

            let actionBtn = '';
            if (isCheckedIn) {
                actionBtn = `
                    <button class="btn-luxury-gold" style="padding: 6px 14px; font-size: 11px; background: linear-gradient(135deg, #c22929 0%, #9e1d1d 100%);"
                            onclick="handleCheckout(${b.id}, '${b.roomNumber}', '${b.guestName}')">
                        🛎 Checkout Guest
                    </button>
                `;
            } else if (isCheckedOut) {
                actionBtn = `<span style="font-size: 11px; color: #888; font-weight: 600;">Checked Out</span>`;
            } else {
                actionBtn = `<span style="font-size: 11px; color: var(--gold-primary); font-weight: 600;">${b.bookingStatus}</span>`;
            }

            tr.innerHTML = `
                <td>#${b.id}</td>
                <td>
                    <strong>${b.guestName}</strong>
                    <div style="font-size: 11px; color: #888;">${b.guestPhone || ''}</div>
                </td>
                <td>
                    <strong>Room ${b.roomNumber || 'N/A'}</strong>
                    <div style="font-size: 11px; color: #888;">${b.roomType || ''}</div>
                </td>
                <td>${b.checkInDate} to ${b.checkOutDate}</td>
                <td>₹${b.totalAmount ? Number(b.totalAmount).toLocaleString('en-IN') : '0'}</td>
                <td>
                    <span class="status-badge ${isCheckedIn ? 'ready' : (isCheckedOut ? 'dirty' : 'cleaning')}">
                        ${b.bookingStatus}
                    </span>
                </td>
                <td>${actionBtn}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Error fetching bookings:', e);
        showToast('Error loading bookings: ' + e.message, 'error');
    }
}

async function handleNewBookingSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        guestName: formData.get('guestName'),
        guestPhone: formData.get('guestPhone'),
        guestEmail: formData.get('guestEmail'),
        guestAddress: formData.get('guestAddress') || 'Luxury Guest',
        roomId: parseInt(formData.get('roomId')),
        checkInDate: formData.get('checkInDate'),
        checkOutDate: formData.get('checkOutDate'),
        numberOfGuests: parseInt(formData.get('numberOfGuests') || 2)
    };

    if (!payload.roomId) {
        showToast('Please select a room.', 'error');
        return;
    }

    try {
        const res = await fetch('/api/bookings', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok) {
            showToast(`Reservation confirmed for ${data.guestName} in Room ${data.roomNumber}!`, 'success');
            closeModal('newBookingModal');
            form.reset();
            loadBookings();
            loadReadyRoomsForBooking();
        } else {
            // Rule 2 validation error message: "Room 205 cannot be booked because it is currently CLEANING."
            showToast(data.message || 'Booking rejected: Room must be READY.', 'error');
        }
    } catch (err) {
        showToast('Network error: ' + err.message, 'error');
    }
}

// Section 12 Checkout Logic
async function handleCheckout(bookingId, roomNumber, guestName) {
    const confirmed = confirm(`Are you sure you want to checkout guest ${guestName} from Room ${roomNumber}?\n\nThis will:\n1. Change Room to DIRTY\n2. Create a Cleaning Task\n3. Automatically assign an available housekeeper`);
    if (!confirmed) return;

    try {
        const res = await fetch(`/api/bookings/${bookingId}/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await res.json();
        if (res.ok) {
            // Display detailed checkout result
            const alertMsg = `
CHECKOUT COMPLETE!
──────────────────────────────────────
• Room Number: ${data.roomNumber}
• Room Status: ${data.roomStatus} (Turned DIRTY)
• Cleaning Task ID: #${data.taskId} (${data.taskStatus})
• Housekeeper Assigned: ${data.housekeeper}
──────────────────────────────────────
${data.message}
            `.trim();

            alert(alertMsg);
            showToast(`Room ${data.roomNumber} marked DIRTY. Task #${data.taskId} assigned to ${data.housekeeper}!`, 'success');
            loadBookings();
            loadReadyRoomsForBooking();
        } else {
            showToast(data.message || 'Checkout failed.', 'error');
        }
    } catch (e) {
        showToast('Checkout error: ' + e.message, 'error');
    }
}
