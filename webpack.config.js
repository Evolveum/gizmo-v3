/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

const webpack = require('./node_modules/webpack');
const MiniCssExtractPlugin = require('./node_modules/mini-css-extract-plugin');
const CssMinimizerPlugin = require('./node_modules/css-minimizer-webpack-plugin');
// const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const path = require('path');

module.exports = {
    entry: {
        gizmo: [
            './src/frontend/js/gizmo.js',
            './src/frontend/scss/gizmo.scss',
            './src/frontend/scss/gizmo-fonts.scss',
        ],
        // fullCalendar: [
        //     './src/frontend/js/fullCalendar.js',
        // ]
    },
    devtool: false,
    output: {
        path: path.resolve(__dirname, 'target/generated-resources/webpack/static/static'),
        publicPath: '../static/',
        filename: './[name].js',
        assetModuleFilename: './[name][ext]',
    },
    module: {
        rules: [
            {
                test: /\.(sass|scss|css)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: './node_modules/css-loader',
                        options: {
                            importLoaders: 2,
                            sourceMap: false,
                            modules: false,
                        },
                    },
                    './node_modules/postcss-loader',
                    {
                        loader: "./node_modules/sass-loader",
                        options: {
                            sassOptions: {
                                outputStyle: "expanded",
                            }
                        },
                    },
                ],
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: "./node_modules/babel-loader"
                }
            },
            // Images: Copy image files to build folder
            {
                test: /\.(?:ico|gif|png|jpg|jpeg)$/i,
                type: 'asset/resource',
                generator: {
                    filename: 'img/[name][ext]'
                }
            },
            // Fonts and SVGs: Inline files
            {
                test: /\.(woff(2)?|eot|ttf|otf|svg|)$/,
                type: 'asset/inline'
            },
        ],
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            moment: 'moment',
        }),
        new MiniCssExtractPlugin({
            filename: '[name].css',
            chunkFilename: '[id].css',
        }),
    ],
    optimization: {
        minimize: true,
        minimizer: [new CssMinimizerPlugin(), '...'],
    },
    performance: {
        hints: false,
        maxEntrypointSize: 512000,
        maxAssetSize: 512000,
    },
    externals: {
        // external (from webjar) not to collide with wicket headers.
        jquery: 'jQuery',
    },
}

