
const index = () => import('@/business/components/statistics/index')
const StatisticsReportHome = () => import('@/business/components/statistics/StatisticsReportHome')

export default {
  path: "/statistics",
  name: "statistics",
  redirect: { name: "StatisticsHome" },
  components: {
    content: index
  },
  children: [
    {
      path: '/home',
      name: 'StatisticsHome',
      component: StatisticsReportHome,
    },
  ]
}
