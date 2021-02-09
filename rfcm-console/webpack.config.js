const path = require('path');
const webpack = require('webpack');
//process.env.NODE_ENV = 'production'; // when release

module.exports = {
    name: 'word-relay-setting', //Whatever you want
    mode: 'development', // release: product
    devtool: 'eval',
    resolve: {
        extensions: ['.js', '.jsx']
    },
    entry: {
        app: ['./src/index']
    },  // input

    module: {
        rules: [{
            test: /\.jsx?/,
            loader: 'babel-loader',
            options:{
                presets: [
                    ['@babel/preset-env', {
                        targets:{
                            browsers: ['> 5% in KR', 'last 2 chrome versions'],
                        },
                        debug: true,
                    }], 
                    '@babel/preset-react',
                ],
                plugins: [
                    '@babel/plugin-proposal-class-properties',
                    'react-hot-loader/babel',
                ],
            },
        }],
    },

    plugins: [
        new webpack.LoaderOptionsPlugin({debug: true}),
    ],

    output: {
        path: path.join(__dirname, 'dist'),
        filename: 'app.js',
        publicPath: '/dist',
    },  // output
};