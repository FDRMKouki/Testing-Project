```mermaid
graph TD
    A[Start] --> B{Is it working?}
    B -->|Yes| C[Great!]
    B -->|Partially Yes| E[dude fix it now]
    B -->|No| D[Fix it]
    D --> B
    E --> B
    C --> A

```
