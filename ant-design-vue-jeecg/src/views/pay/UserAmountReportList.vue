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
            <a-form-item label="报表日期">
              <j-date v-model="queryParam.reportDate"></j-date>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="商户">
              <a-input placeholder="" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="代理">
              <a-input placeholder="" v-model="queryParam.agentUsername"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <!--          <a-col :md="6" :sm="8">-->
            <!--            <a-form-item label="期初金额">-->
            <!--              <a-input placeholder="请输入期初金额" v-model="queryParam.originalamount"></a-input>-->
            <!--            </a-form-item>-->
            <!--          </a-col>-->
            <!--          <a-col :md="6" :sm="8">-->
            <!--            <a-form-item label="支付类型">-->
            <!--              <a-input placeholder="请输入支付类型" v-model="queryParam.payType"></a-input>-->
            <!--            </a-form-item>-->
            <!--          </a-col>-->
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" icon="download" style="margin-left: 8px"
                        @click="handleExportXls('用户余额报表-期初余额 每天0点更新')">导出</a-button>

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
    <!--      &lt;!&ndash;      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>&ndash;&gt;-->
    <!--      <a-button type="primary" icon="download" @click="handleExportXls('用户余额报表-期初余额 每天0点更新')">导出</a-button>-->
    <!--    </div>-->

    <!-- table区域-begin -->
    <div>
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
    <userAmountReport-modal ref="modalForm" @ok="modalFormOk"></userAmountReport-modal>
  </a-card>
</template>

<script>
  import UserAmountReportModal from './modules/UserAmountReportModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import moment from 'moment'
  import JDate from '@/components/jeecg/JDate'


  export default {
    name: 'UserAmountReportList',
    mixins: [JeecgListMixin],
    components: {
      UserAmountReportModal,
      moment,
      JDate
    },
    data() {
      return {
        description: '用户余额报表-期初余额 每天0点更新管理页面',
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
            title: '报表日期',
            align: 'center',
            width: 120,
            dataIndex: 'reportDate'
          },
          {
            title: '商户',
            align: 'center',
            dataIndex: 'userName'
          },
          {
            title: '期初金额',
            align: 'center',
            dataIndex: 'originalamount'
          },
          // {
          //   title: '支付类型',
          //   align: 'center',
          //   dataIndex: 'payType'
          // },
          {
            title: '今日入金',
            align: 'center',
            dataIndex: 'paidAmount'
          },
          {
            title: '今日下发',
            align: 'center',
            dataIndex: 'cashOutAmount'
          },
          {
            title: '剩余可提现金额',
            align: 'center',
            dataIndex: 'availableAmount'
          },
          {
            title: '通道手续费',
            align: 'center',
            dataIndex: 'payFee'
          },
          // {
          //   title: '代理ID',
          //   align: 'center',
          //   dataIndex: 'agentId'
          // },
          {
            title: '代理',
            align: 'center',
            dataIndex: 'agentUsername'
          },
          // {
          //   title: '代理姓名',
          //   align: 'center',
          //   dataIndex: 'agentRealname'
          // },
          // {
          //   title: '介绍人id',
          //   align: 'center',
          //   dataIndex: 'salesmanId'
          // },
          {
            title: '介绍人',
            align: 'center',
            dataIndex: 'salesmanUsername'
          }
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
          list: '/pay/userAmountReport/list',
          delete: '/pay/userAmountReport/delete',
          deleteBatch: '/pay/userAmountReport/deleteBatch',
          exportXlsUrl: 'pay/userAmountReport/exportXls',
          importExcelUrl: 'pay/userAmountReport/importExcel'
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
      onChange(date, dateString) {
        console.log(date, dateString);
        this.queryParam.reportDate = moment(date, 'YYYY-MM-DD')
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>