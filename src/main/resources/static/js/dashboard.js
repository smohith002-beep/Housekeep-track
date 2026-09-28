// Admin Dashboard Live Data & Charts

let statusChart = null;
let workloadChart = null;

document.addEventListener('DOMContentLoaded', () => {
    loadDashboardStats();
    loadDashboardRecentTasks();
    loadDashboardRecentInspections();
    loadDashboardLiveRooms();
    
    // Auto-refresh every 30 seconds
    setInterval(loadDashboardStats, 30000);
});

async function loadDashboardStats() {
    try {
        const res = await fetch('/api/dashboard/stats');
        if (!res.ok) throw new Error('Failed to fetch dashboard stats');
        const data = await res.json();

        // Update KPIs
        const updateElem = (id, val) => {
            const el = document.getElementById(id);
            if (el) el.textContent = val;
        };

        updateElem('kpi-total-rooms', data.totalRooms || 0);
        updateElem('kpi-ready-rooms', data.readyRooms || 0);
        updateElem('kpi-dirty-rooms', data.dirtyRooms || 0);
        updateElem('kpi-cleaning-rooms', data.cleaningRooms || 0);
        updateElem('kpi-inspected-rooms', data.inspectedRooms || 0);
        updateElem('kpi-avail-hk', data.availableHousekeepers || 0);
        updateElem('kpi-busy-hk', data.busyHousekeepers || 0);
        updateElem('kpi-active-tasks', data.activeTasks || 0);
        updateElem('kpi-completed-tasks', data.completedTasks || 0);
        updateElem('kpi-failed-inspections', data.failedInspections || 0);
        updateElem('kpi-avg-turnaround', (data.avgTurnaroundMinutes || 0) + ' min');

        renderCharts(data);
    } catch (e) {
        console.error('Error loading dashboard stats:', e);
    }
}

function renderCharts(data) {
    if (typeof Chart === 'undefined') return;

    // 1. Room Status Doughnut Chart
    const statusCtx = document.getElementById('roomStatusChart');
    if (statusCtx) {
        const dist = data.roomStatusDistribution || {};
        const labels = ['READY', 'DIRTY', 'CLEANING', 'INSPECTED'];
        const values = [dist.READY || 0, dist.DIRTY || 0, dist.CLEANING || 0, dist.INSPECTED || 0];

        if (statusChart) {
            statusChart.data.datasets[0].data = values;
            statusChart.update();
        } else {
            statusChart = new Chart(statusCtx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: ['#107c41', '#c22929', '#d97706', '#2563eb'],
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { boxWidth: 12, font: { family: 'Montserrat', size: 11 } } }
                    },
                    cutout: '70%'
                }
            });
        }
    }

    // 2. Housekeeper Workload Bar Chart
    const workloadCtx = document.getElementById('housekeeperWorkloadChart');
    if (workloadCtx) {
        const workload = data.housekeeperWorkload || {};
        const names = Object.keys(workload);
        const taskCounts = Object.values(workload);

        if (workloadChart) {
            workloadChart.data.labels = names;
            workloadChart.data.datasets[0].data = taskCounts;
            workloadChart.update();
        } else {
            workloadChart = new Chart(workloadCtx, {
                type: 'bar',
                data: {
                    labels: names,
                    datasets: [{
                        label: 'Active Tasks Assigned',
                        data: taskCounts,
                        backgroundColor: '#c5a059',
                        borderRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { stepSize: 1, font: { family: 'Montserrat' } },
                            grid: { color: 'rgba(0,0,0,0.04)' }
                        },
                        x: {
                            grid: { display: false },
                            ticks: { font: { family: 'Montserrat' } }
                        }
                    },
                    plugins: {
                        legend: { display: false }
                    }
                }
            });
        }
    }
}

