/* ==========================================================================
   Company dashboard — campaign CRUD, applicant review, payments, profile
   ========================================================================== */

const session = CCApi.requireRole('COMPANY', 'login.html');

document.querySelectorAll('.dash-tab').forEach((tab) => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.dash-tab').forEach((t) => t.classList.remove('active'));
    document.querySelectorAll('.dash-panel').forEach((p) => p.style.display = 'none');
    tab.classList.add('active');
    document.getElementById(`tab-${tab.dataset.tab}`).style.display = 'block';
    if (tab.dataset.tab === 'campaigns') loadCampaigns();
    if (tab.dataset.tab === 'applications') loadApplications();
    if (tab.dataset.tab === 'payments') loadPayments();
    if (tab.dataset.tab === 'profile') loadProfile();
  });
});

// ---- Overview ----
async function loadOverview() {
  document.getElementById('welcome-heading').textContent = `Welcome back, ${session.fullName.split(' ')[0]}`;
  try {
    const stats = await CCApi.companyDashboard();
    const s = stats.summary;
    document.getElementById('overview-stats').innerHTML = `
      <div class="card stat-card"><div class="sc-label">Active campaigns</div><div class="sc-value">${s.activeCampaigns}</div></div>
      <div class="card stat-card"><div class="sc-label">Total applications</div><div class="sc-value">${s.totalApplications}</div></div>
      <div class="card stat-card"><div class="sc-label">Total spending</div><div class="sc-value">${CCUI.formatCurrency(s.totalSpending)}</div></div>
      <div class="card stat-card"><div class="sc-label">Average rating</div><div class="sc-value">${(s.averageRating || 0).toFixed(1)} ★</div></div>
    `;
    const activityEl = document.getElementById('recent-activity');
    if (!stats.recentActivity || !stats.recentActivity.length) {
      activityEl.innerHTML = `<div class="empty-state"><h3>No activity yet</h3><p>Publish your first campaign to get started.</p></div>`;
    } else {
      activityEl.innerHTML = stats.recentActivity.map((a) => `
        <div class="activity-item"><div class="activity-dot"></div>
          <div><div class="ai-desc">${CCUI.escapeHtml(a.description || a.action)}</div><div class="ai-time">${CCUI.timeAgo(a.timestamp)}</div></div>
        </div>`).join('');
    }
  } catch (err) { CCUI.toast(err.message || 'Could not load dashboard.', 'error'); }
}

// ---- Campaigns ----
async function loadCampaigns() {
  const el = document.getElementById('campaigns-list');
  el.innerHTML = `<div class="skeleton" style="height:80px;margin-bottom:10px;"></div>`.repeat(3);
  try {
    const data = await CCApi.myCampaigns({ page: 0, size: 30 });
    if (!data.content.length) {
      el.innerHTML = `<div class="empty-state"><h3>No campaigns yet</h3><p>Create your first campaign to start receiving applications.</p></div>`;
      return;
    }
    el.innerHTML = data.content.map((c) => `
      <div class="list-row">
        <div>
          <div class="lr-title">${CCUI.escapeHtml(c.title)}</div>
          <div class="lr-meta">${CCUI.formatCurrency(c.budget)} · ${c.applicationCount} applicants · <span class="badge ${CCUI.badgeClassFor(c.status)}">${c.status}</span></div>
        </div>
        <div class="lr-actions">
          <a href="campaign-detail.html?id=${c.id}" class="btn btn-sm btn-outline">View</a>
          <button class="btn btn-sm btn-outline" data-edit="${c.id}">Edit</button>
          ${c.status === 'OPEN' ? `<button class="btn btn-sm btn-outline" data-close="${c.id}">Close</button>` : ''}
          <button class="btn btn-sm btn-outline" data-delete="${c.id}" style="color:var(--danger);">Delete</button>
        </div>
      </div>`).join('');

    el.querySelectorAll('[data-edit]').forEach((btn) => btn.addEventListener('click', () => openCampaignModal(data.content.find(c => String(c.id) === btn.dataset.edit))));
    el.querySelectorAll('[data-close]').forEach((btn) => btn.addEventListener('click', async () => {
      await CCApi.updateCampaignStatus(btn.dataset.close, 'CLOSED');
      CCUI.toast('Campaign closed.', 'success');
      loadCampaigns();
    }));
    el.querySelectorAll('[data-delete]').forEach((btn) => btn.addEventListener('click', async () => {
      if (!confirm('Delete this campaign? This cannot be undone.')) return;
      await CCApi.deleteCampaign(btn.dataset.delete);
      CCUI.toast('Campaign deleted.', 'success');
      loadCampaigns();
    }));
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><h3>Couldn't load campaigns</h3><p>${CCUI.escapeHtml(err.message)}</p></div>`;
  }
}

