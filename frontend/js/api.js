/* ==========================================================================
   CreatorConnect — API client
   Thin fetch() wrapper around the Spring Boot REST API. Handles auth headers,
   JSON parsing, and a consistent error shape the UI can render.
   ========================================================================== */

const API_BASE_URL = window.CC_API_BASE_URL || 'http://localhost:8080/api';

const CCApi = (() => {
  function getToken() {
    return localStorage.getItem('cc_access_token');
  }

  function getSession() {
    const raw = localStorage.getItem('cc_session');
    return raw ? JSON.parse(raw) : null;
  }

  function setSession(auth) {
    localStorage.setItem('cc_access_token', auth.accessToken);
    localStorage.setItem('cc_refresh_token', auth.refreshToken);
    localStorage.setItem('cc_session', JSON.stringify({
      userId: auth.userId, fullName: auth.fullName, email: auth.email, role: auth.role,
    }));
  }

  function clearSession() {
    localStorage.removeItem('cc_access_token');
    localStorage.removeItem('cc_refresh_token');
    localStorage.removeItem('cc_session');
  }

  function isLoggedIn() {
    return !!getToken();
  }

  function requireRole(role, redirectTo = 'login.html') {
    const session = getSession();
    if (!session || session.role !== role) {
      window.location.href = redirectTo;
    }
    return session;
  }

  async function request(path, { method = 'GET', body, params, auth = true } = {}) {
    let url = `${API_BASE_URL}${path}`;
    if (params) {
      const qs = new URLSearchParams(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== '')
      ).toString();
      if (qs) url += `?${qs}`;
    }

    const headers = { 'Content-Type': 'application/json' };
    if (auth && getToken()) headers.Authorization = `Bearer ${getToken()}`;

    let res;
    try {
      res = await fetch(url, { method, headers, body: body ? JSON.stringify(body) : undefined });
    } catch (networkErr) {
      throw { status: 0, message: 'Could not reach the CreatorConnect API. Is the backend running?' };
    }

    let data = null;
    const text = await res.text();
    if (text) {
      try { data = JSON.parse(text); } catch { data = text; }
    }

    if (!res.ok) {
      if (res.status === 401) clearSession();
      const message = (data && data.message) ? data.message : `Request failed (${res.status})`;
      throw { status: res.status, message, validationErrors: data ? data.validationErrors : null };
    }
    return data;
  }

  return {
    // auth
    register: (payload) => request('/auth/register', { method: 'POST', body: payload, auth: false }),
    login: (payload) => request('/auth/login', { method: 'POST', body: payload, auth: false }),
    forgotPassword: (payload) => request('/auth/forgot-password', { method: 'POST', body: payload, auth: false }),
    resetPassword: (payload) => request('/auth/reset-password', { method: 'POST', body: payload, auth: false }),

    // campaigns
    searchCampaigns: (params) => request('/campaigns', { params, auth: false }),
    trendingCampaigns: (params) => request('/campaigns/trending', { params, auth: false }),
    getCampaign: (id) => request(`/campaigns/${id}`, { auth: false }),
    createCampaign: (payload) => request('/campaigns', { method: 'POST', body: payload }),
    updateCampaign: (id, payload) => request(`/campaigns/${id}`, { method: 'PUT', body: payload }),
    updateCampaignStatus: (id, status) => request(`/campaigns/${id}/status`, { method: 'PATCH', params: { status } }),
    deleteCampaign: (id) => request(`/campaigns/${id}`, { method: 'DELETE' }),
    myCampaigns: (params) => request('/campaigns/mine', { params }),

    // applications
    apply: (payload) => request('/applications', { method: 'POST', body: payload }),
    reviewApplication: (id, payload) => request(`/applications/${id}/review`, { method: 'PATCH', body: payload }),
    withdrawApplication: (id) => request(`/applications/${id}/withdraw`, { method: 'PATCH' }),
    completeApplication: (id) => request(`/applications/${id}/complete`, { method: 'PATCH' }),
    myApplications: (params) => request('/applications/mine', { params }),
    applicationsByCampaign: (campaignId, params) => request(`/applications/campaign/${campaignId}`, { params }),
    applicationsByCompany: (params) => request('/applications/company', { params }),

    // creator profile
    myCreatorProfile: () => request('/creators/me'),
    updateCreatorProfile: (payload) => request('/creators/me', { method: 'PUT', body: payload }),
    publicCreatorProfile: (id) => request(`/creators/public/${id}`, { auth: false }),
    searchCreators: (params) => request('/creators/public/search', { params, auth: false }),
    creatorLeaderboard: (params) => request('/creators/public/leaderboard', { params, auth: false }),

    // company profile
    myCompanyProfile: () => request('/companies/me'),
    updateCompanyProfile: (payload) => request('/companies/me', { method: 'PUT', body: payload }),
    publicCompanyProfile: (id) => request(`/companies/public/${id}`, { auth: false }),

    // submissions
    submitWork: (payload) => request('/submissions', { method: 'POST', body: payload }),
    reviewSubmission: (id, payload) => request(`/submissions/${id}/review`, { method: 'PATCH', body: payload }),
    submissionByApplication: (applicationId) => request(`/submissions/application/${applicationId}`),

    // payments
    initiatePayment: (applicationId, amount) => request('/payments', { method: 'POST', params: { applicationId, amount } }),
    updatePaymentStatus: (id, payload) => request(`/payments/${id}/status`, { method: 'PATCH', body: payload }),
    myPayments: (params) => request('/payments/mine', { params }),
    companyPayments: (params) => request('/payments/company', { params }),

    // notifications
    notifications: (params) => request('/notifications', { params }),
    unreadCount: () => request('/notifications/unread-count'),
    markNotificationRead: (id) => request(`/notifications/${id}/read`, { method: 'PATCH' }),
    markAllNotificationsRead: () => request('/notifications/read-all', { method: 'PATCH' }),

    // dashboards
    creatorDashboard: () => request('/dashboard/creator'),
    companyDashboard: () => request('/dashboard/company'),
    adminDashboard: () => request('/dashboard/admin'),

    // admin
    adminListUsers: (params) => request('/admin/users', { params }),
    adminSuspendUser: (id) => request(`/admin/users/${id}/suspend`, { method: 'PATCH' }),
    adminReactivateUser: (id) => request(`/admin/users/${id}/reactivate`, { method: 'PATCH' }),
    adminDeleteUser: (id) => request(`/admin/users/${id}`, { method: 'DELETE' }),
    adminVerifyCompany: (id) => request(`/admin/companies/${id}/verify`, { method: 'PATCH' }),
    adminVerifyCreator: (id) => request(`/admin/creators/${id}/verify`, { method: 'PATCH' }),
    adminDeleteCampaign: (id) => request(`/admin/campaigns/${id}`, { method: 'DELETE' }),

    // session helpers
    getToken, getSession, setSession, clearSession, isLoggedIn, requireRole,
  };
})();
