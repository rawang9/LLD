# Local Redis for atomic rate-limit Lua (token bucket next).
# Official image — we only pin the runtime; Lua scripts are mounted at start.
FROM redis:7-alpine

EXPOSE 6379

# protected-mode no: local lab on a Podman pod published to 127.0.0.1 only.
CMD ["redis-server", "--port", "6379", "--protected-mode", "no"]
