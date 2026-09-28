// Main Global Utility Script

function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = '🔔';
    if (type === 'success') icon = '✓';
    if (type === 'error') icon = '✕';
    if (type === 'info') icon = 'ℹ';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(50px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

// Close modal on escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.active').forEach(m => {
            m.classList.remove('active');
        });
        document.body.style.overflow = '';
    }
});

// Mobile menu toggle
document.addEventListener('DOMContentLoaded', () => {
    const mobileBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');
    if (mobileBtn && navLinks) {
        mobileBtn.addEventListener('click', () => {
            navLinks.classList.toggle('mobile-active');
        });
    }

    // Populate active room dropdowns in booking modals
    loadReadyRoomsDropdown();
});

async function loadReadyRoomsDropdown() {
    const selects = document.querySelectorAll('.ready-rooms-select');
    if (selects.length === 0) return;

    try {
        const res = await fetch('/api/rooms');
        if (!res.ok) return;
        const rooms = await res.json();
        
        selects.forEach(select => {
            select.innerHTML = '<option value="">-- Select a Room --</option>';
            rooms.forEach(r => {
                const isReady = r.status === 'READY';
                const opt = document.createElement('option');
                opt.value = r.id;
                opt.textContent = `Room ${r.roomNumber} - ${r.roomType} (${r.status})${!isReady ? ' - [Not Available]' : ''}`;
                if (!isReady) {
                    opt.style.color = '#999';
                }
                select.appendChild(opt);
            });
        });
    } catch (e) {
        console.error('Failed to load rooms dropdown', e);
    }
}

// Global booking submission handler
async function handleQuickBooking(e) {
    if (e) e.preventDefault();
    const form = document.getElementById('quick-booking-form') || e.target;
    const formData = new FormData(form);

    const payload = {
        guestName: formData.get('guestName'),
        guestPhone: formData.get('guestPhone'),
        guestEmail: formData.get('guestEmail'),
        guestAddress: formData.get('guestAddress') || 'Valued Guest',
        roomId: parseInt(formData.get('roomId')),
        checkInDate: formData.get('checkInDate') || new Date().toISOString().split('T')[0],
        checkOutDate: formData.get('checkOutDate') || new Date(Date.now() + 86400000 * 2).toISOString().split('T')[0],
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
            showToast(`Room ${data.roomNumber} successfully booked for ${data.guestName}!`, 'success');
            closeModal('bookRoomModal');
            if (form) form.reset();
            setTimeout(() => window.location.reload(), 1200);
        } else {
            showToast(data.message || 'Booking failed. Room must be READY.', 'error');
        }
    } catch (err) {
        showToast('Network error while booking: ' + err.message, 'error');
    }
}
