const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

module.exports = {
  productionSourceMap: false,
  devServer: {
    port: 9000,
    proxy: {
      //1.8需求：增加分享功能，不登陆即可看到文档页面。所以代理设置增加了(?!/document)文档页面的相关信息
      // ['^(?!/login)']: {
      //不登录即可看到流水线执行任务的报告
      ['^((?!/login)(?!/document)(?!/httpRunner)(?!/jmeter))']: {
        target: 'http://10.16.25.152:8081',
        ws: true,
        changeOrigin: true,
        secure: false,
      },
    }
  },
  pages: {
    business: {
      entry: "src/business/main.js",
      template: "src/business/index.html",
      filename: "index.html"
    },
    login: {
      entry: "src/login/login.js",
      template: "src/login/login.html",
      filename: "login.html"
    },
    document: {
      entry: "src/document/document.js",
      template: "src/document/document.html",
      filename: "document.html"
    },
    httpRunner: {
      entry: "src/template/report/httprunner/httpRunnerReport.js",
      template: "src/template/report/httprunner/httpRunnerReport.html",
      filename: "httpRunner.html"
    },
    jmeter: {
      entry: "src/template/report/api/apiReportTemplate.js",
      template: "src/template/report/api/apiReportTemplate.html",
      filename: "apiReportTemplate.html"
    }
  },
  configureWebpack: {
    devtool: 'source-map',
    resolve: {
      alias: {
        '@': resolve('src'),
        '@comp': resolve('src/business/components'),
        '@common': resolve('src/business/components/common')
      }
    },
  },
  chainWebpack(config) {
    config.plugins.delete('prefetch')
  }
};
