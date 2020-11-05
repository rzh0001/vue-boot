<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">
            <a-form-item label="账号">
              <a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="会员类型">
              <a-select v-model="queryParam.memberType" placeholder="">
                <a-select-option value="">请选择会员类型</a-select-option>
                <a-select-option value="1">代理</a-select-option>
                <a-select-option value="2">介绍人</a-select-option>
                <a-select-option value="3">商户</a-select-option>
                <a-select-option value="4">操作员</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>


          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
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

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="delete" @click="batchDel"/>
            删除
          </a-menu-item>
          <a-menu-item key="2">
            <a-icon type="lock" @click="batchFrozen('2')"/>
            冻结
          </a-menu-item>
          <a-menu-item key="3">
            <a-icon type="unlock" @click="batchFrozen('1')"/>
            解冻
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table ref="table" bordered size="middle" rowKey="id" :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">


        <span slot="action" slot-scope="text, record">

          <a-dropdown>
            <a-button>
             帐号管理
            </a-button>
            <a-menu slot="overlay">

              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

  </a-card>
</template>

<script>
  import { getAction,httpAction,putAction } from '@/api/manage'
  import { frozenBatch } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'UserList',
    mixins: [JeecgListMixin],
    components: {
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {},
        isAgent:true,
        columns: [
          {
            title: '用户账号',
            align: 'center',
            dataIndex: 'username',
            width: 80
          },
          {
            title: '成功笔数',
            align: 'center',
            width: 100,
            dataIndex: 'realname'
          },
          {
            title: '会员类型',
            align: 'center',
            width: 80,
            dataIndex: 'memberType',
            key: 'memberType',
            customRender: function(text) {
              if (text === '1') {
                return '代理'
              } else if (text === '2') {
                return '介绍人'
              } else if (text === '3') {
                return '商户'
              } else if (text === '4') {
                return '操作员'
              } else {
                return text
              }
            }
          },

          {
            title: '上级代理',
            align: 'center',
            width: 80,
            dataIndex: 'agentRealname',
            sorter: true
          },
          {
            title: '介绍人',
            align: 'center',
            width: 80,
            dataIndex: 'salesmanRealname'
          },
          {
            title: '单笔手续费',
            align: 'center',
            width: 80,
            dataIndex: 'orderFixedFee'
          },{
            title: '交易手续费率',
            align: 'center',
            width: 80,
            dataIndex: 'transactionFeeRate'
          },{
            title: '可提现金额',
            align: 'center',
            width: 80,
            dataIndex: 'amount'
          },
          {
            title: '状态',
            align: 'center',
            width: 60,
            dataIndex: 'status_dictText'
          },
          {
            title: '操作',
            // fixed: 'right',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            align: 'center',
            width: 80
          }

        ],
        url: {
          syncUser: '/process/extActProcess/doSyncUser',
          list: '/sys/user/list',
          delete: '/sys/user/delete',
          deleteBatch: '/sys/user/deleteBatch',
          cleanGoogle:'/sys/user/cleanGoogle'
        }
      }
    },
    computed: {

    },
    methods: {
      rechargeAmount: function(record){
        if(record.memberType != "1"){
          alert("无操作权限");
          return;
        }
        this.$refs.activeBusinessModal.rechargeAmount(record);
      },

      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel()
        } else if (e.key == 2) {
          this.batchFrozen(2)
        } else if (e.key == 3) {
          this.batchFrozen(1)
        }
      },
      handleFrozen: function(id, status) {
        let that = this
        frozenBatch({ ids: id, status: status }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>