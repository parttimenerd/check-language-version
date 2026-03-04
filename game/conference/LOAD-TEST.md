# Conference Mode Load Testing Guide

## Overview

The load tester (`load-test.js`) simulates multiple concurrent players joining a session and answering questions. It measures:

- **Latency**: Response times for joins, answers, and other operations
- **Throughput**: Players and answers per second
- **Reliability**: Success rates and error counts
- **WebSocket stability**: Connection health and message delivery

## Running Load Tests

### Quick Start (Default: 10 players, 5 questions)

```bash
# Build and start the server
npm run build && ADMIN_SECRET=test npm start &

# In another terminal, run the load test
npm run test:load
```

### Customized Load Tests

```bash
# 50 players, 10 questions
node load-test.js --players 50 --questions 10

# 100 players with verbose logging
node load-test.js --players 100 --verbose

# Slow network simulation (50ms join delay)
node load-test.js --players 30 --delay 50

# Custom server
node load-test.js --host http://192.168.1.100:3000 --players 50
```

### Available Options

| Option | Description | Default |
|--------|-------------|---------|
| `--players <n>` | Number of concurrent player instances | 10 |
| `--questions <n>` | Number of questions to run through | 5 |
| `--delay <ms>` | Delay between player joins (ms) | 50 |
| `--host <url>` | Server URL | http://localhost:3000 |
| `--verbose` | Enable detailed logging | false |

## Test Scenarios

### Scenario 1: Small Local Test
```bash
node load-test.js --players 10 --questions 3
```
**Expected Results:**
- Latencies: 10-50ms per operation
- Join rate: 100%
- Answer rate: 95-100%

### Scenario 2: Medium Conference
```bash
node load-test.js --players 50 --questions 5 --delay 30
```
**Expected Results:**
- Latencies: 20-100ms
- Join rate: 98-100%
- Answer rate: 90-98%
- Throughput: 20-30 players/sec

### Scenario 3: Large Conference
```bash
node load-test.js --players 200 --questions 8 --delay 20
```
**Expected Results:**
- Latencies: 50-500ms
- Join rate: 95-99%
- Answer rate: 85-95%
- Throughput: 30-50 players/sec

### Scenario 4: Stress Test
```bash
node load-test.js --players 500 --questions 3 --delay 10
```
**Tests server limits under extreme load**

## Understanding Results

### Performance Metrics Section

```
Duration: 245.3s
Total latency samples: 12450
Avg latency: 45.23ms
```

- **Duration**: Total test execution time
- **Latency samples**: Total number of operations measured
- **Avg latency**: Average response time across all operations

### Latency by Operation

```
join: avg=42.15ms min=12ms max=156ms (n=50)
answer: avg=48.32ms min=15ms max=267ms (n=250)
create_session: avg=8.42ms min=5ms max=18ms (n=1)
load_quiz: avg=3.21ms min=2ms max=7ms (n=1)
```

**Healthy ranges:**
- `load_quiz`: < 50ms (static file)
- `create_session`: < 20ms (database write)
- `join`: 20-100ms (network + database)
- `answer`: 30-150ms (validation + broadcast)

### Player Activity

```
Joined: 50/50
Failed: 0/50
Answers submitted: 245
Errors: 0

Join rate: 100%
Answer rate: 98% (245/250)
```

**Indicators:**
- **Join rate < 90%**: Network timeouts or server overload
- **Failed joins > 5%**: Check server logs for crashes
- **Answer rate < 85%**: Players not receiving questions or WebSocket issues
- **Errors > 0**: Check for unhandled exceptions

### Throughput

```
Players/second: 0.20
Answers/second: 1.00
```

**Expectations:**
- Small servers (1 CPU): 1-2 players/sec
- Standard servers (4 CPU): 5-10 players/sec
- Large servers (16 CPU): 20-50 players/sec

### Overall Health

```
Overall Health: ✅ PASS
```

- **✅ PASS**: Zero failures, ready for production
- **⚠️ DEGRADED**: Some errors detected, review logs

## Troubleshooting

### Connection Timeouts

```
Error: WebSocket connection timeout
```

**Solutions:**
1. Check server is running: `curl http://localhost:3000/presenter.html`
2. Reduce player count: `--players 10`
3. Increase delay: `--delay 100`
4. Check firewall/network: `telnet localhost 3000`

### Join Failures

```
Failed to join player 42: ECONNREFUSED
```

**Solutions:**
1. Restart server: `npm start`
2. Set ADMIN_SECRET: `export ADMIN_SECRET=test`
3. Check port conflicts: `lsof -i :3000`

### Low Answer Rates

```
Answer rate: 45% (225/500)
```

**Solutions:**
1. Check WebSocket logs in server
2. Verify question duration is long enough: `--questions 10` with 30s each
3. Check browser console for JS errors
4. Monitor server CPU/memory: `top`, `free -h`

### High Latency

```
answer: avg=512ms min=234ms max=1200ms
```

**Solutions:**
1. Reduce load: `--players 50`
2. Reduce join rate: `--delay 200`
3. Check server CPU: `top`
4. Check network: `ping -c 10 localhost`
5. Increase server resources (RAM, CPU)

## Performance Tuning

### For Your Server

| Metric | Improve By | Impact |
|--------|-----------|--------|
| Join timeouts | Add database connection pooling | +50% throughput |
| High memory use | Implement session cleanup | -75% memory |
| Question lag | Cache quiz data in memory | -40% latency |
| WebSocket errors | Increase server limits (ulimit) | +100% connections |

### Quick Wins

1. **Enable gzip compression** in Express
2. **Reduce database queries** per answer submission
3. **Cache quiz data** in memory at server startup
4. **Increase Node.js max listeners**: `--max-old-space-size=4096`

## Integration with CI/CD

### Run in GitHub Actions

```yaml
- name: Load Test
  run: |
    npm run build
    npm run test:load -- --players 50 --questions 5
```

### Exit Codes

- `0`: Test passed (health = PASS)
- `1`: Test failed (network/server errors)

## Advanced: Custom Load Profiles

To test specific scenarios, modify `load-test.js`:

1. Change thinking time distribution (line 224-225):
   ```javascript
   const thinkTime = Math.random() * 15000 + 2000; // 2-17 seconds
   ```

2. Add answer distribution bias (skew towards correct answers):
   ```javascript
   const correctAnswer = this.quizData.entries[this.currentQuestion].correct;
   const randomAnswer = Math.random() < 0.8 ? correctAnswer : Math.random() * 29 - 3;
   ```

3. Simulate disconnections:
   ```javascript
   if (Math.random() < 0.05) {
     player.ws.close(); // 5% disconnect rate
   }
   ```

## Performance Baseline

Use these results as reference points:

### Baseline (10 players, 3 questions, localhost)

```
Duration: 120.5s
Avg latency: 25.34ms
Join rate: 100%
Answer rate: 100%
Players/sec: 0.08
Health: ✅ PASS
```

### Degraded Performance Signs

- Latencies exceed 200ms consistently
- Join rate below 95%
- Answer rate below 90%
- WebSocket errors > 5%
- Server CPU sustained > 80%
- Memory usage > available RAM

## Further Reading

- [Node.js Performance Best Practices](https://nodejs.org/en/docs/guides/nodejs-performance-best-practices/)
- [WebSocket Performance Tuning](https://www.nginx.com/blog/websocket-nginx/)
- [Load Testing with Artillery](https://artillery.io/) (alternative tool)

---

**Last Updated**: February 2026  
**Version**: 1.0  
**Tested on**: Node.js 14+, macOS/Linux
