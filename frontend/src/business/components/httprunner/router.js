
export default {
  path: "/httprunner",
  name: "httprunner",
  components: {
    content: () => import('@/business/components/httprunner/index.vue')
  },
  redirect: { name: "httpRunnerRepo" },
  children: [
    {
      path: "repo",
      name: "httpRunnerRepo",
      component: () => import('@/business/components/httprunner/Repo'),
    },
    {
      path: "report",
      name: "httpRunnerReportList",
      component: () => import('@/business/components/httprunner/HttpRunnerReportList'),
    },
    {
      path: "report/:reportId",
      name: "HttpRunnerReportDetail",
      component: () => import('@/business/components/httprunner/HttpRunnerReportDetail'),
    },
  ]
}
