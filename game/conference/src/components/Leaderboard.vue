<template>
    <div class="leaderboard-container">
        <h3>🏆 Leaderboard</h3>
        <div class="leaderboard-scroll">
            <table v-if="players.length > 0" class="leaderboard-table">
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Player</th>
                        <th class="score-col">Score</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="(player, index) in players"
                        :key="player.uuid"
                        :class="{ 'current-player': player.uuid === currentUuid }"
                    >
                        <td>#{{ index + 1 }}</td>
                        <td>{{ player.displayName }}</td>
                        <td class="score-col">{{ player.score ?? player.correctCount ?? 0 }}</td>
                    </tr>
                </tbody>
            </table>
            <p v-else class="no-players">No players yet</p>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        players: {
            type: Array,
            required: true,
        },
        currentUuid: {
            type: String,
            default: '',
        },
    },
};
</script>

<style scoped>
.leaderboard-container {
    margin-top: 30px;
    padding: 15px;
    background: var(--bg-badge);
    border-radius: 4px;
}
.leaderboard-scroll {
    max-height: 250px;
    overflow-y: auto;
}
.leaderboard-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
}
.leaderboard-table thead tr {
    background: var(--bg-card);
    border-bottom: 1px solid var(--border-separator);
}
.leaderboard-table th,
.leaderboard-table td {
    padding: 8px;
    text-align: left;
}
.score-col {
    text-align: right;
}
.leaderboard-table tbody tr {
    border-bottom: 1px solid var(--border-separator);
}
.leaderboard-table tbody tr.current-player {
    background: var(--warning);
    color: var(--bg-body);
}
.no-players {
    color: var(--text-muted);
}
</style>