function openCampaignModal(campaign) {
  const modal = document.getElementById('campaign-modal');
  document.getElementById('campaign-modal-title').textContent = campaign ? 'Edit campaign' : 'New campaign';
  document.getElementById('c-id').value = campaign ? campaign.id : '';
  document.getElementById('c-title').value = campaign ? campaign.title : '';
  document.getElementById('c-description').value = campaign ? campaign.description : '';
  document.getElementById('c-budget').value = campaign ? campaign.budget : '';
  document.getElementById('c-creators').value = campaign ? campaign.creatorsRequired : 1;
  document.getElementById('c-category').value = campaign ? (campaign.category || '') : '';
  document.getElementById('c-min-followers').value = campaign ? (campaign.minFollowers || '') : '';
  document.getElementById('c-platform').value = campaign ? (campaign.preferredPlatform || '') : '';
  document.getElementById('c-deadline').value = campaign ? (campaign.deadline || '') : '';
  document.getElementById('c-banner').value = campaign ? (campaign.bannerUrl || '') : '';
  document.getElementById('c-guidelines').value = campaign ? (campaign.guidelines || '') : '';
  document.getElementById('c-deliverables').value = campaign ? (campaign.deliverables || '') : '';
  modal.style.display = 'flex';
}

document.getElementById('new-campaign-btn').addEventListener('click', () => openCampaignModal(null));
document.getElementById('campaign-cancel').addEventListener('click', () => document.getElementById('campaign-modal').style.display = 'none');

document.getElementById('campaign-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = document.getElementById('campaign-save');
  btn.disabled = true; btn.textContent = 'Saving…';
  const payload = {
    title: document.getElementById('c-title').value.trim(),
    description: document.getElementById('c-description').value.trim(),
    budget: Number(document.getElementById('c-budget').value),
    creatorsRequired: Number(document.getElementById('c-creators').value) || 1,
    category: document.getElementById('c-category').value.trim(),
    minFollowers: Number(document.getElementById('c-min-followers').value) || null,
    preferredPlatform: document.getElementById('c-platform').value || null,
    deadline: document.getElementById('c-deadline').value || null,
    bannerUrl: document.getElementById('c-banner').value.trim(),
    guidelines: document.getElementById('c-guidelines').value.trim(),
    deliverables: document.getElementById('c-deliverables').value.trim(),
  };
  try {
    const id = document.getElementById('c-id').value;
    if (id) await CCApi.updateCampaign(id, payload); else await CCApi.createCampaign(payload);
    CCUI.toast(id ? 'Campaign updated!' : 'Campaign published!', 'success');
    document.getElementById('campaign-modal').style.display = 'none';
    loadCampaigns();
  } catch (err) {
    CCUI.toast(err.message || 'Could not save campaign.', 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Save campaign';
  }
});

// ---- Applications ----
async function loadApplications() {
  const el = document.getElementById('applications-list');
  el.innerHTML = `<div class="skeleton" style="height:80px;margin-bottom:10px;"></div>`.repeat(3);
  try {
    const data = await CCApi.applicationsByCompany({ page: 0, size: 30 });
    if (!data.content.length) {
      el.innerHTML = `<div class="empty-state"><h3>No applicants yet</h3><p>Once creators apply to your campaigns, they'll show up here.</p></div>`;
      return;
    }
    el.innerHTML = data.content.map((a) => `
      <div class="list-row">
        <div>
          <div class="lr-title">${CCUI.escapeHtml(a.creatorName)} → ${CCUI.escapeHtml(a.campaignTitle)}</div>
          <div class="lr-meta">${CCUI.formatNumber(a.creatorFollowers)} followers · <span class="badge ${CCUI.badgeClassFor(a.status)}">${a.status}</span></div>
          <div class="lr-meta" style="margin-top:6px;max-width:480px;">"${CCUI.escapeHtml(a.message || '')}"</div>
        </div>
        <div class="lr-actions">
          ${a.status === 'PENDING' || a.status === 'SHORTLISTED' ? `
            <button class="btn btn-sm btn-outline" data-review="${a.id}:SHORTLISTED">Shortlist</button>
            <button class="btn btn-sm btn-primary" data-review="${a.id}:ACCEPTED">Accept</button>
            <button class="btn btn-sm btn-outline" data-review="${a.id}:REJECTED" style="color:var(--danger);">Reject</button>` : ''}
          ${a.status === 'ACCEPTED' ? `<button class="btn btn-sm btn-primary" data-complete="${a.id}">Mark completed</button>
            <button class="btn btn-sm btn-outline" data-pay="${a.id}">Pay</button>` : ''}
        </div>
      </div>`).join('');

    el.querySelectorAll('[data-review]').forEach((btn) => btn.addEventListener('click', async () => {
      const [id, status] = btn.dataset.review.split(':');
      try {
        await CCApi.reviewApplication(id, { status });
        CCUI.toast(`Application ${status.toLowerCase()}.`, 'success');
        loadApplications();
      } catch (err) { CCUI.toast(err.message, 'error'); }
    }));
    el.querySelectorAll('[data-complete]').forEach((btn) => btn.addEventListener('click', async () => {
      await CCApi.completeApplication(btn.dataset.complete);
      CCUI.toast('Marked as completed.', 'success');
      loadApplications();
    }));
    el.querySelectorAll('[data-pay]').forEach((btn) => btn.addEventListener('click', async () => {
      const amount = prompt('Payment amount (₹)?');
      if (!amount) return;
      try {
        await CCApi.initiatePayment(btn.dataset.pay, amount);
        CCUI.toast('Payment initiated as pending.', 'success');
      } catch (err) { CCUI.toast(err.message, 'error'); }
    }));
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><h3>Couldn't load applications</h3><p>${CCUI.escapeHtml(err.message)}</p></div>`;
  }
}

