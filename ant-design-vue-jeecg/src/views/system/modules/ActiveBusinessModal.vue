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
            <a-input placeholder="商户" style="width:200px;" readonly=true v-model="userName"/>
          </a-form-item>
          通道：<br/><br/>
            <select v-model="selected"  @change="getBusinesses()">
              <option v-for="option in channels" v-bind:value="option.channelCode">
                {{ option.channelName}}
              </option>
            </select>
          <br/>
          <a-form-item
            label="挂码账号">
            <j-checkbox
              v-model="businesses.values"
              :options="businesses.options"
            />
          </a-form-item>

        </a-form>
      </a-spin>
    </a-modal>
    <a-modal
      :title="title4add"
      :width="800"
      :visible="visible4Recharge"
      :confirmLoading="confirmLoading"
      @ok="rechargeAmountOk"
      @cancel="close4Recharge"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item
            label="商户">
            <a-input placeholder="商户" style="width:200px;" readonly=true v-model="userName"/>
          </a-form-item>
          通道：<br/>
          <select v-model="selected"  @change="getBusinessCodesByAgentName()">
            <option v-for="option in channels" v-bind:value="option.channelCode">
              {{ option.channelName}}
            </option>
          </select>
          <br/> <br/>
          挂马账号：<br/>
          <select v-model="business" >
            <option v-for="option in businessCodes" v-bind:value="option">
              {{ option}}
            </option>
          </select>
          <br/>
          <a-form-item
            label="充值金额">
            <a-input placeholder="充值金额" style="width:200px;" v-model="amount"/>
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
    name: "UserChannelModal",
    components: {
      JCheckbox,
    },
    data() {
      return {
        amount:"",
        selected:"",
        business:"",
        businessCodes:[],
        visible4Recharge:false,
        isAgent:false,
        isMenber:true,
        userName: '',
        title4add:"关联子账号",
        visible: false,
        channels: [],
        visible4Add:false,
        model: {},
        data:[],
        businesses: {
          values: '',
          options: []
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          channelCode:{rules: [{ required: true, message: '请选择通道!' }]},
          rechargeAmount:{rules: [{ required: true, message: '充值金额不能为空' }]},
        },
        url: {
          queryBusinessByUserName: "/pay/userBusinessEntity/queryBusinessByUserName",
          channel: "/pay/channelEntity/channel",
          activeBusiness:"/pay/userBusinessEntity/activeBusiness",
          getBusinessCodesByAgentName:"/pay/userBusinessEntity/getBusinessCodesByAgentName",
          rechargeAmount:"/pay/userBusinessEntity/rechargeAmount"
        },
      }
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      channel(){
        httpAction(this.url.channel,null,'get').then((res)=>{
          if(res.success){
          this.channels = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      getBusinesses:function(){
        let formData = [];
        formData.channelCode = this.selected;
        formData.userName = this.userName;
        getAction(this.url.queryBusinessByUserName,formData).then((res)=>{
          if(res.success){
          this.businesses.values = res.result.associated;
          this.businesses.options = res.result.all;
        }else{
        }
      })
      },
      getBusinessCodesByAgentName:function(){
        let formData = [];
        formData.channelCode = this.selected;
        formData.userName = this.userName;
        getAction(this.url.getBusinessCodesByAgentName,formData).then((res)=>{
          if(res.success){
            console.log( res.result)
            this.businessCodes = res.result;
        }else{
        }
      })
      },
      //激活账号
      activeBusiness:function(record){
        this.title4add= "激活子账号";
        this.isAgent=false;
        this.isMenber = true;
        this.visible4Add=true;
        this.selected="";
        if(record.memberType==="1"){
          this.isAgent=true;
          this.isMenber = false;
        }
        this.userName = record.username;
      },
      rechargeAmount:function(record){
        this.visible4Add=false;
        this.visible4Recharge = true;
        this.selected="";
        this.title4add='充值';
        this.userName = record.username;
      },
      rechargeAmountOk(){
        let formData = [];
        formData.userName = this.userName;
        formData.channelCode = this.selected;
        formData.businessCode = this.business;
        formData.rechargeAmount = this.amount;
        console.log(formData);
        getAction(this.url.rechargeAmount,formData).then((res)=>{
          if(res.success){
          this.visible4Add=false;
          this.visible4Recharge = false;
          this.selected = "";
          this.business = "";
          this.amount="";
          this.$message.success(res.message);
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      })
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
      close4Add(){
        this.$emit('close');
        this.selected="";
        this.businesses.values="";
        this.businesses.options=[];
        this.visible4Add = false;
      },
      close4Recharge(){
        this.$emit('close');
        this.selected="";
        this.business = "";
        this.amount="";
        this.visible4Recharge = false;
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