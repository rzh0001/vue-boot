<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="订单号">
              <a-input placeholder="" v-model="queryParam.orderId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="外部订单号">
              <a-input placeholder="" v-model="queryParam.outerOrderId"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="商户">
                <a-input placeholder="请输入商户" v-model="queryParam.userName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="商户编号">
                <a-input placeholder="请输入商户编号" v-model="queryParam.businessCode"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="支付通道">
                <a-select v-model="queryParam.payType" placeholder="">
                  <a-select-option v-for="option in channels" :key="option.toString()" :value="option.channelCode">
                    {{ option.channelName }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="0">未支付</a-select-option>
                  <a-select-option value="1">成功，未返回</a-select-option>
                  <a-select-option value="2">成功，已返回</a-select-option>
                  <a-select-option value="-1">失效</a-select-option>
                </a-select>
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

            <a-col :md="6" :sm="8">
              <a-form-item label="是否补单">
                <a-select v-model="queryParam.replacementOrder" placeholder="">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">是</a-select-option>
                  <a-select-option value="2">否</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="IP">
                <a-input placeholder="" v-model="queryParam.ip"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQueryLocal" icon="search">查询</a-button>
               <a-button type="primary" @click="deleteOrder" icon="search" style="margin-left: 8px">删除</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-tooltip placement="topLeft" title="请勿一次性导出太多数据，导出失败请缩短查询时间区间">
               <a-button type="primary" icon="download" @click="handleExportXls('订单信息')" style="margin-left: 8px">导出</a-button>
              </a-tooltip>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        总订单数：<a-tag color="cyan">{{summary.totalOrderCount}} </a-tag>
        成功订单数：<a-tag color="cyan">{{summary.paidOrderCount}} </a-tag>
        失败订单数：<a-tag color="red">{{summary.unpaidOrderCount}} </a-tag>
        总金额：<a-tag color="cyan">{{summary.totalOrderAmount}}元 </a-tag>
        成功金额：<a-tag color="cyan">{{summary.paidOrderAmount}}元 </a-tag>
        失败金额：<a-tag color="red">{{summary.unpaidOrderAmount}}元 </a-tag>
        预计收入：<a-tag color="cyan">{{summary.income}}元 </a-tag>
        预计手续费：<a-tag color="cyan">{{summary.fee}}元 </a-tag>
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
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定手动补单吗?手动补单会重新通知商户" @confirm="() => againRequest(record.orderId)">
                  <a>手动补单</a>
                </a-popconfirm>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="线下补单不通知商户，直接进行统计" @confirm="() => offline(record.orderId)">
                  <a>线下补单</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <orderInfoEntity-modal ref="modalForm" @ok="modalFormOk"></orderInfoEntity-modal>
  </a-card>
</template>

<script>
  import JDate from '@/components/jeecg/JDate'
  import OrderInfoEntityModal from './modules/OrderInfoEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction } from '@/api/manage'
  import { colAuthFilter } from "@/utils/authFilter"

  export default {
    name: 'OrderInfoEntityList',
    mixins: [JeecgListMixin],
    components: {
      JDate,
      OrderInfoEntityModal
    },
    data() {
      return {
        channels: [],
        description: '订单信息管理页面',
        summary: {},
        // 表头
        columns: [
          {
            title: '订单号',
            align: 'center',
            dataIndex: 'orderId'
          },
          {
            title: '外部订单号',
            align: 'center',
            dataIndex: 'outerOrderId',
            color: 'red'
          },
          {
            title: '商户',
            align: 'center',
            dataIndex: 'userName'
          },
          /*{
            title: '代理',
            align: 'center',
            dataIndex: 'parentUser'
          },*/
          {
            title: '商户编号',
            align: 'center',
            dataIndex: 'businessCode'
          },
          {
            title: '订单金额',
            align: 'center',
            dataIndex: 'submitAmount'
          },
          {
            title: '手续费',
            align: 'center',
            dataIndex: 'poundage'
          },
          {
            title: '实际金额',
            align: 'center',
            dataIndex: 'actualAmount'
          },
          {
            title: '支付状态',
            align: 'center',
            dataIndex: 'status',
            key: 'status',
            customRender: function(text) {
              if (text == -1) {
                return <a-tag color="red">无效</a-tag>
              } else if (text == 0) {
                return  <a-tag color="red">未支付</a-tag>
              } else if (text == 1) {
                return <a-tag color="red">成功，未返回</a-tag>
              } else if (text == 2) {
                return <a-tag color="cyan">成功，已返回</a-tag>
              } else {
                return text
              }
            }
          },
          {
            title: '是否补单',
            align: 'center',
            dataIndex: 'replacementOrder',
            key: 'replacementOrder',
            customRender: function(text) {
              if (text == 1) {
                return <a-tag color="red">是</a-tag>
              } else {
                return <a-tag color="cyan">否</a-tag>
              }
            }
          },
          {
            title: '支付通道',
            align: 'center',
            dataIndex: 'payType',
            key: 'payType',
            customRender: function(text) {
              if (text == 'ysf') {
                return '云闪付'
              } else if (text == 'ali_bank') {
                return '支付宝转卡'
              } else if (text == 'ali_zz') {
                return '支付宝转账'
              } else if (text == 'nxys_wx') {
                return '农信易扫微信'
              } else if (text == 'nxys_alipay') {
                return '农信易扫支付宝'
              } else if (text == 'wechat_bank') {
                return '微信转卡'
              } else if (text == 'xin_pay_alipay') {
                return '信付-支付宝'
              }  else if (text == 'baiyitong_pay_wechat') {
                return '百易通-微信'
              } else if (text == 'paofen_pay_wechat') {
                 return '跑分-微信'
              } else if (text == 'paofen_pay_alipay') {
                 return '跑分-支付宝'
              } else if (text == 'paofen_pay_yinlian') {
                 return '跑分-银联'
              } else {
                return text
              }
            }
          },
          {
            title: '创建时间',
            align: 'center',
            width: 150,
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title: '成功时间',
            align: 'center',
            width: 150,
            dataIndex: 'successTime',
            sorter: true
          },
          {
            title: 'IP',
            align: 'center',
            width: 150,
            dataIndex: 'ip',
            sorter: true
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/pay/orderInfoEntity/list',
          deleteOrder:'/pay/orderInfoEntity/deleteOrder',
          delete: '/pay/orderInfoEntity/delete',
          deleteBatch: '/pay/orderInfoEntity/deleteBatch',
          exportXlsUrl: 'pay/orderInfoEntity/exportXls',
          importExcelUrl: 'pay/orderInfoEntity/importExcel',
          againRequest: 'pay/orderInfoEntity/againRequest',
          channel: '/pay/channelEntity/channel',
          summaryUrl: '/pay/orderInfoEntity/summary',
          offline: 'pay/orderInfoEntity/offline',

        }
      }
    },
    created() {
      this.columns = colAuthFilter(this.columns,'orderList:');
      this.searchQueryLocal();
      this.initDictConfig();
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    mounted: function() {
      this.channel()
    },
    methods: {
      againRequest(orderId) {
        getAction(this.url.againRequest, { id: orderId }).then((res) => {
          alert(res.msg)
      })
      },
      offline(orderId) {
        getAction(this.url.offline, { id: orderId }).then((res) => {
          alert(res.msg)
      })
      },
      channel() {
        getAction(this.url.channel, null).then((res) => {
          if (res.success) {
          this.channels = res.result
        } else {
          this.$message.warning(res.message)
        }
      })
      },
      searchQueryLocal(){
        this.searchQuery()
        var params = this.getQueryParams();
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
      deleteOrder(){
        var params = this.getQueryParams();
        var deleteTime =params.createTime_end;
        // 在这里调用删除接口
        getAction(this.url.deleteOrder,  { lastTime: deleteTime }).then((res) => {
          if (res.success) {
          this.$message.message (res.result) ;
        } else {
          this.$message.warning(res.message)
        }
      })

      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>