# Player Communication

A pure Java implementation of a two-player message-passing system.
One player initiates, the other replies — for exactly 10 rounds, then both stop cleanly.

---

## How It Works

- Each `Player` runs on its own thread (or process)
- On every received message: **append own counter → reply**
- The initiator owns the stop condition — after 10 rounds, signals shutdown

---

## Two Modes

| Mode | Technology | Requirement |
|---|---|---|
| Same JVM | `BlockingQueue` + Threads | Requirement 5 |
| Separate JVMs | TCP Sockets | Requirement 7 |

---

## Key Design Decisions

- **`BlockingQueue`** — thread-safe message hand-off, no manual `synchronized` needed
- **`AtomicInteger`** — race-free counter increment without locking
- **`SocketPlayer` (abstract base)** — server and client share the same message loop via Template Method pattern
- **Graceful shutdown** — poison-pill `__STOP__` for threads · stop-token `__DONE__` over socket

---

## Build

```bash
mvn clean package
```

---

## Run

**Same process** — both players in one JVM:
```bash
java -jar target/player-same-process.jar
```

**Multi process** — each player in its own JVM:
```bash
# Terminal 1 — start server first
java -jar target/player-server.jar

# Terminal 2 — then start client
java -jar target/player-client.jar
```

---

## Test

```bash
mvn clean test
```

- 23 unit tests across 4 test classes
- Covers positive and negative scenarios for all classes

---