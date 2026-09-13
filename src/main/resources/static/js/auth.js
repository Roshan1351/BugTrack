const auth = {
    isAuthenticated() {
        return !!localStorage.getItem('token');
    },

    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },

    getBasePath() {
        const role = this.getRole();
        if (role === 'Admin' || role === 'Project Manager') return '/admin';
        if (role === 'Tester') return '/tester';
        if (role === 'Developer') return '/developer';
        return '';
    },

    login(token, user) {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
        this.redirectBasedOnRole();
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
    },

    redirectBasedOnRole() {
        const base = this.getBasePath();
        window.location.href = base ? `${base}/dashboard.html` : '/login.html';
    },

    requireAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = '/login.html';
        }
    },

    requireRole(roles) {
        this.requireAuth();
        const userRole = this.getRole();
        if (!roles.includes(userRole)) {
            alert('Unauthorized access');
            this.redirectBasedOnRole();
        }
    }
};

window.auth = auth;
