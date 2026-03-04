# Conference Mode - Java Version Quiz

A real-time, multiplayer quiz game for conferences and presentations.

## Quick Start

### 1. Generate the Quiz Data

From the project root:

```bash
python game/generate_quiz.py --base-url "http://localhost:3000/" --conference
```

This will create:
- `game/conference/public/code.json` - Quiz questions
- `game/conference/public/descriptions.json` - Feature metadata
- `game/conference/server.js` - Node.js server
- `game/conference/package.json` - Dependencies

### 2. Install Dependencies

```bash
cd game/conference
npm install
```

### 3. Start the Server

```bash
ADMIN_SECRET="your-secret-password" npm start
```

The server will start on `http://localhost:3000`

### URLs

- **Player**: http://localhost:3000 (or share the session ID)
- **Presenter**: http://localhost:3000/presenter
- **WebSocket**: ws://localhost:3000/ws

## How It Works

### Player Flow

1. Player joins with a Session ID
2. Gets a unique anonymous name (e.g., "HappyCleverPanda")
3. Waits for presenter to start a question
4. When question starts, player has X seconds to answer
5. Answer is submitted via WebSocket and scored
6. Player sees live leaderboard updating

### Presenter Flow

1. Login with `ADMIN_SECRET`
2. Create a new session (generates unique Session ID)
3. Share Session ID with players
4. Select a question and set timer duration (default 30s)
5. Click "Start Question" to send to all players
6. View answer distribution and leaderboard in real-time
7. Click "Stop Question" when done
8. Repeat or delete session

## Scoring

- **Fastest correct answer**: 3 points
- **2nd fastest correct**: 2 points
- **Other correct answers**: 1 point
- Ties broken with random shuffle

## Features

✓ **Anonymous player names** - Generated from adjectives + animals (e.g., "BravePanda", "CleverQuickFox")
✓ **Server-authoritative timer** - No client-side cheating
✓ **Real-time WebSocket sync** - Instant updates for all players
✓ **SQLite persistence** - Crash protection with in-memory database
✓ **Live leaderboard** - Updated as answers come in
✓ **Admin controls** - Full presenter dashboard
✓ **Privacy-first** - No PII, easy data deletion
✓ **Responsive design** - Works on desktop, tablet, mobile

## Environment Variables

- `ADMIN_SECRET` (required) - Password for presenter login
- `PORT` (optional) - Server port (default: 3000)

## Architecture

```
conference/
├── server.js              # Express + WebSocket server
├── package.json
├── public/
│   ├── player.html        # Player UI
│   ├── presenter.html     # Presenter control panel
│   ├── code.json          # Quiz entries (auto-generated)
│   └── descriptions.json  # Feature metadata (auto-generated)
```

### WebSocket Messages

**Player → Server**
- `{ type: 'join', sessionId, uuid }` - Join a session
- `{ type: 'answer', sessionId, answer }` - Submit answer

**Server → Players**
- `{ type: 'question_started', questionId, durationSeconds }` - New question
- `{ type: 'question_stopped' }` - Question closed
- `{ type: 'player_answered', uuid }` - Another player answered (for real-time leaderboard)

### REST Endpoints

**Admin**
- `POST /admin/auth` - Authenticate with ADMIN_SECRET
- `POST /admin/session/create` - Create new session
- `POST /admin/session/delete` - Delete session and disconnect players
- `POST /admin/start_question` - Start a question with timer
- `POST /admin/stop_question` - Stop accepting answers
- `POST /admin/stats` - Get answer distribution and leaderboard

**Players**
- `POST /player/join` - Join with a session ID
- `POST /player/leave` - Leave and delete player data
- `GET /leaderboard/:sessionId` - Get top 10 leaderboard

## Database Schema

All data is stored in-memory (lost when server restarts). For persistence, modify `initDb()` in server.js to use a file-based database:

```javascript
let db = new sqlite3.Database('quiz.db', (err) => { ... });
```

**Tables**
- `sessions` - Active game sessions
- `players` - Player records with scores
- `answers` - Individual answer submissions with timestamps

## Customization

### Change Name Generation

Edit the `ADJECTIVES` and `ANIMALS` arrays in server.js:

```javascript
const ADJECTIVES = ['Happy', 'Silly', ...];
const ANIMALS = ['Panda', 'Eagle', ...];
```

### Change Scoring Rules

Edit the scoring logic when answers are processed. Currently in `/admin/stats`:
- Fastest correct: 3pts
- 2nd fastest: 2pts
- Other correct: 1pt

### Change Default Timer

In presenter.html, change the default question duration:

```html
<input type="number" id="questionDuration" value="30" ...>
```

## Troubleshooting

**"ADMIN_SECRET environment variable must be set"**
- Set the variable: `export ADMIN_SECRET="my-secret"` then `npm start`

**Players can't connect**
- Check firewall/network settings
- Verify WebSocket URL matches server address
- Check browser console for connection errors

**Timer doesn't sync**
- Server uses `new Date()` as source of truth
- Client timer updates every 100ms
- Small variations are normal

**Names are duplicated**
- Name uniqueness is per-session only
- Different sessions can reuse names
- If you need global uniqueness, modify `generateUniqueName()`

## Future Enhancements

- [ ] Persistent database (SQLite file)
- [ ] Multi-session support
- [ ] Question categories/difficulty
- [ ] Streak tracking
- [ ] Export results as CSV
- [ ] Custom branding/theming
- [ ] Mobile app for presenters
- [ ] Recording and replay

## License

Same as parent project
