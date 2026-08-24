/* ==========================================================================
   Creator dashboard — tabs, data loading, application/submission/profile actions
   ========================================================================== */

const session = CCApi.requireRole('CREATOR', 'login.html');

// ---- Tabs ----
document.querySelectorAll('.dash-tab').forEach((tab) => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.dash-tab').forEach((t) => t.classList.remove('active'));
    document.querySelectorAll('.dash-panel').forEach((p) => p.style.display = 'none');
    tab.classList.add('active');
    document.getElementById(`tab-${tab.dataset.tab}`).style.display = 'block';
    if (tab.dataset.tab === 'applications') loadApplications();
    if (tab.dataset.tab === 'earnings') loadEarnings();
    if (tab.dataset.tab === 'profile') loadProfile();
  });
});

// ---- Overview ----
async function loadOverview() {
  document.getElementById('welcome-heading').textContent = `Welcome back, ${session.fullName.split(' ')[0]}`;
  try {
    const stats = await CCApi.creatorDashboard();
    const s = stats.summary;
    document.getElementById('overview-stats').innerHTML = `
      <div class="card stat-card"><div class="sc-label">Total applications</div><div class="sc-value">${s.totalApplications}</div></div>
      <div class="card stat-card"><div class="sc-label">Accepted</div><div class="sc-value">${s.acceptedApplications}</div></div>
      <div class="card stat-card"><div class="sc-label">Completed campaigns</div><div class="sc-value">${s.completedCampaigns}</div></div>
      <div class="card stat-card"><div class="sc-label">Total earnings</div><div class="sc-value">${CCUI.formatCurrency(s.totalEarnings)}</div></div>
    `;
    const activityEl = document.getElementById('recent-activity');
    if (!stats.recentActivity || !stats.recentActivity.length) {
      activityEl.innerHTML = `<div class="empty-state"><h3>No activity yet</h3><p>Apply to a campaign to get started.</p></div>`;
    } else {
      activityEl.innerHTML = stats.recentActivity.map((a) => `
        <div class="activity-item">
          <div class="activity-dot"></div>
          <div><div class="ai-desc">${CCUI.escapeHtml(a.description || a.action)}</div><div class="ai-time">${CCUI.timeAgo(a.timestamp)}</div></div>
        </div>`).join('');
    }
  } catch (err) {
    CCUI.toast(err.message || 'Could not load dashboard.', 'error');
  }
}

// ---- Applications ----
async function loadApplications() {
  const el = document.getElementById('applications-list');
  el.innerHTML = `<div class="skeleton" style="height:80px;margin-bottom:10px;"></div>`.repeat(3);
  try {
    const data = await CCApi.myApplications({ page: 0, size: 30 });
    if (!data.content.length) {
      el.innerHTML = `<div class="empty-state"><h3>No applications yet</h3><p>Browse open campaigns and apply to get started.</p></div>`;
      return;
    }
    el.innerHTML = data.content.map((a) => `
      <div class="list-row">
        <div>
          <div class="lr-title">${CCUI.escapeHtml(a.campaignTitle)}</div>
          <div class="lr-meta">Applied ${CCUI.timeAgo(a.createdAt)} · <span class="badge ${CCUI.badgeClassFor(a.status)}">${a.status}</span></div>
        </div>
        <div class="lr-actions">
          ${a.status === 'ACCEPTED' ? `<button class="btn btn-sm btn-primary" data-submit="${a.id}">Submit work</button>` : ''}
          ${a.status === 'PENDING' ? `<button class="btn btn-sm btn-outline" data-withdraw="${a.id}">Withdraw</button>` : ''}
        </div>
      </div>`).join('');

    el.querySelectorAll('[data-withdraw]').forEach((btn) => btn.addEventListener('click', async () => {
      try {
        await CCApi.withdrawApplication(btn.dataset.withdraw);
        CCUI.toast('Application withdrawn.', 'success');
        loadApplications();
      } catch (err) { CCUI.toast(err.message, 'error'); }
    }));
    el.querySelectorAll('[data-submit]').forEach((btn) => btn.addEventListener('click', () => {
      openSubmitModal(btn.dataset.submit);
    }));
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><h3>Couldn't load applications</h3><p>${CCUI.escapeHtml(err.message)}</p></div>`;
  }
}

