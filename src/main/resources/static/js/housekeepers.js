// Housekeeper Management Script

let allHousekeepers = [];

document.addEventListener('DOMContentLoaded', () => {
    loadHousekeepers();

    const addHkForm = document.getElementById('addHousekeeperForm');
    if (addHkForm) {
        addHkForm.addEventListener('submit', handleAddHousekeeper);
    }
});

async function loadHousekeepers() {
    const grid = document.getElementById('housekeepers-grid');
    if (!grid) return;

    try {
        const res = await fetch('/api/housekeepers');
        if (!res.ok) throw new Error('Failed to load housekeepers');
        allHousekeepers = await res.json();

        grid.innerHTML = '';
        if (allHousekeepers.length === 0) {
            grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #888;">No housekeeping staff recorded.</div>';
            return;
        }

        allHousekeepers.forEach(hk => {
            const card = document.createElement('div');
            card.className = 'luxury-card';
            
            let statusColor = 'ready';
            if (hk.status === 'BUSY') statusColor = 'cleaning';
            if (hk.status === 'OFF_DUTY') statusColor = 'dirty';

            card.innerHTML = `
                <div class="card-body">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
                        <div>
                            <span class="section-label" style="font-size: 10px; margin-bottom: 2px;">Housekeeping Staff</span>
                            <h3 style="font-size: 1.3rem;">${hk.name}</h3>
                        </div>
                        <span class="status-badge ${statusColor}">${hk.status}</span>
                    </div>

                    <div style="font-size: 12.5px; color: var(--text-secondary); line-height: 1.8; margin-bottom: 16px;">
                        <div>📞 Phone: <strong>${hk.phone}</strong></div>
                        <div>✉️ Email: <strong>${hk.email || 'N/A'}</strong></div>
                        <div>🧹 Current Task: <strong>${hk.currentRoomNumber ? 'Room ' + hk.currentRoomNumber : (hk.status === 'AVAILABLE' ? 'Standing by (Available)' : 'None')}</strong></div>
                        <div>✓ Completed Tasks: <strong>${hk.completedTasksCount || 0}</strong></div>
                    </div>

                    <div style="border-top: 1px solid rgba(0,0,0,0.06); padding-top: 14px; display: flex; justify-content: space-between; align-items: center;">
                        <button class="btn-luxury-outline" style="padding: 6px 12px; font-size: 11px;" onclick="viewHousekeeperTasks(${hk.id}, '${hk.name}')">
                            View Tasks
                        </button>
                        <select onchange="updateHousekeeperStatus(${hk.id}, this.value)" style="padding: 5px 8px; font-size: 11px; border-radius: 4px; border: 1px solid var(--gold-border);">
                            <option value="AVAILABLE" ${hk.status === 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                            <option value="BUSY" ${hk.status === 'BUSY' ? 'selected' : ''}>BUSY</option>
                            <option value="OFF_DUTY" ${hk.status === 'OFF_DUTY' ? 'selected' : ''}>OFF_DUTY</option>
                        </select>
                    </div>
                </div>
            `;
            grid.appendChild(card);
        });
    } catch (e) {
        console.error('Error fetching housekeepers:', e);
        showToast('Error loading housekeepers: ' + e.message, 'error');
    }
}

async function updateHousekeeperStatus(id, newStatus) {
    const hk = allHousekeepers.find(h => h.id === id);
    if (!hk) return;

    try {
        const res = await fetch(`/api/housekeepers/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: hk.name,
                phone: hk.phone,
                email: hk.email,
                status: newStatus
            })
        });

        if (res.ok) {
            showToast(`${hk.name}'s status updated to ${newStatus}`, 'success');
            loadHousekeepers();
        } else {
            showToast('Failed to update status', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

async function viewHousekeeperTasks(id, name) {
    try {
        const res = await fetch(`/api/housekeepers/${id}/tasks`);
        if (!res.ok) return;
        const tasks = await res.json();

        let details = `Tasks assigned to ${name} (${tasks.length} total):\n\n`;
        if (tasks.length === 0) {
            details += 'No tasks currently assigned.';
        } else {
            tasks.forEach(t => {
                details += `• Task #${t.id} - Room ${t.roomNumber} [${t.status}] - ${t.notes || 'No notes'}\n`;
            });
        }
        alert(details);
    } catch (e) {
        showToast('Failed to load tasks for ' + name, 'error');
    }
}

async function handleAddHousekeeper(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        name: formData.get('name'),
        phone: formData.get('phone'),
        email: formData.get('email'),
        status: formData.get('status') || 'AVAILABLE'
    };

    try {
        const res = await fetch('/api/housekeepers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok) {
            showToast(`Housekeeper ${data.name} added successfully!`, 'success');
            closeModal('addHousekeeperModal');
            form.reset();
            loadHousekeepers();
        } else {
            showToast(data.message || 'Failed to add housekeeper', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}
