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
          <a-form-item label="产品" style="width: 300px">
            <j-dict-select-tag  @change="changeFunction()" :triggerChange="true" placeholder="请选择产品类型" dictCode="pay_v2_product,product_name,product_code" v-decorator="['productCode', validatorRules.productCode ]"/>
          </a-form-item>
          <a-form-item label="请选择要关联的通道" style="width: 300px">
            <j-multi-select-tag
              v-model="channelCodes"
              dictCode="pay_v2_channel,channel_name,channel_code"
              placeholder="请选择要关联的通道">
            </j-multi-select-tag>
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
    name: "RelatedProductChannelsModal",
    components: {
      JCheckbox,
      JMultiSelectTag
    },
    data() {
      return {
        title:"设置入金渠道",
        visible: false,
        channelCodes:"",
        productCode:"",
        productName:"",
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          productCode:{rules: [{ required: true, message: '产品类型不能为空!' }]},
          channelName:{rules: [{ required: true, message: '请输入通道名称!' }]}
        },
        url: {
          getChannelsByProductCode:"/v2/payProductChannel/saveProductChannels",
          getNotRelatedChannelCodeByProductCode:"/v2/payProductChannel/getNotRelatedChannelCodeByProductCode"
        },
      }
    },
    mounted:function () {

    },
    methods: {
      changeFunction:function(){
        let formData = [];
        formData.productCode = this.productCode;
        getAction(this.url.getNotRelatedChannelCodeByProductCode,formData).then((res)=>{
          if(res.success){
          this.channelCodes=res.result;
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      relatedProductChannels:function(record){
        this.title= "产品关联通道信息";
        this.visible=true;
      },
      handleOk () {
        let formData = [];
        formData.productCode = this.productCode;
        formData.channelCodes = this.channelCodes;
        getAction(this.url.saveProductChannels,formData).then((res)=>{
          if(res.success){
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
        this.channelCodes="";
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