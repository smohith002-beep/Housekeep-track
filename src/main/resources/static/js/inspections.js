// Supervisor Inspection Management Script

let selectedInspectionId = null;
let selectedRoomId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadInspectionsList();
    loadInspectedRoomsForApproval();

    const failForm = document.getElementById('failInspectionForm');
    if (failForm) {
        failForm.addEventListener('submit', handleFailInspectionSubmit);
    }
});

async function loadInspectedRoomsForApproval() {
    const container = document.getElementById('pending-inspections-tbody');
    if (!container) return;

    try {
        const res = await fetch('/api/rooms/status/INSPECTED');
        if (!res.ok) return;
        const rooms = await res.json();

        container.innerHTML = '';
        if (rooms.length === 0) {
            container.innerHTML = '<tr><td colspan="6" class="text-center" style="padding: 24px; color: #888;">No rooms currently pending inspection. Excellent work!</td></tr>';
            return;
        }

        // For each room in INSPECTED, fetch its latest task to show housekeeper
        for (const room of rooms) {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>Room ${room.roomNumber}</strong></td>
                <td>${room.roomType} (Floor ${room.floor})</td>
                <td><span class="status-badge inspected">INSPECTED</span></td>
                <td>Awaiting Supervisor Review</td>
                <td>
                    <div style="display:flex; gap:8px;">
                        <button class="btn-luxury-gold" style="padding: 6px 14px; font-size: 11px;" onclick="passRoomInspection(${room.id}, '${room.roomNumber}')">
                            ✓ Pass Inspection
                        </button>
                        <button class="btn-luxury-outline" style="padding: 6px 14px; font-size: 11px; border-color: #c22929; color: #c22929;" onclick="openFailModal(${room.id}, '${room.roomNumber}')">
                            ✕ Fail Inspection
                        </button>
                    </div>
                </td>
            `;
            container.appendChild(tr);
        }
    } catch (e) {
        console.error('Failed to load pending inspections', e);
    }
}

async function loadInspectionsList() {
    const tbody = document.getElementById('inspections-history-tbody');
    if (!tbody) return;

    try {
        const res = await fetch('/api/inspections');
        if (!res.ok) return;
        const list = await res.json();

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center" style="padding: 24px; color: #888;">No inspection history found.</td></tr>';
            return;
        }

        list.forEach(ins => {
            const tr = document.createElement('tr');
            const isPassed = ins.inspectionStatus === 'PASSED';
            tr.innerHTML = `
                <td>#${ins.id}</td>
                <td><strong>Room ${ins.roomNumber || 'N/A'}</strong></td>
                <td>${ins.supervisorName || 'Supervisor'}</td>
                <td><span class="status-badge ${isPassed ? 'ready' : 'dirty'}">${ins.inspectionStatus}</span></td>
                <td>${ins.remarks || '-'}</td>
                <td>${ins.inspectedAt ? ins.inspectedAt.replace('T', ' ').substring(0, 16) : '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load inspection history', e);
    }
}

async function passRoomInspection(roomId, roomNumber) {
    const supervisorName = prompt(`Pass Inspection for Room ${roomNumber}. Enter Supervisor Name:`, 'Supervisor Alok');
    if (!supervisorName) return;

    try {
        const res = await fetch(`/api/inspections/rooms/${roomId}/pass`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                supervisorName: supervisorName,
                remarks: '5-star inspection criteria passed. Room is READY for guests.'
            })
        });

        const data = await res.json();
        if (res.ok) {
            showToast(`Room ${roomNumber} PASSED! Status updated to READY. Housekeeper is now AVAILABLE.`, 'success');
            loadInspectedRoomsForApproval();
            loadInspectionsList();
        } else {
            showToast(data.message || 'Failed to pass inspection', 'error');
        }
    } catch (e) {
        showToast('Network error: ' + e.message, 'error');
    }
}

function openFailModal(roomId, roomNumber) {
    selectedRoomId = roomId;
    const targetRoomSpan = document.getElementById('failModalRoomNumber');
    if (targetRoomSpan) {
        targetRoomSpan.textContent = roomNumber;
    }
    openModal('failInspectionModal');
}

async function handleFailInspectionSubmit(e) {
    e.preventDefault();
    if (!selectedRoomId) return;

    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        supervisorName: formData.get('supervisorName') || 'Supervisor',
        remarks: formData.get('remarks') || 'Cleaning incomplete. Bathroom and linen re-inspection required.'
    };

    try {
        const res = await fetch(`/api/inspections/rooms/${selectedRoomId}/fail`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok) {
            showToast('Inspection FAILED! Room reverted to CLEANING and task REOPENED.', 'error');
            closeModal('failInspectionModal');
            form.reset();
            selectedRoomId = null;
            loadInspectedRoomsForApproval();
            loadInspectionsList();
        } else {
            showToast(data.message || 'Failed to record failed inspection', 'error');
        }
    } catch (err) {
        showToast('Network error: ' + err.message, 'error');
    }
}
