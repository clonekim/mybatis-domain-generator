module.exports = {
  productionSourceMap: false,
  outputDir: '../src/main/resources/static/',
  devServer: {
    port: 3000,
    proxy: 'http://127.0.0.1:8080'
  }
}