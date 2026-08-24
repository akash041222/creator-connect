if (
    window.location.hostname === 'localhost' ||
    window.location.hostname === '127.0.0.1'
  ) {
    window.CC_API_BASE_URL = 'http://localhost:8080/api';
  } else {
    window.CC_API_BASE_URL = 'https://creator-connect-api.onrender.com/api';
  }