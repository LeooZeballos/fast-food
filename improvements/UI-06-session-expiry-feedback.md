# UI-06 — Session Expiry Feedback

**Priority:** MEDIUM
**Domain:** Designer / UX / Security
**Effort:** Small (1h)

---

## Summary

`AuthContext` checks authentication on mount but not during long sessions. When the Spring Security session expires (typically 30 min default), the next API call returns HTTP 401 and the app silently breaks — queries fail with no user-facing explanation.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/api.ts` | Add 401 response interceptor to redirect to login |
| `frontend/src/AuthContext.tsx` | Expose `logout()` for the interceptor to call |
| `frontend/src/locales/en.json` + `es.json` | Add `auth.sessionExpired` i18n key |

---

## Implementation Steps

### Step 1 — Add i18n Keys

`en.json` under `"login"`:
```json
"sessionExpired": "Your session has expired. Please sign in again."
```

`es.json`:
```json
"sessionExpired": "Tu sesión ha expirado. Por favor, inicia sesión de nuevo."
```

### Step 2 — Create a Session Expiry Event

Use a custom event or a simple module-level callback to decouple `api.ts` from React:

```typescript
// frontend/src/api.ts
let onSessionExpired: (() => void) | null = null;

export const setSessionExpiredHandler = (handler: () => void) => {
  onSessionExpired = handler;
};
```

### Step 3 — Add 401 Response Interceptor

In the response interceptor already in `api.ts`:

```typescript
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && onSessionExpired) {
      onSessionExpired();
    }
    // ... existing error handling
    return Promise.reject(error);
  }
);
```

### Step 4 — Register Handler in AuthContext

```tsx
// In AuthContext.tsx, inside the Provider:
useEffect(() => {
  setSessionExpiredHandler(() => {
    toast.error(t('login.sessionExpired'));
    setIsAuthenticated(false);
  });
}, []);
```

### Step 5 — Test

Manually test by:
1. Log in
2. Kill the Spring Boot server session (or wait for timeout)
3. Click any action → should see the toast and redirect to login

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] When any API call returns 401 (session expired), the user sees a toast with the `sessionExpired` message
- [ ] The user is returned to the login screen
- [ ] The login form is pre-cleared (not pre-filled with stale credentials)
- [ ] Message is translated in both languages
- [ ] `cd frontend && pnpm test` passes
