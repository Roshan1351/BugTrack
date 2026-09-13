const notificationsPage = {
    bugDetailPath: '/admin/bug-details.html',

    init(bugDetailPath) {
        this.bugDetailPath = bugDetailPath || auth.getBasePath() + '/bug-details.html';
        document.getElementById('markAllBtn')?.addEventListener('click', () => this.markAllRead());
        this.load();
    },

    async load() {
        const container = document.getElementById('notifList');
        try {
            const notifications = await api.get('/notifications');
            if (!notifications.length) {
                container.innerHTML = '<div class="text-center py-5 text-muted"><i class="bi bi-bell-slash fs-2 d-block mb-2"></i>No notifications yet</div>';
                return;
            }
            container.innerHTML = notifications.map(n => `
                <div class="notif-item p-3 border-bottom ${n.isRead ? '' : 'unread'}" data-id="${n.notificationId}">
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <div class="flex-grow-1" style="cursor:pointer" onclick="notificationsPage.openBug(${n.bugId}, ${n.notificationId}, ${n.isRead})">
                            <div class="fw-semibold">${utils.escapeHtml(n.bugTitle || 'Bug #' + n.bugId)}</div>
                            <p class="mb-1 small">${utils.escapeHtml(n.message)}</p>
                            <span class="text-muted small">${utils.formatDateTime(n.createdAt)}</span>
                        </div>
                        ${!n.isRead ? `<button class="btn btn-sm btn-light" onclick="notificationsPage.markRead(${n.notificationId})"><i class="bi bi-check"></i></button>` : ''}
                    </div>
                </div>`).join('');
        } catch (err) {
            container.innerHTML = `<div class="alert alert-danger m-3">${utils.escapeHtml(err.message)}</div>`;
        }
    },

    async markRead(id) {
        try {
            await api.patch(`/notifications/${id}/read`);
            this.load();
            loadNotificationBadge();
        } catch (err) {
            utils.showToast(err.message, 'danger');
        }
    },

    async markAllRead() {
        try {
            await api.patch('/notifications/read-all');
            utils.showToast('All notifications marked as read');
            this.load();
            loadNotificationBadge();
        } catch (err) {
            utils.showToast(err.message, 'danger');
        }
    },

    async openBug(bugId, notifId, isRead) {
        if (!isRead) {
            try { await api.patch(`/notifications/${notifId}/read`); } catch {}
        }
        window.location.href = `${this.bugDetailPath}?id=${bugId}`;
    }
};

window.notificationsPage = notificationsPage;
