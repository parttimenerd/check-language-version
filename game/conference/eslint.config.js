import js from '@eslint/js';

export default [
    {
        ignores: ['node_modules/', 'dist/', 'build/', 'public/dist/']
    },
    {
        files: ['src/**/*.js', 'server.js', 'load-test.js'],
        languageOptions: {
            ecmaVersion: 2021,
            sourceType: 'module',
            globals: {
                // Browser globals
                console: 'readonly',
                fetch: 'readonly',
                WebSocket: 'readonly',
                window: 'readonly',
                document: 'readonly',
                setTimeout: 'readonly',
                setInterval: 'readonly',
                clearInterval: 'readonly',
                clearTimeout: 'readonly',
                URL: 'readonly',
                // Node.js globals
                process: 'readonly',
                __dirname: 'readonly',
                require: 'readonly',
                // Others
                confirm: 'readonly'
            }
        },
        rules: {
            ...js.configs.recommended.rules,
            'indent': ['error', 4],
            'linebreak-style': ['error', 'unix'],
            'quotes': ['error', 'single'],
            'semi': ['error', 'always'],
            'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
            'no-console': 'off'
        }
    }
];
