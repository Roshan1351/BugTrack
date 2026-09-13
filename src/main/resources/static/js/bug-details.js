const bugDetailsPage = {
    bugId: null,
    basePath: null,

    init() {
        this.bugId = new URLSearchParams(window.location.search).get('id');
        this.basePath = auth.getBasePath();
        if (!this.bugId) {
            document.querySelector('.content-area').innerHTML =
                '<div class="alert alert-danger">Invalid bug ID</div>';
            return;
        }
        this.configureRolePanels();
        this.bindForms();
        this.load();
    },

    configureRolePanels() {
        const role = auth.getRole();
        const statusPanel = document.getElementById('updateStatusForm')?.closest('.card-panel');
        if (statusPanel && role !== 'Developer' && role !== 'Tester') {
            statusPanel.innerHTML = `
                <div class="card-header"><h6 class="mb-0">Status Updates</h6></div>
                <div class="card-body">
                    <p class="text-muted small mb-0">Status changes are handled by Developers and Testers. Use "Assign Developer" to re-route this bug.</p>
                </div>`;
        }
    },

    bindForms() {
        document.getElementById('addCommentForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();
            const text = document.getElementById('commentText').value.trim();
            if (!text) return;
            try {
                await api.post(`/bugs/${this.bugId}/comments`, { commentText: text });
                document.getElementById('commentText').value = '';
                utils.showToast('Comment posted');
                this.loadComments();
            } catch (err) {
                utils.showToast(err.message, 'danger');
            }
        });

        document.getElementById('updateStatusForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();
            try {
                await api.patch(`/bugs/${this.bugId}/status`, {
                    statusName: document.getElementById('newStatus').value,
                    remark: document.getElementById('statusRemarks').value
                });
                document.getElementById('statusRemarks').value = '';
                utils.showToast('Status updated');
                this.load();
            } catch (err) {
                utils.showToast(err.message, 'danger');
            }
        });

        document.getElementById('assignBugForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();
            const devId = document.getElementById('developerId').value;
            if (!devId) return;
            try {
                await api.patch(`/bugs/${this.bugId}/assign/${devId}`);
                utils.showToast('Bug assigned to developer');
                this.load();
            } catch (err) {
                utils.showToast(err.message, 'danger');
            }
        });
    },

    getStatusOptionsForRole(role, currentStatus) {
        const devStatuses = ['Assigned', 'In Progress', 'Resolved'];
        const testerStatuses = ['Re-Testing', 'Closed', 'Reopened', 'Rejected'];
        let allowed = utils.BUG_STATUSES;
        if (role === 'Developer') allowed = devStatuses;
        else if (role === 'Tester') allowed = testerStatuses;
        if (!allowed.includes(currentStatus)) allowed = [currentStatus, ...allowed];
        return [...new Set(allowed)];
    },

    async load() {
        try {
            const bug = await api.get(`/bugs/${this.bugId}`);
            document.getElementById('pageTitle').textContent = `Bug #${bug.bugId}`;
            document.getElementById('bugTitle').textContent = bug.title;

            document.getElementById('bugDetailsBody').innerHTML = `
                <div class="d-flex flex-wrap gap-2 mb-3">
                    <span class="badge ${utils.getPriorityBadgeClass(bug.priority)}">${utils.escapeHtml(bug.priority)}</span>
                    <span class="badge bg-info text-dark">${utils.escapeHtml(bug.severity)}</span>
                    <span class="badge ${utils.getStatusBadgeClass(bug.status)}">${utils.escapeHtml(bug.status)}</span>
                    <span class="badge bg-dark">${utils.escapeHtml(bug.projectName || '-')}</span>
                </div>
                <div class="bug-meta-grid mb-4">
                    <div><span class="meta-label">Raised By</span><span>${utils.escapeHtml(bug.raisedBy)}</span></div>
                    <div><span class="meta-label">Assigned To</span><span>${utils.escapeHtml(bug.assignedTo || 'Unassigned')}</span></div>
                    <div><span class="meta-label">Created</span><span>${utils.formatDateTime(bug.createdAt)}</span></div>
                    <div><span class="meta-label">SLA Due</span><span>${utils.formatSla(bug)}</span></div>
                </div>
                <h6 class="section-label">Description</h6>
                <p class="bug-text">${utils.escapeHtml(bug.description)}</p>
                <h6 class="section-label">Steps to Reproduce</h6>
                <pre class="bug-steps">${utils.escapeHtml(bug.stepsToReproduce || 'Not provided')}</pre>
                <div id="attachmentsSection"></div>`;

            const statusSelect = document.getElementById('newStatus');
            if (statusSelect) {
                const options = this.getStatusOptionsForRole(auth.getRole(), bug.status);
                statusSelect.innerHTML = options.map(s =>
                    `<option value="${s}" ${s === bug.status ? 'selected' : ''}>${s}</option>`
                ).join('');
            }

            await this.loadAttachments();
            await this.loadComments();
            await this.loadHistory();
        } catch (err) {
            document.getElementById('bugDetailsBody').innerHTML =
                `<div class="alert alert-danger">${utils.escapeHtml(err.message)}</div>`;
        }
    },

    async loadHistory() {
        const historyEl = document.getElementById('historyTimeline');
        if (!historyEl) return;
        try {
            const history = await api.get(`/bugs/${this.bugId}/history`);
            if (!history.length) {
                historyEl.innerHTML = '<p class="text-muted small mb-0">No status history yet.</p>';
                return;
            }
            historyEl.innerHTML = history.map(h => {
                const from = h.oldStatus && h.oldStatus !== '-' ? h.oldStatus : 'New';
                const to = h.newStatus || '-';
                const by = h.changedBy || 'System';
                const remark = h.comment || h.remarks || '';
                return `
                    <div class="timeline-item">
                        <div class="timeline-dot"></div>
                        <div class="timeline-content">
                            <div class="fw-semibold small">${utils.escapeHtml(by)}</div>
                            <div class="small">${utils.escapeHtml(from)} → <strong>${utils.escapeHtml(to)}</strong></div>
                            ${remark ? `<div class="text-muted small mt-1">${utils.escapeHtml(remark)}</div>` : ''}
                            <div class="text-muted small">${utils.formatDateTime(h.changedAt)}</div>
                        </div>
                    </div>`;
            }).join('');
        } catch (err) {
            historyEl.innerHTML = `<p class="text-danger small mb-0">${utils.escapeHtml(err.message)}</p>`;
        }
    },

    async loadAttachments() {
        const section = document.getElementById('attachmentsSection');
        if (!section) return;
        try {
            const attachments = await api.get(`/bugs/${this.bugId}/attachments`);
            if (!attachments.length) {
                section.innerHTML = '';
                return;
            }
            section.innerHTML = `
                <h6 class="section-label mt-4">Attachments</h6>
                <div class="d-flex flex-wrap gap-2">
                    ${attachments.map(a => `
                        <a href="${this.attachmentHref(a.fileUrl)}" target="_blank" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-paperclip"></i> ${utils.escapeHtml(a.uploadedBy)} — ${utils.formatDateTime(a.uploadedAt)}
                        </a>`).join('')}
                </div>`;
        } catch {
            section.innerHTML = '';
        }
    },

    async loadComments() {
        const list = document.getElementById('commentsList');
        if (!list) return;
        try {
            const comments = await api.get(`/bugs/${this.bugId}/comments`);
            if (!comments.length) {
                list.innerHTML = '<p class="text-muted mb-0">No comments yet. Start the discussion.</p>';
                return;
            }
            list.innerHTML = comments.map(c => `
                <div class="comment-card">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="fw-semibold text-primary">${utils.escapeHtml(c.commentedBy)}</span>
                        <span class="text-muted small">${utils.formatDateTime(c.createdAt)}</span>
                    </div>
                    <p class="mb-0">${utils.escapeHtml(c.commentText)}</p>
                </div>`).join('');
        } catch (err) {
            list.innerHTML = `<p class="text-danger">${utils.escapeHtml(err.message)}</p>`;
        }
    },

    attachmentHref(fileUrl) {
        if (!fileUrl) return '#';
        let url = String(fileUrl).replace(/\\/g, '/');
        if (url.startsWith('http://') || url.startsWith('https://')) return url;
        url = url.replace('/admin/uploads/', '/uploads/');
        if (!url.startsWith('/')) url = '/' + url.replace(/^\/+/, '');
        return url;
    },

    async loadDevelopersForAssign() {
        const assignCard = document.getElementById('assignCard');
        const role = auth.getRole();
        if (!assignCard || (role !== 'Admin' && role !== 'Project Manager')) return;
        try {
            const developers = await api.get('/users/role/Developer');
            const select = document.getElementById('developerId');
            select.innerHTML = '<option value="">Select developer</option>' +
                developers.map(d => `<option value="${d.userId}">${utils.escapeHtml(d.fullName)} (${utils.escapeHtml(d.email)})</option>`).join('');
            assignCard.classList.remove('d-none');
        } catch {
            assignCard.classList.add('d-none');
        }
    }
};

window.bugDetailsPage = bugDetailsPage;