// ---- Payments ----
async function loadPayments() {
  const el = document.getElementById('payments-list');
  el.innerHTML = `<div class="skeleton" style="height:70px;margin-bottom:10px;"></div>`.repeat(3);
  try {
    const data = await CCApi.companyPayments({ page: 0, size: 30 });
    if (!data.content.length) {
      el.innerHTML = `<div class="empty-state"><h3>No payments yet</h3><p>Initiate a payment from the Applications tab once work is approved.</p></div>`;
      return;
    }
    el.innerHTML = data.content.map((p) => `
      <div class="list-row">
        <div>
          <div class="lr-title">${CCUI.escapeHtml(p.creatorName)} — ${CCUI.escapeHtml(p.campaignTitle)}</div>
          <div class="lr-meta">${p.invoiceNumber || 'No invoice yet'} · <span class="badge ${CCUI.badgeClassFor(p.status)}">${p.status}</span></div>
        </div>
        <div style="display:flex;align-items:center;gap:12px;">
          <div class="cc-budget" style="font-size:1.1rem;">${CCUI.formatCurrency(p.amount)}</div>
          ${p.status === 'PENDING' ? `<button class="btn btn-sm btn-outline" data-approve="${p.id}">Approve</button>` : ''}
          ${p.status === 'APPROVED' ? `<button class="btn btn-sm btn-primary" data-pay-now="${p.id}">Mark paid</button>` : ''}
        </div>
      </div>`).join('');

    el.querySelectorAll('[data-approve]').forEach((btn) => btn.addEventListener('click', async () => {
      await CCApi.updatePaymentStatus(btn.dataset.approve, { status: 'APPROVED' });
      CCUI.toast('Payment approved.', 'success');
      loadPayments();
    }));
    el.querySelectorAll('[data-pay-now]').forEach((btn) => btn.addEventListener('click', async () => {
      await CCApi.updatePaymentStatus(btn.dataset.payNow, { status: 'PAID' });
      CCUI.toast('Payment released!', 'success');
      loadPayments();
    }));
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><h3>Couldn't load payments</h3><p>${CCUI.escapeHtml(err.message)}</p></div>`;
  }
}

// ---- Profile ----
async function loadProfile() {
  try {
    const p = await CCApi.myCompanyProfile();
    document.getElementById('p-name').value = p.companyName || '';
    document.getElementById('p-logo').value = p.logoUrl || '';
    document.getElementById('p-website').value = p.website || '';
    document.getElementById('p-industry').value = p.industry || '';
    document.getElementById('p-location').value = p.location || '';
    document.getElementById('p-description').value = p.description || '';
  } catch (err) { CCUI.toast(err.message || 'Could not load profile.', 'error'); }
}

document.getElementById('profile-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = document.getElementById('profile-save');
  btn.disabled = true; btn.textContent = 'Saving…';
  try {
    await CCApi.updateCompanyProfile({
      companyName: document.getElementById('p-name').value.trim(),
      logoUrl: document.getElementById('p-logo').value.trim(),
      website: document.getElementById('p-website').value.trim(),
      industry: document.getElementById('p-industry').value.trim(),
      location: document.getElementById('p-location').value.trim(),
      description: document.getElementById('p-description').value.trim(),
    });
    CCUI.toast('Profile updated!', 'success');
  } catch (err) {
    CCUI.toast(err.message || 'Could not save profile.', 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Save profile';
  }
});

loadOverview();
