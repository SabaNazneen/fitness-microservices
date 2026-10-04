export const authConfig = {
  clientId: 'oath2-pkce-client',

  authorizationEndpoint:
    'http://127.0.0.1:8181/realms/fitness-app/protocol/openid-connect/auth',

  tokenEndpoint:
    'http://127.0.0.1:8181/realms/fitness-app/protocol/openid-connect/token',

  redirectUri: 'http://localhost:5174/',

  scope: 'openid profile email offline_access',

  onRefreshTokenExpire: (event) =>
    event.logIn(),
};