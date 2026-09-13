const utils = {
    getErrorMessage(data) {
        if (!data) return 'Something went wrong';
        if (typeof data === 'string') return data;
        return data.message || data.Message || data['message '] || Object.values(data)[0] || 'API Error';
    },

    escapeHtml(text) {
        if (text == null) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    formatDateTime(value) {
        if (!value) return '-';
        return new Date(value).toLocaleString();
    },

    getPriorityBadgeClass(priority) {
        const map = {
            Critical: 'badge-priority-critical',
            High: 'badge-priority-high',
            Medium: 'badge-priority-medium',
            Low: 'badge-priority-low'
        };
        return map[priority] || 'bg-secondary';
    },

    getStatusBadgeClass(status) {
        const map = {
            Open: 'bg-primary',
            Assigned: 'bg-info text-dark',
            'In Progress': 'bg-warning text-dark',
            Resolved: 'bg-success',
            'Re-Testing': 'bg-secondary',
            Closed: 'bg-dark',
            Reopened: 'bg-danger',
            Rejected: 'bg-secondary'
        };
        return map[status] || 'bg-secondary';
    },

    getSlaClass(bug) {
        if (!bug.dueDate) return 'sla-ok';
        if (bug.status === 'Closed' || bug.status === 'Resolved') return 'sla-ok';
        const due = new Date(bug.dueDate);
        const now = new Date();
        if (due < now) return 'sla-overdue';
        const hoursLeft = (due - now) / (1000 * 60 * 60);
        if (hoursLeft <= 24) return 'sla-warning';
        return 'sla-ok';
    },

    formatSla(bug) {
        if (!bug.dueDate) return '-';
        const cls = this.getSlaClass(bug);
        const label = this.formatDateTime(bug.dueDate);
        if (cls === 'sla-overdue') return `<span class="${cls}"><i class="bi bi-exclamation-triangle"></i> Overdue — ${label}</span>`;
        if (cls === 'sla-warning') return `<span class="${cls}"><i class="bi bi-clock"></i> Due soon — ${label}</span>`;
        return `<span class="${cls}">${label}</span>`;
    },

    parseDuplicateResponse(res) {
        const hasDuplicates = !!res.hasDuplicates;
        const message = res.message || res.Message || '';
        const rawList = res.similarBugs || res['Similar Bugs'] || [];
        const similarBugs = rawList.map(item => {
            const bug = item.bug || item.bugResponse || item;
            return {
                bugId: bug.bugId,
                title: bug.title,
                status: bug.status,
                similarityPercent: item.similarityPercent
            };
        });
        return { hasDuplicates, message, similarBugs };
    },

    async fetchAllBugs() {
        const projects = await api.get('/projects');
        if (!projects.length) return [];
        const results = await Promise.all(
            projects.map(p =>
                api.get(`/bugs/project/${p.projectId}`).catch(() => [])
            )
        );
        return results.flat().sort((a, b) => b.bugId - a.bugId);
    },

    renderBugTableRow(bug, detailPath) {
        return `<tr class="bug-row" data-href="${detailPath}?id=${bug.bugId}">
            <td><span class="text-muted">#${bug.bugId}</span></td>
            <td>${this.escapeHtml(bug.projectName || '-')}</td>
            <td class="fw-medium">${this.escapeHtml(bug.title)}</td>
            <td><span class="badge ${this.getPriorityBadgeClass(bug.priority)}">${this.escapeHtml(bug.priority)}</span></td>
            <td><span class="badge ${this.getStatusBadgeClass(bug.status)}">${this.escapeHtml(bug.status)}</span></td>
            <td>${this.escapeHtml(bug.assignedTo || 'Unassigned')}</td>
            <td>${this.formatSla(bug)}</td>
        </tr>`;
    },

    bindBugRowClicks(container) {
        container.querySelectorAll('.bug-row').forEach(row => {
            row.addEventListener('click', () => {
                window.location.href = row.dataset.href;
            });
        });
    },

    showToast(message, type = 'success') {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
            document.body.appendChild(container);
        }
        const id = `toast-${Date.now()}`;
        container.insertAdjacentHTML('beforeend', `
            <div id="${id}" class="toast align-items-center text-bg-${type} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">${this.escapeHtml(message)}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>`);
        const toastEl = document.getElementById(id);
        const toast = new bootstrap.Toast(toastEl, { delay: 3500 });
        toast.show();
        toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
    },

    BUG_STATUSES: [
        'Open', 'Assigned', 'In Progress', 'Resolved',
        'Re-Testing', 'Closed', 'Reopened', 'Rejected'
    ],

    PRIORITIES: ['Low', 'Medium', 'High', 'Critical'],
    SEVERITIES: ['Cosmetic', 'Minor', 'Major', 'Blocker']
};

window.utils = utils;
