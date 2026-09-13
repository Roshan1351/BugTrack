function exportBugsToCsv(bugs, filename = 'bugtrack_report.csv') {
    if (!bugs || !bugs.length) {
        utils.showToast('No bugs to export', 'warning');
        return;
    }

    const headers = ['Bug ID', 'Title', 'Project', 'Status', 'Priority', 'Severity', 'Raised By', 'Assigned To', 'Due Date', 'Created At'];
    const rows = bugs.map(b => [
        b.bugId,
        `"${(b.title || '').replace(/"/g, '""')}"`,
        `"${(b.projectName || '').replace(/"/g, '""')}"`,
        b.status,
        b.priority,
        b.severity,
        `"${(b.raisedBy || '').replace(/"/g, '""')}"`,
        `"${(b.assignedTo || '').replace(/"/g, '""')}"`,
        b.dueDate || '',
        b.createdAt || ''
    ]);

    const csv = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    utils.showToast('Report exported successfully');
}

async function exportAllBugs() {
    try {
        const role = auth.getRole();
        let bugs;
        if (role === 'Developer') {
            bugs = await api.get('/bugs/my-assigned');
        } else if (role === 'Tester') {
            bugs = await api.get('/bugs/my-raised');
        } else {
            bugs = await utils.fetchAllBugs();
        }
        exportBugsToCsv(bugs);
    } catch (err) {
        utils.showToast(err.message || 'Export failed', 'danger');
    }
}

window.exportAllBugs = exportAllBugs;
window.exportBugsToCsv = exportBugsToCsv;
