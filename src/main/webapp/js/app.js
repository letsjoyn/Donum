/* ============================================================
   Donum Platform v2.0 - Frontend Logic
   ============================================================ */

// Mobile navigation toggle
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.mobile-toggle');
    const nav = document.querySelector('.nav-links');
    if (toggle && nav) {
        toggle.addEventListener('click', () => nav.classList.toggle('active'));
    }
});

/* ---------- Chart defaults ---------- */
if (typeof Chart !== 'undefined') {
    Chart.defaults.color = '#94a3b8';
    Chart.defaults.borderColor = 'rgba(255,255,255,0.06)';
    Chart.defaults.font.family = "'Inter', sans-serif";
}

/* ---------- Dashboard chart loaders ---------- */

function loadDonationTrends(canvasId) {
    fetch('api/dashboard/trends')
        .then(r => r.json())
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: data.map(d => d.month),
                    datasets: [{
                        label: 'Cash Donations (\u20B9)',
                        data: data.map(d => d.total_cash),
                        borderColor: '#00d2ff',
                        backgroundColor: 'rgba(0,210,255,0.08)',
                        fill: true,
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#00d2ff'
                    }, {
                        label: 'In-Kind Items',
                        data: data.map(d => d.total_kind),
                        borderColor: '#7c3aed',
                        backgroundColor: 'rgba(124,58,237,0.08)',
                        fill: true,
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#7c3aed'
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { position: 'top' } },
                    scales: { y: { beginAtZero: true, grid: { color: 'rgba(255,255,255,0.04)' } } }
                }
            });
        })
        .catch(() => {});
}

function loadDonationTypes(canvasId) {
    fetch('api/dashboard/donation-types')
        .then(r => r.json())
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.map(d => d.type),
                    datasets: [{
                        data: data.map(d => d.count),
                        backgroundColor: ['#00d2ff', '#7c3aed', '#10b981', '#f59e0b', '#ef4444'],
                        borderWidth: 0,
                        hoverOffset: 8
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { position: 'bottom' } },
                    cutout: '65%'
                }
            });
        })
        .catch(() => {});
}

function loadUrgencyChart(canvasId) {
    fetch('api/dashboard/urgency')
        .then(r => r.json())
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(d => d.urgency),
                    datasets: [{
                        label: 'Requirements',
                        data: data.map(d => d.count),
                        backgroundColor: ['#ef4444', '#f59e0b', '#3b82f6', '#10b981'],
                        borderRadius: 8,
                        barThickness: 40
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: { beginAtZero: true, ticks: { stepSize: 1 }, grid: { color: 'rgba(255,255,255,0.04)' } },
                        x: { grid: { display: false } }
                    }
                }
            });
        })
        .catch(() => {});
}

function loadCampaignProgress(canvasId) {
    fetch('api/dashboard/campaigns')
        .then(r => r.json())
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(d => d.name),
                    datasets: [{
                        label: 'Progress (%)',
                        data: data.map(d => d.progress),
                        backgroundColor: data.map((_, i) => ['#00d2ff', '#7c3aed', '#10b981', '#f59e0b'][i % 4]),
                        borderRadius: 8,
                        barThickness: 30
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        x: { max: 100, grid: { color: 'rgba(255,255,255,0.04)' } },
                        y: { grid: { display: false } }
                    }
                }
            });
        })
        .catch(() => {});
}

/* ---------- Auto-init charts on page load ---------- */
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('chartTrends'))    loadDonationTrends('chartTrends');
    if (document.getElementById('chartTypes'))     loadDonationTypes('chartTypes');
    if (document.getElementById('chartUrgency'))   loadUrgencyChart('chartUrgency');
    if (document.getElementById('chartCampaigns')) loadCampaignProgress('chartCampaigns');
});

/* ---------- Donation form toggle ---------- */
function toggleDonationFields() {
    const type = document.getElementById('donationType');
    const cashSection = document.getElementById('cashSection');
    const kindSection = document.getElementById('kindSection');
    if (!type || !cashSection || !kindSection) return;
    if (type.value === 'Cash') {
        cashSection.style.display = '';
        kindSection.style.display = 'none';
    } else {
        cashSection.style.display = 'none';
        kindSection.style.display = '';
    }
}

/* ---------- Confirm action ---------- */
function confirmAction(msg) {
    return confirm(msg || 'Are you sure?');
}
