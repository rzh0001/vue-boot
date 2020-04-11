<template>
  <div>
  <a-modal
    :title="title4add"
    :width="800"
    :visible="visible4Add"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="close4Add"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          label="商户">
            <a-input placeholder="商户" style="width:200px;" readOnly=true v-model="userName"/>
        </a-form-item>
        请选择产品：<br/>
        <select v-model="selected"  @change="getProductChannel()">
          <option v-for="option in products" v-bind:value="option.productCode">
            {{ option.productName}}
          </option>
        </select>
        <br/> <br/>
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
    name: "UserProductModal",
    components: {
      JCheckbox,
    },
    data() {
      return {
        isAgent:false,
        isMenber:true,
        userName: '',
        memberType:'',
        agentUsername:'',
        title: "通道详细",
        title4add:"添加",
        visible: false,
        selected:'',
        products: [],
        channels: {
          values: '',
          options: []
        },
        visible4Add:false,
        model: {},
        data:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          channelCode:{rules: [{ required: true, message: '请选择通道!' }]},

        },
        url: {
          getUserProductChannel: "/productChannel/productChannel/getUserProductChannel",
          getAllProduct: "/product/product/getAllProduct",
          saveUserChannels: "/pay/userChannelEntity/saveUserChannels"
        },
      }
    },
    mounted:function () {

    },
    methods: {
      getProduct(){
        let formData = [];
        formData.userName = this.userName;
        formData.memberType = this.memberType;
        formData.agentUsername = this.agentUsername;
        if(this.agentUsername===null){
          formData.agentUsername = "";
        }
        getAction(this.url.getAllProduct,formData).then((res)=>{
          if(res.success){
          this.products = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      getProductChannel:function(){
        let formData = [];
        formData.productCode = this.selected;
        formData.userName = this.userName;
        getAction(this.url.getUserProductChannel,formData).then((res)=>{
          if(res.success){
          this.channels.values = res.result.associated;
          this.channels.options = res.result.all;
        }else{
        }
      })
      },
      //关联产品信息
      relationProduct: function(record){
        this.products=[];
        this.isAgent=false;
        this.isMenber = true;
        this.visible4Add=true;
        if(record.memberType==="1"){
          this.isAgent=true;
          this.isMenber = false;
        }
        this.userName = record.username;
        this.memberType = record.memberType;
        this.agentUsername=record.agentUsername;
        this.getProduct();
      },
      handleOk () {
        let formData = [];
        formData.userName = this.userName;
        formData.channelCodes = this.channels.values;
        formData.productCode = this.selected;
        getAction(this.url.saveUserChannels,formData).then((res)=>{
          if(res.success){
            formData = [];
          this.visible4Add=false;
          this.$message.success(res.message);
          this.$emit('ok');
        }else{
            formData = [];
          this.$message.warning(res.message);
        }
      })
      },
      close4Add(){
        this.$emit('close');
        this.visible4Add = false;
        this.channels.values ="";
        this.channels.options = [];
        this.selected = "";
      },
      handleCancel() {
        this.close()
      },
      //删除通道
      removeRow (record) {
        const that = this;
       let userName = record.userName;
       let channelCode = record.channelCode;
       let businessCode = record.businessCode;
        var params = {
          userName:userName,
          channelCode:channelCode,
          businessCode:businessCode
        };
        httpAction(this.url.deleteUserChannel,params,"post").then((res)=>{
          if(res.success){
          that.$message.success(res.message);
          that.close();
        }else{
          that.$message.warning(res.message);
        }
      }).finally(() => {
          that.confirmLoading = false;
          that.close();
      })
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>