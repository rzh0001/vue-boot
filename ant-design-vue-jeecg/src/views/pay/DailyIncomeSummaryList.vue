<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

<!--          <a-col :md="6" :sm="8">-->
<!--            <a-form-item label="用户id">-->
<!--              <a-input placeholder="请输入用户id" v-model="queryParam.userId"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :md="6" :sm="8">
            <a-form-item label="商户">
              <a-input placeholder="" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :md="6" :sm="8">-->
<!--            <a-form-item label="日期">-->
<!--              <j-date v-model="queryParam.date" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="12">日期选择框(v-model)：{{ jdate.value }}</a-col>-->
          <a-col :md="6" :sm="8">
            <a-form-item label="日期" >
              <j-date v-model="queryParam.transTime"></j-date>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="用户名称">
                <a-input placeholder="请输入用户名称" v-model="queryParam.realname"></a-input>
              </a-form-item>
            </a-col>
<!--            <a-col :md="6" :sm="8">-->
<!--              <a-form-item label="总订单数">-->
<!--                <a-input placeholder="请输入总订单数" v-model="queryParam.totalOrderCount"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :md="6" :sm="8">-->
<!--              <a-form-item label="总订单金额">-->
<!--                <a-input placeholder="请输入总订单金额" v-model="queryParam.totalOrderAmount"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
<!--              <a @click="handleToggleSearch" style="margin-left: 8px">-->
<!--                {{ toggleSearchStatus ? '收起' : '展开' }}-->
<!--                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>-->
<!--              </a>-->
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('今日交易统计')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
<!--                @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel">-->
<!--            <a-icon type="delete"/>-->
<!--            删除-->
<!--          </a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作-->
<!--          <a-icon type="down"/>-->
<!--        </a-button>-->
<!--      </a-dropdown>-->
<!--    </div>-->

    <!-- table区域-begin -->
    <div>
<!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
<!--        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{-->
<!--        selectedRowKeys.length }}</a>项-->
<!--        <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--      </div>-->

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <dailyIncomeSummary-modal ref="modalForm" @ok="modalFormOk"></dailyIncomeSummary-modal>
  </a-card>
</template>

<script>
  import DailyIncomeSummaryModal from './modules/DailyIncomeSummaryModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import moment from 'moment'
  import { filterObj } from '@/utils/util';



  export default {
    name: 'DailyIncomeSummaryList',
    mixins: [JeecgListMixin],
    components: {
      DailyIncomeSummaryModal,
      JDate,moment
    },
    data() {
      return {
        description: '今日交易统计管理页面',
        sum: {
          totalCount: 0,

        },
        // 表头
        columns: [
          // {
          //   title: '#',
          //   dataIndex: '',
          //   key: 'rowIndex',
          //   width: 60,
          //   align: 'center',
          //   customRender: function(t, r, index) {
          //     return parseInt(index) + 1
          //   }
          // },
          // {
          //   title: '用户id',
          //   align: 'center',
          //   dataIndex: 'userId'
          // },
          {
            title: '商户',
            align: 'center',
            dataIndex: 'username'
          },
          // {
          //   title: '用户名称',
          //   align: 'center',
          //   dataIndex: 'userRealname'
          // },
          {
            title: '支付通道',
            align: 'center',
            dataIndex: 'payType'
          },
          {
            title: '总订单数',
            align: 'center',
            dataIndex: 'totalOrderCount'
          },
          {
            title: '已付订单数',
            align: 'center',
            dataIndex: 'paidOrderCount'
          },
          {
            title: '未付订单数',
            align: 'center',
            dataIndex: 'unpaidOrderCount'
          },
          {
            title: '总金额',
            align: 'center',
            dataIndex: 'totalOrderAmount'
          },
          {
            title: '已付金额',
            align: 'center',
            dataIndex: 'paidOrderAmount'
          },
          {
            title: '未付金额',
            align: 'center',
            dataIndex: 'unpaidOrderAmount'
          },
          {
            title: '手续费',
            align: 'center',
            dataIndex: 'payFee'
          },
          // {
          //   title: '代理ID',
          //   align: 'center',
          //   dataIndex: 'agentId'
          // },
          // {
          //   title: '代理帐号',
          //   align: 'center',
          //   dataIndex: 'agentUsername'
          // },
          // {
          //   title: '代理姓名',
          //   align: 'center',
          //   dataIndex: 'agentRealname'
          // },
          // {
          //   title: '介绍人ID',
          //   align: 'center',
          //   dataIndex: 'salesmanId'
          // },
          // {
          //   title: '介绍人帐号',
          //   align: 'center',
          //   dataIndex: 'salesmanUsername'
          // },
          // {
          //   title: '介绍人姓名',
          //   align: 'center',
          //   dataIndex: 'salesmanRealname'
          // },
          // {
          //   title: '操作',
          //   dataIndex: 'action',
          //   align: 'center',
          //   scopedSlots: { customRender: 'action' }
          // }
        ],
        url: {
          list: '/pay/dailyIncomeSummary/list',
          delete: '/pay/dailyIncomeSummary/delete',
          deleteBatch: '/pay/dailyIncomeSummary/deleteBatch',
          exportXlsUrl: 'pay/dailyIncomeSummary/exportXls',
          importExcelUrl: 'pay/dailyIncomeSummary/importExcel',
        }
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      moment,
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>