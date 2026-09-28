// Room Management Script - Complete CRUD Integration

let allRooms = [];
let currentFilter = 'ALL';
let editingRoomId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadRooms();

    const searchInput = document.getElementById('roomSearchInput');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            filterAndRenderRooms(e.target.value);
        });
    }

    const filterButtons = document.querySelectorAll('.room-filter-btn');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            filterButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentFilter = btn.dataset.status;
            filterAndRenderRooms(searchInput ? searchInput.value : '');
        });
    });

    const addRoomForm = document.getElementById('addRoomForm');
    if (addRoomForm) {
        addRoomForm.addEventListener('submit', handleCreateRoom);
    }

    const editRoomForm = document.getElementById('editRoomForm');
    if (editRoomForm) {
        editRoomForm.addEventListener('submit', handleUpdateRoom);
    }
});

async function loadRooms() {
    const grid = document.getElementById('rooms-grid-container') || document.getElementById('room-status-grid');
    if (!grid) return;

    try {
        const res = await fetch('/api/rooms');
        if (!res.ok) throw new Error('Failed to load rooms');
        allRooms = await res.json();
        filterAndRenderRooms('');
    } catch (e) {
        console.error('Error fetching rooms:', e);
        showToast('Error loading rooms: ' + e.message, 'error');
    }
}

function filterAndRenderRooms(searchQuery = '') {
    const grid = document.getElementById('rooms-grid-container') || document.getElementById('room-status-grid');
    if (!grid) return;

    const query = searchQuery.trim().toLowerCase();
    const filtered = allRooms.filter(r => {
        const matchesFilter = (currentFilter === 'ALL' || r.status === currentFilter);
        const matchesQuery = !query || r.roomNumber.toLowerCase().includes(query) || r.roomType.toLowerCase().includes(query);
        return matchesFilter && matchesQuery;
    });

    grid.innerHTML = '';
    if (filtered.length === 0) {
        grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #888;">No rooms match the selected criteria.</div>';
        return;
    }

    const isStatusPage = !!document.getElementById('room-status-grid');

    filtered.forEach(room => {
        if (isStatusPage) {
            grid.appendChild(createStatusCard(room));
        } else {
            grid.appendChild(createRoomCard(room));
        }
    });
}

function createRoomCard(room) {
    const card = document.createElement('div');
    card.className = 'luxury-card';
    const isReady = room.status === 'READY';

    card.innerHTML = `
        <div class="card-img-wrap">
            <img src="${room.imageUrl || 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80'}" alt="Room ${room.roomNumber}">
            <div class="card-status-overlay">
                <span class="status-badge ${room.status.toLowerCase()}">${room.status}</span>
            </div>
            <div class="card-room-num">ROOM ${room.roomNumber}</div>
        </div>
        <div class="card-body">
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <div class="card-room-type">${room.roomType} • Floor ${room.floor}</div>
                <div style="display:flex; gap:6px;">
                    <button class="btn-luxury-outline" style="padding:3px 8px; font-size:10px;" onclick="openEditRoomModal(${room.id})">✎ Edit</button>
                    <button class="btn-luxury-outline" style="padding:3px 8px; font-size:10px; border-color:#c22929; color:#c22929;" onclick="confirmDeleteRoom(${room.id}, '${room.roomNumber}')">✕ Del</button>
                </div>
            </div>
            <h3 class="card-title">${room.roomType} Chamber</h3>
            <p class="card-desc">${room.description || 'Finest craftsmanship, bespoke luxury bedding, Italian marble bathroom, and 24/7 dedicated concierge service.'}</p>
            <div class="card-meta">
                <div class="card-price">₹${Number(room.pricePerNight).toLocaleString('en-IN')} <small>/ night</small></div>
                <div>
                    ${isReady ? 
                        `<button class="btn-luxury-gold" style="padding: 8px 16px; font-size: 11px;" onclick="openBookModalForRoom(${room.id}, '${room.roomNumber}')">Book Room</button>` :
                        `<button class="btn-luxury-outline" style="padding: 8px 16px; font-size: 11px; opacity: 0.65; cursor: not-allowed;" onclick="showToast('Room ${room.roomNumber} cannot be booked because it is ${room.status}.', 'error')">Unavailable</button>`
                    }
                </div>
            </div>
        </div>
    `;
    return card;
}

