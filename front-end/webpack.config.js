var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");

module.exports = {
    entry: {
        app: './src/app/js/app.js',
        sign: './src/app/js/sign.js'
    },
    output: {
        filename: '[name].js',
        path: './dist/assets/app/js'
    },
    plugins: [
        new CommonsChunkPlugin('common.js')
    ],
    module: {
        loaders: [{
            test: /\.js$/,
            exclude: /node_modules/,
            loaders: ['babel-loader']
        }, {
            test: /\.html$/,
            loader: 'file?name=[name].[ext]'
        }, {
            test: /\.scss$/,
            loader: 'style!sass?sourceMap'
        }]
    }
};
