const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = [
  {
    name: 'player',
    mode: 'production',
    entry: './src/player.js',
    output: {
      filename: 'player.bundle.js',
      path: path.resolve(__dirname, 'public/dist'),
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          use: 'vue-loader',
        },
        {
          test: /\.css$/i,
          use: ['style-loader', 'css-loader'],
        },
      ],
    },
    plugins: [new VueLoaderPlugin()],
    performance: {
      maxAssetSize: 512000,
    },
  },
  {
    name: 'presenter',
    mode: 'production',
    entry: './src/presenter.js',
    output: {
      filename: 'presenter.bundle.js',
      path: path.resolve(__dirname, 'public/dist'),
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          use: 'vue-loader',
        },
        {
          test: /\.css$/i,
          use: ['style-loader', 'css-loader'],
        },
      ],
    },
    plugins: [new VueLoaderPlugin()],
    performance: {
      maxAssetSize: 512000,
    },
  },
];
