<template>
  <a-card :bordered="false">


    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">测试</a-button>
    </div>

    <!-- table区域-end -->

    <!-- 表单区域 -->
    <channelEntity-modal ref="modalForm" @ok="modalFormOk"></channelEntity-modal>
  </a-card>
</template>

<script>
  import ChannelEntityModal from './modules/PayTestModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "ChannelEntityList",
    mixins:[JeecgListMixin],
    components: {
      ChannelEntityModal
    },
    data () {
      return {
        description: '通道设置管理页面',
        url: {
          list: "/pay/channelEntity/list",
          delete: "/pay/channelEntity/delete",
          deleteBatch: "/pay/channelEntity/deleteBatch",
          exportXlsUrl: "pay/channelEntity/exportXls",
          importExcelUrl: "pay/channelEntity/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>