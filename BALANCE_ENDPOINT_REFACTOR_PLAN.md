[x] Confirm and document API contract for GET /balance with required query parameter accountId (type Long, positive, mandatory).
[x] Define expected response shape for balance endpoint (e.g., account_id and balance), aligning JSON naming conventions with existing API responses.
[x] Add/extend OpenAPI documentation for /balance, including success and error responses, examples, and parameter description.
[x] Review and reuse existing global error handling behavior for invalid query parameter type/value and account not found scenarios.
[x] Introduce a dedicated application use case/service method to retrieve account balance while preserving current layering and responsibilities.
[x] Refactor shared account-id validation into a reusable path (if needed) to avoid duplicated validation logic across account/transaction/balance flows.
[x] Extend domain repository contract to support balance retrieval by account id using transaction aggregation.
[x] Implement persistence query in JDBC repository to read database transactions and return aggregated balance using SQL SUM with COALESCE for no-transaction cases.
[x] Ensure account existence is validated before returning balance so non-existent accounts continue to produce consistent 404 behavior.
[x] Decide and document balance behavior for existing account with no transactions (expected zero balance) and encode that in service/repository logic.
[x] Add controller-level integration tests for /balance covering: success, invalid accountId format, invalid accountId value (<=0), missing accountId, and account not found.
[x] Add application/service unit tests for balance computation flow, including account validation, repository interaction, and zero-balance edge case.
[x] Add repository-level integration test (or persistence-focused test) validating SQL aggregation correctness with mixed debit/credit transactions.
[x] Add end-to-end test scenario creating account + transactions and asserting /balance returns exact aggregated value.
[x] Add end-to-end test scenario asserting /balance returns zero for account with no transactions.
[ ] Run full automated test suite and confirm no regressions in existing /accounts and /transactions behavior.
[x] Perform final architecture conformance review to ensure solution follows current controller -> application -> domain repository -> JDBC pattern.
[x] Update README/API usage section with /balance endpoint request/response examples and error semantics.