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
                <select v-model="queryParam.payType">
                  <option v-for="option in channels" v-bind:value="option.channelCode">
                    {{ option.channelName}}
                  </option>
                </select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="订单状态">
                <select v-model="queryParam.status">
                  <option v-for="option in orderStatus" v-bind:value="option.code">
                    {{ option.name}}
                  </option>
                </select>
              </a-form-item>

            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="订单创建时间">
                <j-date v-model="queryParam.createTime_begin" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
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

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
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
        orderStatus:[
          {
            "code":-1,
            "name":"无效"
          },
          {
            "code":0,
            "name":"未支付"
          },
          {
            "code":1,
            "name":"成功，未返回"
          },
          {
            "code":2,
            "name":"成功，已返回"
          }
        ],
        description: '订单信息管理页面',
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
            title: '四方系统订单号',
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
          {
            title: '代理',
            align: 'center',
            dataIndex: 'parentUser'
          },
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
                return '无效'
              } else if (text == 0) {
                return '未支付'
              } else if (text == 1) {
                return '成功，未返回'
              } else if (text == 2) {
                return '成功，已返回'
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
            dataIndex: 'updateTime',
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
          channel: "/pay/channelEntity/channel"

        }
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      againRequest(orderId){
        getAction(this.url.againRequest,{id:orderId}).then((res)=>{
          alert(res.msg)
      })
      },
      channel(){
        getAction(this.url.channel,null).then((res)=>{
          if(res.success){
          this.channels = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>