function openSubmitModal(applicationId) {
  document.querySelectorAll('.dash-tab').forEach((t) => t.classList.remove('active'));
  document.querySelectorAll('.dash-panel').forEach((p) => p.style.display = 'none');
  document.querySelector('[data-tab="submissions"]').classList.add('active');
  document.getElementById('tab-submissions').style.display = 'block';

  document.getElementById('submissions-list').innerHTML = `
    <form id="submit-work-form" class="card" style="padding:24px;max-width:560px;">
      <div class="field"><label>Instagram reel link</label><input type="url" id="s-ig" /></div>
      <div class="field"><label>YouTube link</label><input type="url" id="s-yt" /></div>
      <div class="field"><label>TikTok link</label><input type="url" id="s-tt" /></div>
      <div class="field"><label>Drive link</label><input type="url" id="s-drive" /></div>
      <div class="field"><label>Comments</label><textarea id="s-comments"></textarea></div>
      <button type="submit" class="btn btn-gradient">Submit work</button>
    </form>`;

  document.getElementById('submit-work-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      await CCApi.submitWork({
        applicationId: Number(applicationId),
        instagramReelLink: document.getElementById('s-ig').value.trim(),
        youtubeLink: document.getElementById('s-yt').value.trim(),
        tiktokLink: document.getElementById('s-tt').value.trim(),
        driveLink: document.getElementById('s-drive').value.trim(),
        comments: document.getElementById('s-comments').value.trim(),
      });
      CCUI.toast('Work submitted for review!', 'success');
      document.getElementById('submissions-list').innerHTML = `<div class="empty-state"><h3>Submitted</h3><p>The brand will review your work shortly.</p></div>`;
    } catch (err) { CCUI.toast(err.message || 'Could not submit work.', 'error'); }
  });
}

// ---- Earnings ----
async function loadEarnings() {
  const el = document.getElementById('earnings-list');
  el.innerHTML = `<div class="skeleton" style="height:70px;margin-bottom:10px;"></div>`.repeat(3);
  try {
    const data = await CCApi.myPayments({ page: 0, size: 30 });
    if (!data.content.length) {
      el.innerHTML = `<div class="empty-state"><h3>No payments yet</h3><p>Completed campaigns will show up here once payment is released.</p></div>`;
      return;
    }
    el.innerHTML = data.content.map((p) => `
      <div class="list-row">
        <div>
          <div class="lr-title">${CCUI.escapeHtml(p.campaignTitle)} — ${p.invoiceNumber || 'No invoice yet'}</div>
          <div class="lr-meta">${p.companyName} · <span class="badge ${CCUI.badgeClassFor(p.status)}">${p.status}</span></div>
        </div>
        <div class="cc-budget" style="font-size:1.2rem;">${CCUI.formatCurrency(p.amount)}</div>
      </div>`).join('');
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><h3>Couldn't load payments</h3><p>${CCUI.escapeHtml(err.message)}</p></div>`;
  }
}

// ---- Profile ----
async function loadProfile() {
  try {
    const p = await CCApi.myCreatorProfile();
    document.getElementById('p-pic').value = p.profilePictureUrl || '';
    document.getElementById('p-cover').value = p.coverPhotoUrl || '';
    document.getElementById('p-bio').value = p.bio || '';
    document.getElementById('p-location').value = p.location || '';
    document.getElementById('p-category').value = p.category || '';
    document.getElementById('p-followers').value = p.followerCount || '';
    document.getElementById('p-engagement').value = p.engagementRate || '';
    document.getElementById('p-instagram').value = p.instagramHandle || '';
    document.getElementById('p-youtube').value = p.youtubeHandle || '';
    document.getElementById('p-portfolio').value = p.portfolioUrl || '';
    document.getElementById('p-skills').value = p.skills || '';
  } catch (err) {
    CCUI.toast(err.message || 'Could not load profile.', 'error');
  }
}

document.getElementById('profile-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = document.getElementById('profile-save');
  btn.disabled = true; btn.textContent = 'Saving…';
  try {
    await CCApi.updateCreatorProfile({
      profilePictureUrl: document.getElementById('p-pic').value.trim(),
      coverPhotoUrl: document.getElementById('p-cover').value.trim(),
      bio: document.getElementById('p-bio').value.trim(),
      location: document.getElementById('p-location').value.trim(),
      category: document.getElementById('p-category').value.trim(),
      followerCount: Number(document.getElementById('p-followers').value) || 0,
      engagementRate: Number(document.getElementById('p-engagement').value) || 0,
      instagramHandle: document.getElementById('p-instagram').value.trim(),
      youtubeHandle: document.getElementById('p-youtube').value.trim(),
      portfolioUrl: document.getElementById('p-portfolio').value.trim(),
      skills: document.getElementById('p-skills').value.trim(),
    });
    CCUI.toast('Profile updated!', 'success');
  } catch (err) {
    CCUI.toast(err.message || 'Could not save profile.', 'error');
  } finally {
    btn.disabled = false; btn.textContent = 'Save profile';
  }
});

loadOverview();
