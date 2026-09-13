async function loadNotificationBadge() {
    const badge = document.getElementById('notifBadge');
    if (!badge) return;
    try {
        const data = await api.get('/notifications/count');
        const count = data.unreadCount || 0;
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.classList.remove('d-none');
        } else {
            badge.classList.add('d-none');
        }
    } catch {
        badge.classList.add('d-none');
    }
}

function renderNavbar() {
    const user = auth.getUser();
    if (!user) return;

    const basePath = auth.getBasePath();

    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        let navLinks = `
            <a href="${basePath}/dashboard.html" class="nav-link">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a href="${basePath}/bugs.html" class="nav-link">
                <i class="bi bi-bug"></i> Bugs
            </a>
            <a href="${basePath}/notifications.html" class="nav-link">
                <i class="bi bi-bell"></i> Notifications
            </a>`;

        if (user.role === 'Admin' || user.role === 'Project Manager') {
            navLinks += `
            <a href="${basePath}/projects.html" class="nav-link">
                <i class="bi bi-folder2-open"></i> Projects
            </a>`;
        }

        if (user.role === 'Admin') {
            navLinks += `
            <a href="${basePath}/users.html" class="nav-link">
                <i class="bi bi-people"></i> Users
            </a>`;
        }

        if (user.role === 'Tester') {
            navLinks += `
            <a href="${basePath}/raise-bug.html" class="nav-link">
                <i class="bi bi-plus-circle"></i> Raise Bug
            </a>`;
        }

        sidebar.innerHTML = `
            <div class="sidebar-brand">
                <a href="${basePath}/dashboard.html" class="brand-logo">Bug<span>Track</span></a>
                <p class="sidebar-tagline">Smart Bug Tracking</p>
            </div>
            <nav class="sidebar-nav">${navLinks}</nav>
            <div class="sidebar-footer">
                <div class="user-chip">
                    <div class="user-avatar">${user.fullName.charAt(0).toUpperCase()}</div>
                    <div>
                        <div class="user-name">${utils.escapeHtml(user.fullName)}</div>
                        <div class="user-role">${utils.escapeHtml(user.role)}</div>
                    </div>
                </div>
            </div>`;
    }

    const topnav = document.getElementById('topnav');
    if (topnav) {
        const pageTitle = document.body.dataset.pageTitle || 'BugTrack Workspace';
        topnav.innerHTML = `
            <div class="topnav-inner">
                <div>
                    <h5 class="page-title mb-0">${pageTitle}</h5>
                    <p class="page-subtitle mb-0">Welcome back, ${utils.escapeHtml(user.fullName)}</p>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <a href="${basePath}/notifications.html" class="btn btn-light btn-sm position-relative">
                        <i class="bi bi-bell"></i>
                        <span id="notifBadge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">0</span>
                    </a>
                    <button onclick="auth.logout()" class="btn btn-outline-danger btn-sm">
                        <i class="bi bi-box-arrow-right"></i> Logout
                    </button>
                </div>
            </div>`;
    }

    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    loadNotificationBadge();
}

document.addEventListener('DOMContentLoaded', () => {
    if (auth.isAuthenticated()) {
        renderNavbar();
    }
});
