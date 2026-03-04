<template>
    <div
        class="leaderboard-container"
        style="margin-top: 30px; padding: 15px; background: #f8f9fa; border-radius: 4px"
    >
        <h3>🏆 Leaderboard</h3>
        <div style="max-height: 250px; overflow-y: auto">
            <table
                v-if="players.length > 0"
                style="width: 100%; border-collapse: collapse; font-size: 14px"
            >
                <thead>
                    <tr style="background: #e9ecef; border-bottom: 1px solid #dee2e6">
                        <th style="padding: 8px; text-align: left">Rank</th>
                        <th style="padding: 8px; text-align: left">Player</th>
                        <th style="padding: 8px; text-align: right">Score</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="(player, index) in players"
                        :key="player.uuid"
                        :style="{
                            background: player.uuid === currentUuid ? '#fff3cd' : '',
                            borderBottom: '1px solid #dee2e6',
                        }"
                    >
                        <td style="padding: 8px">#{{ index + 1 }}</td>
                        <td style="padding: 8px">{{ player.displayName }}</td>
                        <td style="padding: 8px; text-align: right">{{ player.score ?? player.correctCount ?? 0 }}</td>
                    </tr>
                </tbody>
            </table>
            <p v-else style="color: #6c757d">No players yet</p>
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
