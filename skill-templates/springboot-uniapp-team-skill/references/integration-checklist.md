# Integration Checklist

Run this checklist before closing a task.

## Scope

- [ ] Change scope is clear (`backend`, `frontend`, or both).
- [ ] API contract was updated when request/response changed.

## Backend

- [ ] Input validation exists for new or changed request fields.
- [ ] Error handling returns stable structure and meaningful message.
- [ ] `mvn test` passed in `backend/`.
- [ ] `mvn clean package` passed for backend behavior changes.

## Frontend

- [ ] Request payload and response parsing match backend contract.
- [ ] Loading, empty, error, and retry states are handled.
- [ ] Manual smoke test completed in `npm run dev:h5` or IDE equivalent.
- [ ] UI text and interactions remain accessibility-friendly.

## Cross-End Consistency

- [ ] Field names and enum values match exactly.
- [ ] Time and timezone assumptions are consistent.
- [ ] Error codes/messages map to clear frontend actions.

## Delivery Note

- [ ] Final summary includes changed modules and verification commands.
- [ ] Known risks and follow-ups are listed.
