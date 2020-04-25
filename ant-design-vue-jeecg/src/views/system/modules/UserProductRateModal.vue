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
        请选择通道：<br/>
        <select v-model="channelCode">
          <option v-for="option in channels" v-bind:value="option.channelCode">
            {{ option.channelName}}
          </option>
        </select>
        <a-form-item label="单笔金额限制">
          <a-input-group compact>
            <a-input-number placeholder="下限"
                            v-decorator="[ 'lowerLimit']" style="width: 30%;text-align: center"
            />
            <a-input placeholder="~" disabled
                     style="width: 35px; border-left: 0px; pointer-events: none;background-color: #fff;text-align: center"/>
            <a-input-number placeholder="上限"
                            v-decorator="[ 'upperLimit']" style="width: 35%; border-left: 0px;text-align: center"
            />
          </a-input-group>
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
    name: "UserProductRateModal",
    components: {
      JCheckbox,
    },
    data() {
      return {
        isAgent:false,
        isMenber:true,
        channelCode:'',
        userName: '',
        memberType:'',
        agentUsername:'',
        title: "通道详细",
        title4add:"添加",
        visible: false,
        selected:'',
        products: [],
        channels: {

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
          getUserProductChannel: "/productChannel/productChannel/getChannelByProductAndUserName",
          getAllProduct: "/product/product/getAllProduct",
          saveUserChannels: "/pay/userChannelEntity/saveUserChannels",
          saveRate:"/pay/userChannelEntity/saveRate"
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
        console.log("请求通道入参："+formData)
        getAction(this.url.getUserProductChannel,formData).then((res)=>{
          if(res.success){
            this.channels = res.result
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
        if(this.memberType!='3'){
          alert("只能给商户设置通道限额")
          return;
        }
        let formData = [];
        formData.userName = this.userName;
        formData.channelCode = this.channelCode;
        formData.productCode = this.selected;
        formData.lowerLimit = this.form.fieldsStore.fields.lowerLimit.value;
        formData.upperLimit = this.form.fieldsStore.fields.upperLimit.value;
        console.log(formData);
        getAction(this.url.saveRate,formData).then((res)=>{
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