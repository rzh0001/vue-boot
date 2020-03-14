<template>
  <div>
    <a-modal
      :title="title4add"
      :width="800"
      :visible="visible4Add"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item
            label="通道选择:">
            <j-checkbox
              v-model="channels.values"
              :options="channels.options"
            />
          </a-form-item>

        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  import JCheckbox from '@/components/jeecg/JCheckbox'
  export default {
    name: "ActiveChannelModal",
    components: {
      JCheckbox,
    },
    data() {
      return {
        title4add:'关联通道',
        productCode:'',
        visible4Add:false,
        model: {},
        data:[],
        channels: {
          values: '',
          options: []
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          queryBusinessByUserName: "/pay/userBusinessEntity/queryBusinessByUserName",
          channel: "/pay/channelEntity/showChannel",
          saveProductAndChannels: "/productChannel/productChannel/saveProductAndChannels"
        },
      }
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      channel:function(){
        let formData = [];
        formData.productCode = this.productCode;
        getAction(this.url.channel,formData).then((res)=>{
          if(res.success){
          this.channels.values = res.result.associated;
          this.channels.options = res.result.all;
        }else{
        }
      })
      },
      //产品关联通道
      relationChannel:function(record){
        this.visible4Add=true;
        this.productCode = record.productCode;
      },
      handleOk () {
        let formData = [];
        formData.productCode = this.productCode;
        formData.channelCodes = this.channels.values;
        getAction(this.url.saveProductAndChannels,formData).then((res)=>{
          if(res.success){
          this.visible4Add=false;
          this.$message.success(res.message);
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      close() {
        this.$emit('close');
        this.visible4Add = false;
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