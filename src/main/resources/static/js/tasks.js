// Cleaning Task Management Script

let allTasks = [];
let taskFilter = 'ALL';

document.addEventListener('DOMContentLoaded', () => {
    loadTasks();

    const filterBtns = document.querySelectorAll('.task-filter-btn');
    filterBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            filterBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            taskFilter = btn.dataset.status;
            renderTasks();
        });
    });

    const createTaskForm = document.getElementById('createTaskForm');
    if (createTaskForm) {
        createTaskForm.addEventListener('submit', handleCreateTask);
    }
});

async function loadTasks() {
    const tbody = document.getElementById('tasks-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/cleaning-tasks');
        if (!res.ok) throw new Error('Failed to load cleaning tasks');
        allTasks = await res.json();
        renderTasks();
    } catch (e) {
        console.error('Error fetching tasks:', e);
        showToast('Error loading tasks: ' + e.message, 'error');
    }
}

function renderTasks() {
    const tbody = document.getElementById('tasks-table-body');
    if (!tbody) return;

    const filtered = allTasks.filter(t => {
        if (taskFilter === 'ALL') return true;
        return t.status === taskFilter;
    });

    tbody.innerHTML = '';
    if (filtered.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center" style="padding: 30px; color: #888;">No tasks found for this status.</td></tr>';
        return;
    }

    filtered.forEach(task => {
        const tr = document.createElement('tr');
        
        let actions = '';
        if (task.status === 'ASSIGNED') {
            actions = `<button class="btn-luxury-outline" style="padding: 5px 12px; font-size: 11px;" onclick="startTask(${task.id})">Start Cleaning</button>`;
        } else if (task.status === 'IN_PROGRESS') {
            actions = `<button class="btn-luxury-gold" style="padding: 5px 12px; font-size: 11px;" onclick="completeTask(${task.id})">Complete Cleaning</button>`;
        } else if (task.status === 'COMPLETED') {
            actions = `<button class="btn-luxury-outline" style="padding: 5px 12px; font-size: 11px;" onclick="promptReopenTask(${task.id})">Reopen</button>`;
        } else if (task.status === 'REOPENED') {
            actions = `<button class="btn-luxury-gold" style="padding: 5px 12px; font-size: 11px;" onclick="startTask(${task.id})">Resume Cleaning</button>`;
        }

        tr.innerHTML = `
            <td>#${task.id}</td>
            <td><strong>Room ${task.roomNumber || 'N/A'}</strong></td>
            <td><strong>${task.housekeeperName || 'Unassigned / Pending'}</strong></td>
            <td><span class="status-badge ${task.status.toLowerCase()}">${task.status}</span></td>
            <td>${task.assignedAt ? task.assignedAt.replace('T', ' ').substring(0, 16) : '-'}</td>
            <td>${task.notes || '-'}</td>
            <td>${actions}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function startTask(taskId) {
    try {
        const res = await fetch(`/api/cleaning-tasks/${taskId}/start`, { method: 'PUT' });
        const data = await res.json();
        if (res.ok) {
            showToast(`Task #${taskId} started! Room is now CLEANING.`, 'success');
            loadTasks();
        } else {
            showToast(data.message || 'Failed to start task.', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

async function completeTask(taskId) {
    try {
        const res = await fetch(`/api/cleaning-tasks/${taskId}/complete`, { method: 'PUT' });
        const data = await res.json();
        if (res.ok) {
            showToast(`Task #${taskId} completed! Room is now INSPECTED.`, 'success');
            loadTasks();
        } else {
            showToast(data.message || 'Failed to complete task.', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

function promptReopenTask(taskId) {
    const notes = prompt('Enter reason / remarks for reopening this cleaning task:', 'Additional sanitation required');
    if (notes === null) return; // user cancelled

    fetch(`/api/cleaning-tasks/${taskId}/reopen`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ notes: notes })
    })
    .then(res => res.json())
    .then(data => {
        showToast(`Task #${taskId} reopened. Room reverted to CLEANING.`, 'info');
        loadTasks();
    })
    .catch(err => showToast('Error reopening task: ' + err.message, 'error'));
}

async function handleCreateTask(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const payload = {
        roomId: parseInt(formData.get('roomId')),
        housekeeperId: formData.get('housekeeperId') ? parseInt(formData.get('housekeeperId')) : null,
        notes: formData.get('notes')
    };

    try {
        const res = await fetch('/api/cleaning-tasks', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok) {
            showToast(`Cleaning task created and assigned to ${data.housekeeperName}!`, 'success');
            closeModal('createTaskModal');
            form.reset();
            loadTasks();
        } else {
            showToast(data.message || 'Failed to create task.', 'error');
        }
    } catch (err) {
        showToast('Network error: ' + err.message, 'error');
    }
}
