---
name: springboot-uniapp-team
description: Standardize delivery for Spring Boot + uni-app projects with consistent API design, frontend-backend integration, accessibility-minded UX, and release-quality validation. Use when implementing or changing backend APIs, uni-app pages, shared contracts, error handling, or cross-end debugging in repositories with backend/ and frontend/ modules.
---

# Enforce Project Workflow

Follow this order for any non-trivial change:

1. Confirm scope and affected modules (`backend`, `frontend`, or both).
2. Define or update API contract first. Use [backend-api-contract](references/backend-api-contract.md).
3. Implement backend endpoint and validation.
4. Implement frontend request, state handling, and page interaction.
5. Run local verification. Use [integration-checklist](references/integration-checklist.md).
6. Summarize results with touched modules, verification commands, and residual risks.

# Keep Backend Consistent

Apply these rules:

- Place controller classes under `backend/src/main/java/com/example/demo/controller`.
- Keep route prefixes under `/api/*`.
- Use request DTO validation for user input.
- Return stable JSON shape for success and error responses.
- Mirror package structure in `backend/src/test/java` and add/update `*Tests` classes.

When adding or modifying endpoints, record contract updates in [backend-api-contract](references/backend-api-contract.md).

# Keep Frontend Consistent

Apply these rules:

- Add or update pages under `frontend/pages/*` using existing uni-app style.
- Keep reusable static resources in `frontend/static`.
- Centralize request logic and avoid inline duplicated request snippets across pages.
- Handle loading, empty, error, and retry states explicitly for each page flow.

For accessibility-sensitive features, prefer concise voice prompts, high-contrast visuals, and clear button labels.

# Coordinate Backend and Frontend Changes

For every API consumed by uni-app pages:

- Align field names and enum values exactly.
- Align time format and timezone assumptions.
- Align error code and error message mapping.
- Align pagination and filter semantics.

If contract drift appears, fix contract first, then code.

# Validate Before Completion

Run the relevant checks:

- `cd backend && mvn test`
- `cd backend && mvn clean package` when backend behavior changed
- `cd frontend && npm run dev:h5` smoke check for UI behavior when frontend changed

Use [integration-checklist](references/integration-checklist.md) to confirm readiness.

# Output Format for Task Completion

When finishing work, report:

- What changed and why
- Which module(s) were touched
- Commands executed and key results
- Manual smoke-test note for frontend changes
- Known limitations or follow-up items
