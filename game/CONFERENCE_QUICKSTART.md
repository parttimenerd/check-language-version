# Java Version Quiz - Conference Mode

## Quick Setup

### 1. Generate Conference Setup

```bash
python generate_quiz.py --base-url "http://localhost:3000/" --conference
```

### 2. Install & Run

```bash
cd conference
npm install
ADMIN_SECRET="your-secret" npm start
```

Then:
- **Players**: Open http://localhost:3000 and enter the Session ID
- **Presenter**: Open http://localhost:3000/presenter and login with your secret

## What You Get

✓ Real-time multiplayer quiz with 100+ question from Java 1.0 to Java 25
✓ Anonymous player names (e.g., "HappyCleverPanda")  
✓ Server-authoritative timer (prevents cheating)
✓ Live leaderboard and answer distribution charts
✓ Presenter control panel with full statistics
✓ WebSocket-based real-time synchronization
✓ Session-scoped data with automatic cleanup

## Features

- **Unique Names**: Each player gets a random name combining 1-3 adjectives + animal (e.g., "BraveQuickEagle")
- **Scoring**: Fastest correct = 3pts, 2nd = 2pts, other correct = 1pt
- **Timer**: Server enforces answer deadline (default 30s, configurable)
- **Privacy**: Anonymous-only display names, no PII stored
- **Responsive**: Works on desktop, tablet, mobile

## How to Use

### Presenter
1. Login with ADMIN_SECRET
2. Click "New Session" to create a game
3. Share the Session ID with audience
4. Select a question and click "Start Question"
5. View answer distribution and leaderboard in real-time
6. Click "Stop Question" when done

### Players
1. Join with the Session ID provided by presenter
2. Wait for each question
3. When question appears, you have X seconds to answer
4. See your score on the live leaderboard
5. Compete to get the highest score

## Directory Structure

```
conference/
├── server.js              # Node.js Express + WebSocket server
├── package.json
├── README.md
└── public/
    ├── player.html        # Player interface
    ├── presenter.html     # Presenter control panel
    ├── code.json          # Quiz questions (auto-generated)
    └── descriptions.json  # Feature metadata (auto-generated)
```

## Environment Variables

- `ADMIN_SECRET` (required) - Presenter login password
- `PORT` (optional) - Server port (default: 3000)

## URL Parameters

Both player and presenter pages accept these query parameters:

| Parameter | Values | Example |
|---|---|---|
| `theme` | `light`, `dark` | `?theme=dark` |
| `secret` | your admin password | `?secret=mypass` |

**Examples:**

- Auto-login presenter in dark mode:  
  `http://localhost:3000/presenter?secret=mypass&theme=dark`
- Auto-login presenter in light mode:  
  `http://localhost:3000/presenter?secret=mypass&theme=light`
- Open a specific session view (e.g. for embedding in slides):  
  `http://localhost:3000/presenter/view/MySession?secret=mypass&theme=dark`
- Force dark mode for players:  
  `http://localhost:3000/?session=abc123&theme=dark`

The theme can also be toggled at any time via the 🌙/☀️ button in the footer (player) or header (presenter).

## For More Details

See [conference/README.md](conference/README.md)
