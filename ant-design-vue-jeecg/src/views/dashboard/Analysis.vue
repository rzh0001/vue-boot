<template>
  <div class="page-header-index-wide">

    <div style="background: #f0f2f5; padding: 15px">
      <a-row :gutter="12">
        <a-col :span="6">
          <a-card>
            <a-statistic title="剩余可提现金额" :value="data.userAmount" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="订单总额" :value="data.payAmount" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="手续费" :value="data.payFee" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-button shape="circle" icon="reload" @click="getSummary"></a-button>
        </a-col>
      </a-row>
    </div>
    <div style="background: #f0f2f5; padding: 15px">
      <a-row :gutter="12">
        <a-col :span="6">
          <a-card>
            <a-statistic title="今日下发" :value="data.todayCashOutAmount" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="今日订单" :value="data.todayPayAmount" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="今日手续费" :value="data.todayPayFee" :precision="2" :valueStyle="{color: '#2e97ef'}" style="margin-right: 50px"/>
          </a-card>
        </a-col>
      </a-row>
    </div>


  </div>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import ACol from 'ant-design-vue/es/grid/Col'
  import ATooltip from 'ant-design-vue/es/tooltip/Tooltip'
  import MiniArea from '@/components/chart/MiniArea'
  import MiniBar from '@/components/chart/MiniBar'
  import MiniProgress from '@/components/chart/MiniProgress'
  import RankList from '@/components/chart/RankList'
  import Bar from '@/components/chart/Bar'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  import HeadInfo from '@/components/tools/HeadInfo.vue'

  import Trend from '@/components/Trend'
  import { getLoginfo, getVisitInfo } from '@/api/api'
  import { getAction } from '../../api/manage'

  const rankList = []
  for (let i = 0; i < 7; i++) {
    rankList.push({
      name: '白鹭岛 ' + (i + 1) + ' 号店',
      total: 1234.56 - i * 100
    })
  }
  const barData = []
  for (let i = 0; i < 12; i += 1) {
    barData.push({
      x: `${i + 1}月`,
      y: Math.floor(Math.random() * 1000) + 200
    })
  }
  export default {
    name: 'Analysis',
    components: {
      ATooltip,
      ACol,
      ChartCard,
      MiniArea,
      MiniBar,
      MiniProgress,
      RankList,
      Bar,
      Trend,
      LineChartMultid,
      HeadInfo
    },
    data() {
      return {
        loading: true,
        center: null,
        rankList,
        barData,
        loginfo: {},
        data: {},
        visitFields: ['ip', 'visit'],
        visitInfo: [],
        url: {
          summary: '/sys/dashboard/summary'
        }
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initLogInfo()
    },
    methods: {
      initLogInfo() {
        getLoginfo(null).then(res => {
          if (res.success) {
            Object.keys(res.result).forEach(key => {
              res.result[key] = res.result[key] + ''
            })
            this.loginfo = res.result
          }
        })
        getVisitInfo().then(res => {
          if (res.success) {
            console.log('aaaaaa', res.result)
            this.visitInfo = res.result
          }
        })

        this.getSummary()
      },

      getSummary() {
        getAction(this.url.summary, {}).then(res => {
            if (res.success) {
              this.data = res.result
            }
          }
        )
      }
    }
  }
</script>

<style lang="scss" scoped>
  .circle-cust {
    position: relative;
    top: 28px;
    left: -100%;
  }

  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

  .extra-item {
    display: inline-block;
    margin-right: 24px;

  a {
    margin-left: 24px;
  }

  }
  }

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

  &
  .center {
    text-align: center;
    padding: 0 32px;
  }

  span {
    color: rgba(0, 0, 0, 0.45);
    display: inline-block;
    font-size: 0.95rem;
    line-height: 42px;
    margin-bottom: 4px;
  }

  p {
    line-height: 42px;
    margin: 0;

  a {
    font-weight: 600;
    font-size: 1rem;
  }

  }
  }
</style>
