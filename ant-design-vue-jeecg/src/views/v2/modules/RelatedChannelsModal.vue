<template>
  <div>
    <a-modal
      :title="title"
      :width="800"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item label="请选择要关联的通道" style="width: 300px">
            <j-multi-select-tag
              v-model="channelCodes"
              dictCode="pay_v2_channel,channel_name,channel_code"
              placeholder="请选择要关联的通道">
            </j-multi-select-tag>
            {{ channelCodes }}
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  import JCheckbox from '@/components/jeecg/JCheckbox'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  export default {
    name: "RelatedChannelsModal",
    components: {
      JCheckbox,
      JMultiSelectTag
    },
    data() {
      return {
        title:"关联通道信息",
        visible: false,
        channelCodes:"",
        channels: [],
        model: {},
        data:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        url: {
        },
      }
    },
    mounted:function () {
    },
    methods: {
      //激活账号
      relatedProductChannels:function(record){
        this.title= "产品关联通道信息";
        this.visible=true;
        this.userName = record.username;
      },
      handleOk () {
        let formData = [];
        formData.userName = this.userName;
        formData.channelCode = this.selected;
        formData.businesses = this.businesses.values;
        console.log(formData);
        getAction(this.url.activeBusiness,formData).then((res)=>{
          if(res.success){
          this.visible4Add=false;
          this.selected = "";
          this.businesses.values="";
          this.businesses.options=[];
          this.$message.success(res.message);
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel() {
        this.close()
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>