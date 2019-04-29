module.exports = {
    entry: {
        index: "./src/index.js"
    },
    output: {
        filename: "[name].js",
        path: __dirname + "/static/js/"
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: "babel-loader"
                }
            }
        ]
    },
    mode: "development"
};