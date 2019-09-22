<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="四方系统订单号">
              <a-input placeholder="请输入四方系统订单号" v-model="queryParam.orderId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="外部订单号">
              <a-input placeholder="请输入外部订单号" v-model="queryParam.outerOrderId"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="商户">
                <a-input placeholder="请输入商户" v-model="queryParam.userName"></a-input>
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

            <a-col :md="8" :sm="10">
              <a-form-item label="起始时间">
                <j-date v-model="queryParam.createTime_begin" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <!--              <a-button type="primary" @click="searchQueryLocal" icon="search">查询</a-button>-->
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

    <div>
      <!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
      <!--        提交订单数：{{summary.totalOrderCount}} 订单总金额：{{summary.totalOrderAmount}} 已付订单数：{{summary.paidOrderCount}}-->
      <!--        已付总金额：{{summary.paidOrderAmount}}-->
      <!--        预计收入：{{summary.totalCount}} 预计手续费：{{summary.feeIncome}} 未付订单数：{{summary.unpaidOrderCount}} 未付总金额-->
      <!--        {{summary.unpaidOrderAmount}}-->
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
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定补单吗?" @confirm="() => againRequest(record.orderId)">
                  <a>补单</a>
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
            title: '系统订单号',
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
              }  else {
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
          delete: '/pay/orderInfoEntity/delete',
          deleteBatch: '/pay/orderInfoEntity/deleteBatch',
          exportXlsUrl: 'pay/orderInfoEntity/exportXls',
          importExcelUrl: 'pay/orderInfoEntity/importExcel',
          againRequest: 'pay/orderInfoEntity/againRequest',
          channel: '/pay/channelEntity/channel',
          summaryUrl: '/pay/orderInfoEntity/summary'

        }
      }
    },
    created() {
      this.columns = colAuthFilter(this.columns,'orderList:');
      this.loadData();
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

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>