async function loadDashboardRecentTasks() {
    const tbody = document.getElementById('dashboard-tasks-table');
    if (!tbody) return;

    try {
        const res = await fetch('/api/cleaning-tasks');
        if (!res.ok) return;
        const tasks = await res.json();
        
        tbody.innerHTML = '';
        if (tasks.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No cleaning tasks recorded.</td></tr>';
            return;
        }

        tasks.slice(0, 6).forEach(t => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>Room ${t.roomNumber || 'N/A'}</strong></td>
                <td>${t.housekeeperName || 'Unassigned'}</td>
                <td><span class="status-badge ${t.status ? t.status.toLowerCase() : ''}">${t.status}</span></td>
                <td>${t.assignedAt ? t.assignedAt.replace('T', ' ').substring(0, 16) : '-'}</td>
                <td>
                    ${t.status === 'ASSIGNED' ? `<button class="btn-luxury-outline" style="padding:4px 10px;font-size:11px;" onclick="quickStartTask(${t.id})">Start</button>` : ''}
                    ${t.status === 'IN_PROGRESS' ? `<button class="btn-luxury-gold" style="padding:4px 10px;font-size:11px;" onclick="quickCompleteTask(${t.id})">Complete</button>` : ''}
                    ${t.status === 'COMPLETED' ? `<span style="font-size:11px;color:#107c41;font-weight:600;">✓ Done</span>` : ''}
                    ${t.status === 'REOPENED' ? `<button class="btn-luxury-outline" style="padding:4px 10px;font-size:11px;" onclick="quickStartTask(${t.id})">Restart</button>` : ''}
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load recent tasks', e);
    }
}

async function loadDashboardRecentInspections() {
    const tbody = document.getElementById('dashboard-inspections-table');
    if (!tbody) return;

    try {
        const res = await fetch('/api/inspections');
        if (!res.ok) return;
        const inspections = await res.json();

        tbody.innerHTML = '';
        if (inspections.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No inspections recorded.</td></tr>';
            return;
        }

        inspections.slice(0, 6).forEach(ins => {
            const tr = document.createElement('tr');
            const isPassed = ins.inspectionStatus === 'PASSED';
            tr.innerHTML = `
                <td><strong>Room ${ins.roomNumber || 'N/A'}</strong></td>
                <td>${ins.supervisorName || 'Supervisor'}</td>
                <td><span class="status-badge ${isPassed ? 'ready' : 'dirty'}">${ins.inspectionStatus}</span></td>
                <td>${ins.remarks || 'Standard inspection'}</td>
                <td>${ins.inspectedAt ? ins.inspectedAt.replace('T', ' ').substring(0, 16) : '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load recent inspections', e);
    }
}

async function loadDashboardLiveRooms() {
    const tbody = document.getElementById('dashboard-rooms-table');
    if (!tbody) return;

    try {
        const res = await fetch('/api/rooms');
        if (!res.ok) return;
        const rooms = await res.json();

        tbody.innerHTML = '';
        rooms.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>Room ${r.roomNumber}</strong></td>
                <td>${r.roomType}</td>
                <td>Floor ${r.floor}</td>
                <td><span class="status-badge ${r.status.toLowerCase()}">${r.status}</span></td>
                <td>₹${Number(r.pricePerNight).toLocaleString('en-IN')}</td>
                <td>
                    <a href="/room-status" class="btn-luxury-outline" style="padding:4px 10px;font-size:11px;">Manage</a>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load dashboard rooms', e);
    }
}

async function quickStartTask(taskId) {
    try {
        const res = await fetch(`/api/cleaning-tasks/${taskId}/start`, { method: 'PUT' });
        if (res.ok) {
            showToast('Cleaning task started. Room is now CLEANING.', 'success');
            loadDashboardStats();
            loadDashboardRecentTasks();
            loadDashboardLiveRooms();
        } else {
            const err = await res.json();
            showToast(err.message || 'Failed to start task', 'error');
        }
    } catch (e) {
        showToast('Network error: ' + e.message, 'error');
    }
}

async function quickCompleteTask(taskId) {
    try {
        const res = await fetch(`/api/cleaning-tasks/${taskId}/complete`, { method: 'PUT' });
        if (res.ok) {
            showToast('Cleaning task marked COMPLETED. Room is now INSPECTED.', 'success');
            loadDashboardStats();
            loadDashboardRecentTasks();
            loadDashboardLiveRooms();
        } else {
            const err = await res.json();
            showToast(err.message || 'Failed to complete task', 'error');
        }
    } catch (e) {
        showToast('Network error: ' + e.message, 'error');
    }
}
