/* ==========================================================================
   CreatorConnect — shared UI utilities used across every page
   ========================================================================== */

const CCUI = (() => {
  function toast(message, type = 'default', duration = 3800) {
    let region = document.querySelector('.toast-region');
    if (!region) {
      region = document.createElement('div');
      region.className = 'toast-region';
      document.body.appendChild(region);
    }
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = message;
    region.appendChild(el);
    setTimeout(() => el.remove(), duration);
  }

  function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '₹0';
    return '₹' + Number(amount).toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function formatNumber(n) {
    if (n === null || n === undefined) return '0';
    if (n >= 1_000_000) return (n / 1_000_000).toFixed(1).replace(/\.0$/, '') + 'M';
    if (n >= 1_000) return (n / 1_000).toFixed(1).replace(/\.0$/, '') + 'K';
    return String(n);
  }

  function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  function timeAgo(iso) {
    if (!iso) return '';
    const seconds = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
    const steps = [[31536000, 'y'], [2592000, 'mo'], [86400, 'd'], [3600, 'h'], [60, 'm']];
    for (const [s, label] of steps) {
      const v = Math.floor(seconds / s);
      if (v >= 1) return `${v}${label} ago`;
    }
    return 'just now';
  }

  function badgeClassFor(status) {
    const map = {
      OPEN: 'badge-open', ACCEPTED: 'badge-accepted', APPROVED: 'badge-accepted', PAID: 'badge-accepted',
      COMPLETED: 'badge-accepted', PENDING: 'badge-pending', SHORTLISTED: 'badge-pending',
      SUBMITTED: 'badge-pending', REJECTED: 'badge-rejected', CLOSED: 'badge-closed',
      CANCELLED: 'badge-rejected', WITHDRAWN: 'badge-closed', CHANGES_REQUESTED: 'badge-pending',
    };
    return map[status] || 'badge-closed';
  }

  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str).replace(/[&<>"']/g, (c) => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
    })[c]);
  }

  function initTheme() {
    const saved = localStorage.getItem('cc_theme') || 'light';
    document.documentElement.setAttribute('data-theme', saved);
    document.querySelectorAll('[data-theme-toggle]').forEach((btn) => {
      btn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        localStorage.setItem('cc_theme', next);
      });
    });
  }

  function initMobileNav() {
    const toggle = document.querySelector('.mobile-toggle');
    const links = document.querySelector('.nav-links');
    if (!toggle || !links) return;
    toggle.addEventListener('click', () => {
      const isOpen = links.style.display === 'flex';
      links.style.display = isOpen ? '' : 'flex';
      links.style.flexDirection = 'column';
      links.style.position = 'absolute';
      links.style.top = '64px';
      links.style.left = '0';
      links.style.right = '0';
      links.style.background = 'var(--paper-raised)';
      links.style.padding = '20px 24px';
      links.style.borderBottom = '1px solid var(--line)';
    });
  }

  function renderAuthNav() {
    const session = CCApi.getSession();
    const slot = document.querySelector('[data-nav-actions]');
    if (!slot) return;
    if (session) {
      const dashboardHref = session.role === 'COMPANY' ? 'company-dashboard.html'
        : session.role === 'CREATOR' ? 'creator-dashboard.html' : 'admin-dashboard.html';
      slot.innerHTML = `
        <a href="${dashboardHref}" class="btn btn-outline btn-sm">${escapeHtml(session.fullName.split(' ')[0])}'s Dashboard</a>
        <button class="btn btn-primary btn-sm" id="cc-logout-btn">Log out</button>
      `;
      document.getElementById('cc-logout-btn').addEventListener('click', () => {
        CCApi.clearSession();
        window.location.href = 'index.html';
      });
    } else {
      slot.innerHTML = `
        <a href="login.html" class="btn btn-outline btn-sm">Log in</a>
        <a href="register.html" class="btn btn-primary btn-sm">Get started</a>
      `;
    }
  }

  function initReveal() {
    const items = document.querySelectorAll('[data-reveal]');
    if (!items.length || !('IntersectionObserver' in window)) return;
    const io = new IntersectionObserver((entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) {
          e.target.classList.add('is-visible');
          io.unobserve(e.target);
        }
      });
    }, { threshold: 0.15 });
    items.forEach((el) => io.observe(el));
  }

  document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initMobileNav();
    renderAuthNav();
    initReveal();
  });

  return { toast, formatCurrency, formatNumber, formatDate, timeAgo, badgeClassFor, escapeHtml, renderAuthNav };
})();
