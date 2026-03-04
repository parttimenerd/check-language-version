# Load Testing Quick Start

## One-Command Testing

```bash
# Terminal 1: Start the server
cd game/conference
npm install
npm start &  # Builds webpack bundles and starts server on :3000

# Terminal 2: Run load tests
cd game/conference

# Quick test (10 players, 5 questions)
npm run test:load

# Medium conference (50 players, 8 questions)
npm run test:load:medium

# Large conference (200 players, 10 questions)
npm run test:load:large

# Stress test (500 players)
npm run test:load:stress
```

## Custom Testing

```bash
# X players, Y questions
node load-test.js --players X --questions Y

# With verbose output
node load-test.js --players 50 --questions 5 --verbose

# Remote server
node load-test.js --host http://your-server.com --players 100
```

## What Gets Tested

✅ Multiple concurrent player joins  
✅ WebSocket connection stability  
✅ Real-time question broadcasting  
✅ Answer submission and validation  
✅ Latency measurements  
✅ Error tracking and reporting  
✅ Server throughput under load  

## Key Metrics to Watch

| Metric | Good | Warning | Critical |
|--------|------|---------|----------|
| Join rate | 100% | 95-99% | < 95% |
| Answer rate | 98%+ | 90-97% | < 90% |
| Avg latency | < 50ms | 50-200ms | > 200ms |
| Errors | 0 | 1-5 | > 5 |

## Troubleshooting

**Server won't start?**  
```bash
export ADMIN_SECRET=test
npm start
```

**Port already in use?**  
```bash
lsof -i :3000  # Find process
kill -9 <PID>   # Kill it
```

**Load test hangs?**  
- Check server logs: `npm start` output
- Verify ADMIN_SECRET is set
- Try smaller load: `--players 5`

---

📊 See [LOAD-TEST.md](LOAD-TEST.md) for detailed documentation.
