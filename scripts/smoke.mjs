import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';

// Run only against an isolated/staging deployment: this creates one test account.
const base = (process.argv[2] || 'http://127.0.0.1:18080').replace(/\/$/, '');
async function call(path, { method = 'GET', body, token } = {}) {
  const response = await fetch(base + path, {
    method,
    headers: { ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
    signal: AbortSignal.timeout(15000),
  });
  return response;
}
const health = await call('/healthz');
assert.equal(health.status, 200);
assert.ok(health.headers.get('content-security-policy')?.includes("frame-ancestors 'none'"));
assert.equal((await call('/dashboard')).status, 200);
assert.equal((await call('/assets/missing.js')).status, 404);
assert.equal((await call('/api/auth/me')).status, 401);
assert.equal((await call('/api/auth/me', { token: 'invalid' })).status, 401);
const email = `smoke-${randomUUID()}@example.com`;
const password = `Test-${randomUUID()}!`;
const registration = await call('/api/auth/register', { method: 'POST', body: { fullName: 'Smoke Test', email, password } });
assert.equal(registration.status, 200);
const { token } = await registration.json();
assert.ok(token);
const me = await call('/api/auth/me', { token });
assert.equal(me.status, 200);
assert.equal((await me.json()).email, email);
assert.equal((await call('/api/auth/login', { method: 'POST', body: { email, password } })).status, 200);
for (const path of ['/api/question-banks', '/api/interviews/history']) {
  const response = await call(path, { token });
  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), []);
}
const invalid = await call('/api/interviews/start', { method: 'POST', token,
  body: { topic: 'Java', targetRole: 'Developer', experienceLevel: 'INTERMEDIATE', totalQuestions: 21 } });
assert.equal(invalid.status, 400);
console.log('PASS: frontend routing/security headers, protected APIs, registration/login, PostgreSQL persistence, and request validation.');
