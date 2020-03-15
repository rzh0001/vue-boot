<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="订单号">
              <a-input placeholder="请输入订单号" v-model="queryParam.orderId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="外部订单号">
              <a-input placeholder="请输入外部订单号" v-model="queryParam.outerOrderId"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="用户">
                <a-input placeholder="请输入用户" v-model="queryParam.userName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="账户名">
                <a-input placeholder="" v-model="queryParam.accountName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="开号">
                <a-input placeholder="" v-model="queryParam.cardNumber"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="开始时间">
                <j-date v-model="queryParam.createTime_begin" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="结束时间">
                <j-date v-model="queryParam.createTime_end" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
<!--              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>-->
              <a-button type="primary" @click="searchQueryLocal" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" v-has="'payOrder:add'" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('代付订单')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
<!--                @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        总订单数：<a-tag color="cyan">{{summary.totalOrderCount}} </a-tag>
        已付订单数：<a-tag color="cyan">{{summary.paidOrderCount}} </a-tag>
        待处理订单数：<a-tag color="red">{{summary.unpaidOrderCount}} </a-tag>
        总金额：<a-tag color="cyan">{{summary.totalOrderAmount}}元 </a-tag>
        已付金额：<a-tag color="cyan">{{summary.paidOrderAmount}}元 </a-tag>
<!--        已返回金额：<a-tag color="cyan">{{summary.paidOrderAmount}}元 </a-tag>-->
        待处理金额：<a-tag color="red">{{summary.unpaidOrderAmount}}元 </a-tag>
<!--        预计收入：<a-tag color="cyan">{{summary.income}}元 </a-tag>-->
        订单手续费：<a-tag color="cyan">{{summary.fee}}元 </a-tag>
      </div>

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
          <a-popconfirm title="确定开始处理吗?" v-has="'payOrder:approval'" v-if="record.status==0" @confirm="() => handleApproval({id: record.id, status: '1'})">
                  <a>接单</a>
          </a-popconfirm>
          <a-popconfirm title="确定已打款吗?" v-has="'payOrder:approval'" v-if="record.status==1" @confirm="() => handleApproval({id: record.id, status: '2'})">
                  <a>已打款</a> <a-divider type="vertical"/>
          </a-popconfirm>
          <a-popconfirm title="确定拒绝代付申请吗?" v-has="'payOrder:approval'" v-if="record.status==1" @confirm="() => handleApproval({id: record.id, status: '3'})">
                  <a>拒绝</a>
          </a-popconfirm>
          <a-popconfirm title="确定手动回调吗?" v-has="'payOrder:approval'" v-if="(record.status==2) || (record.status==3) && (record.callbackUrl!=null)" @confirm="() => manualCallback({id: record.id})">
                  <a>手动回调</a>
          </a-popconfirm>

        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <payOrder-modal ref="modalForm" @ok="modalFormOk"></payOrder-modal>
  </a-card>
</template>

<script>
  import PayOrderModal from './modules/PayOrderModal'
  import JDate from '@/components/jeecg/JDate'
  import { getAction, putAction } from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'PayOrderList',
    mixins: [JeecgListMixin],
    components: {
      PayOrderModal,
      JDate
    },
    data() {
      return {
        description: '代付订单管理页面',
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
          {
            title: '订单号',
            align: 'center',
            dataIndex: 'orderId'
          },
          // {
          //   title: '外部订单号',
          //   align: 'center',
          //   dataIndex: 'outerOrderId'
          // },
          // {
          //   title: '用户id',
          //   align: 'center',
          //   dataIndex: 'userId'
          // },
          {
            title: '用户',
            align: 'center',
            dataIndex: 'userName'
          },
          // {
          //   title: '用户昵称',
          //   align: 'center',
          //   dataIndex: 'userRealname'
          // },
          // {
          //   title: '商户编号',
          //   align: 'center',
          //   dataIndex: 'merchantId'
          // },
          {
            title: '金额',
            align: 'center',
            dataIndex: 'amount'
          },
          {
            title: '交易手续费',
            align: 'center',
            dataIndex: 'transactionFee'
          },
          {
            title: '固定手续费',
            align: 'center',
            dataIndex: 'fixedFee'
          },
          // {
          //   title: '订单总手续费',
          //   align: 'center',
          //   dataIndex: 'orderFee'
          // },
          {
            title: '通道',
            align: 'center',
            dataIndex: 'channel'
          },
          {
            title: '账户类型',
            align: 'center',
            dataIndex: 'accountType',
            customRender: function(text) {
              if (text == 1) {
                return '对私'
              }else if (text == 2) {
                return '对公'
              } else {
                return text
              }
            }
          },
          {
            title: '账户名',
            align: 'center',
            dataIndex: 'accountName'
          },
          {
            title: '卡号',
            align: 'center',
            dataIndex: 'cardNumber'
          },
          {
            title: '银行名称',
            align: 'center',
            dataIndex: 'bankName'
          },
          {
            title: '开户行',
            align: 'center',
            dataIndex: 'branchName'
          },
          {
            title: '订单状态',
            align: 'center',
            dataIndex: 'status',
            customRender: function(text) {
              if (text == 0) {
                return '待处理'
              } else if (text == 1) {
                return '已接单'
              }else if (text == 2) {
                return '已打款'
              } else if (text == 3) {
                return '审核拒绝'
              } else {
                return text
              }
            }
          },
          {
            title: '回调状态',
            align: 'center',
            dataIndex: 'callbackStatus',
            customRender: function(text) {
              if (text == 0) {
                return '未回调'
              } else if (text == 1) {
                return '未返回'
              }else if (text == 2) {
                return '已返回'
              }  else {
                return text
              }
            }
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
          },
          {
            title: '创建时间',
            align: 'center',
            dataIndex: 'createTime'
          },
          {
            title: '成功时间',
            align: 'center',
            dataIndex: 'successTime'
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
          //   title: '操作IP',
          //   align: 'center',
          //   dataIndex: 'ip'
          // },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/df/payOrder/list',
          approval: '/df/payOrder/approval',
          delete: '/df/payOrder/delete',
          deleteBatch: '/df/payOrder/deleteBatch',
          exportXlsUrl: 'df/payOrder/exportXls',
          importExcelUrl: 'df/payOrder/importExcel',
          summaryUrl: '/df/payOrder/summary',
          manualCallback: '/df/payOrder/manualCallback'
        },
        summary: {},
      }
    },
    created() {
      // this.columns = colAuthFilter(this.columns,'orderList:');
      this.searchQueryLocal();
      // this.initDictConfig();
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      searchQueryLocal(){
        this.searchQuery()
        getAction(this.url.summaryUrl, this.queryParam).then((res) => {
          if (res.success) {
            this.summary = res.result;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      manualCallback: function(params){
        putAction(this.url.manualCallback, params).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>