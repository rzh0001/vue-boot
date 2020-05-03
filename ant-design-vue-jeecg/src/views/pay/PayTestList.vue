<template>
  <a-card :bordered="false">


    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">测试</a-button>
      <a-button @click="handleAdd2" type="primary" icon="plus">测试V2</a-button>
    </div>

    <!-- table区域-end -->

    <!-- 表单区域 -->
    <payTest-modal ref="modalForm" @ok="modalFormOk"></payTest-modal>
    <payTestV2-modal ref="modalForm2" @ok="modalFormOk"></payTestV2-modal>
  </a-card>
</template>

<script>
  import PayTestModal from './modules/PayTestModal'
  import PayTestV2Modal from './modules/PayTestV2Modal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "ChannelEntityList",
    mixins:[JeecgListMixin],
    components: {
      PayTestModal,
      PayTestV2Modal,
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
      handleAdd2: function () {
        this.$refs.modalForm2.add();
        this.$refs.modalForm2.title = "新增";
        this.$refs.modalForm2.disableSubmit = false;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>