function createStatusCard(room) {
    const card = document.createElement('div');
    card.className = `room-grid-card border-${room.status.toLowerCase()}`;

    let actionButtons = '';
    if (room.status === 'DIRTY') {
        actionButtons = `<button class="btn-luxury-outline" style="padding: 6px 12px; font-size: 11.5px;" onclick="changeRoomStatusPrompt(${room.id}, 'CLEANING')">Mark Cleaning</button>`;
    } else if (room.status === 'CLEANING') {
        actionButtons = `<button class="btn-luxury-outline" style="padding: 6px 12px; font-size: 11.5px;" onclick="changeRoomStatusPrompt(${room.id}, 'INSPECTED')">Send for Inspection</button>`;
    } else if (room.status === 'INSPECTED') {
        actionButtons = `
            <a href="/inspections" class="btn-luxury-gold" style="padding: 6px 12px; font-size: 11.5px;">Inspect Room</a>
        `;
    } else if (room.status === 'READY') {
        actionButtons = `<span style="font-size: 12px; color: var(--status-ready); font-weight: 600;">✓ Ready for Check-in</span>`;
    }

    card.innerHTML = `
        <div class="room-grid-header">
            <div>
                <span style="font-size: 11px; color: var(--gold-primary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.1em;">${room.roomType}</span>
                <div class="room-grid-num">Room ${room.roomNumber}</div>
            </div>
            <span class="status-badge ${room.status.toLowerCase()}">${room.status}</span>
        </div>
        <div class="room-grid-body">
            <p style="font-size: 12.5px; color: var(--text-secondary); margin-bottom: 8px;">
                Floor: <strong>${room.floor}</strong> &nbsp;|&nbsp; Capacity: <strong>${room.capacity} Guests</strong>
            </p>
            <p style="font-size: 12px; color: #888;">
                Tariff: ₹${Number(room.pricePerNight).toLocaleString('en-IN')}/night
            </p>
        </div>
        <div class="room-grid-footer">
            ${actionButtons}
        </div>
    `;
    return card;
}

function openBookModalForRoom(roomId, roomNumber) {
    const select = document.getElementById('bookingRoomSelect');
    if (select) {
        select.value = roomId;
    }
    openModal('bookRoomModal');
}

async function changeRoomStatusPrompt(roomId, newStatus) {
    try {
        const res = await fetch(`/api/rooms/${roomId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });

        const data = await res.json();
        if (res.ok) {
            showToast(`Room status updated to ${newStatus}`, 'success');
            loadRooms();
        } else {
            showToast(data.message || 'Status transition rejected.', 'error');
        }
    } catch (e) {
        showToast('Network error: ' + e.message, 'error');
    }
}

// CREATE ROOM (POST /api/rooms)
async function handleCreateRoom(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        roomNumber: formData.get('roomNumber'),
        floor: parseInt(formData.get('floor')),
        roomType: formData.get('roomType'),
        status: formData.get('status') || 'READY',
        capacity: parseInt(formData.get('capacity') || 2),
        pricePerNight: parseFloat(formData.get('pricePerNight') || 18000),
        description: formData.get('description') || 'Luxury chamber at HouseKeepTrack Hotel.',
        imageUrl: formData.get('imageUrl') || 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80'
    };

    try {
        const res = await fetch('/api/rooms', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.status === 201 || res.ok) {
            showToast('Room created successfully.', 'success');
            closeModal('addRoomModal');
            form.reset();
            loadRooms();
        } else {
            showToast(data.message || 'Failed to create room', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// OPEN EDIT MODAL
function openEditRoomModal(roomId) {
    const room = allRooms.find(r => r.id === roomId);
    if (!room) return;

    editingRoomId = roomId;
    const form = document.getElementById('editRoomForm');
    if (!form) return;

    form.elements['roomNumber'].value = room.roomNumber;
    form.elements['floor'].value = room.floor;
    form.elements['roomType'].value = room.roomType;
    form.elements['status'].value = room.status;
    form.elements['capacity'].value = room.capacity || 2;
    form.elements['pricePerNight'].value = room.pricePerNight || 18000;
    form.elements['description'].value = room.description || '';
    form.elements['imageUrl'].value = room.imageUrl || '';

    openModal('editRoomModal');
}

// UPDATE ROOM (PUT /api/rooms/{id})
async function handleUpdateRoom(e) {
    e.preventDefault();
    if (!editingRoomId) return;

    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        roomNumber: formData.get('roomNumber'),
        floor: parseInt(formData.get('floor')),
        roomType: formData.get('roomType'),
        status: formData.get('status'),
        capacity: parseInt(formData.get('capacity')),
        pricePerNight: parseFloat(formData.get('pricePerNight')),
        description: formData.get('description'),
        imageUrl: formData.get('imageUrl')
    };

    try {
        const res = await fetch(`/api/rooms/${editingRoomId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok) {
            showToast('Room updated successfully.', 'success');
            closeModal('editRoomModal');
            editingRoomId = null;
            loadRooms();
        } else {
            showToast(data.message || 'Failed to update room.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// DELETE ROOM (DELETE /api/rooms/{id})
async function confirmDeleteRoom(roomId, roomNumber) {
    const confirmed = confirm(`Are you sure you want to delete Room ${roomNumber}?\n\nNote: If the room has associated bookings, tasks, or inspections, the database will preserve foreign-key integrity.`);
    if (!confirmed) return;

    try {
        const res = await fetch(`/api/rooms/${roomId}`, {
            method: 'DELETE'
        });

        if (res.status === 204 || res.ok) {
            showToast('Room deleted successfully.', 'success');
            loadRooms();
        } else {
            let errorMsg = 'Unable to delete room.';
            try {
                const data = await res.json();
                if (data && data.message) errorMsg = 'Unable to delete room: ' + data.message;
            } catch (ignore) {}
            showToast(errorMsg, 'error');
        }
    } catch (err) {
        showToast('Unable to delete room: ' + err.message, 'error');
    }
}
