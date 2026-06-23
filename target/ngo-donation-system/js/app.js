/* ============================================================
   Donum Platform v2.0 - Frontend Logic
   ============================================================ */

function donumApiUrl(path) {
    const base = (typeof window.DONUM_CONTEXT !== 'undefined' && window.DONUM_CONTEXT)
        ? window.DONUM_CONTEXT : '';
    return base + '/api/dashboard/' + path + '?_=' + Date.now();
}

function destroyChart(canvasId) {
    if (typeof Chart === 'undefined') return;
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const existing = Chart.getChart(canvas);
    if (existing) existing.destroy();
}

// Mobile navigation toggle
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.mobile-toggle');
    const nav = document.querySelector('.nav-links');
    if (toggle && nav) {
        toggle.addEventListener('click', () => nav.classList.toggle('active'));
    }

    const refreshBtn = document.getElementById('refreshChartsBtn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => refreshAllDashboardCharts());
    }

    if (document.getElementById('chartTrends')) {
        refreshAllDashboardCharts();
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
    return fetch(donumApiUrl('trends'), { cache: 'no-store', credentials: 'same-origin' })
        .then(r => {
            if (!r.ok) throw new Error('trends ' + r.status);
            return r.json();
        })
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            destroyChart(canvasId);
            const labels = data.length ? data.map(d => d.month) : ['No data'];
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Cash Donations (\u20B9)',
                        data: data.length ? data.map(d => d.cashTotal) : [0],
                        borderColor: '#00d2ff',
                        backgroundColor: 'rgba(0,210,255,0.08)',
                        fill: true,
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#00d2ff'
                    }, {
                        label: 'In-Kind Items',
                        data: data.length ? data.map(d => d.goodsTotal) : [0],
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
        });
}

function loadDonationTypes(canvasId) {
    return fetch(donumApiUrl('donation-types'), { cache: 'no-store', credentials: 'same-origin' })
        .then(r => {
            if (!r.ok) throw new Error('types ' + r.status);
            return r.json();
        })
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            destroyChart(canvasId);
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.map(d => d.type),
                    datasets: [{
                        data: data.map(d => d.count),
                        backgroundColor: ['#00d2ff', '#7c3aed'],
                        borderWidth: 0,
                        hoverOffset: 8
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function (ctx) {
                                    const row = data[ctx.dataIndex];
                                    return ctx.label + ': ' + row.count + ' (\u20B9' + Math.round(row.total) + ' total)';
                                }
                            }
                        }
                    },
                    cutout: '65%'
                }
            });
        });
}

function loadUrgencyChart(canvasId) {
    return fetch(donumApiUrl('urgency'), { cache: 'no-store', credentials: 'same-origin' })
        .then(r => {
            if (!r.ok) throw new Error('urgency ' + r.status);
            return r.json();
        })
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            destroyChart(canvasId);
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(d => d.urgency),
                    datasets: [{
                        label: 'Open Requirements',
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
        });
}

function loadCampaignProgress(canvasId) {
    return fetch(donumApiUrl('campaigns'), { cache: 'no-store', credentials: 'same-origin' })
        .then(r => {
            if (!r.ok) throw new Error('campaigns ' + r.status);
            return r.json();
        })
        .then(data => {
            const ctx = document.getElementById(canvasId);
            if (!ctx) return;
            destroyChart(canvasId);
            const colors = ['#00d2ff', '#7c3aed', '#10b981', '#f59e0b', '#ec4899', '#6366f1'];
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(d => d.name),
                    datasets: [{
                        label: 'Progress (%)',
                        data: data.map(d => d.progressPct),
                        backgroundColor: data.map((_, i) => colors[i % colors.length]),
                        borderRadius: 8,
                        barThickness: 30
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true, maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                afterLabel: function (ctx) {
                                    const row = data[ctx.dataIndex];
                                    return 'Raised: \u20B9' + Math.round(row.raisedAmount) +
                                        ' / \u20B9' + Math.round(row.targetAmount) +
                                        ' (' + row.donationCount + ' donations)';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { max: 100, grid: { color: 'rgba(255,255,255,0.04)' } },
                        y: { grid: { display: false } }
                    }
                }
            });
        });
}

function refreshAllDashboardCharts() {
    const btn = document.getElementById('refreshChartsBtn');
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
    }

    const jobs = [];
    if (document.getElementById('chartTrends')) jobs.push(loadDonationTrends('chartTrends'));
    if (document.getElementById('chartTypes')) jobs.push(loadDonationTypes('chartTypes'));
    if (document.getElementById('chartUrgency')) jobs.push(loadUrgencyChart('chartUrgency'));
    if (document.getElementById('chartCampaigns')) jobs.push(loadCampaignProgress('chartCampaigns'));

    return Promise.all(jobs)
        .catch(err => {
            console.error('Chart refresh failed:', err);
            alert('Could not load chart data. Stay logged in as Admin and refresh the page.');
        })
        .finally(() => {
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh Charts';
            }
        });
}

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
